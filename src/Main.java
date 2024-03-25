import GameInterfaces.GameInterface;
import GameSubject.Game;
import Gamers.Gamer;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		GameInterface gameInterface = new GameInterface();
		Gamer gamer = new Gamer();
		int diff = gameInterface.newGame(gamer);
		Game game = new Game(diff);
		gameInterface.setGame(game);
		gameInterface.fillGamerUnitsArray();
		gameInterface.gaming();
	}
}