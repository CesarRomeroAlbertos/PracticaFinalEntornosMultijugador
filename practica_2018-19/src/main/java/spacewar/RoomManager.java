package spacewar;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import spacewar.Room.GameStyle;

public class RoomManager {
	private Executor roomExecutor;
	private final int roomCapacity = 6;
	private Map<GameStyle, ConcurrentHashMap<Integer, Room>> waitingQueues = new ConcurrentHashMap<GameStyle, ConcurrentHashMap<Integer, Room>>();
	// private BlockingQueue<Room> waitingRooms;
	private ConcurrentHashMap<Integer, Room> fullRooms;
	private AtomicInteger roomIdCounter;

	// El contructor inicializa las colas y el threadpool que controla las salas
	public RoomManager() {
		this.roomExecutor = Executors.newCachedThreadPool();
		for (GameStyle gs : GameStyle.values())
			waitingQueues.put(gs, new ConcurrentHashMap<Integer, Room>());
		fullRooms = new ConcurrentHashMap<Integer, Room>();
		roomIdCounter = new AtomicInteger(0);
	}

	// Recibe un jugador, mira si hay salas disponibles y le asocia a la primera de
	// la cola,
	// que se presume que es la más antigua. Si no hay salas crea una.
	public void ConnectNewPlayer(Player player, GameStyle gameStyle) {
		ConcurrentHashMap<Integer, Room> waitingRooms = (ConcurrentHashMap<Integer, Room>) waitingQueues.get(gameStyle);
		synchronized (waitingRooms) {
			if (waitingRooms.isEmpty()) {
				Room room = new Room(roomIdCounter.incrementAndGet(), this, gameStyle);
				room.addPlayer(player);
				roomExecutor.execute(() -> room.PreMatchLobbyThread());
				waitingRooms.put(room.getId(), room);
			} else {
				Room tempRoom = waitingRooms.elements().nextElement();
				if (!tempRoom.addPlayer(player)) {

					if (tempRoom.getPeopleInside() == roomCapacity) {
						fullRooms.put(tempRoom.getId(),tempRoom);
						waitingRooms.remove(tempRoom);
						Room room = new Room(roomIdCounter.incrementAndGet(), this, gameStyle);
						room.addPlayer(player);
						roomExecutor.execute(() -> room.PreMatchLobbyThread());
						waitingRooms.put(room.getId(), room);
					} else {
						System.out.println("Error al añadir al jugador a una sala, intentando de nuevo.");
						ConnectNewPlayer(player, gameStyle);
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
		if (waitingQueues.get(room.getGameStyle()).remove(room.getId()) == null)
			fullRooms.remove(room.getId());
	}

	public void removePlayer(Player player) {
		for(GameStyle gs: GameStyle.values())
		{
			waitingQueues.get(gs).get(player.GetRoomId()).removePlayer(player);
			fullRooms.get(player.GetRoomId()).removePlayer(player);
		}
	}

}
