import GameInterfaces.GameBattleEditorGUI;
import GameInterfaces.GameInterface;
import Gamers.Gamer;

public class Main {

	public static void main(String[] args){
		Gamer gamer = new Gamer();
		GameInterface gameInterface = new GameInterface(gamer);
		String choice = gameInterface.startGame();
		if (gameInterface.getChoiceArray().contains(choice)) {
			gameInterface.setGame(choice);
			gameInterface.choiceView();
		}
		else if (choice.equals(GameInterface.CHOICE_MAP)) {
			GameBattleEditorGUI gameBattleEditorGUI = new GameBattleEditorGUI(gamer);
			gameBattleEditorGUI.createBattleMap();
		}
	}
}