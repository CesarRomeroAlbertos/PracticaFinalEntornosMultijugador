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

import spacewar.Room.GameStyle;

@RunWith(value = Parameterized.class)
public class RoomManagerTest {

	RoomManager roomManager;
	final static int numPlayers = 100;
	Player testPlayer;

	//Creamos jugadores con una sesión falsa que serán los parametros para el test
	@Parameters
	public static Iterable<Object[]> data() {
		List<Object[]> list = new ArrayList<Object[]>();

		WebSocketSession session = mock(WebSocketSession.class);

		for (int i = 0; i < numPlayers; i++) {
			list.add(new Object[] { new Player(i, session) });
		}
		return list;
	}

	//inicializamos la clase del test con los parámetros establecidos
	public RoomManagerTest(Player testPlayer) {
		this.testPlayer = testPlayer;
	}

	//Inicializamos antes de los tests el room manager
	@Before
	public void SetUp() {
		roomManager = new RoomManager();
	}

	//Probamos a asociar a todos los jugadores a salas y ver si tienen asociada una sala,
	//en cuyo caso el test ha ido bien
	@Test
	public void testConnectNewPlayer() {
		roomManager.ConnectNewPlayer(testPlayer,GameStyle.MeteorParty);
		assertNotNull("Existe la sala del jugador", testPlayer.GetRoomId());
	}

	/*
	 * @Test public void testDeleteRoom() { fail("Not yet implemented"); }
	 */

}
