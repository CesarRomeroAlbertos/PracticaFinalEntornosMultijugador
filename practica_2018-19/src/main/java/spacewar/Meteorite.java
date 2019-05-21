package spacewar;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.WebSocketSession;

public class Meteorite {

	private final int meteoriteId;
	private Room room;
	AtomicInteger hitPoints = new AtomicInteger(10);
	
	
	public Meteorite(int meteoriteId) {
		this.meteoriteId = meteoriteId;
		
	}
	synchronized public void counthit() {
		hitPoints.decrementAndGet();
		
	}
	synchronized public boolean gethits() {
		return (hitPoints.get() <= 0 );
	}
}
