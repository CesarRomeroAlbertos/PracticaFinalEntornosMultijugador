package spacewar;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.WebSocketSession;

public class Meteorite {

	private final WebSocketSession session;
	private final int meteoriteId;
	private Room room;
	AtomicInteger hitPoints = new AtomicInteger(10);
	
	public Meteorite(int meteoriteId, WebSocketSession session) {
		this.meteoriteId = meteoriteId;
		this.session = session;
	
	}
	synchronized public void counthit() {
		hitPoints.decrementAndGet();
		if (hitPoints.get() == 0) {
			//Destruir el meteorito 
			
		}
		
	}
}
