package spacewar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Room {

	private final int id;
	ConcurrentHashMap<Integer, Player> playerMap;
	ConcurrentHashMap<Integer, Meteorite> meteoriteMap;
	Map<GameStyle, Integer> capacityValues= new HashMap<GameStyle, Integer>(){
		{
			put(GameStyle.MeteorParty, 10);
		}
	};
	

	public enum State {
		Waiting, Full, Playing
	}
	
	public enum GameStyle{
		MeteorParty
	}

	public State state;
	private final GameStyle gameStyle;

	private AtomicInteger peopleInside;
	private final int capacity;

	private RoomManager roomManager;
	
	public void initMeteorites() {
		//Poner meteoritos en el mapa de meteoritos , habra que asignarles ids de forma concurente 
	}

	//inicializa las variables y estructuras de la clase
	//y asocia el room manager para poder controlar cuando se borra la sala
	public Room(int id, RoomManager roomManager , GameStyle gameStyle) {
		this.id = id;
		this.peopleInside = new AtomicInteger(0);
		this.capacity = capacityValues.get(gameStyle);
		this.state = State.Waiting;
		this.playerMap = new ConcurrentHashMap<Integer, Player>();
		this.roomManager = roomManager;
		this.gameStyle = gameStyle;
		if (gameStyle == GameStyle.MeteorParty) {
			initMeteorites();
		}
	}

	//aumenta el contador de personas en la sala y añade a la persona al mapa,
	//pero si la sala ya está llena devuelve false
	public boolean addPlayer(Player player) {
		if (peopleInside.get() < capacity) {
			playerMap.putIfAbsent(player.getPlayerId(), player);
			player.setRoomId(this.id);
			peopleInside.incrementAndGet();
			return true;
		} else
			return false;
	}

	//Reduce el contador de personas dentro de la sala y quita al jugador del mapa de jugadores
	//Si no quedan jugadores en la sala la borra
	public void RemovePlayer(Player player) {
		peopleInside.decrementAndGet();
		playerMap.remove(player.getPlayerId());
		if (peopleInside.get() == 0)
			roomManager.deleteRoom(this);
	}

	// No sé si se usará o no este método. En caso de que no, se borrará.
	public int getPeopleInside() {
		return peopleInside.get();
	}
	
	public GameStyle getGameStyle()
	{
		return this.gameStyle;
	}

	public int getId()
	{
		return this.id;
	}
	
	//Este es el hilo que se ejecuta durante la espera para iniciar las partidas
	public void PreMatchLobbyThread() {
		do {

		} while (this.state != State.Playing);
	}
	
	public void removePlayer(Player player)
	{
		playerMap.remove(player.getPlayerId());
	}

}
