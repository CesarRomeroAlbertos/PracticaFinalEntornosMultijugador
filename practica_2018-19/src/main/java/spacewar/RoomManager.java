package spacewar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.node.ObjectNode;

import spacewar.Room.GameStyle;

public class RoomManager {
	private Executor roomExecutor;
	private final int roomCapacity = 6;
	private Map<GameStyle, ConcurrentHashMap<Integer, Room>> waitingRoomsMap = new ConcurrentHashMap<GameStyle, ConcurrentHashMap<Integer, Room>>();
	private ConcurrentHashMap<Integer, Room> fullRooms;
	private AtomicInteger roomIdCounter;

	// El contructor inicializa las colas y el threadpool que controla las salas
	public RoomManager() {
		this.roomExecutor = Executors.newCachedThreadPool();
		for (GameStyle gs : GameStyle.values())
			waitingRoomsMap.put(gs, new ConcurrentHashMap<Integer, Room>());
		fullRooms = new ConcurrentHashMap<Integer, Room>();
		roomIdCounter = new AtomicInteger(0);
	}

	// Recibe un jugador, mira si hay salas disponibles y le asocia a la primera de
	// la cola,
	// que se presume que es la más antigua. Si no hay salas crea una.
	public void ConnectNewPlayer(Player player, GameStyle gameStyle) {
		ConcurrentHashMap<Integer, Room> waitingRooms = (ConcurrentHashMap<Integer, Room>) waitingRoomsMap
				.get(gameStyle);
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
						fullRooms.put(tempRoom.getId(), tempRoom);
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
		if (waitingRoomsMap.get(room.getGameStyle()).remove(room.getId()) == null)
			fullRooms.remove(room.getId());
	}

	public void deleteRoom(int id) {
		for (GameStyle gs : GameStyle.values()) {
			if (waitingRoomsMap.get(gs).containsKey(id))
				waitingRoomsMap.get(gs).remove(id);
			else if (fullRooms.containsKey(id))
				fullRooms.remove(id);
		}
	}

	public void removePlayer(Player player) {
		for (GameStyle gs : GameStyle.values()) {
			if (waitingRoomsMap.get(gs).containsKey(player.GetRoomId()))
				waitingRoomsMap.get(gs).get(player.GetRoomId()).removePlayer(player);
			else if (fullRooms.containsKey(player.GetRoomId()))
				fullRooms.get(player.GetRoomId()).removePlayer(player);
		}
	}

	public void getChatMessage(ObjectNode msg) {
		int id = msg.get("room").asInt();
		for (GameStyle gs : GameStyle.values()) {
			if (waitingRoomsMap.get(gs).containsKey(id))
				waitingRoomsMap.get(gs).get(id).sendChatMessage(msg);
		}
		if (fullRooms.containsKey(id))
			fullRooms.get(id).sendChatMessage(msg);
	}

	/// METODOS PARA TESTEO///

	// Este método existe para testear y comprueba si un jugador existe
	public boolean checkPlayer(Player player) {
		boolean check = false;
		for (GameStyle gs : GameStyle.values()) {
			if (waitingRoomsMap.get(gs).containsKey(player.GetRoomId())) {
				if (waitingRoomsMap.get(gs).get(player.GetRoomId()).checkPlayer(player))
					check = true;
			} else if (fullRooms.containsKey(player.GetRoomId())) {
				if (fullRooms.get(player.GetRoomId()).checkPlayer(player))
					check = true;
			}
		}
		return check;
	}

	public boolean checkRoom(int id) {
		boolean check = false;
		for (GameStyle gs : GameStyle.values()) {
			if (waitingRoomsMap.get(gs).containsKey(id))
				check = true;
			else if (fullRooms.containsKey(id))
				check = true;
		}
		return check;

	}

}
