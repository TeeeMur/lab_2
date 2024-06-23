package GameInterfaces;

import BattlePlace.BattleMap;
import Buildings.*;
import GameSubjects.Game;
import GameSubjects.GameBattle;
import GameSubjects.GameManager;
import Gamers.Gamer;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static GameInterfaces.InputChecker.ANSWER_CHECK_LIST;
import static GameInterfaces.InputChecker.checkAnswer;

public class GameInterface {

	private final String CHOICE_DOWNLOAD = "Загрузиться";
	private final String CHOICE_GAME = "Новая игра";
	public static final String CHOICE_MAP = "Новая карта";
	public final ArrayList<String> gameActionChoiceList = new ArrayList<>() {{
		add("сыграть бой");
		add("купить новое здание");
		add("сохранить игру на твоем компьютере");
		add("обменять ресурсы");
		add("добавить своих героев");
	}};

	private final Gamer gamer;
	private Game game;
	private String absolutePathSaveGame;
	private static final Logger logger = Logger.getLogger(GameInterface.class.getName());

	public GameInterface(Gamer gamer) {
		this.gamer = gamer;
	}

	public ArrayList<String> getChoiceArray() {
		return new ArrayList<>() {{
			add(CHOICE_DOWNLOAD);
			add(CHOICE_GAME);
		}};
	}

	public String startGame() {
		System.out.println("Привет!");
		System.out.println("""
				Введи:
				1 - загрузиться с сохраненного файла
				2 - начать новую игру
				3 - создать новую карту с новыми полями
				4 - выйти из игры""");
		int choice = Integer.parseInt(checkAnswer(gamer, gamer.inputOneWord(), new ArrayList<>(Arrays.asList("1", "2", "3", "4"))));
		return switch (choice) {
			case (1) -> CHOICE_DOWNLOAD;
			case (2) -> CHOICE_GAME;
			case (3) -> CHOICE_MAP;
			default -> "Выйти";
		};
	}

	public void setGame(String choice) {
		int attempts = 0;
		switch (choice) {
			case (CHOICE_DOWNLOAD):
				System.out.println("Введи путь к твоей игре:");
				String gameLocation = gamer.input();
				GameManager<Game> gameManager = new GameManager<>();
				Game inputGame = gameManager.getGameItemByFilename(gameLocation);
				while (inputGame == null) {
					System.out.println("Введи путь к твоей игре заново!");
					gameLocation = gamer.input();
					inputGame = gameManager.getGameItemByFilename(gameLocation);
					if (attempts++ >= 5) {
						System.out.println("Слишком много попыток ввести путь...");
					}
				}
				this.game = inputGame;
				try {
					LogManager.getLogManager().readConfiguration();
				} catch (IOException | SecurityException e) {
					System.out.println("Система логирования не прочитала конфигурационный файл.");
				}
				absolutePathSaveGame = gameLocation;
				return;
			case (CHOICE_GAME):
				absolutePathSaveGame = "";
				this.game = new Game(1000000, 100000);
				try {
					LogManager.getLogManager().readConfiguration();
				} catch (IOException | SecurityException e) {
					System.out.println("Система логирования не прочитала конфигурационный файл.");
				}
				System.out.println("Твоя новая игра создана!");
				return;
			default:
		}
	}

	private void printAcademyUnits() {
		HashMap<String, ArrayList<Integer>> unitSpecsMap = new HashMap<>();
		HashMap<String, ArrayList<String>> unitsTyping = new HashMap<>() {{
			for (String unitType : game.getAcademyUnits().keySet()) {
				put(unitType, new ArrayList<>(game.getAcademyUnits().get(unitType).keySet()));
				for (String unitName : game.getAcademyUnits().get(unitType).keySet()) {
					unitSpecsMap.put(unitName, game.getAcademyUnits().get(unitType).get(unitName));
				}
			}
		}};
		GameBattleInterface.printUnitsArray(
				BattleMap.getDefaultPenalties(),
				unitsTyping,
				unitSpecsMap);
	}

	private ArrayList<String> getBuildingImage(String buildingName, int level) {
		StringBuilder houseName = new StringBuilder(buildingName);
		int maxNameLength;
		if (game.getBuildingsNames().stream().max(Comparator.comparing(String::length)).isPresent()) {
			maxNameLength = game.getBuildingsNames().stream().max(Comparator.comparing(String::length)).get().length();
		} else {
			maxNameLength = 8;
		}
		while (houseName.length() < maxNameLength) {
			houseName.append(" ");
		}
		String levelString = ((Integer) level).toString();
		if (levelString.length() < 2) {
			levelString = " " + levelString;
		}
		String finalLevelString = levelString;
		return switch (level) {
			case (2) -> new ArrayList<>() {{
				add("      []_______     ");
				add("     /\\" + houseName + "\\    ");
				add(" ___/" + finalLevelString + "\\__/\\____\\__ ");
				add("/\\___\\ |'[]''[]'|__\\");
				add("||'''| |''||''''|''|");
				add("\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"");
			}};
			case (1) -> new ArrayList<>() {{
				add(" ///////////\\ ");
				add("///////////  \\");
				add("|" + houseName + " |" + finalLevelString + "|");
				add("|[] | | []|[]|");
				add("|   | |   |  |");
				add("\"\"\"\"\"\"\"\"\"\"\"\"\"\"");
			}};
			default -> new ArrayList<>() {{
				add("    ________________   ");
				add("   /  " + houseName + "  " + finalLevelString + "  \\  ");
				add("  /__________________\\ ");
				add("   ||  || /--\\ ||  ||  ");
				add("   ||[]|| | .| ||[]||  ");
				add(" ()||__||_|__|_||__||()");
			}};
		};
	}

	private ArrayList<ArrayList<String>> getVillageImages() {
		HashMap<String, Buildable> buildings = game.getBuildings();
		return new ArrayList<>() {{
			for (String buildingName : buildings.keySet()) {
				if (buildings.get(buildingName).getLevel() > 0) {
					add(getBuildingImage(buildingName, buildings.get(buildingName).getLevel()));
				}
			}
		}};
	}

	private void printVillage() {
		ArrayList<ArrayList<String>> buildingImages = getVillageImages();
		int maxHeight;
		if (!buildingImages.isEmpty()) {
			maxHeight = buildingImages.stream().max(Comparator.comparing(ArrayList::size)).get().size();
		} else {
			maxHeight = 0;
		}
		int VILLAGE_WIDTH = 3;
		int preLines = game.getBuildings().size() / VILLAGE_WIDTH;
		for (int i = 0; i < preLines; i++) {
			for (int k = 0; k < maxHeight; k++) {
				for (int j = 0; j < VILLAGE_WIDTH; j++) {
					System.out.print(buildingImages.get(i * VILLAGE_WIDTH + j).get(k));
				}
				System.out.println();
			}
		}
		if (game.getBuildings().size() % VILLAGE_WIDTH != 0) {
			for (int k = 0; k < maxHeight; k++) {
				for (int j = 0; j < game.getBuildings().size() % VILLAGE_WIDTH; j++) {
					System.out.print(buildingImages.get(preLines * VILLAGE_WIDTH + j).get(k));
				}
				System.out.println();
			}
		}
	}

	private void printResources() {
		for (String resourceName : game.getResources().keySet()) {
			System.out.println(resourceName + ": " + game.getResources().get(resourceName));
		}
	}

	private void saveMapPathByGamer(String mapPath) {
		System.out.print("Хочешь сохранить путь к карте на твоем аккаунте?" +
				"\nНапиши да или нет:");
		String answer = checkAnswer(gamer, gamer.inputOneWord(), ANSWER_CHECK_LIST);
		if (answer.equals("да")) {
			System.out.println("Введи имя карты:");
			String mapPathName = gamer.inputOneWord();
			game.addMapPath(mapPathName, mapPath);
			System.out.println("Твоя карта " + mapPathName + "(" + mapPath + ") сохранена!");
		}
	}

	private void deleteMapPathByGamer() {
		if (game.getMapPaths().isEmpty()) {
			return;
		}
		System.out.println("Хочешь удалить какой-либо путь из твоего списка?" +
				"\nНапиши да или нет:");
		String answerToDelete = checkAnswer(gamer, gamer.inputOneWord(), ANSWER_CHECK_LIST);
		if (Objects.equals(answerToDelete, "да")) {
			String answerMapPathName;
			do {
				System.out.println("Введи имя пути своей карты (введи нет, если хочешь прекратить удаление карт):");
				answerMapPathName = checkAnswer(gamer, gamer.inputOneWord(), ANSWER_CHECK_LIST);
				if (Objects.equals(answerMapPathName, "нет")) {
					return;
				}
				if (game.getMapPaths().containsKey(answerMapPathName)) {
					game.removeMapPath(answerMapPathName);
				} else {
					System.out.println("Ты, похоже, ввел имя пути с ошибкой!");
				}
				if (game.getMapPaths().isEmpty()) {
					System.out.println("Похоже, ты удалил все свои пути для карт...");
				}
			} while (true);
		}
	}

	private HashMap<Integer, String> gameActionChoiceHashMap() {
		String buildingUpgradeAbilityString = "";
		boolean swapResourcesAbility = game.getBuildings().containsKey(Market.NAME),
				addUnitsAbility = game.getBuildings().containsKey(Academy.NAME),
				buildingUpgradeAbility = false;
		for (String buildingName : game.getBuildings().keySet()) {
			if (game.getBuildings().get(buildingName).getLevel() > 0) {
				buildingUpgradeAbility = true;
				break;
			}
		}
		if (buildingUpgradeAbility) {
			buildingUpgradeAbilityString = "/улучшить уже построенное";
		}
		String finalBuildingUpgradeAbilityString = buildingUpgradeAbilityString;
		return new HashMap<>() {{
			put(0, "выйти");
			put(1, gameActionChoiceList.get(0));
			put(2, gameActionChoiceList.get(1) + finalBuildingUpgradeAbilityString);
			put(3, gameActionChoiceList.get(2));
			if (swapResourcesAbility) {
				put(4, gameActionChoiceList.get(3));
			} else if (addUnitsAbility) {
				put(4, gameActionChoiceList.get(4));
			}
			if (swapResourcesAbility && addUnitsAbility) {
				put(5, gameActionChoiceList.get(4));
			}
		}};
	}

	private void gamePlayGameBattle() {
		GameBattle gameBattle;
		int answer;
		HashMap<String, Integer> gameBattleAcquiredResources;
		GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
		int diff = gameBattleInterface.newGameBattle();
		System.out.println("""
				Введи:
				1 - сыграть в игру по умолчанию,
				2 - сыграть в игру со своими полями/юнитами,
				0 - выйти в меню""");
		answer = Integer.parseInt(checkAnswer(gamer, gamer.input(), new ArrayList<>(Arrays.asList("1", "2", "0"))));
		switch (answer) {
			case (1):
				gameBattle = new GameBattle(diff, game);
				break;
			case (2):
				BattleMap battleMap;
				HashMap<String, String> mapPaths = game.getMapPaths();
				String getMapByName = "";
				if (!mapPaths.isEmpty()) {
					System.out.println("Вот твои сохраненные на этом аккаунте карты(не факт, что они все еще существуют...):");
					for (String pathName : mapPaths.keySet()) {
						System.out.println(pathName + ": " + mapPaths.get(pathName));
					}
					getMapByName = " или имя твоей карты в списке выше";
					deleteMapPathByGamer();
				}
				System.out.println("Введи путь к твоей карте" + getMapByName + " (введи да, если хочешь создать новую карту, " +
						"введи нет, если хочешь карту по умолчанию):");
				String mapAnswerPath = gamer.input();
				if (Objects.equals(mapAnswerPath, "нет")) {
					battleMap = new BattleMap(15, 15, diff);
				} else {
					if (Objects.equals(mapAnswerPath, "да")) {
						GameBattleEditorGUI gameBattleEditorGUI = new GameBattleEditorGUI(gamer);
						gameBattleEditorGUI.createBattleMap();
					} else if (mapPaths.containsKey(mapAnswerPath)) {
						mapAnswerPath = mapPaths.get(mapAnswerPath);
					}
					GameManager<BattleMap> gameManager = new GameManager<>();
					battleMap = gameManager.getGameItemByFilename(mapAnswerPath);
					while (battleMap == null) {
						System.out.print("""
								Выбери карту заново!
								Если хочешь вернуться к меню, введи "нет",
								иначе - введи путь к карте (введи да, если хочешь создать новую):""");
						mapAnswerPath = gamer.input();
						if (mapPaths.containsKey(mapAnswerPath)) {
							mapAnswerPath = mapPaths.get(mapAnswerPath);
						} else if (Objects.equals(mapAnswerPath, "да")) {
							GameBattleEditorGUI gameBattleEditorGUI = new GameBattleEditorGUI(gamer);
							mapAnswerPath = gameBattleEditorGUI.createBattleMap();
							if (mapAnswerPath.isEmpty()) {
								continue;
							}
						} else if (mapAnswerPath.equalsIgnoreCase("нет")) {
							return;
						}
						battleMap = gameManager.getGameItemByFilename(mapAnswerPath);
					}
					saveMapPathByGamer(mapAnswerPath);
				}
				HashMap<String, Buildable> buildableHashMap = game.getDefaultBuildings();
				HashMap<String, HashMap<String, ArrayList<Integer>>> hostelUnits = game.getHostelUnits(diff);
				HashMap<String, ArrayList<String>> hostelUnitsTyping = new HashMap<>(){{
					for (String unitType: hostelUnits.keySet()) {
						put(unitType, new ArrayList<>(hostelUnits.get(unitType).keySet()));
					}
				}};
				HashMap<String, ArrayList<Integer>> hostelUnitsSpecsMap = new HashMap<>() {{
					for (String unitType: hostelUnits.keySet()) {
						for (String unitName: hostelUnits.get(unitType).keySet()) {
							put(unitName, hostelUnits.get(unitType).get(unitName));
						}
					}
				}};
				if (buildableHashMap.get(Hostel.NAME).getLevel() != 0) {
					if (hostelUnitsSpecsMap.containsKey(Game.STEALER)) {
						System.out.println("Хехе, у тебя завелся вор!");
					} else {
						System.out.println("Твои наемники:");
						GameBattleInterface.printUnitsArray(
								BattleMap.getDefaultPenalties(),
								hostelUnitsTyping,
								hostelUnitsSpecsMap
						);
					}
				}
				gameBattle = new GameBattle(game, diff,
						buildableHashMap.get(Healer.NAME).getBuildingUpper(),
						buildableHashMap.get(BlacksmithHouse.NAME).getBuildingUpper(),
						buildableHashMap.get(Arsenal.NAME).getBuildingUpper(),
						buildableHashMap.get(Tavern.NAME).getBuildingUpper(Tavern.PENALTY_TYPE),
						buildableHashMap.get(Tavern.NAME).getBuildingUpper(Tavern.MOVE_TYPE),
						battleMap,
						game.getAcademyUnits(),
						hostelUnits);
				break;
			default:
				return;
		}
		gameBattleInterface.setGameBattle(gameBattle);
		gameBattleInterface.fillGamerUnitsArray();
		gameBattleAcquiredResources = gameBattleInterface.gaming();
		game.addResource(gameBattleAcquiredResources.get(Game.GOLD), Game.GOLD);
		game.addResource(gameBattleAcquiredResources.get(Game.ELIXIR), Game.ELIXIR);
		System.out.println("После битвы ты получил столько ресурсов:");
		for (String resourceType : gameBattleAcquiredResources.keySet()) {
			System.out.println(resourceType + ": " + gameBattleAcquiredResources.get(resourceType));
		}
	}

	private void gameBuyNewBuilding() {
		boolean spendAbility;
		HashMap<String, Buildable> buildingsHashMap = game.getDefaultBuildings();
		ArrayList<String> spendableBuildings = new ArrayList<>();
		System.out.println("Вот твои ресурсы:");
		printResources();
		System.out.println("Вот цены на покупку/улучшение зданий:");
		for (String buildingName : game.getDefaultBuildings().keySet()) {
			spendAbility = false;
			Buildable building = buildingsHashMap.get(buildingName);
			String costType = building.getCostType();
			String costTypeString = "";
			String firstWord;
			if (building.getLevel() == 0) {
				firstWord = "Построить";
			} else {
				firstWord = "Улучшить";
			}
			if (costType.equals(Game.GOLD)) {
				if (game.getResource(Game.GOLD) >= building.getUpgradeCost() &&
						building.getLevel() != building.getMaxLevel()) {
					spendAbility = true;
					spendableBuildings.add(buildingName);
				}
				costTypeString = " золота";
			} else if (costType.equals(Game.ELIXIR)) {
				if (game.getResource(Game.ELIXIR) >= building.getUpgradeCost() &&
						building.getLevel() != building.getMaxLevel()) {
					spendAbility = true;
					spendableBuildings.add(buildingName);
				}
				costTypeString = " эликсира";
			}
			if (spendAbility) {
				System.out.println(firstWord + " здание " + buildingName + ": " +
					building.getUpgradeCost() + costTypeString);
			}
		}
		if (spendableBuildings.isEmpty()) {
			System.out.println("У тебя не хватает ресурсов для покупки/улучшения зданий!");
			logger.log(Level.WARNING, "Не хватает ресурсов на покупку ни одного здания.");
		} else {
			logger.log(Level.INFO, "Есть ресурсы на покупку зданий.");
			System.out.print("Введи название здания, которое хочешь построить/улучшить;" +
					"\nЕсли не хочешь, введи \"нет\":");
			ArrayList<String> answerList = new ArrayList<>(spendableBuildings.stream().map(String::toLowerCase).toList());
			answerList.add("нет");
			String inputBuildingName = checkAnswer(gamer, gamer.inputOneWord(), answerList);
			if (!Objects.equals(inputBuildingName, "нет")) {
				String type = "";
				String upgradeResultString;
				inputBuildingName = inputBuildingName.substring(0, 1).toUpperCase() + inputBuildingName.substring(1);
				if (inputBuildingName.equals(Tavern.NAME)) {
					logger.log(Level.WARNING, "Пользователь выбрал здание " + Tavern.NAME + " для улучшения.");
					String penalty = "штраф", move = "перемещение";
					System.out.print("Введи тип улучшения: на уменьшение штрафов - введи \"" + penalty + "\"" +
							"\nНа увеличение дальности перемещения - \"" + move + "\":");
					type = checkAnswer(gamer, gamer.inputOneWord(),
							new ArrayList<>() {{
								add(penalty);
								add(move);
							}});
					logger.log(Level.WARNING, "Пользователь выбрал способность таверны \"" + type + "\" для улучшения.");
					upgradeResultString = "Способность таверны - " + type + " - улучшена до ";
					if (Objects.equals(type, penalty)) {
						type = Tavern.PENALTY_TYPE;
					} else {
						type = Tavern.MOVE_TYPE;
					}
					upgradeResultString += (buildingsHashMap.get(inputBuildingName).getLevel(type) + 1) + " уровня!";
				} else {
					upgradeResultString = "Твое здание " + inputBuildingName;
					if (buildingsHashMap.get(inputBuildingName).getLevel() == 0) {
						upgradeResultString += " построено!";
					} else {
						upgradeResultString += " улучшено до " + (buildingsHashMap.get(inputBuildingName).getLevel() + 1) +
								" уровня!";
					}
				}
				int upgradeCost = buildingsHashMap.get(inputBuildingName).getUpgradeCost(type);
				String costType = buildingsHashMap.get(inputBuildingName).getCostType();
				game.upgradeGameBuilding(inputBuildingName, type);
				game.spendResource(upgradeCost, costType);
				System.out.println(upgradeResultString);
				return;
			}
			logger.log(Level.WARNING, "Пользователь отказался от улучшения здания.");
		}
	}

	private void gameSaveGame() {
		GameManager<Game> gameManager = new GameManager<>();
		if (absolutePathSaveGame.isEmpty()) {
			System.out.println("Введи путь для сохранения игры (введи \"нет\", если передумал):");
			String inputAnswer = gamer.input();
			if (!inputAnswer.toLowerCase().split(" ")[0].equals("нет")) {
				while (gameManager.checkIsNotDirectory(inputAnswer) && !inputAnswer.equals("нет")) {
					System.out.println("Это не директория! Введи путь к ней или введи \"нет\", если передумал!");
					inputAnswer = gamer.input();
				}
				if (inputAnswer.equals("нет")) {
					return;
				}
				System.out.println("Введи имя файла без пробелов:");
				String fileName = gamer.input();
				while (fileName.contains(" ")) {
					System.out.println("Введи имя файла без пробелов!:");
					fileName = gamer.input();
				}
				if (!inputAnswer.endsWith("\\")) {
					inputAnswer = inputAnswer + "\\";
				}
				absolutePathSaveGame = inputAnswer + fileName;
				if (!absolutePathSaveGame.endsWith(".ser")) {
					absolutePathSaveGame = absolutePathSaveGame + ".ser";
				}
			}
		}
		boolean saveResult = gameManager.saveGameItemToDirectory(game, absolutePathSaveGame);
		if (saveResult) {
			System.out.println("Твоя игра сохранена!");
			System.out.println("Путь к твоей игре: " + absolutePathSaveGame);
		} else {
			System.out.println("Что-то пошло не так и твоя игра не сохранилась...");
		}
	}

	private void gameSwapResources() {
		System.out.println("Таак... хочешь поменять ресурсы..." +
				"\nУ тебя есть рынок, он берет " + game.getBuildings().get(Market.NAME).getBuildingUpper() + "% комиссии!");
		ArrayList<String> resourcesAbleToChange = new ArrayList<>();
		float marketUpper = 1f - (float) game.getBuildings().get(Market.NAME).getBuildingUpper() / 100f;
		for (String resourceType : game.getResources().keySet()) {
			if (game.getResources().get(resourceType) * (marketUpper) >= 1) {
				resourcesAbleToChange.add(resourceType);
			}
		}
		System.out.println("Ты можешь обменять эти ресурсы:");
		for (String resourceType : resourcesAbleToChange) {
			System.out.println(resourceType);
		}
		System.out.println("Что хочешь обменять? Если передумал, введи \"нет\":");
		ArrayList<String> answerList = new ArrayList<>(resourcesAbleToChange.stream().map(String::toLowerCase).toList());
		answerList.add("нет");
		String inputFirstResource = checkAnswer(gamer, gamer.inputOneWord(), answerList);
		if (Objects.equals(inputFirstResource, "нет")) {
			return;
		}
		System.out.println("На что хочешь обменять?");
		String inputSecondResource = checkAnswer(gamer, gamer.inputOneWord(), answerList);
		if (!Objects.equals(inputFirstResource, "нет") && Objects.equals(inputFirstResource, inputSecondResource)) {
			System.out.println("Ты хочешь обменять " + inputFirstResource + " на " + inputSecondResource + "!");
			System.out.println("Ты задумал что-то неладное...");
		} else if (!Objects.equals(inputFirstResource, "нет") && !Objects.equals(inputSecondResource, "нет")) {
			inputFirstResource = inputFirstResource.substring(0, 1).toUpperCase() + inputFirstResource.substring(1);
			inputSecondResource = inputSecondResource.substring(0, 1).toUpperCase() + inputSecondResource.substring(1);
			String resourceRequest = "Введи количество ресурса " + inputFirstResource + ", который хочешь обменять на " + inputSecondResource +
					"; \nКак всегда, можешь ввести \"нет\":";
			System.out.print(resourceRequest);
			String countOfChangeResource = gamer.inputOneWord();
			while (true) {
				if (countOfChangeResource.equals("нет")) {
					return;
				} else if (InputChecker.isNotNumeric(countOfChangeResource)) {
					System.out.println("Введи число!");
				} else if (Integer.parseInt(countOfChangeResource) > game.getResources().get(inputFirstResource)) {
					System.out.println("У тебя столько ресурса " + inputFirstResource + " нет!");
				} else {
					int addResource = ((Float) (Float.parseFloat(countOfChangeResource) * marketUpper)).intValue();
					game.spendResource(Integer.parseInt(countOfChangeResource), inputFirstResource);
					game.addResource(addResource, inputSecondResource);
					System.out.println("Ты поменял " + countOfChangeResource + " " + inputFirstResource + " на " +
							addResource + " " + inputSecondResource + "!");
					return;
				}
				System.out.println(resourceRequest);
				countOfChangeResource = gamer.inputOneWord();
			}
		}
	}

	public void gameAddUnits() {
		int maxAcademyUnitsCapacity = game.getBuildings().get(Academy.NAME).getBuildingUpper();
		System.out.println("Ты зашел в академию!");
		if (game.getAcademyUnits().isEmpty()) {
			System.out.println("Твоя академия пока пуста.");
		} else {
			System.out.println("Вот твой набор юнитов в академии (штрафы указаны для базовой карты):");
			printAcademyUnits();
			if (game.getAcademyUnits().size() == maxAcademyUnitsCapacity) {
				System.out.println("Ты больше юнитов не можешь добавить!");
				System.out.println("Если хочешь кого-то удалить, введи \"да\", иначе - \"нет\"");
				String inputAnswer = checkAnswer(gamer, gamer.inputOneWord(), ANSWER_CHECK_LIST);
				if (Objects.equals(inputAnswer, "да")) {
					System.out.print("Введи имя героя, которого хочешь удалить(введи \"нет\", если передумал:");
					ArrayList<String> choiceList = new ArrayList<>(game.getAcademyUnits().keySet());
					choiceList.add("нет");
					inputAnswer = checkAnswer(gamer, gamer.inputOneWord(), choiceList);
					if (Objects.equals(inputAnswer, "нет")) {
						return;
					} else {
						game.deleteUnit(inputAnswer);
						System.out.println("Твой герой " + inputAnswer + " был успешно удален из Академии!");
					}
				}
			}
		}
		System.out.println("Введи номер типа героя:");
		for (int i = 0; i < GameBattle.getUnitsTypes().size(); i++) {
			System.out.println((i + 1) + ": " + GameBattle.getUnitsTypes().get(i));
		}
		String typeOfUnit = gamer.inputOneWord();
		while (true) {
			if (InputChecker.isNotNumeric(typeOfUnit)) {
				System.out.println("Введи целое число!");
			} else if (Integer.parseInt(typeOfUnit) <= 0 || Integer.parseInt(typeOfUnit) > GameBattle.getUnitsTypes().size()) {
				System.out.println("Введи индекс из списка сверху!");
			} else {
				typeOfUnit = GameBattle.getUnitsTypes().get(Integer.parseInt(typeOfUnit) - 1);
				break;
			}
			typeOfUnit = gamer.inputOneWord();
		}
		System.out.println("Введи имя нового юнита, можешь написать \"нет\", если хочешь вернуться в меню" +
				"(длина имени не должна превышать 15):");
		String inputUnitName = gamer.input().split(" ")[0];
		if (Objects.equals(inputUnitName.toLowerCase(), "нет")) {
			return;
		}
		while (inputUnitName.length() >= 15) {
			System.out.println("Длина имени не должна превышать 15! " +
					"Введи имя еще раз, введи \"нет\", если хочешь вернуться в меню:");
			inputUnitName = gamer.input().split(" ")[0];
			if (Objects.equals(inputUnitName.toLowerCase(), "нет")) {
				return;
			}
		}
		ArrayList<String> paramsStringList = new ArrayList<>() {{
			add("Здоровье");
			add("Атака");
			add("Дальность атаки");
			add("Защита");
			add("Перемещение");
		}};
		ArrayList<Integer> paramsList = new ArrayList<>(6);
		for (String s : paramsStringList) {
			System.out.println("Введи параметр " + s + " юнита " + inputUnitName);
			String param = gamer.inputOneWord();
			while (true) {
				if (InputChecker.isNotNumeric(param)) {
					System.out.println("Введи целое число!");
				} else if (Integer.parseInt(param) <= 0) {
					System.out.println("Введи число больше 0!");
				} else {
					paramsList.add(Integer.parseInt(param));
					break;
				}
				param = gamer.input().split(" ")[0];
			}
		}
		int unitCost = Game.calculateUnitCost(paramsList.get(0),
				paramsList.get(1),
				paramsList.get(2),
				paramsList.get(3),
				paramsList.get(4));
		if (unitCost > GameBattle.maxWallet()) {
			System.out.println("Этого юнита невозможно будет купить!");
		} else {
			paramsList.add(unitCost);
			game.addUnit(typeOfUnit, inputUnitName, paramsList);
			System.out.println("Ты добавил нового юнита к себе в академию! Стоимость юнита " + inputUnitName + ": " + unitCost + " монет.");
		}
	}

	public void choiceView() {
		int answer;
		while (true) {
			if (game.getBuildings().containsKey(Hotel.NAME)) {
				int gotFromHotel = game.refreshResourcesFromHotel(game.getBuildings().get(Hotel.NAME).getCostType());
				if (gotFromHotel > 0) {
					System.out.println("Твой отель принес тебе " + gotFromHotel +
							" ресурса " + game.getBuildings().get(Hotel.NAME).getCostType());
				}
			}
			System.out.println("Вот твои ресурсы:");
			printResources();
			if (getVillageImages().isEmpty()) {
				System.out.println("Твоя деревня пока что пустая...");
			} else {
				System.out.println("Вот твоя деревня:");
				printVillage();
			}
			System.out.println("Выбери следующие свои действия:");
			HashMap<Integer, String> actionChoiceHashMap = gameActionChoiceHashMap();
			for (int i = 1; i < actionChoiceHashMap.size() + 1; i++) {
				int choiceNum = i % actionChoiceHashMap.size();
				System.out.println((choiceNum) + ": " + actionChoiceHashMap.get(choiceNum));
			}
			answer = Integer.parseInt(checkAnswer(gamer, gamer.input(), new ArrayList<>(actionChoiceHashMap.keySet().
					stream().map(Object::toString).toList())));
			if (answer == 1) {
				gamePlayGameBattle();
			} else if (answer == 2) {
				logger.log(Level.INFO, "Выбор действия: " + actionChoiceHashMap.get(answer) + ".");
				gameBuyNewBuilding();
			} else if (answer == 3) {
				gameSaveGame();
			} else if (answer == 4 || answer == 5) {
				if (actionChoiceHashMap.size() == 3) {
					System.out.println("Ты сюда не должен был зайти...");
				} else if (Objects.equals(actionChoiceHashMap.get(answer), gameActionChoiceList.get(3))) {
					gameSwapResources();
				} else {
					gameAddUnits();
				}
			} else {
				System.out.println("Ты вышел из игры!");
				break;
			}
		}
	}
}
