import GameInterfaces.GameInterface;
import GameBattleSubjects.GameBattle;
import Gamers.Gamer;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		GameInterface gameInterface = new GameInterface();
		Gamer gamer = new Gamer();
		int diff = gameInterface.newGame(gamer);
		GameBattle gameBattle = new GameBattle(diff);
		gameInterface.setGame(gameBattle);
		gameInterface.fillGamerUnitsArray();
		gameInterface.gaming();
	}
}