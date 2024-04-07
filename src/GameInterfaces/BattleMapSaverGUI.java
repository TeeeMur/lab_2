package GameInterfaces;

import BattlePlace.BattleMap;
import Gamers.Gamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static GameInterfaces.InputChecker.checkAnswer;
import static GameInterfaces.InputChecker.isNotNumeric;

public class BattleMapSaverGUI {

	Gamer gamer;

	BattleMapSaverGUI(Gamer gamer) {
		this.gamer = gamer;
	}

	public void createBattleMap() {
		String mapSizesConstraintString = "Высота и ширина карты не могут быть меньше 6 или больше 20";
		System.out.println("Для того, чтобы создать новую карту, тебе нужно ввести размер - ширину и высоту карты.");
		System.out.println(mapSizesConstraintString);
		System.out.print("Введи числами X (ширину) и Y (высоту) карты:");
		int sizeX, sizeY, answer;
		ArrayList<String> inputAnswerList = new ArrayList<>(Arrays.asList(gamer.input().split(" ")));
		String inputAnswer;
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
		System.out.println("Вот твой шаблон для карты:");
		printBattleMapExample(sizeX, sizeY);
		System.out.println("Скопируй его, вставь в консоль, отредактируй и нажми Enter!");

	}

	private void printBattleMapExample(int sizeX, int sizeY) {
		for (int i = 0; i < sizeY; i++) {
			for (int j = 0; j < sizeX - 1; j++) {
				System.out.println(BattleMap.getDefaultFields().getFirst() + " ");
			}
				System.out.println(BattleMap.getDefaultFields().getFirst());
		}
	}

	private String[][] inputBattleMap(int sizeX, int sizeY) {
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		return new String[1][1];
	}
}
