package spacewar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import spacewar.Room.GameStyle;

public class RoomManager {
	private Executor roomExecutor;
	private final int roomCapacity = 6;

	private Map<GameStyle, ConcurrentHashMap<Integer, Room>> waitingRoomsMap;
	private ConcurrentHashMap<Integer, Room> fullRooms;
	private ConcurrentHashMap<Integer, Player> noRoomPlayers;
	private AtomicInteger roomIdCounter;
	private ObjectMapper mapper = new ObjectMapper();

	private static RoomManager roomManagerInstance;

	// El contructor inicializa las colas y el threadpool que controla las salas
	private RoomManager() {
		this.roomExecutor = Executors.newCachedThreadPool();
		waitingRoomsMap = new ConcurrentHashMap<GameStyle, ConcurrentHashMap<Integer, Room>>();
		for (GameStyle gs : GameStyle.values())
			waitingRoomsMap.put(gs, new ConcurrentHashMap<Integer, Room>());
		fullRooms = new ConcurrentHashMap<Integer, Room>();
		noRoomPlayers = new ConcurrentHashMap<Integer, Player>();
		roomIdCounter = new AtomicInteger(0);
	}

	// Método para obtener la instancia de roomManager que es un singleton
	public static RoomManager getSingletonInstance() {
		if (roomManagerInstance == null)
			roomManagerInstance = new RoomManager();

		return roomManagerInstance;
	}

	// override para que no se pueda clonar la clase
	@Override
	public RoomManager clone() {
		try {
			throw new CloneNotSupportedException();
		} catch (CloneNotSupportedException ex) {
			System.out.println("RoomManager es un objeto singleton, no se puede clonar.");
		}
		return null;
	}

	// Recibe un jugador, mira si hay salas disponibles y le asocia a la primera de
	// la cola,
	// que se presume que es la más antigua. Si no hay salas crea una.

	public void addNoRoomPlayer(Player player) {
		noRoomPlayers.put(player.getPlayerId(), player);
	}
	
	public void auxStartGame(int roomid , Player player)  {
	 waitingRoomsMap.get(GameStyle.battleRoyale).get(roomid).addPlayer(player);
	}
	
	
	public void updateMyTable(Player player) {
		for (Room room : waitingRoomsMap.get(GameStyle.battleRoyale).values()) {
			
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "UPDATE ROOM TABLE");
			msg.put("roomname", room.name);
			msg.put("roomcreator", room.creator);
			msg.put("roomid", room.getId());
			msg.put("playersinside", room.getPeopleInside());
			msg.put("totalcapacity", room.capacity);
			roomExecutor.execute(()->{try {
				player.sendMessage(msg.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}});			
		
	}
		
	}
	
	public void updateAllTableOf(Player player) {
		for (Room room : waitingRoomsMap.get(GameStyle.battleRoyale).values()) {
			
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "UPDATE ROOM TABLE");
			msg.put("roomname", room.name);
			msg.put("roomcreator", room.creator);
			msg.put("roomid", room.getId());
			msg.put("playersinside", room.getPeopleInside());
			msg.put("totalcapacity", room.capacity);
			roomExecutor.execute(()->{try {
				player.sendMessage(msg.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}});			
		
	}
	}
	

	
	
	public void clearAllTables() {
		

		ObjectNode msg = mapper.createObjectNode();
		msg.put("event", "CLEAR TABLE");
		for(Player player: noRoomPlayers.values()) {
		roomExecutor.execute(()->{try {
			player.sendMessage(msg.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
		}
		
	}
	
	public void sendPlayerReady(Player player , int roomid)  {
		waitingRoomsMap.get(GameStyle.battleRoyale).get(roomid).readyAndCheck(player);
	}

	public void createNewRoom(GameStyle gameStyle, String roomname, String roomcreator) {
		Room room = new Room(roomIdCounter.incrementAndGet(), this, gameStyle);
		room.name = roomname;
		room.creator = roomcreator;
		waitingRoomsMap.get(gameStyle).put(room.getId(), room);
		clearAllTables();


	}

	public void ConnectToExisting(Player player, GameStyle gameStyle, int id)  {
		if (waitingRoomsMap.get(gameStyle).containsKey(id)) {
			Room room = waitingRoomsMap.get(gameStyle).get(id);
			room.addPlayer(player);
			noRoomPlayers.remove(player.getPlayerId());
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "ROOM ASIGNED");
			msg.put("roomid", room.getId());
			msg.put("roomname",room.name);
			player.sendMessage(msg.toString());
		} else {
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "ROOM DENIED");
			player.sendMessage(msg.toString());
		}
		// }
	}
	
	public void requestRoomStatus(Player player , int roomid) {
		if (waitingRoomsMap.get(GameStyle.battleRoyale).contains(roomid)) {
			Room current = waitingRoomsMap.get(GameStyle.battleRoyale).get(roomid);
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "ROOM STATUS");
			msg.put("totalcapacity", current.capacity);
			msg.put("playersinside", current.getPeopleInside());
			msg.put("playersready", current.getNReady());
			
			player.sendMessage(msg.toString());
			
		}else if (fullRooms.containsKey(roomid)) {
			ObjectNode msg = mapper.createObjectNode();
			Room current = fullRooms.get(roomid);
			msg.put("event", "ROOM STATUS");
			msg.put("totalcapacity", current.capacity);
			msg.put("playersinside", current.getPeopleInside());
			msg.put("playersready", current.getNReady());

			player.sendMessage(msg.toString());

		}
		
		
	}

	
	public void roomIsFull(int roomid , GameStyle style , Room room) {
		
		waitingRoomsMap.get(style).remove(roomid);
		fullRooms.put(roomid, room);
		clearAllTables();
	}
	// Vamos a dejar esto por si nos hace falta para el matchmaking
	public void ConnectNewPlayer(Player player, GameStyle gameStyle){
		if (waitingRoomsMap.get(gameStyle).isEmpty()) {
			Room room = new Room(roomIdCounter.incrementAndGet(), this, gameStyle);
			room.addPlayer(player);
			roomExecutor.execute(() -> {
				try {
					room.PreMatchLobbyThread();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			waitingRoomsMap.get(gameStyle).put(room.getId(), room);
			clearAllTables();
		} else {
			Room tempRoom = waitingRoomsMap.get(gameStyle).elements().nextElement();
			if (!tempRoom.addPlayer(player)) {

				if (tempRoom.getPeopleInside() == roomCapacity) {
					fullRooms.put(tempRoom.getId(), tempRoom);
					waitingRoomsMap.get(gameStyle).remove(tempRoom);
					Room room = new Room(roomIdCounter.incrementAndGet(), this, gameStyle);
					clearAllTables();
					room.addPlayer(player);
					roomExecutor.execute(() -> {
						try {
							room.PreMatchLobbyThread();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					waitingRoomsMap.get(gameStyle).put(room.getId(), room);
				} else {
					System.out.println("Error al añadir al jugador a una sala, intentando de nuevo.");
					ConnectNewPlayer(player, gameStyle);
				}
			}
		}
		// }
	}

	// intenta borrar la sala de la lista de salas en espera, donde debería estar si
	// se ha borrado de forma normal,
	// pero si no está entonces la borra de las que no están en espera, el otro
	// sitio donde puede estar
	public void deleteRoom(Room room) {
		if (waitingRoomsMap.get(room.getGameStyle()).remove(room.getId()) == null)
			fullRooms.remove(room.getId());
		clearAllTables();
			
	}

	// sobrecarga del método anterior usando la id en vez de la sala
	public void deleteRoom(int id) {
		if (fullRooms.containsKey(id))
			fullRooms.remove(id);
		else {
			for (GameStyle gs : GameStyle.values()) {
				if (waitingRoomsMap.get(gs).containsKey(id))
					waitingRoomsMap.get(gs).remove(id);
			}
		}
		clearAllTables();
	}

	// método para borrar a un jugador, el cual busca la sala del mismo y la
	// notifica
	public void removePlayer(Player player) {
		if (fullRooms.containsKey(player.GetRoomId()))
			fullRooms.get(player.GetRoomId()).removePlayer(player);
		else {
			for (GameStyle gs : GameStyle.values()) {
				if (waitingRoomsMap.get(gs).containsKey(player.GetRoomId()))
					waitingRoomsMap.get(gs).get(player.GetRoomId()).removePlayer(player);
			}
		}
	}

	// método que busca la sala a la que pertenece
	public void getChatMessage(JsonNode msg) {
		int id = msg.get("room").asInt();
		if (fullRooms.containsKey(id))
			fullRooms.get(id).sendChatMessage(msg);
		else {
			for (GameStyle gs : GameStyle.values()) {
				if (waitingRoomsMap.get(gs).containsKey(id))
					waitingRoomsMap.get(gs).get(id).sendChatMessage(msg);
			}
		}
	}

	public SpacewarGame getGame(int id) {
		if (fullRooms.containsKey(id)) {
			return fullRooms.get(id).getGame();
		} else {
			for (GameStyle gs : GameStyle.values()) {
				if (waitingRoomsMap.get(gs).containsKey(id))
					return waitingRoomsMap.get(gs).get(id).getGame();
			}
		}
		return null;
	}

	/// METODOS PARA TESTEO///

	// Este método existe para testear y comprueba si un jugador existe
	public boolean checkPlayer(Player player) {
		boolean check = false;
		if (fullRooms.containsKey(player.GetRoomId())) {
			if (fullRooms.get(player.GetRoomId()).checkPlayer(player))
				check = true;
		} else {
			for (GameStyle gs : GameStyle.values()) {
				if (waitingRoomsMap.get(gs).containsKey(player.GetRoomId())) {
					if (waitingRoomsMap.get(gs).get(player.GetRoomId()).checkPlayer(player))
						check = true;
				}

			}
		}
		return check;
	}

	public boolean checkRoom(int id) {
		boolean check = false;
		if (fullRooms.containsKey(id))
			check = true;
		else {
			for (GameStyle gs : GameStyle.values()) {
				if (waitingRoomsMap.get(gs).containsKey(id))
					check = true;
			}
		}
		return check;

	}

}
