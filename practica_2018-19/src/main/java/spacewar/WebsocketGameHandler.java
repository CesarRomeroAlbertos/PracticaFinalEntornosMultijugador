package spacewar;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import spacewar.Room.GameStyle;

public class WebsocketGameHandler extends TextWebSocketHandler {

	// private SpacewarGame game = SpacewarGame.INSTANCE;
	private static final String PLAYER_ATTRIBUTE = "PLAYER";
	private ObjectMapper mapper = new ObjectMapper();
	private AtomicInteger playerId = new AtomicInteger(0);
	private AtomicInteger projectileId = new AtomicInteger(0);
	private RoomManager roomManager = RoomManager.getSingletonInstance();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Player player = new Player(playerId.incrementAndGet(), session);
		session.getAttributes().put(PLAYER_ATTRIBUTE, player);

		ObjectNode msg = mapper.createObjectNode();
		msg.put("event", "JOIN");
		msg.put("id", player.getPlayerId());
		msg.put("shipType", player.getShipType());
		msg.put("health", player.getHealth());
		msg.put("ghost", player.getGhost());
		player.sendMessage(msg.toString());
		roomManager.addNoRoomPlayer(player);


		// game.addPlayer(player);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		try {
			JsonNode node = mapper.readTree(message.getPayload());
			ObjectNode msg = mapper.createObjectNode();
			Player player = (Player) session.getAttributes().get(PLAYER_ATTRIBUTE);

			switch (node.get("event").asText()) {
			case "RESURECTION":
				player.revive();
				msg.put("event", "PLAYER RESURECTS");
				msg.put("id", player.getPlayerId());
				roomManager.getGame(player.GetRoomId()).broadcast(msg.toString());

				break;
			case "PLAYER LEFT":
				msg.put("id", player.getPlayerId());
				msg.put("event", "REMOVE PLAYER");
				roomManager.getGame(player.GetRoomId()).broadcast(msg.toString());
				roomManager.getGame(player.GetRoomId()).removePlayer(player);

				break;
				
			case "PLAYER IS READY" :
				
				break;

			case "NAME":
				player.setName(node.get("name").asText());
				msg.put("event", "SET NAME");
				msg.put("name", player.getName());
				msg.put("id", player.getPlayerId());
				player.sendMessage(msg.toString());
				// roomManager.getGame(player.GetRoomId()).broadcast(msg.toString());
				// game.getforNames();
				break;
			case "JOIN":
				player.setName(node.get("name").asText());
				msg.put("event", "JOIN");
				msg.put("id", player.getPlayerId());
				msg.put("shipType", player.getShipType());
				msg.put("health", player.getHealth());
				msg.put("ghost", player.getGhost());
				roomManager.addNoRoomPlayer(player);
				player.sendMessage(msg.toString());
				break;
				
			case "JOIN EXISTING ROOM":
			    roomManager.ConnectToExisting(player, GameStyle.battleRoyale, node.get("roomid").asInt());
				break;
			case "JOIN ROOM":
				msg.put("event", "NEW ROOM");
				roomManager.ConnectNewPlayer(player, GameStyle.battleRoyale);
				msg.put("room", player.GetRoomId());
				player.sendMessage(msg.toString());
				break;
			
			case "UPDATE MOVEMENT":
				player.loadMovement(node.path("movement").get("thrust").asBoolean(),
						node.path("movement").get("brake").asBoolean(),
						node.path("movement").get("rotLeft").asBoolean(),
						node.path("movement").get("rotRight").asBoolean());
				if (node.path("bullet").asBoolean() && !player.getGhost()) {
					Projectile projectile = new Projectile(player, this.projectileId.incrementAndGet());
					roomManager.getGame(player.GetRoomId()).addProjectile(projectile.getId(), projectile);
				}
				break;
			case "UPDATE HEALTH":
				player.hitPlayer();
				msg.put("event", "UPDATE HEALTH");
				player.sendMessage(msg.toString());
				break;
			case "PLAYER DEAD":
				player.setGhost();
				msg.put("event", "PLAYER GHOST");
				msg.put("id", player.getPlayerId());
				roomManager.getGame(player.GetRoomId()).broadcast(msg.toString());

				break;
			case "CHAT MESSAGE":
				roomManager.getChatMessage(msg);
				break;
				
			case "MAKE ROOM":
				roomManager.createNewRoom(GameStyle.battleRoyale,node.get("roomname").asText(), node.get("roomcreator").asText());
				break;

			default:

				break;
			}

		} catch (Exception e) {
			System.err.println("Exception processing message " + message.getPayload());
			e.printStackTrace(System.err);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		Player player = (Player) session.getAttributes().get(PLAYER_ATTRIBUTE);
		roomManager.removePlayer(player);
		roomManager.getGame(player.GetRoomId()).removePlayer(player);

		ObjectNode msg = mapper.createObjectNode();
		msg.put("event", "REMOVE PLAYER");
		msg.put("id", player.getPlayerId());
		roomManager.getGame(player.GetRoomId()).broadcast(msg.toString());
	}
}
