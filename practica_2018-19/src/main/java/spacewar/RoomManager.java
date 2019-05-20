package spacewar;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class RoomManager {
	private Executor roomExecutor;
	private final int roomCapacity = 6;
	public BlockingQueue<Room> waitingRooms;
	private BlockingQueue<Room> fullRooms;

	// El contructor inicializa las colas y el threadpool que controla las salas
	public RoomManager() {
		this.roomExecutor = Executors.newCachedThreadPool();
		waitingRooms = new LinkedBlockingQueue<Room>();
		fullRooms = new LinkedBlockingQueue<Room>();
	}

	// Recibe un jugador, mira si hay salas disponibles y le asocia a la primera de
	// la cola,
	// que se presume que es la más antigua. Si no hay salas crea una.
	public void ConnectNewPlayer(Player player) {
		synchronized (waitingRooms) {
			if (waitingRooms.isEmpty()) {
				Room room = new Room(roomCapacity, this);
				room.addPlayer(player);
				roomExecutor.execute(() -> room.PreMatchLobbyThread());
				waitingRooms.add(room);
			} else if (!waitingRooms.peek().addPlayer(player)) {
				if (waitingRooms.peek().getPeopleInside() == roomCapacity) {
					try {
						fullRooms.add(waitingRooms.take());
						Room room = new Room(roomCapacity, this);
						room.addPlayer(player);
						roomExecutor.execute(() -> room.PreMatchLobbyThread());
						waitingRooms.add(room);
					} catch (InterruptedException e) {
						System.out.println("Error al añadir al jugador a una sala, intentando de nuevo.");
						ConnectNewPlayer(player);
						e.printStackTrace();
					}
				}
			}
		}
	}

	// intenta borrar la sala de la lista de salas en espera, donde debería estar si
	// se ha borrado de forma normal,
	// pero si no está entonces la borra de las que no están en espera, el otro
	// sitio donde puede estar
	public void deleteRoom(Room room) {
		if (!waitingRooms.remove(room))
			fullRooms.remove(room);
	}

}
