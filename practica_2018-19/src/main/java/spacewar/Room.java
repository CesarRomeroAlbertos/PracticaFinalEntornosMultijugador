package spacewar;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Room {

	ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<Integer, Player>();

	public enum State {
		Waiting, Playing
	}

	public State state;

	private AtomicInteger peopleInside;
	private final int capacity;

	public Room(int capacity) {
		this.peopleInside = new AtomicInteger(0);
		this.capacity = capacity;
		this.state = State.Waiting;
	}

	public boolean addPlayer(Player player) {
			if (peopleInside.get() < capacity) {
				players.putIfAbsent(player.getPlayerId(), player);
				peopleInside.incrementAndGet();
				return true;
			} else
				return false;
	}
	
	public void RemovePlayer(Player player)
	{
		peopleInside.decrementAndGet();
		players.remove(player.getPlayerId());
			
	}
	
	//No sé si se usará o no este método. En caso de que no, se borrará.
	public int getPeopleInside()
	{
		return peopleInside.get();
	}
	
	public void PreMatchLobbyThread()
	{
		
	}

}
