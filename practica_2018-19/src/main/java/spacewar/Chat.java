package spacewar;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Chat {

	private BlockingQueue<ObjectNode> messages;
	private Map<Integer, Player> playerMap;
	private ObjectMapper mapper;
	private CyclicBarrier barrier;
	private Executor executor;

	public Chat(Map<Integer, Player> playerMap) {
		this.playerMap = playerMap;
		messages = new LinkedBlockingQueue<ObjectNode>();
		mapper = new ObjectMapper();
		barrier = new CyclicBarrier(1, () -> deleteMessage());
		executor = Executors.newCachedThreadPool();
	}

	public void receiveMessage(JsonNode msg) {
		GregorianCalendar calendar = new GregorianCalendar();
		String messageText = msg.get("player").asText()
				+ " (" + calendar.get(Calendar.HOUR_OF_DAY)
				+ ":" + calendar.get(Calendar.MINUTE) +"): "
				+ msg.get("message").asText() + "\n";
		broadcastMessage(messageText);
	}

	//función que manda mensajes a todos los jugadores
	//utiliza hilos para mandarlo a todos y luego la cyclic barrier al llegar todos
	//borra el mensaje de la pila
	public void broadcastMessage(String messageText) {
		ObjectNode message = mapper.createObjectNode();
		message.put("event", "chatMessageReception");
		message.put("messageText", messageText);
		messages.add(message);
		barrier.reset();
		for (Player player : playerMap.values()) {
			executor.execute(() -> sendMessage(player.getPlayerId()));
		}
	}

	//función que manda un mensaje a un jugador
	private void sendMessage(int id) {
		try {
			playerMap.get(id).sendMessage(messages.peek().toString());
			barrier.await();
		} catch (Exception e) {
			System.out.println("Error al enviar mensaje al jugador id: " + id);
			e.printStackTrace();
		}
	}

	//método que borra el primer mensaje de la pila
	//lo llama la cyclic barrier cuando el mensaje se ha enviado a todos los jugadores
	private void deleteMessage() {
		try {
			messages.take();
		} catch (InterruptedException e) {
			System.out.println("Error borrando el último mensaje de la cola del chat.");
			e.printStackTrace();
		}
	}

	//método para añadir jugadores al chat
	public void addPlayer(Player player) {
		playerMap.put(player.getPlayerId(), player);
		barrier = new CyclicBarrier(playerMap.size(), () -> deleteMessage());
		broadcastMessage("Ha entrado " + player.getName() + " en la sala");
	}

	//método para quitar jugadores del chat
	public void removePlayer(int id) {
		String message = "Ha salido " + playerMap.get(id).getName() + " de la sala";
		playerMap.remove(id);
		barrier = new CyclicBarrier(playerMap.size(), () -> deleteMessage());
		broadcastMessage(message);

	}

	//sobrecarga del método anterior
	public void removePlayer(Player player) {
		String message = "Ha salido " + player.getName() + " de la sala";
		playerMap.remove(player.getPlayerId());
		barrier = new CyclicBarrier(playerMap.size(), () -> deleteMessage());
		broadcastMessage(message);
	}

}
