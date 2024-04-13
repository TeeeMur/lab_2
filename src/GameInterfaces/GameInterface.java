package GameInterfaces;

import GameSubjects.Game;
import GameSubjects.GameManager;
import Gamers.Gamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import static GameInterfaces.InputChecker.checkAnswer;

public class GameInterface {

	private final String choiceToDownload = "Загрузиться";
	private final String choiceNewGame = "Новая игра";
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
			case (3) -> "Новая карта";
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

	private ArrayList<String> getBuildingImage(String buildingName, int level) {
		StringBuilder houseName = new StringBuilder(buildingName);
		int maxNameLength;
		if (game.getBuildingsNames().stream().max(Comparator.comparing(String::length)).isPresent()) {
			maxNameLength = game.getBuildingsNames().stream().max(Comparator.comparing(String::length)).get().length();
		}
		else {
			maxNameLength = 8;
		}
		while (houseName.length() < maxNameLength) {
			houseName.append(" ");
		}
		return switch (level) {
			case (2) -> new ArrayList<>() {{
				add("      []_______     ");
				add("     /\\" + houseName + "\\    ");
				add(" ___/  \\__/\\____\\__ ");
				add("/\\___\\ |'[]''[]'|__\\");
				add("||'''| |''||''''|''|");
				add("\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"");
			}};
			case (3) -> new ArrayList<>() {{
				add("    ________________   ");
				add("   /    " + houseName + "    \\  ");
				add("  /__________________\\ ");
				add("   ||  || /--\\ ||  ||  ");
				add("   ||[]|| | .| ||[]||  ");
				add(" ()||__||_|__|_||__||()");
			}};
			default -> new ArrayList<>() {{
				add(" ///////////\\ ");
				add("///////////  \\");
				add("|" + houseName + "|  |");
				add("|[] | | []|[]|");
				add("|   | |   |  |");
				add("\"\"\"\"\"\"\"\"\"\"\"\"\"\"");
			}};
		};
	}

	private void printVillage() {
		HashMap<String, Integer> buildings = game.getBuildings();
		ArrayList<String> buildingsKeys = new ArrayList<>(buildings.keySet());
		ArrayList<ArrayList<String>> buildingImages = new ArrayList<>() {{
			for (int i = 0; i < buildings.size(); i++) {
				add(getBuildingImage(buildingsKeys.get(i), buildings.get(buildingsKeys.get(i))));
			}
		}};
		int maxHeight;
		if (!buildingImages.isEmpty()) {
			maxHeight = buildingImages.stream().max(Comparator.comparing(ArrayList::size)).get().size();
		}
		else {
			maxHeight = 0;
		}
		int VILLAGE_WIDTH = 3;
		int preLines = buildings.size() / VILLAGE_WIDTH;
		for (int i = 0; i < preLines; i++) {
			for (int j = 0; j < VILLAGE_WIDTH; j++) {
				for (int k = 0; k < maxHeight; k++) {
					System.out.println(buildingImages.get(i * VILLAGE_WIDTH + j).get(k));
				}
			}
		}
	}

	public int choiceView() {
		String buildingUpgradeAbilityString = "";
		if (game.getBuildings().isEmpty()) {
			System.out.println("У тебя, к сожалению, пока что деревни нет((");
		}
		else {
			System.out.println("Вот твоя деревня:");
			printVillage();
			buildingUpgradeAbilityString = " или улучшить старое";
		}
		System.out.print("Что ты хочешь сделать? Сыграть бой - введи 1, купить новое" + buildingUpgradeAbilityString +
				" - 2, выйти - 3:");
		int answer = Integer.parseInt(checkAnswer(gamer, gamer.input(), new ArrayList<>(Arrays.asList("1", "2"))));
		return 0;
	}
}
