import GameSubject.Game;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		Game game = new Game();
		game.start();
		game.setGamerUnitsArray();
		game.game();
	}
}