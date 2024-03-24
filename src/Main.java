import GameInterfaces.GameInterface;
import GameSubject.Game;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		GameInterface gameInterface = new GameInterface();
		gameInterface.newGame();
		gameInterface.fillGamerUnitsArray();
		gameInterface.gaming();
	}
}