package spacewar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Room {
	public String name;
	public String creator;
	private final int id;
	private ConcurrentHashMap<Integer, Player> playerMap;
	private ConcurrentHashMap<Integer, Meteorite> meteoriteMap;
	
	//Mapa con las capacidades de cada modo de juego
	private Map<GameStyle, Integer> capacityValues = new HashMap<GameStyle, Integer>() {
		{
			put(GameStyle.battleRoyale, 3);
		}
	};

	//Enumerador con todos los posibles estados del juego
	public enum State {
		Waiting, Full, Playing
	}

	//Modos de juego
	public enum GameStyle {
		battleRoyale
	}

	public State state;
	private final GameStyle gameStyle;

	private AtomicInteger peopleInside;
	public final int capacity;

	private RoomManager roomManager;
	private Chat chat;
	private SpacewarGame game;
	private ObjectMapper mapper = new ObjectMapper();

	// inicializa las variables y estructuras de la clase
	// y asocia el room manager para poder controlar cuando se borra la sala
	public Room(int id, RoomManager roomManager, GameStyle gameStyle) {
		this.id = id;
		this.peopleInside = new AtomicInteger(0);
		this.capacity = capacityValues.get(gameStyle);
		this.state = State.Waiting;
		this.playerMap = new ConcurrentHashMap<Integer, Player>();
		this.roomManager = roomManager;
		this.gameStyle = gameStyle;
		this.chat = new Chat(playerMap);
		this.game = new SpacewarGame();

	}

	public void playerCanceled(Player player) {
		synchronized (playerMap) {
			player.setReady(false);
			this.RemovePlayer(player);
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "CANCELED UPDATE");
			player.sendMessage(msg.toString());

		}
	}

	//Este método se llama cuando un jugador en la sala indica que está listo
	//y si todos están listos inicia el juego
	public void readyAndCheck(Player player) {
		synchronized (playerMap) {
			player.setReady(true);
			boolean allready = true;

			for (Player p : playerMap.values()) {
				if (!p.getReady()) {
					allready = false;
					break;
				}
			}

			if (allready && state == State.Full) {
				System.out.println("All players are ready");
				this.state = State.Playing;
				roomManager.clearAllTables();
				startGame();
			} else {
				System.out.println("Not all players are ready");
			}

		}
	}

	// aumenta el contador de personas en la sala y añade a la persona al mapa,
	// pero si la sala ya está llena devuelve false

	public int getNReady() {
		int nready = 0;
		synchronized (playerMap) {
			for (Player player : playerMap.values()) {
				if (player.getReady()) {
					nready++;
				}
			}
			return nready;
		}
	}

	public boolean addPlayer(Player player) {
		synchronized (playerMap) {
			if (peopleInside.get() < capacity) {

				playerMap.putIfAbsent(player.getPlayerId(), player);
				player.setRoomId(this.id);
				player.setReady(false);
				peopleInside.incrementAndGet();
				chat.addPlayer(player);
				roomManager.clearAllTables();
				// game.addPlayer(player);
				if (peopleInside.get() == capacity) {
					state = State.Full;
				}
				if (state == State.Full) {
					roomManager.roomIsFull(this.id, this.gameStyle, this);
				}

				return true;

			} else
				return false;
		}
	}

	// Reduce el contador de personas dentro de la sala y quita al jugador del mapa
	// de jugadores
	// Si no quedan jugadores en la sala la borra
	public void RemovePlayer(Player player) {
		synchronized (playerMap) {
			player.setWaiting(false);
			peopleInside.decrementAndGet();
			playerMap.remove(player.getPlayerId());
			chat.removePlayer(player);
			roomManager.clearAllTables();
			game.removePlayer(player);

			if (peopleInside.get() < capacity && state != State.Waiting && state != State.Playing)  {
				state = State.Waiting;
				roomManager.roomIsWaiting(this);
			}
			if (peopleInside.get() == 0)
				roomManager.deleteRoom(this);
		}
	}

	// Devuelve el número de jugadores en la sala
	public int getPeopleInside() {
		synchronized (playerMap) {
			return peopleInside.get();
		}

	}

	public GameStyle getGameStyle() {
		return this.gameStyle;
	}

	public int getId() {
		return this.id;
	}
	
	// Este método es el que unseals la partida
	public void startGame() {
		synchronized (playerMap) {
			game = new SpacewarGame();

			for (Player player : playerMap.values()) {
				game.addPlayer(player);
			}
			this.state = State.Playing;
			game.startGameLoop();
		}
	}

	/*
	 * public void endGame() { game=null; }
	 */

	// Este método se usa cuando se borra a un jugador
	// lo elimina del mapa y notifica al juego para que borre al jugador
	/*
	 * public void removePlayer(Player player) { synchronized(playerMap) {
	 * playerMap.remove(player.getPlayerId()); if (game != null) {
	 * game.removePlayer(player); } } }
	 */

	// Este método devuelve el juego, se usa para las funciones externas que
	// necesitan
	// llamar a métodos del mismo
	public SpacewarGame getGame() {
		return this.game;
	}

	// este método se usa para notificar al chat para que envie un mensaje a todos
	// los jugadores
	public void sendChatMessage(JsonNode msg) {
		chat.receiveMessage(msg);
	}

	/// MÉTODOS PARA TESTEO
	public boolean checkPlayer(Player player) {
		return (playerMap.contains(player));
	}

}
