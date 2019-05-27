package spacewar;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SpacewarGame {

	//public final static SpacewarGame INSTANCE = new SpacewarGame();

	private final static int FPS = 30;
	private final static long TICK_DELAY = 1000 / FPS;
	public final static boolean DEBUG_MODE = true;
	public final static boolean VERBOSE_MODE = true;
	
	

	ObjectMapper mapper = new ObjectMapper();
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	// GLOBAL GAME ROOM
	private Map<String, Player> players = new ConcurrentHashMap<>();
	private Map<Integer, Projectile> projectiles = new ConcurrentHashMap<>();
	private AtomicInteger numPlayers = new AtomicInteger();
	private ArrayBlockingQueue <Player> ghosts = new ArrayBlockingQueue<Player>(3);//Hardcoded

	/*public SpacewarGame() {

	}*/

	public void addNewGhost(Player player) throws InterruptedException {
	player.setGhost();
	ghosts.put(player);
	ObjectNode msg = mapper.createObjectNode();
	msg.put("event", "PLAYER GHOST");
	msg.put("id", player.getPlayerId());
	player.sendMessage(msg.toString());
	
	for (Player dedplayer : ghosts) {
		ObjectNode msg2 = mapper.createObjectNode();
		msg2.put("event", "CLEAR RESULTS TABLE");
		dedplayer.sendMessage(msg2.toString());

	}
	}
	
	public void getGhostInfo(Player askingplayer) {
		int position = 1 ;
		for(Player player : ghosts) {
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event","UPDATE SCORE TABLE");
			msg.put("playername", player.getName());
			msg.put("position", position);
			askingplayer.sendMessage(msg.toString());
			position++;
		}
	}
	
	public void addPlayer(Player player)  {
		players.put(player.getSession().getId(), player);

		
			ObjectNode msg = mapper.createObjectNode();
			msg.put("event", "START GAME");
			player.sendMessage(msg.toString());
			
			//this.startGameLoop();
		
	}

	public Collection<Player> getPlayers() {
		return players.values();
	}

	public void getforNames() {
		ArrayNode arrayNodePlayers = mapper.createArrayNode();
		ObjectNode json = mapper.createObjectNode();
		for (Player player : getPlayers()) {
			ObjectNode jsonPlayer = mapper.createObjectNode();

			jsonPlayer.put("name", player.getName());
			arrayNodePlayers.addPOJO(jsonPlayer);
		}
		json.putPOJO("players", arrayNodePlayers);
		json.put("event", "PAINT NAMES");
		this.broadcast(json.toString());
	}

	public void removePlayer(Player player) {
		players.remove(player.getSession().getId());

		int count = this.numPlayers.decrementAndGet();
		if (count == 0) {
			this.stopGameLoop();
		}
	}

	public void addProjectile(int id, Projectile projectile) {
		projectiles.put(id, projectile);
	}

	public Collection<Projectile> getProjectiles() {
		return projectiles.values();
	}

	public void removeProjectile(Projectile projectile) {
		players.remove(projectile.getId(), projectile);
	}

	public void startGameLoop() {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> tick(), TICK_DELAY, TICK_DELAY, TimeUnit.MILLISECONDS);
	}

	public void stopGameLoop() {
		if (scheduler != null) {
			scheduler.shutdown();
		}
	}

	public void broadcast(String message) {
		for (Player player : players.values()) {
			try {
				player.sendMessage(message.toString());
			} catch (Throwable ex) {
				System.err.println("Execption sending message to player " + player.getSession().getId());
				ex.printStackTrace(System.err);
				this.removePlayer(player);
			}
		}
	}
	
	public void forceAllPlayersOut(){
		
	}
	
	private void checkGhosts() {
		int alivecount = 0 ; 
		for (Player player : players.values()) {
			if (!player.getGhost()) {
				alivecount++;
			}
			if(alivecount >= 2) {
				break;
			}
		}
		if(alivecount <= 1) {
			System.out.println("El juego ha terminado");
			this.stopGameLoop();
			forceAllPlayersOut();
		}
	}

	private void tick() {
		ObjectNode json = mapper.createObjectNode();
		ArrayNode arrayNodePlayers = mapper.createArrayNode();
		ArrayNode arrayNodeProjectiles = mapper.createArrayNode();

		long thisInstant = System.currentTimeMillis();
		Set<Integer> bullets2Remove = new HashSet<>();
		boolean removeBullets = false;

		try {
			// Update players
			for (Player player : getPlayers()) {
				if(!player.getIsResults()) {
				player.calculateMovement();

				ObjectNode jsonPlayer = mapper.createObjectNode();
				jsonPlayer.put("id", player.getPlayerId());
				jsonPlayer.put("shipType", player.getShipType());
				jsonPlayer.put("posX", player.getPosX());
				jsonPlayer.put("posY", player.getPosY());
				jsonPlayer.put("facingAngle", player.getFacingAngle());
				jsonPlayer.put("name", player.getName());
				arrayNodePlayers.addPOJO(jsonPlayer);
				}
			}

			// Update bullets and handle collision
			for (Projectile projectile : getProjectiles()) {
				projectile.applyVelocity2Position();

				// Handle collision
				for (Player player : getPlayers()) {
					if(!player.getIsResults()) {
					if ((projectile.getOwner().getPlayerId() != player.getPlayerId()) && player.intersect(projectile)
							&& !player.getGhost()) {
						// System.out.println("Player " + player.getPlayerId() + " was hit!!!");
						projectile.setHit(true);
						ObjectNode msg = mapper.createObjectNode();
						player.hitPlayer();
						msg.put("event", "UPDATE HEALTH");
						player.sendMessage(msg.toString());
						
							if (player.getHealth() <= 0 ) {
							addNewGhost(player);
							msg.put("event", "PLAYER GHOST");
							msg.put("id", player.getPlayerId());
							this.broadcast(msg.toString());
						}
						
					
						break;
					}
					}
				}

				ObjectNode jsonProjectile = mapper.createObjectNode();
				jsonProjectile.put("id", projectile.getId());

				if (!projectile.isHit() && projectile.isAlive(thisInstant)) {
					jsonProjectile.put("posX", projectile.getPosX());
					jsonProjectile.put("posY", projectile.getPosY());
					jsonProjectile.put("facingAngle", projectile.getFacingAngle());
					jsonProjectile.put("isAlive", true);
				} else {
					removeBullets = true;
					bullets2Remove.add(projectile.getId());
					jsonProjectile.put("isAlive", false);
					if (projectile.isHit()) {
						jsonProjectile.put("isHit", true);
						jsonProjectile.put("posX", projectile.getPosX());
						jsonProjectile.put("posY", projectile.getPosY());
					}
				}
				arrayNodeProjectiles.addPOJO(jsonProjectile);
			}

			if (removeBullets)
				this.projectiles.keySet().removeAll(bullets2Remove);

			json.put("event", "GAME STATE UPDATE");
			json.putPOJO("players", arrayNodePlayers);
			json.putPOJO("projectiles", arrayNodeProjectiles);
			this.broadcast(json.toString());
			checkGhosts();
			
		} catch (Throwable ex) {

		}
	}

	public void handleCollision() {

	}
}
