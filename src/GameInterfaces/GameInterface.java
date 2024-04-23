package GameInterfaces;

import BattlePlace.BattleMap;
import Buildings.*;
import GameSubjects.Game;
import GameSubjects.GameBattle;
import GameSubjects.GameManager;
import Gamers.Gamer;
import java.util.*;

import static GameInterfaces.InputChecker.ANSWER_CHECK_LIST;
import static GameInterfaces.InputChecker.checkAnswer;

public class GameInterface {

	private final String CHOICE_DOWNLOAD = "Загрузиться";
	private final String CHOICE_GAME = "Новая игра";
	public static final String CHOICE_MAP = "Новая карта";

	Gamer gamer;
	Game game;

	public GameInterface(Gamer gamer) {
		this.gamer = gamer;
	}

	public ArrayList<String> getChoiceArray() {
		return new ArrayList<>(){{add(CHOICE_DOWNLOAD);add(CHOICE_GAME);}};
	}

	public String startGame() {
		System.out.println("Привет!");
		System.out.print("Хочешь загрузиться с сохраненного файла - введи 1, начать новую игру - введи 2, " +
				"\nсоздать новую карту c новыми полями - введи 3, выйти из программы - 4:");
		int choice = Integer.parseInt(checkAnswer(gamer, gamer.input().split(" ")[0], new ArrayList<>(Arrays.asList("1", "2", "3", "4"))));
		return switch (choice) {
			case (1) -> CHOICE_DOWNLOAD;
			case (2) ->	CHOICE_GAME;
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
				return;
			case(CHOICE_GAME):
				this.game = new Game(10, 10);
				System.out.println("Твоя новая игра создана!");
				return;
			default:
		}
	}

	private void printAcademyUnits() {
		HashMap<String, ArrayList<Integer>> unitSpecsMap = new HashMap<>();
		HashMap<String, ArrayList<String>> unitsTyping = new HashMap<>() {{
			for (String unitType : game.getAcademyUnits().keySet()) {
				put(unitType, new ArrayList<>(game.getAcademyUnits().keySet()));
				for (String unitName : game.getAcademyUnits().keySet()) {
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
			case (2) -> new ArrayList<>() {{
				add("      []_______     ");
				add("     /\\" + houseName + "\\    ");
				add(" ___/" + finalLevelString + "\\__/\\____\\__ ");
				add("/\\___\\ |'[]''[]'|__\\");
				add("||'''| |''||''''|''|");
				add("\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"");
			}};
			case (3) -> new ArrayList<>() {{
				add("    ________________   ");
				add("   /  " + houseName + "  " + finalLevelString + "  \\  ");
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
		}
		else {
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

	private void saveMapPathByGamer(String mapPath){
		System.out.print("Хочешь сохранить путь к карте на твоем аккаунте?" +
				"\nНапиши да или нет:");
		String answer = checkAnswer(gamer, gamer.input().split(" ")[0], ANSWER_CHECK_LIST);
		if (answer.equals("да")) {
			System.out.println("Введи имя карты:");
			String mapPathName = gamer.input().split(" ")[0];
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
		String answerToDelete = checkAnswer(gamer, gamer.input().split(" ")[0], ANSWER_CHECK_LIST);
		if (Objects.equals(answerToDelete, "да")) {
			String answerMapPathName;
			do {
				System.out.println("Введи имя пути своей карты (введи нет, если хочешь прекратить удаление карт):");
				answerMapPathName = checkAnswer(gamer, gamer.input().split(" ")[0], ANSWER_CHECK_LIST);
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

	public void choiceView() {
		String buildingUpgradeAbilityString = "";
		String addUnitsAbilityString = "";
		String swapResourcesAbilityString = "";
		String absolutePathSave = "";
		int answer;
		int actionOrder = 3;
		boolean toHome = false, swapResourcesAbility = false, addUnitsAbility = false;
		GameBattle gameBattle;
		while (true) {
			System.out.println("Вот твои ресурсы:");
			printResources();
			if (getVillageImages().isEmpty()) {
				System.out.println("Твоя деревня пока что пустая...");
			} else {
				System.out.println("Вот твоя деревня:");
				printVillage();
				buildingUpgradeAbilityString = " или улучшить старое";
				if (game.getBuildings().containsKey(Market.NAME)) {
					swapResourcesAbilityString = "поменять ресурсы - " + (++actionOrder) + ", ";
					swapResourcesAbility = true;
				}
				if (game.getBuildings().containsKey(Academy.NAME)) {
					if (!game.academyUnitsIsEmpty()) {
						System.out.println("Вот твои юниты:");
						printAcademyUnits();
					}
					addUnitsAbilityString = "добавить своих юнитов - " + (++actionOrder) + ", ";
					addUnitsAbility = true;
				}
			}
			System.out.print("Что ты хочешь сделать? Сыграть бой - введи 1, купить новое здание" + buildingUpgradeAbilityString +
					" - 2, сохранить игру - 3, " + addUnitsAbilityString + swapResourcesAbilityString + "выйти - 0:");
			boolean finalSwapResourcesAbility = swapResourcesAbility;
			boolean finalAddUnitsAbility = addUnitsAbility;
			ArrayList<String> actionChoiceList = new ArrayList<>(){{
				add("0");
				add("1");
				add("2");
				add("3");
				if (finalSwapResourcesAbility || finalAddUnitsAbility) {
					add("4");
				}
				if (finalAddUnitsAbility && finalSwapResourcesAbility) {
					add("5");
				}
			}};
			answer = Integer.parseInt(checkAnswer(gamer, gamer.input(), actionChoiceList));
			if (answer == 1) {
				HashMap<String, Integer> gameBattleAcquiredResources;
				GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer);
				int diff = gameBattleInterface.newGameBattle();
				System.out.print("Хочешь сыграть в игру по умолчанию - введи 1, со своими полями/юнитами - 2:");
				answer = Integer.parseInt(checkAnswer(gamer, gamer.input(), new ArrayList<>(Arrays.asList("1", "2"))));
				if (answer == 1) {
					gameBattle = new GameBattle(diff);
				} else {
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
					System.out.println("Введи путь к твоей карте" + getMapByName + " (введи да, если хочешь создать новую карту):");
					String mapAnswerPath = gamer.input();
					if (Objects.equals(mapAnswerPath, "да")) {
						GameBattleEditorGUI gameBattleEditorGUI = new GameBattleEditorGUI(gamer);
						gameBattleEditorGUI.createBattleMap();
					}
					if (mapPaths.containsKey(mapAnswerPath)) {
						mapAnswerPath = mapPaths.get(mapAnswerPath);
					}
					GameManager<BattleMap> gameManager = new GameManager<>();
					BattleMap battleMap = gameManager.getGameItemByFilename(mapAnswerPath);
					while (battleMap == null) {
						System.out.print("""
								Выбери карту заново!
								Если хочешь вернуться к меню, введи "нет", иначе - введи путь к карте (введи да, если хочешь создать новую карту):""");
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
							toHome = true;
							break;
						}
						battleMap = gameManager.getGameItemByFilename(mapAnswerPath);
					}
					if (toHome) {
						continue;
					}
					saveMapPathByGamer(mapAnswerPath);
					HashMap<String, Buildable> buildableHashMap = game.getDefaultBuildings();
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
				game.addResource(gameBattleAcquiredResources.get(Game.GOLD), Game.GOLD);
				game.addResource(gameBattleAcquiredResources.get(Game.ELIXIR), Game.ELIXIR);
				System.out.println("После битвы ты получил столько ресурсов:");
				for (String resourceType: gameBattleAcquiredResources.keySet()) {
					System.out.println(resourceType + ": " + gameBattleAcquiredResources.get(resourceType));
				}
				System.out.println("Итого твои ресурсы:");
				printResources();

			}
			else if (answer == 2) {
				boolean spendAbility = false;
				HashMap<String, Buildable> buildingsHashMap = game.getDefaultBuildings();
				ArrayList<String> spendableBuildings = new ArrayList<>();
				System.out.println("Вот твои ресурсы:");
				printResources();
				System.out.println("Вот цены на покупку/улучшение зданий:");
				for (String buildingName: game.getDefaultBuildings().keySet()) {
					Buildable building = buildingsHashMap.get(buildingName);
					String costType = building.getCostType();
					String costTypeString = "";
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
					String firstWord;
					if (building.getLevel() == 0) {
						firstWord = "Построить";
					}
					else {
						firstWord = "Улучшить";
					}
					System.out.println(firstWord + " здание " + buildingName + ": " +
							building.getUpgradeCost() + costTypeString);
				}
				if (!spendAbility) {
					System.out.println("У тебя не хватает ресурсов для покупки/улучшения зданий!");
				} else {
					System.out.println("Вот список зданий, которые ты можешь построить/улучшить:");
					for (String buildingName: spendableBuildings) {
						System.out.println(buildingName);
					}
					System.out.print("Введи название здания, которое хочешь построить/улучшить;" +
							"\nЕсли не хочешь, введи \"нет\":");
					ArrayList<String> answerList = new ArrayList<>(spendableBuildings.stream().map(String::toLowerCase).toList());
					answerList.add("нет");
					String inputBuildingName = checkAnswer(gamer, gamer.input().toLowerCase().split(" ")[0], answerList);
					if (!Objects.equals(inputBuildingName, "нет")) {
						String type = "";
						String upgradeResultString;
						inputBuildingName = inputBuildingName.substring(0, 1).toUpperCase() + inputBuildingName.substring(1);
						if (inputBuildingName.equals(Tavern.NAME)) {
							String penalty = "штраф", move = "перемещение";
							System.out.print("Введи тип улучшения: на уменьшение штрафов - введи \"" + penalty + "\"" +
									"\nНа увеличение дальности перемещения - \"" + move + "\":");
							type = checkAnswer(gamer, gamer.input().toLowerCase().split(" ")[0],
									new ArrayList<>(){{add(penalty); add(move);}});
							upgradeResultString = "Способность таверны - " + type + " - улучшена до ";
							if (Objects.equals(type, penalty)) {
								type = Tavern.PENALTY_TYPE;
							}
							else {
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
							System.out.println(upgradeResultString);
						}
						int upgradeCost = buildingsHashMap.get(inputBuildingName).getUpgradeCost(type);
						String costType = buildingsHashMap.get(inputBuildingName).getCostType();
						game.upgradeGameBuilding(inputBuildingName, type);
						game.spendResource(upgradeCost, costType);
						System.out.println(upgradeResultString);
					}
				}
			} else if (answer == 3) {
				if (!absolutePathSave.isEmpty()){
					System.out.println("Твоя игра уже сохранена!");
					System.out.println("Путь к твоей игре: " + absolutePathSave);
					continue;
				}
				System.out.println("Введи путь для сохранения игры (введи \"нет\", если передумал):");
				String inputAnswer = gamer.input();
				if (!inputAnswer.toLowerCase().split(" ")[0].equals("нет")) {
					GameManager<Game> gameManager = new GameManager<>();
					while (!gameManager.checkIsDirectory(inputAnswer) && !inputAnswer.equals("нет")) {
						System.out.println("Это не директория! Введи путь к ней или введи \"нет\", если передумал!");
						inputAnswer = gamer.input();
					}
					if (inputAnswer.equals("нет")) {
						break;
					}
					System.out.println("Введи имя файла:");
					String fileName = gamer.input();
					while (fileName.contains(" ")) {
						System.out.println("Введи имя файла без пробела!");
						fileName = gamer.input();
					}
					if (!inputAnswer.endsWith("\\")) {
						inputAnswer = inputAnswer + "\\";
					}
					absolutePathSave = inputAnswer + fileName + ".ser";
					boolean saveResult = gameManager.saveGameItemToDirectory(game, absolutePathSave);
					if (saveResult) {
						System.out.println("Твоя игра сохранена!");
						System.out.println("Путь к твоей игре: " + absolutePathSave);
					}
					else {
						System.out.println("Что-то пошло не так и твоя игра не сохранилась...");
					}
				}
			}
			else if (answer == 4 || answer == 5) {
				if (actionOrder == 3) {
					System.out.println("Ты сюда не должен был зайти...");
				}
				else {
					if (answer == 4 && swapResourcesAbility) {
						System.out.println("Таак... хочешь поменять ресурсы..." +
								"\nУ тебя есть рынок, он берет " + game.getBuildings().get(Market.NAME).getBuildingUpper() + "% комиссии!");
						System.out.print("У тебя в наличии:");
						ArrayList<String> resourcesAbleToChange = new ArrayList<>();
						float marketUpper = 1f - (float)game.getBuildings().get(Market.NAME).getBuildingUpper() / 100f;
						for (String resourceType: game.getResources().keySet()) {
							System.out.println(resourceType + ": " + game.getResources().get(resourceType));
							if (game.getResources().get(resourceType) * (marketUpper) >= 1) {
								resourcesAbleToChange.add(resourceType);
							}
						}
						System.out.println("Ты можешь обменять эти ресурсы:");
						for (String resourceType: resourcesAbleToChange) {
							System.out.println(resourceType);
						}
						System.out.println("Что хочешь обменять? Если передумал, введи \"нет\":");
						ArrayList<String> answerList = new ArrayList<>(resourcesAbleToChange.stream().map(String::toLowerCase).toList());
						answerList.add("нет");
						String inputFirstResource = checkAnswer(gamer, gamer.input().toLowerCase().split(" ")[0], answerList);
						System.out.println("На что хочешь обменять?");
						String inputSecondResource = checkAnswer(gamer, gamer.input().toLowerCase().split(" ")[0], answerList);
						if (!Objects.equals(inputFirstResource, "нет") && Objects.equals(inputFirstResource, inputSecondResource)) {
							System.out.println("Ты хочешь обменять " + inputFirstResource + " на " + inputSecondResource + "!");
							System.out.println("Ты задумал что-то неладное...");
						}
						else if (!Objects.equals(inputFirstResource, "нет") && !Objects.equals(inputSecondResource, "нет")){
							inputFirstResource = inputFirstResource.substring(0, 1).toUpperCase() + inputFirstResource.substring(1);
							inputSecondResource = inputSecondResource.substring(0, 1).toUpperCase() + inputSecondResource.substring(1);
							String resourceRequest = "Введи количество ресурса " + inputFirstResource + ", который хочешь обменять на " + inputSecondResource +
									"; \nКак всегда, можешь ввести \"нет\":";
							System.out.print(resourceRequest);
							String countOfChangeResource = gamer.input().toLowerCase().split(" ")[0];
							while (true) {
								if (countOfChangeResource.equals("нет")) {
									break;
								}
								else if (InputChecker.isNotNumeric(countOfChangeResource)) {
									System.out.println("Введи число!");
								}
								else if (Integer.parseInt(countOfChangeResource) > game.getResources().get(inputFirstResource)) {
									System.out.println("У тебя столько ресурса " + inputFirstResource + " нет!");
								}
								else {
									int addResource = ((Float)(Float.parseFloat(countOfChangeResource) * marketUpper)).intValue();
									game.spendResource(Integer.parseInt(countOfChangeResource), inputFirstResource);
									game.addResource(addResource, inputSecondResource);
									System.out.println("Ты поменял " + countOfChangeResource + " " + inputFirstResource + " на " +
											addResource + " " + inputSecondResource + "!");
									break;
								}
								System.out.println(resourceRequest);
								countOfChangeResource = gamer.input().toLowerCase().split(" ")[0];
							}
						}
					}
					else if (addUnitsAbility){
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
								String inputAnswer = checkAnswer(gamer, gamer.input().toLowerCase().split(" ")[0], ANSWER_CHECK_LIST);
								if (Objects.equals(inputAnswer, "да")) {
									System.out.print("Введи имя героя, которого хочешь удалить(введи \"нет\", если передумал:");
									ArrayList<String> choiceList = new ArrayList<>(game.getAcademyUnits().keySet());
									choiceList.add("нет");
									inputAnswer = checkAnswer(gamer, gamer.input().toLowerCase().split(" ")[0], choiceList);
									if (Objects.equals(inputAnswer, "нет")) {
										continue;
									}
									else {
										game.deleteUnit(inputAnswer);
										System.out.println("Твой герой " + inputAnswer + " был успешно удален из Академии!");
									}
								}
							}
						}
						System.out.println("Введи номер типа героя:");
						for (int i = 0; i < GameBattle.getUnitsTypes().size(); i++) {
							System.out.println(i + ": " + GameBattle.getUnitsTypes().get(i));
						}
						String typeOfUnit = gamer.input().toLowerCase().split(" ")[0];
						while (true) {
								if (InputChecker.isNotNumeric(typeOfUnit)) {
									System.out.println("Введи целое число!");
								} else if (Integer.parseInt(typeOfUnit) <= 0 || Integer.parseInt(typeOfUnit) > GameBattle.getUnitsTypes().size()) {
									System.out.println("Введи индекс из списка сверху!");
								}
								else {
									typeOfUnit = GameBattle.getUnitsTypes().get(Integer.parseInt(typeOfUnit));
									break;
								}
								typeOfUnit = gamer.input().split(" ")[0];
							}
						System.out.println("Введи имя нового юнита, можешь написать \"нет\", если хочешь выйти(длина имени не должна превышать 15):");
						String inputUnitName = gamer.input().split(" ")[0];
						if (Objects.equals(inputUnitName.toLowerCase(), "нет") ) {
							continue;
						}
						toHome = false;
						while (inputUnitName.length() < 15) {
							System.out.println("Длина имени не должна превышать 15! Введи имя еще раз:");
							inputUnitName = gamer.input().split(" ")[0];
							if (Objects.equals(inputUnitName.toLowerCase(), "нет")) {
								toHome = true;
								break;
							}
						}
						if (toHome) {
							continue;
						}
						ArrayList<String> paramsStringList = new ArrayList<>(){{
							add("Здоровье");
							add("Атака");
							add("Дальность атаки");
							add("Защита");
							add("Перемещение");
						}};
						ArrayList<Integer> paramsList = new ArrayList<>(6);
						for (String s : paramsStringList) {
							System.out.println("Введи параметр " + s + " юнита " + inputUnitName);
							String param = gamer.input().split(" ")[0];
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
						}
						else {
							paramsList.add(unitCost);
							game.addUnit(typeOfUnit, inputUnitName, paramsList);
							System.out.println("Ты добавил нового юнита к себе в академию!");
						}
					}
				}
			} else {
				System.out.println("Ты вышел из игры!");
				break;
			}
		}
	}
}
