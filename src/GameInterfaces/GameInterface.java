package GameInterfaces;

import BattlePlace.BattleMap;
import Buildings.Buildable;
import Buildings.Building;
import Buildings.Tavern;
import GameSubjects.Game;
import GameSubjects.GameBattle;
import GameSubjects.GameManager;
import Gamers.Gamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import static GameInterfaces.InputChecker.checkAnswer;

public class GameInterface {

	private final String CHOICE_DOWNLOAD = "Загрузиться";
	private final String CHOICE_GAME = "Новая игра";
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
			case (1) -> CHOICE_DOWNLOAD;
			case (2) ->	CHOICE_GAME;
			case (3) -> "Новая карта";
			default -> "Выйти";
		};
	}

	public int setGame(String choice) {
		int attempts = 0;
		switch (choice) {
			case (CHOICE_DOWNLOAD):
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
			case(CHOICE_GAME):
				this.game = new Game(10, 10);
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
		String levelString = ((Integer)level).toString();
		if (levelString.length() < 2) {
			levelString = " " + levelString;
		}
		String finalLevelString = levelString;
		return switch (level) {
			case (1) -> new ArrayList<>() {{
				add("      []_______     ");
				add("     /\\" + houseName + "\\    ");
				add(" ___/" + finalLevelString + "\\__/\\____\\__ ");
				add("/\\___\\ |'[]''[]'|__\\");
				add("||'''| |''||''''|''|");
				add("\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"");
			}};
			case (2) -> new ArrayList<>() {{
				add("    ________________   ");
				add("   /    " + houseName + "  " + finalLevelString + "\\  ");
				add("  /__________________\\ ");
				add("   ||  || /--\\ ||  ||  ");
				add("   ||[]|| | .| ||[]||  ");
				add(" ()||__||_|__|_||__||()");
			}};
			default -> new ArrayList<>() {{
				add(" ///////////\\ ");
				add("///////////  \\");
				add("|" + houseName + "|" + finalLevelString + "|");
				add("|[] | | []|[]|");
				add("|   | |   |  |");
				add("\"\"\"\"\"\"\"\"\"\"\"\"\"\"");
			}};
		};
	}

	private void printVillage() {
		HashMap<String, Buildable> buildings = game.getBuildings();
		ArrayList<ArrayList<String>> buildingImages = new ArrayList<>() {{
			for (String buildingName : buildings.keySet()) {
				add(getBuildingImage(buildingName, buildings.get(buildingName).getLevel()));
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

	public void choiceView() {
		String buildingUpgradeAbilityString = "";
		int answer;
		boolean toHome = false;
		GameBattle gameBattle;
		while (true) {
			if (game.getBuildings().isEmpty()) {
				System.out.println("У тебя, к сожалению, пока что деревни нет((");
			} else {
				System.out.println("Вот твоя деревня:");
				printVillage();
				buildingUpgradeAbilityString = " или улучшить старое";
			}
			System.out.print("Что ты хочешь сделать? Сыграть бой - введи 1, купить новое здание" + buildingUpgradeAbilityString +
					" - 2, выйти - 3:");
			answer = Integer.parseInt(checkAnswer(gamer, gamer.input(), new ArrayList<>(Arrays.asList("1", "2"))));
			if (answer == 1) {
				HashMap<String, Integer> gameBattleAcquiredResources;
				GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer);
				int diff = gameBattleInterface.newGameBattle();
				System.out.print("Хочешь сыграть в игру по умолчанию - введи 1, со своими полями/юнитами - 2");
				answer = Integer.parseInt(checkAnswer(gamer, gamer.input(), new ArrayList<>(Arrays.asList("1", "2"))));
				if (answer == 1) {
					gameBattle = new GameBattle(diff);
				} else {
					HashMap<String, String> mapPaths = game.getMapPaths();
					System.out.println("Вот твои сохраненные на этом аккаунте карты(не факт, что они все еще существуют...):");
					for (String pathName: mapPaths.keySet()) {
						System.out.println(pathName + ": " + mapPaths.get(pathName));
					}
					System.out.println("Введи имя пути к твоей карте из списка выше (или можешь ввести новый путь):");
					String mapPath = gamer.input();
					if (mapPaths.containsKey(mapPath)) {
						mapPath = mapPaths.get(mapPath);
					}
					GameManager<BattleMap> gameManager = new GameManager<>();
					BattleMap battleMap = gameManager.getGameItemByFilename(mapPath);
					while (battleMap == null) {
						System.out.print("""
								Ты ввел неправильный путь!
								Если хочешь вернуться к меню, введи "нет", иначе - введи путь к карте:""");
						mapPath = gamer.input();
						if (mapPaths.containsKey(mapPath)) {
							mapPath = mapPaths.get(mapPath);
						}
						if (mapPath.equalsIgnoreCase("нет")) {
							toHome = true;
							break;
						}
						battleMap = gameManager.getGameItemByFilename(mapPath);
					}
					if (toHome) {
						continue;
					}
					HashMap<String, Buildable> buildableHashMap = game.getBuildings();
					gameBattle = new GameBattle(diff,
							buildableHashMap.get(Building.HEALER.getName()).getBuildingUpper(),
							buildableHashMap.get(Building.BLACKSMITH_HOUSE.getName()).getBuildingUpper(),
							buildableHashMap.get(Building.ARSENAL.getName()).getBuildingUpper(),
							buildableHashMap.get(Tavern.NAME).getBuildingUpper(Tavern.PENALTY_TYPE),
							buildableHashMap.get(Tavern.NAME).getBuildingUpper(Tavern.PENALTY_TYPE),
							battleMap,
							game.getAcademyUnits());
				}
				gameBattleInterface.setGameBattle(gameBattle);
				gameBattleInterface.fillGamerUnitsArray();
				gameBattleAcquiredResources = gameBattleInterface.gaming();
				game.addGold(gameBattleAcquiredResources.get(Game.GOLD));
				game.addElixir(gameBattleAcquiredResources.get(Game.ELIXIR));
			}
			else if (answer == 2) {

			}
		}
	}
}
