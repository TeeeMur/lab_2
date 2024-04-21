package GameInterfaces;

import BattlePlace.BattleMap;
import GameSubjects.GameBattle;
import GameSubjects.GameManager;
import Gamers.Gamer;

import java.util.*;

import static GameInterfaces.InputChecker.*;
import static GameSubjects.GameManager.getStringMapBasicFields;
import static GameSubjects.GameManager.inputBattleMap;

public class GameBattleEditorGUI {

	Gamer gamer;

	public GameBattleEditorGUI(Gamer gamer) {
		this.gamer = gamer;
	}

	public void createBattleMap() {
		String mapSizesConstraintString = "Высота и ширина карты не могут быть меньше " + BattleMap.getMinSizeX() +
				" или больше " + BattleMap.getMaxSizeX();
		System.out.println("Для того, чтобы создать новую карту, тебе нужно ввести размер - ширину и высоту карты.");
		System.out.println(mapSizesConstraintString);
		System.out.print("Введи числами X (ширину) и Y (высоту) карты:");
		int sizeX, sizeY, answer;
		ArrayList<String> inputAnswerList = new ArrayList<>(Arrays.asList(gamer.input().split(" ")));
		while (true) {
			if (inputAnswerList.size() != 2) {
				System.out.println("Давай еще раз, введи два целых числа:");
 			} else if (isNotNumeric(inputAnswerList.getFirst()) || isNotNumeric(inputAnswerList.get(1))) {
				System.out.println("Попросил же ввести два целых числа! Давай еще раз:");
			}
			else {
				sizeX = Integer.parseInt(inputAnswerList.getFirst());
				sizeY = Integer.parseInt(inputAnswerList.get(1));
				if (sizeX < BattleMap.getMinSizeX() || sizeX > BattleMap.getMaxSizeX() || sizeY < BattleMap.getMinSizeY() || sizeY > BattleMap.getMaxSizeY()) {
					System.out.println("Предупреждал же! " + mapSizesConstraintString);
					System.out.println("Введи еще раз два целых числа");
				}
				else {
					break;
				}
			}
			inputAnswerList.clear();
			inputAnswerList.addAll(Arrays.asList(gamer.input().split(" ")));
		}
		System.out.println("Есть препятствия по умолчанию:");
		for (String field: BattleMap.getDefaultFields()) {
			System.out.println(field);
		}
		System.out.print("Хочешь их выбрать или поставишь свои? Выберешь по умолчанию - введи 1, свои - введи 2:");
		answer =  Integer.parseInt(checkAnswer(gamer, gamer.input().split(" ")[0], new ArrayList<>(Arrays.asList("1", "2"))));
		HashMap<String, HashMap<String, Float>> inputUnitTypePenaltyMap;
		ArrayList<String> obstacles;
		if (answer == 1) {
			obstacles = BattleMap.getDefaultFields();
			inputUnitTypePenaltyMap = BattleMap.getDefaultPenalties();
		}
		else {
			inputUnitTypePenaltyMap = new HashMap<>();
			System.out.println("Напиши в строку свои иконки препятсвий! Учти, что их не может быть только 4:");
			obstacles = new ArrayList<>(Arrays.asList(gamer.input().split(" ")));
			while (obstacles.size() != 4) {
				System.out.println("Ты НЕ 4 препятствия!");
			}
			System.out.println("Для каждого типа юнитов введи штрафы за передвижение по препятствиям:");
			for (String unitType: GameBattle.getUnitsTypes()) {
				System.out.println(unitType + ":");
				inputUnitTypePenaltyMap.put(unitType, new HashMap<>());
				for (String obstacle: obstacles) {
					System.out.print(obstacle + ":");
					String obstaclePenaltyString = gamer.input().split(" ")[0];
					while (true) {
						if (isNotNumeric(obstaclePenaltyString)) {
							System.out.println("Ты, похоже, ввел не число! Введи еще раз:");
						} else if (Float.parseFloat(obstaclePenaltyString) <= 0) {
							System.out.println("Штраф не может быть меньше нуля или равным ему, а также не может" +
									" быть больше 3!");
						} else {
							break;
						}
						obstaclePenaltyString = gamer.input().split(" ")[0];
					}
					inputUnitTypePenaltyMap.get(unitType).put(obstacle, Float.parseFloat(obstaclePenaltyString));
				}
			}
		}
		System.out.println("Вот твой шаблон для карты:");
		printBattleMapExample(sizeX, sizeY);
		System.out.println("Скопируй его, вставляй по одной строке в консоль, редактируй и в конце нажми Enter!");
		String[][] inputMap = inputBattleMap(gamer, sizeX, sizeY);
		Set<String> obstacleSet = new HashSet<>(obstacles);
		Set<String> inputMapObstacleSet;
		while (true) {
			if (inputMap == null) {
				System.out.println("Ты, похоже, неправильно скопировал или ввел карту! Давай еще раз:");
			} else {
				inputMapObstacleSet = getStringMapBasicFields(inputMap);
				if (!obstacleSet.containsAll(inputMapObstacleSet)) {
					System.out.println("Количество различных полей, которые ты ввел ранее, не соответствует количеству" +
							" различных полей карты. Давай еще раз:");
				}
				else {
					break;
				}
			}
			System.out.print("Вот твои поля на выбор для карты:");
			for (String obstacle : obstacles) {
				System.out.print(obstacle + " ");
			}
			System.out.println();
			System.out.println("Вот твой шаблон для карты:");
			printBattleMapExample(sizeX, sizeY);
			System.out.println("Скопируй его, вставляй по одной строке в консоль, редактируй и в конце нажми Enter!");
			inputMap = inputBattleMap(gamer, sizeX, sizeY);
		}
		BattleMap battleMap = new BattleMap(inputMap, obstacles, inputUnitTypePenaltyMap);
		System.out.println("Введи путь для сохранения карты(введи \"нет\", если передумал):");
		String inputAnswer = gamer.input();
		if (!inputAnswer.toLowerCase().split(" ")[0].equals("нет")) {
			GameManager<BattleMap> gameManager = new GameManager<>();
			while (!gameManager.checkDirectory(inputAnswer)) {
				System.out.println("Это не директория! Введи путь к ней!");
				inputAnswer = gamer.input();
			}
			System.out.println("Введи имя файла сохраненной карты:");
			String fileName = gamer.input();
			while (fileName.contains(" ")) {
				System.out.println("Введи имя файла без пробела!");
				fileName = gamer.input();
			}
			if (!inputAnswer.endsWith("\\")) {
				inputAnswer = inputAnswer + "\\";
			}
			String absolutePathSave = inputAnswer + fileName + ".ser";
			boolean saveResult = gameManager.saveGameItemToDirectory(battleMap, absolutePathSave);
			if (saveResult) {
				System.out.println("Твоя игра сохранена!");
				System.out.println("Путь к твоей карте: " + absolutePathSave);
			} else {
				System.out.println("Что-то пошло не так и твоя карта не сохранилась...");
			}
		}
	}

	private void printBattleMapExample(int sizeX, int sizeY) {
		for (int i = 0; i < sizeY; i++) {
			for (int j = 0; j < sizeX - 1; j++) {
				System.out.print(BattleMap.getDefaultFields().getFirst() + " ");
			}
				System.out.println(BattleMap.getDefaultFields().getFirst());
		}
	}
}
