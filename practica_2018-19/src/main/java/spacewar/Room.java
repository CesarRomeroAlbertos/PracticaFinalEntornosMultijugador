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

	public Room(int capacity, RoomManager roomManager) {
		this.peopleInside = new AtomicInteger(0);
		this.capacity = capacity;
		this.state = State.Waiting;
		this.playerMap = new ConcurrentHashMap<Integer, Player>();
		this.roomManager = roomManager;
	}

	public boolean addPlayer(Player player) {
		if (peopleInside.get() < capacity) {
			playerMap.putIfAbsent(player.getPlayerId(), player);
			player.setRoom(this);
			peopleInside.incrementAndGet();
			return true;
		} else
			return false;
	}

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

	public void PreMatchLobbyThread() {
		do {

		} while (this.state != State.Playing);
	}

}
