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
import spacewar.Room.State;

public class RoomManager {
	private Executor roomExecutor;

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


	//Añade un jugador al lobby, el cual de primeras no forma parte de ninguna sala
	public void addNoRoomPlayer(Player player) {
		noRoomPlayers.put(player.getPlayerId(), player);
	}

	
	//Método que se usa para dar al jugador la información de las salas cuando entra al lobby
	public void updateMyTable(Player player) {
		for (Room room : waitingRoomsMap.get(GameStyle.battleRoyale).values()) {

			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "UPDATE ROOM TABLE");
			msg.put("roomname", room.name);
			msg.put("roomcreator", room.creator);
			msg.put("roomid", room.getId());
			msg.put("playersinside", room.getPeopleInside());
			msg.put("totalcapacity", room.capacity);
			roomExecutor.execute(() -> {
				try {
					player.sendMessage(msg.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

		}
		for (Room room : fullRooms.values()) {
			if (room.state != State.Playing) {
				ObjectNode msg = mapper.createObjectNode();
				msg.put("event", "UPDATE ROOM TABLE");
				msg.put("roomname", room.name);
				msg.put("roomcreator", room.creator);
				msg.put("roomid", room.getId());
				msg.put("playersinside", room.getPeopleInside());
				msg.put("totalcapacity", room.capacity);
				roomExecutor.execute(() -> {
					try {
						player.sendMessage(msg.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}

		}

	}

	//método que se usa para actualizar las tablas de salas de todos los jugadores del lobby,
	//generalmente cuando hay cambios en dichas tablas
	public void updateAllTableOf(Player player) {

		for (Room room : waitingRoomsMap.get(GameStyle.battleRoyale).values()) {
			if (!player.getWaiting()) {

				ObjectNode msg = mapper.createObjectNode();
				msg.put("event", "UPDATE ROOM TABLE");
				msg.put("roomname", room.name);
				msg.put("roomcreator", room.creator);
				msg.put("roomid", room.getId());
				msg.put("playersinside", room.getPeopleInside());
				msg.put("totalcapacity", room.capacity);
				roomExecutor.execute(() -> {
					try {
						player.sendMessage(msg.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}

		}
		for (Room room : fullRooms.values()) {
			if (room.state != State.Playing) {
				ObjectNode msg = mapper.createObjectNode();
				msg.put("event", "UPDATE ROOM TABLE");
				msg.put("roomname", room.name);
				msg.put("roomcreator", room.creator);
				msg.put("roomid", room.getId());
				msg.put("playersinside", room.getPeopleInside());
				msg.put("totalcapacity", room.capacity);
				roomExecutor.execute(() -> {
					try {
						player.sendMessage(msg.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}

		}
	}

	//Método que se usa para vaciar las tablas de salas de los jugadores
	public void clearAllTables() {

		ObjectNode msg = mapper.createObjectNode();
		msg.put("event", "CLEAR TABLE");
		for (Player player : noRoomPlayers.values()) {
			roomExecutor.execute(() -> {
				try {
					player.sendMessage(msg.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}

	}
	
	//método que saca de las salas activas del lobby una sala, siendo que dicha sala
	//esté llena
	public void roomIsFull(int roomid, GameStyle style, Room room) {

		waitingRoomsMap.get(style).remove(roomid);
		fullRooms.put(roomid, room);
	}

	//método que devuelve a las salas activas del lobby una sala
	public void roomIsWaiting(Room room) {
		fullRooms.remove(room.getId());
		waitingRoomsMap.get(GameStyle.battleRoyale).put(room.getId(), room);
	}

	//método que avisa a la sala de que un jugador se ha salido de la misma
	//antes de empezar la partida
	public void sendPlayerCanceled(Player player, int roomid) {
		if (waitingRoomsMap.get(GameStyle.battleRoyale).containsKey(roomid)) {
			waitingRoomsMap.get(GameStyle.battleRoyale).get(roomid).playerCanceled(player);
		} else if (fullRooms.containsKey(roomid)) {
			fullRooms.get(roomid).playerCanceled(player);
		}
	}

	//método que indica a la sala que un jugador está listo para empezar la partida
	public void sendPlayerReady(Player player, int roomid) {

		if (waitingRoomsMap.get(GameStyle.battleRoyale).containsKey(roomid)) {
			waitingRoomsMap.get(GameStyle.battleRoyale).get(roomid).readyAndCheck(player);
		} else if (fullRooms.containsKey(roomid)) {
			fullRooms.get(roomid).readyAndCheck(player);
		}
	}

	//método que crea una nueva sala, usado cuando el jugador crea la sala él mismo,
	//no a través del matchmaking
	public void createNewRoom(GameStyle gameStyle, String roomname, String roomcreator) {
		Room room = new Room(roomIdCounter.incrementAndGet(), this, gameStyle);
		room.name = roomname;
		room.creator = roomcreator;
		waitingRoomsMap.get(gameStyle).put(room.getId(), room);
		clearAllTables();

	}

	//método que se usa cuando un jugador quiere unirse a una sala concreta ya creada
	public void ConnectToExisting(Player player, GameStyle gameStyle, int id) {
		if (waitingRoomsMap.get(gameStyle).containsKey(id)) {
			Room room = waitingRoomsMap.get(gameStyle).get(id);
			room.addPlayer(player);
			noRoomPlayers.remove(player.getPlayerId());
			player.setWaiting(true);
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "ROOM ASIGNED");
			msg.put("roomid", room.getId());
			msg.put("roomname", room.name);
			player.sendMessage(msg.toString());

		} else {
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "ROOM DENIED");
			player.sendMessage(msg.toString());
		}
		// }
	}

	/*
	public void requestRoomStatus(Player player, int roomid) {
		if (waitingRoomsMap.get(GameStyle.battleRoyale).contains(roomid)) {
			Room current = waitingRoomsMap.get(GameStyle.battleRoyale).get(roomid);
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "ROOM STATUS");
			msg.put("totalcapacity", current.capacity);
			msg.put("playersinside", current.getPeopleInside());
			msg.put("playersready", current.getNReady());

			player.sendMessage(msg.toString());

		} else if (fullRooms.containsKey(roomid)) {
			ObjectNode msg = mapper.createObjectNode();
			Room current = fullRooms.get(roomid);
			msg.put("event", "ROOM STATUS");
			msg.put("totalcapacity", current.capacity);
			msg.put("playersinside", current.getPeopleInside());
			msg.put("playersready", current.getNReady());

			player.sendMessage(msg.toString());

		}

	}*/


	// Este método se usa cuando el jugador elige el matchmaking automático en cuyo
	// caso
	// primero si no hay salas crea una y le mete en esta, si hay alguna le intenta
	// meter en esa
	// y si dicha sala está llena crea una nueva y le mete
	public void ConnectNewPlayer(Player player, GameStyle gameStyle) {
		if (waitingRoomsMap.get(gameStyle).isEmpty()) {
			Room room = new Room(roomIdCounter.incrementAndGet(), this, gameStyle);
			room.addPlayer(player);
			room.name = "auto - " + player.getName();
			room.state = State.Waiting;

			waitingRoomsMap.get(gameStyle).put(room.getId(), room);

			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "ROOM ASIGNED");
			msg.put("roomid", room.getId());
			msg.put("roomname", room.name);
			player.sendMessage(msg.toString());
		} else {
			Room tempRoom = waitingRoomsMap.get(gameStyle).elements().nextElement();
			if (!tempRoom.addPlayer(player)) {

				if (tempRoom.getPeopleInside() == tempRoom.capacity) {
					fullRooms.put(tempRoom.getId(), tempRoom);
					waitingRoomsMap.get(gameStyle).remove(tempRoom);
					Room room = new Room(roomIdCounter.incrementAndGet(), this, gameStyle);
					clearAllTables();
					room.addPlayer(player);
					room.name = "auto - " + player.getName();
					room.state = State.Waiting;

					waitingRoomsMap.get(gameStyle).put(room.getId(), room);

					ObjectNode msg = mapper.createObjectNode();
					msg.put("event", "ROOM ASIGNED");
					msg.put("roomid", room.getId());
					msg.put("roomname", room.name);
					player.sendMessage(msg.toString());
				} else {
					System.out.println("Error al añadir al jugador a una sala, intentando de nuevo.");
					ConnectNewPlayer(player, gameStyle);
				}
			} else {
				ObjectNode msg = mapper.createObjectNode();
				msg.put("event", "ROOM ASIGNED");
				msg.put("roomid", tempRoom.getId());
				msg.put("roomname", tempRoom.name);
				player.sendMessage(msg.toString());
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

	// este método sirve para borrar a los jugadores que no están en una sala
	public void removeForLobby(Player player) {
		noRoomPlayers.put(player.getPlayerId(), player);
		this.removePlayer(player);

	}

	// método para borrar a un jugador, el cual busca la sala del mismo y la
	// notifica
	public void removePlayer(Player player) {
		if (fullRooms.containsKey(player.GetRoomId()))
			fullRooms.get(player.GetRoomId()).RemovePlayer(player);
		else {
			for (GameStyle gs : GameStyle.values()) {
				if (waitingRoomsMap.get(gs).containsKey(player.GetRoomId()))
					waitingRoomsMap.get(gs).get(player.GetRoomId()).RemovePlayer(player);
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

	// este método sirve para propagar game cuando se quiere llamar desde fuera,
	// escogiendo a que sala se busca acceder
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

	// ESTOS MÉTODOS PUEDEN NO FUNCIONAR EN LA VERSIÓN ACTUAL,
	// AL IGUAL QUE LOS TESTS, QUE SE HAN USADO DURANTE EL DESARROLLO PERO
	// PUEDEN NO FUNCIONAR BIEN EN LA VERSIÓN FINAL DE ESTE

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

	// Este método comprueba si existe una habitación
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
