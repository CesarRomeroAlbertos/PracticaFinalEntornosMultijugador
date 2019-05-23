package spacewar;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Player extends Spaceship {

	private final WebSocketSession session;
	private final int playerId;
	private final String shipType;
	private String name = null;
	private int roomId;
	private  String playerNick;
	private AtomicInteger  health = new AtomicInteger(1);//hardcoded
	private boolean isGhost;

	public Player(int playerId, WebSocketSession session) {
		this.playerId = playerId;
		this.session = session;
		this.shipType = this.getRandomShipType();
		this.isGhost = false;
	}

	public void revive() {
		this.isGhost = true;
	}

	public boolean getGhost() {
		return this.isGhost;
	}
	public void setGhost() {
		this.isGhost = true;
	}
	public String getPlayerName() {
		return this.name;
	}

	

	public int getPlayerId() {
		return this.playerId;
	}

	public WebSocketSession getSession() {
		return this.session;
	}

	public void sendMessage(String msg) throws Exception {
		this.session.sendMessage(new TextMessage(msg));
	}

	public String getShipType() {
		return shipType;
	}

	private String getRandomShipType() {
		String[] randomShips = { "blue", "darkgrey", "green", "metalic", "orange", "purple", "red" };
		String ship = (randomShips[new Random().nextInt(randomShips.length)]);
		ship += "_0" + (new Random().nextInt(5) + 1) + ".png";
		return ship;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int GetRoomId() {
		return this.roomId;
	}
	public int hitPlayer() {
		return health.decrementAndGet();
	}
	public int getHealth() {
		return health.get();
	}

}
