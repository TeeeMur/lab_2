package GameInterfaces;

import BattlePlace.BattleMap;
import GameSubjects.GameBattle;
import Gamers.Gamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static GameInterfaces.InputChecker.*;
import static GameSubjects.GameManager.inputBattleMap;

public class GameBattleEditorGUI {

	Gamer gamer;
	GameBattle game;

	GameBattleEditorGUI(Gamer gamer, GameBattle gameBattle) {
		this.gamer = gamer;
		this.game = gameBattle;
	}

	public void createGameBattle() {
		String mapSizesConstraintString = "Высота и ширина карты не могут быть меньше 6 или больше 20";
		System.out.println("Для того, чтобы создать новую карту, тебе нужно ввести размер - ширину и высоту карты.");
		System.out.println(mapSizesConstraintString);
		System.out.print("Введи числами X (ширину) и Y (высоту) карты:");
		int sizeX, sizeY, answer;
		ArrayList<String> inputAnswerList = new ArrayList<>(Arrays.asList(gamer.input().split(" ")));
		while (true) {
			if (inputAnswerList.size() != 2) {
				System.out.println("Давай еще раз, введи два целых числа:");
			} else if (!isNotNumeric(inputAnswerList.getFirst()) || !isNotNumeric(inputAnswerList.get(1))) {
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
		if (answer == 2) {
			inputUnitTypePenaltyMap = new HashMap<>();
			System.out.println("Напиши в строку свои иконки препятсвий! Учти, что их не может быть больше 5 или меньше 3:");
			ArrayList<String> obstacles = new ArrayList<>(Arrays.asList(gamer.input().split(" ")));
			while (obstacles.size() < 2) {
				System.out.println("Ты ввел только одно препятствие!");
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
		System.out.println("Скопируй его, вставь в консоль, отредактируй и нажми Enter!");
		String[][] map = inputBattleMap(gamer, sizeX, sizeY);
		while (map == null) {
			System.out.println("Ты, похоже, неправильно скопировал или ввел карту! Давай еще раз:");
			System.out.println("Вот твой шаблон для карты:");
			printBattleMapExample(sizeX, sizeY);
			map = inputBattleMap(gamer, sizeX, sizeY);
		}
	}

	private void printBattleMapExample(int sizeX, int sizeY) {
		for (int i = 0; i < sizeY; i++) {
			for (int j = 0; j < sizeX - 1; j++) {
				System.out.println(BattleMap.getDefaultFields().getFirst() + " ");
			}
				System.out.println(BattleMap.getDefaultFields().getFirst());
		}
	}
}
