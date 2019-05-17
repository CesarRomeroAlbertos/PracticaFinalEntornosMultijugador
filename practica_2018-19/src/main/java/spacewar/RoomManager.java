package spacewar;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class RoomManager {
	private Executor roomExecutor;
	private final int roomCapacity = 6;
	private BlockingQueue<Room> waitingRooms;
	private BlockingQueue<Room> fullRooms;

	public RoomManager() {
		this.roomExecutor = Executors.newCachedThreadPool();
		waitingRooms = new LinkedBlockingQueue<Room>();
		fullRooms = new LinkedBlockingQueue<Room>();
	}

	public void ConnectNewPlayer(Player player) {
		synchronized (waitingRooms) {
			if (!waitingRooms.peek().addPlayer(player)) {
				if (waitingRooms.peek().getPeopleInside() == roomCapacity) {
					try {
						fullRooms.add(waitingRooms.take());
						Room room = new Room(roomCapacity, this);
						roomExecutor.execute(() -> room.PreMatchLobbyThread());
					} catch (InterruptedException e) {
						System.out.println("Error al añadir al jugador a una sala, intentando de nuevo.");
						ConnectNewPlayer(player);
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void deleteRoom(Room room) {
		if (!waitingRooms.remove(room))
			fullRooms.remove(room);
	}

}
