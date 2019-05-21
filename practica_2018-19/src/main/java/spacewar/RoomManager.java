package spacewar;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {
	private Executor roomExecutor;
	private final int roomCapacity = 6;
	public ConcurrentHashMap<Integer,Room> waitingRoomsMap;
	private ArrayBlockingQueue<Room> fullRooms;
	private ArrayBlockingQueue<Room> waitingRooms;

	private AtomicInteger roomids = new AtomicInteger();
	
	public enum gameStyle{
		MeteorParty
	}

	// El contructor inicializa las colas y el threadpool que controla las salas
	public RoomManager() {
		this.roomExecutor = Executors.newCachedThreadPool();
		waitingRoomsMap = new ConcurrentHashMap<Integer,Room>();
		fullRooms = new ArrayBlockingQueue<Room>(roomCapacity);
		waitingRooms = new ArrayBlockingQueue<Room>(roomCapacity);
	}

	// Recibe un jugador, mira si hay salas disponibles y le asocia a la primera de
	// la cola,
	// que se presume que es la más antigua. Si no hay salas crea una.
	public int ConnectNewPlayer(Player player ,String style ) {
		synchronized (waitingRoomsMap) {
			if (waitingRoomsMap.isEmpty()) {
				Room room = new Room(roomCapacity, this, style);
				room.roomid = roomids.incrementAndGet();
				room.addPlayer(player);
				roomExecutor.execute(() -> room.PreMatchLobbyThread());
				waitingRoomsMap.put(room.roomid,room);
				waitingRooms.add(room);
				return room.roomid;
			} else if (!waitingRooms.peek().addPlayer(player)) {
				if (waitingRooms.peek().getPeopleInside() == roomCapacity) {
					try {
						fullRooms.add(waitingRooms.take());
						Room room = new Room(roomCapacity, this, null);
						room.roomid = roomids.incrementAndGet();
						room.addPlayer(player);
						roomExecutor.execute(() -> room.PreMatchLobbyThread());
						waitingRoomsMap.putIfAbsent(room.roomid,room);
						waitingRooms.add(room);
						return room.roomid;
					} catch (InterruptedException e) {
						System.out.println("Error al añadir al jugador a una sala, intentando de nuevo.");
						ConnectNewPlayer(player,style);
						e.printStackTrace();
					}
				}
			}
		}
		return 0;
	}

	// intenta borrar la sala de la lista de salas en espera, donde debería estar si
	// se ha borrado de forma normal,
	// pero si no está entonces la borra de las que no están en espera, el otro
	// sitio donde puede estar
	public void deleteRoom(Room room) {
		waitingRoomsMap.remove(room.roomid);
		if (!waitingRooms.remove(room))
			fullRooms.remove(room);
		roomids.decrementAndGet();
	}
	

}
