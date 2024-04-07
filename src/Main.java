import GameInterfaces.GameInterface;
import GameSubjects.GameBattle;
import Gamers.Gamer;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		Gamer gamer = new Gamer();
		GameInterface gameInterface = new GameInterface(gamer);
		int action = gameInterface.startGameInterface();
		int diff = gameInterface.newGameBattle();
		GameBattle gameBattle = new GameBattle(diff);
		gameInterface.setGame(gameBattle);
		gameInterface.fillGamerUnitsArray();
		gameInterface.gaming();
	}
}