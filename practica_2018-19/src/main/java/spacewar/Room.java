package spacewar;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Room {

	ConcurrentHashMap<Integer, Player> playerMap;
	ConcurrentHashMap<Integer, Meteorite> meteoriteMap;
	int N_Meteorites = 20; 
	

	public enum State {
		Waiting, Full, Playing
	}
	
	

	public State state;
	public String style;
	public int roomid;

	private AtomicInteger peopleInside;
	private final int capacity;

	private RoomManager roomManager;
	private AtomicInteger meteoriteIdcounter = new AtomicInteger();
	
	public void initMeteorites() {
		for (int i = 0 ; i < N_Meteorites ; i++) {
		Meteorite m = new Meteorite(meteoriteIdcounter.incrementAndGet());
		meteoriteMap.put(meteoriteIdcounter.get(), m);
		}
		//Poner meteoritos en el mapa de meteoritos , habra que asignarles ids de forma concurente 
	}

	//inicializa las variables y estructuras de la clase
	//y asocia el room manager para poder controlar cuando se borra la sala
	public Room(int capacity, RoomManager roomManager , String style) {
		this.peopleInside = new AtomicInteger(0);
		if (style == "1v1") {
			this.capacity = 2;
		}else {
		this.capacity = capacity;
		}
		this.state = State.Waiting;
		this.playerMap = new ConcurrentHashMap<Integer, Player>();
		this.roomManager = roomManager;
		this.style = style;
		if (style == "MeteorParty") {
			initMeteorites();
		}
	}

	//aumenta el contador de personas en la sala y añade a la persona al mapa,
	//pero si la sala ya está llena devuelve false
	public boolean addPlayer(Player player) {
		if (peopleInside.get() < capacity) {
			playerMap.putIfAbsent(player.getPlayerId(), player);
			player.setRoom(this);
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

	//Este es el hilo que se ejecuta durante la espera para iniciar las partidas
	public void PreMatchLobbyThread() {
		do {

		} while (this.state != State.Playing);
	}

}
