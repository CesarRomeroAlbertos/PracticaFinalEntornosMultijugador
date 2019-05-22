package spacewar;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Chat {

	private BlockingQueue<ObjectNode> messages;
	private HashMap<Integer, Player> playerMap;
	private ObjectMapper mapper;
	private CyclicBarrier barrier;
	private Executor executor;

	public Chat(Map<Integer, Player> playerMap) {
		this.playerMap = (HashMap<Integer, Player>) playerMap;
		messages = new LinkedBlockingQueue<ObjectNode>();
		mapper = new ObjectMapper();
		barrier = new CyclicBarrier(playerMap.size(), () -> deleteMessage());
		executor = Executors.newCachedThreadPool();
	}

	public void receiveMessage(ObjectNode msg) {
		String messageText = msg.get("player").asText() + " (" + new GregorianCalendar().HOUR_OF_DAY + "): "
				+ msg.get("message").asText() + "\n";
		broadcastMessage(messageText);
	}
	
	public void broadcastMessage(String messageText)
	{
		ObjectNode message = mapper.createObjectNode();
		message.put("event", "chatMessageReception");
		message.put("messageText", messageText);
		try {
			messages.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		barrier.reset();
		for (Player player : playerMap.values()) {
			executor.execute(() -> sendMessage(player.getPlayerId()));
		}
	}

	private void sendMessage(int id) {
		try {
			playerMap.get(id).getSession().sendMessage(new TextMessage(messages.peek().toString()));
			barrier.await();
		} catch (IOException e) {
			System.out.println("Error al enviar mensaje al jugador id: " + id);
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deleteMessage() {
		try {
			messages.take();
		} catch (InterruptedException e) {
			System.out.println("Error borrando el último mensaje de la cola del chat.");
			e.printStackTrace();
		}
	}
	
	private void addPlayer(Player player)
	{
		playerMap.put(player.getPlayerId(), player);
		barrier = new CyclicBarrier(playerMap.size(), () -> deleteMessage());
		broadcastMessage("Ha entrado "+ player.getName()+ " en la sala");
	}
	
	private void removePlayer(int id)
	{
		String message = "Ha salido "+ playerMap.get(id).getName()+ " de la sala";
		playerMap.remove(id);
		barrier = new CyclicBarrier(playerMap.size(), () -> deleteMessage());
		broadcastMessage(message);
		
	}
	
	private void removePlayer(Player player)
	{
		String message = "Ha salido "+ player.getName()+ " de la sala";
		playerMap.remove(player.getPlayerId());
		barrier = new CyclicBarrier(playerMap.size(), () -> deleteMessage());
		broadcastMessage(message);
	}

}
