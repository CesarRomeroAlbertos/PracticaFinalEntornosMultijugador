package spacewar;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Player extends Spaceship {

	private final WebSocketSession session;
	private final int playerId;
	private final String shipType;
	private String name = null;
	private int roomId;
	private AtomicInteger health = new AtomicInteger(5);// hardcoded
	private boolean isGhost;
	private int maxAmmo = 5;
	private AtomicInteger ammo = new AtomicInteger(maxAmmo);// hardcoded
	private boolean isReady;
	private boolean isWaiting;
	private ScheduledExecutorService ammoRecharger;
	private final static int rechargeTime = 1000;
	private boolean executorStarted = false;
	private ObjectMapper mapper = new ObjectMapper();
	private boolean isResults;

	// Constructor de la clase Player que inicializa sus variables
	public Player(int playerId, WebSocketSession session) {
		this.playerId = playerId;
		this.session = session;
		this.shipType = this.getRandomShipType();
		this.isGhost = false;
		this.ammoRecharger = Executors.newScheduledThreadPool(1);
	}

	// esta función se llama cada vez que el jugador dispara, y si se no ha
	// inicializado el executor que
	// recarga las balas lo inicializa
	public void decrementAmmo() {
		this.ammo.decrementAndGet();
		if (!executorStarted) {
			ammoRecharger.scheduleAtFixedRate(() -> rechargeBullet(), rechargeTime, rechargeTime,
					TimeUnit.MILLISECONDS);
			executorStarted = true;
		}
	}

	public void setIsResults(boolean r) {
		this.isResults = r;
	}

	public boolean getIsResults() {
		return this.isResults;
	}

	// Este método recarga una bala y se llama desde un scheduled executor service
	// en base al tiempo de recarga
	private void rechargeBullet() {
		synchronized (ammo) {
			if (ammo.get() < maxAmmo)
				ammo.incrementAndGet();
		}

		ObjectNode msg = mapper.createObjectNode();
		msg.put("event", "AMMO UPDATE");
		msg.put("ammo", getAmmo());
		sendMessage(msg.toString());
	}

	public void setAmmo() {
		this.ammo.set(20);
	}

	public int getAmmo() {
		return this.ammo.get();
	}

	public void setWaiting(boolean w) {
		this.isWaiting = w;

	}

	public boolean getWaiting() {
		return this.isWaiting;
	}

	public void setReady(boolean ready) {
		this.isReady = ready;
	}

	public boolean getReady() {
		return this.isReady;
	}

	public void revive() {
		this.isGhost = true;
		this.health.set(1);// hardcoded
		// this.name = null;
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

	// Este método manda mensajes al cliente del jugador usando un lock en la misma
	// ya que la función de mandar mensajes a la sesión no es thread-safe
	public void sendMessage(String msg) {
		synchronized (this.session) {
			try {
				this.session.sendMessage(new TextMessage(msg));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getShipType() {
		return shipType;
	}

	// Este método asocia al jugador una nave aleatoria de las disponibles
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
