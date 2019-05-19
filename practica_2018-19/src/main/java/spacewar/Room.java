package spacewar;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Room {

	ConcurrentHashMap<Integer, Player> playerMap;

	public enum State {
		Waiting, Full, Playing
	}

	public State state;

	private AtomicInteger peopleInside;
	private final int capacity;

	private RoomManager roomManager;

	//inicializa las variables y estructuras de la clase
	//y asocia el room manager para poder controlar cuando se borra la sala
	public Room(int capacity, RoomManager roomManager) {
		this.peopleInside = new AtomicInteger(0);
		this.capacity = capacity;
		this.state = State.Waiting;
		this.playerMap = new ConcurrentHashMap<Integer, Player>();
		this.roomManager = roomManager;
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
