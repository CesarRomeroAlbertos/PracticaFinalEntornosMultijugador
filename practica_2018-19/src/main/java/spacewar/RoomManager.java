package spacewar;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RoomManager {
	private final int waitingQueueCapacity;
	private Executor roomExecutor;
	private BlockingQueue waitingRooms;
	
	public RoomManager(int waitingQueueCapacity)
	{
		this.waitingQueueCapacity = waitingQueueCapacity;
		this.waitingRooms = new ArrayBlockingQueue(this.waitingQueueCapacity);
		this.roomExecutor = Executors.newCachedThreadPool();
	}
	
	public void StartRoom(Room room) {
		roomExecutor.execute(()->room.PreMatchLobbyThread());
	}
	
}
