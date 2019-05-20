package spacewar;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.web.socket.WebSocketSession;

@RunWith(value = Parameterized.class)
public class RoomManagerTest {

	RoomManager roomManager;
	final static int numPlayers = 100;
	Player testPlayer;

	@Parameters
	public static Iterable<Object[]> data() {
		List<Object[]> list = new ArrayList<Object[]>();

		WebSocketSession session = mock(WebSocketSession.class);

		for (int i = 0; i < numPlayers; i++) {
			list.add(new Object[] { new Player(i, session) });
		}
		return list;
	}

	public RoomManagerTest(Player testPlayer) {
		this.testPlayer = testPlayer;
	}

	@Before
	public void SetUp() {
		roomManager = new RoomManager();
	}

	@Test
	public void testConnectNewPlayer() {
		roomManager.ConnectNewPlayer(testPlayer);
		assertNotNull("Existe la sala del jugador", testPlayer.GetRoom());
	}

	/*
	 * @Test public void testDeleteRoom() { fail("Not yet implemented"); }
	 */

}
