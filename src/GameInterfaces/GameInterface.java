package GameInterfaces;

import GameSubjects.Game;
import GameSubjects.GameManager;
import Gamers.Gamer;

import java.util.ArrayList;
import java.util.Arrays;

import static GameInterfaces.InputChecker.checkAnswer;

public class GameInterface {

	private final String choiceToDownload = "Загрузиться";
	private final String choiceNewGame = "Новая игра";
	private final String choiceNewMap = "Новая карта";
	Gamer gamer;
	Game game;


	GameInterface(Gamer gamer) {
		this.gamer = gamer;
	}

	public String startGame() {
		System.out.print("Хочешь загрузиться с сохраненного файла - введи 1, начать новую игру - введи 2, " +
				"\nсоздать новую карту c новыми героями - введи 3, выйти из программы - 4:");
		int choice = Integer.parseInt(checkAnswer(gamer, gamer.input().split(" ")[0], new ArrayList<>(Arrays.asList("1", "2", "3", "4"))));
		return switch (choice) {
			case (1) -> choiceToDownload;
			case (2) ->	choiceNewGame;
			case (3) -> choiceNewMap;
			default -> "Выйти";
		};
	}

	public int setGame(String choice) {
		int attempts = 0;
		switch (choice) {
			case (choiceToDownload):
				System.out.println("Введи путь к твоей игре:");
				String gameLocation = gamer.input();
				GameManager<Game> gameManager = new GameManager<>();
				Game inputGame = gameManager.getGameItemByFilename(gameLocation);
				while (game == null) {
					System.out.println("Введи путь к твоей игре заново!");
					inputGame = gameManager.getGameItemByFilename(gameLocation);
					if (attempts++ >= 5) {
						System.out.println("Слишком много попыток ввести путь...");
					}
				}
				this.game = inputGame;
				return 0;
			case(choiceNewGame):
				this.game = new Game();
				System.out.println("Твоя новая игра создана!");
				return 0;
			default:
				return 1;
		}
	}

}
