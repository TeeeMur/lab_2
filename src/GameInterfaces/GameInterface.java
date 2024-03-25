package GameInterfaces;

import BattlePlace.BattleMap;
import GameSubject.Game;
import Gamers.Gamer;
import Units.Unit;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.min;

public class GameInterface {

	private final ArrayList<String> answerCheckList = new ArrayList<>() {{
			add("да");
			add("нет");
		}};

	public static final String CONSOLE_FLUSH = "\033[H\033[2J";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_CYAN = "\u001B[36m";
	Game game;
	Gamer gamer;

	public int newGame(Gamer gamer) throws InterruptedException {
		System.out.println("Добро пожаловать в игру Bauman's gate!");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("Выберите сложность игры от 1 до 5:");
		ArrayList<String> checkList = new ArrayList<>() {{
			for (int i = 1; i < 6; i++) {
				add(String.format("%d", i));
			}
		}};
		String answ = new Scanner(System.in).nextLine();
		this.gamer = gamer;
		return Integer.parseInt(checkAnswer(answ, checkList));
	}

	public void setGame(Game game) {
		this.game = game;
	}

	String checkAnswer(String answ, ArrayList<String> checkList) {
		int exitCounter = 1;
		while (!checkList.contains(answ.toLowerCase())) {
			if (exitCounter++ > 5) {
				System.out.println("Ты слишком долго пытался ввести ответ...");
				System.exit(0);
			}
			System.out.print("Ты ввел что-то не то, введи еще раз:");
			answ = new Scanner(System.in).nextLine().toLowerCase();
		}
		return answ;
	}

	private static boolean isNotNumeric(String str) {
		try {
			Integer.parseInt(str);
			return false;
		} catch (NumberFormatException e) {
			return true;
		}
	}

	private void printExampleOfGame() {
		System.out.println("Каким образом играть? Сначала по номеру выбери героя, потом действие, и назначение действия, например:");
		ArrayList<String> exampleUnitStateList = getCurrentUnitsState(game.getGamerUnitsArray());
		for (String eachUnitState : exampleUnitStateList) {
			System.out.println(eachUnitState);
		}
		System.out.println("""
				Выбери героя по номеру:
				Твой выбор героя:1
				Выбери действие: Передвижение - 1, атака - 2:
				Твой выбор действия:1
				Дальше будет понятно (❛ᴗ❛)
				Вперед, игра началась!
				""");
	}

	public void fillGamerUnitsArray() throws InterruptedException {
		ArrayList<String> purchaseStringList;
		int sum = 0, purchaseUnitCount;
		boolean answ;
		HashMap<String, Integer> hasPurchasedStringMap = new HashMap<>(),
				unitsLowerNamePrices = new HashMap<>() {{
			for (String unitSpecName: game.getUnitsSpecsMap().keySet()) {
				put(unitSpecName.toLowerCase(), game.getUnitsSpecsMap().get(unitSpecName).getLast());
			}
		}};
		String purchaseUnitName;
		printGamerUnitsArrayChoice();
		TimeUnit.SECONDS.sleep(3);
		String writeYourChoiceString = """
				Напиши в строку, каких и сколько героев хочешь взять, например: Мечник 2
				""";
		String writeAndAnswerChoiceString = writeYourChoiceString +
				"""
				Я тебе отвечу, сколько у тебя осталось денег, ты сможешь далее покупать героев, пока у тебя остаются средства на них;
				Также можешь написать Да, тогда процесс покупки закончится; нет - тогда процесс покупки начнется заново""";
		System.out.println(writeAndAnswerChoiceString);
		while (true) {
			answ = false;
			if (!hasPurchasedStringMap.keySet().isEmpty()) {
				System.out.println("Твой набор сейчас: ");
				for (String hasPurchasedUnitName: hasPurchasedStringMap.keySet()) {
					System.out.println(hasPurchasedUnitName + ": " + hasPurchasedStringMap.get(hasPurchasedUnitName));
				}
			}
			System.out.println("У тебя осталось " + ANSI_RED + (game.getWallet() - sum) + ANSI_RESET + " денежек в кошельке");
			purchaseStringList = new ArrayList<>(Arrays.asList(new Scanner(System.in).nextLine().toLowerCase().split(" "))); // строка "Герой Кол-во" из ввода
			switch (purchaseStringList.size()) {
				case (3):
					purchaseStringList.set(0, purchaseStringList.getFirst() + " " + purchaseStringList.get(1));
					purchaseStringList.remove(1);
					break;
				case (2):
					if (unitsLowerNamePrices.containsKey(purchaseStringList.getFirst() + " " + purchaseStringList.get(1))) {
						purchaseStringList.set(0, purchaseStringList.getFirst() + " " + purchaseStringList.get(1));
						purchaseStringList.remove(1);
						System.out.println("Похоже, ты не указал количество воинов! Укажи здесь:");
						purchaseStringList.add(new Scanner(System.in).nextLine());
						break;
					}
					break;
				case (1):
					if (unitsLowerNamePrices.containsKey(purchaseStringList.getFirst())) {
						System.out.println("Похоже, ты не указал количество воинов! Укажи здесь:");
						purchaseStringList.add(new Scanner(System.in).nextLine());
						break;
					}
					if (Objects.equals(checkAnswer(purchaseStringList.getFirst(), answerCheckList), "да")) {
						answ = true;
					}
					break;
				default:
					purchaseStringList.set(0, "");
					break;
			}
			if (answ) {
				if (Objects.equals(purchaseStringList.getFirst(), "нет")) {
					hasPurchasedStringMap.clear();
					sum = 0;
					System.out.println("Набор юнитов начинается заново!");
					printGamerUnitsArrayChoice();
					System.out.println(writeYourChoiceString);
					continue;
				} else if (Objects.equals(purchaseStringList.getFirst(), "да")) {
					if (hasPurchasedStringMap.isEmpty()) {
						System.out.println("Ты берешь пустой набор героев, так нельзя!");
						continue;
					} else {
						game.setGamerUnits(hasPurchasedStringMap);
						System.out.println("Твой набор героев:");
						for (Unit unit : game.getGamerUnitsArray()) {
							System.out.println(unit.getName());
						}
						return;
					}
				}
			} else if (unitsLowerNamePrices.containsKey(purchaseStringList.getFirst()) && !isNotNumeric(purchaseStringList.getLast())) {
				purchaseUnitCount = Integer.parseInt(purchaseStringList.getLast());
				purchaseUnitName = purchaseStringList.getFirst();
				if (game.getWallet() - sum - unitsLowerNamePrices.get(purchaseUnitName) *  purchaseUnitCount < 0) {
					System.out.print("У тебя не хватает денег на такую покупку, выбирай еще раз!");
					continue;
				}
				if (hasPurchasedStringMap.containsKey(purchaseStringList.getFirst())) {
					int purchasedCount = hasPurchasedStringMap.get(purchaseUnitName);
					hasPurchasedStringMap.put(purchaseUnitName, purchaseUnitCount + purchasedCount);
				}
				else {
					hasPurchasedStringMap.put(purchaseUnitName, purchaseUnitCount);
				}
				sum += (unitsLowerNamePrices.get(purchaseUnitName) *  purchaseUnitCount);
			}
			else {
				System.out.println("Ты что-то не так написал...");
				System.out.println(writeYourChoiceString);
				continue;
			}
			if (game.getWallet() - sum < min(unitsLowerNamePrices.values())) {
				System.out.println("У тебя осталось недостаточно денег для покупки героев.");
				System.out.println("Итого твой выбор:");
				for (String unitName : hasPurchasedStringMap.keySet()) {
					System.out.println(unitName + ": " + hasPurchasedStringMap.get(unitName));
				}
				System.out.println("Покупаем такой набор? Напиши Да или Нет:");
				String purchaseConfirmation = checkAnswer(new Scanner(System.in).nextLine().toLowerCase(), answerCheckList);
				if (Objects.equals(purchaseConfirmation, "да")) {
					game.setGamerUnits(hasPurchasedStringMap);
					System.out.println("Твой набор героев:");
					for (Unit unit : game.getGamerUnitsArray()) {
						System.out.println(unit.getName());
					}
					return;
				}
				else {
					hasPurchasedStringMap.clear();
					sum = 0;
					System.out.println("Набор юнитов начинается заново!");
					printGamerUnitsArrayChoice();
					System.out.println(writeYourChoiceString);
				}
			}
		}
	}

	private void printGamerUnitsArrayChoice() {
		BattleMap battleMap = game.getBattleMap();
		ArrayList<ArrayList<Float>> unitTypesPenalties = game.getUnitTypesPenalties();
		ArrayList<ArrayList<String>> unitsTyping = game.getUnitsTyping();
		HashMap<String, ArrayList<Integer>> unitsSpecsMap = game.getUnitsSpecsMap();
		System.out.println("Для покупки у тебя есть на выбор 9 бойцов, внимательно изучи их характеристики и выбери, кого и сколько ты купишь:");
		String divider = "+------+-----------------+----------+-------+-----------------+--------+---------------+-----------+\n";
		String columnNames = "|   №  |     Название    | Здоровье | Атака | Дальность атаки | Защита |  Перемещение  | Стоимость |\n";
		String footColumnName = "|      |            Пешие           |   " +
				formattedTypeOfUnitsColumnName(battleMap.getBasicFields(), unitTypesPenalties.getFirst()) + "      |\n";
		String archerColumnName = "|      |           Лучники          |   " +
				formattedTypeOfUnitsColumnName(battleMap.getBasicFields(), unitTypesPenalties.get(1)) + "      |\n";
		String horseColumnName = "|      |           Всадники         |   " +
				formattedTypeOfUnitsColumnName(battleMap.getBasicFields(), unitTypesPenalties.get(2)) + "      |\n";
		String[] typeColumnNames = {footColumnName, archerColumnName, horseColumnName};
		System.out.print(divider);
		Object[] tempSpecs = new Object[8];
		int iter = 1;
		for (int i = 0; i < unitsTyping.size(); i++) {
			System.out.print(typeColumnNames[i]);
			System.out.print(divider);
			System.out.print(columnNames);
			System.out.print(divider);
			for (int j = 0; j < unitsTyping.get(i).size(); j++) {
				tempSpecs[0] = iter++;
				tempSpecs[1] = unitsTyping.get(i).get(j);
				for (int a = 2; a < 8; a++) {
					tempSpecs[a] = unitsSpecsMap.get(unitsTyping.get(i).get(j)).get(a - 2);
				}
				System.out.format(specsAlignmentByType(i), tempSpecs);
				System.out.print(divider);
			}
		}
	}

	private String specsAlignmentByType(int unitsTyping) {
		String color = game.colorByType(unitsTyping);
		return "|  %2d  | " + color + "%-15s" + ANSI_RESET +
				" |    %2d    |  %2d   |        %2d       |   %2d   |      %2d       |    %3d    |\n";
	}

	private String formattedTypeOfUnitsColumnName(ArrayList<String> fields, ArrayList<Float> penalties) {
		return String.format(
				"Штраф за 1 клетку:  %s: %.1f   %s: %.1f   %s: %.1f   %s: %.1f",
				fields.getFirst(),
				penalties.getFirst(),
				fields.get(1),
				penalties.get(1),
				fields.get(2),
				penalties.get(2),
				fields.get(3),
				penalties.get(3)
		);
	}

	private ArrayList<String> getCurrentUnitsState(ArrayList<Unit> unitsList) {
		ArrayList<String> result = new ArrayList<>();
		for (Unit unit : unitsList) {
			result.add("| " + unit.getMapImage() + ")" + unit.getName() + " - " + "Здоровье:" +
					ANSI_GREEN + unit.getHealthPoints() + ANSI_RESET + "; Защита:" + ANSI_BLUE + unit.getDefensePoints() + ANSI_RESET +
					"; Атака:" + ANSI_RED + unit.getAttackPoints() + ANSI_RESET + "; |");
			result.add("| Дальность атаки:" + unit.getAttackDistance() + "; Дальность перемещения:" + unit.getMovePoints() + " |");
		}
		return result;
	}

	private void printCurrentMapAndState() {
		ArrayList<String> currentGamerUnitsStateLines = getCurrentUnitsState(game.getGamerUnitsArray());
		currentGamerUnitsStateLines.add("Герои твоего врага:");
		currentGamerUnitsStateLines.addAll(getCurrentUnitsState(game.getSecondGamerUnitsArray()));
		int minLines = Math.min(game.getBattleMap().getSizeY(), currentGamerUnitsStateLines.size());
		System.out.print("   ");
		for (int i = 1; i < 10; i++) {
			System.out.format("%d ", i);
		}
		for (int i = 10; i < 16; i++) {
			System.out.format("%d", i);
		}
		System.out.println(" X");
		for (int i = 0; i < minLines; i++) {
			printBattleMapLine(i);
			System.out.println("  " + currentGamerUnitsStateLines.get(i));
		}
		int a = game.getBattleMap().getSizeY() - currentGamerUnitsStateLines.size();
		if (a > 0) {
			for (int i = minLines; i < minLines + a; i++) {
				printBattleMapLine(i);
				System.out.println();
			}
		} else if (a < 0) {
			String emptyString = " ".repeat(3 + game.getBattleMap().getSizeX() * 2 + 2);
			for (int i = minLines; i < minLines - a; i++) {
				System.out.print(emptyString);
				System.out.println(currentGamerUnitsStateLines.get(i));
			}
		}
		System.out.println("Y");
	}

	private void printBattleMapLine(int i) {
		String[] battleLine;
		System.out.format("%d ", i + 1);
		if (i < 9) {
			System.out.print(" ");
		}
		battleLine = game.getBattleMap().getBattleMapLine(i);
		for (int j = 0; j < game.getBattleMap().getSizeX(); j++) {
			System.out.print(battleLine[j] + " ");
		}
	}

	private String removeAscii(String str) {
		if (str.length() >= 5) {
			return str.substring(5, 6);
		}
		return str;
	}

	public void gaming() throws InterruptedException {
		int xMoveCoord, yMoveCoord, checkRes,
				inputActionNum;
		printExampleOfGame();
		TimeUnit.SECONDS.sleep(3);
		String inputHero, inputAction, inputAnswer, secondGamerActionString;
		ArrayList<String> heroCheckList, attackableUnitsIndexList, inputCoords;
		ArrayList<String> actionCheck = new ArrayList<>() {{
			for (int i = 1; i < 3; i++) {
				add(String.format("%d", i));
			}
		}};
		boolean gamerWin = false;
		while (!game.endOfGame()) {
			inputCoords = new ArrayList<>();
			heroCheckList = new ArrayList<>() {{
				for (int i = 0; i < game.getGamerUnitsArray().size(); i++) {
					add(removeAscii(game.getGamerUnitsArray().get(i).getMapImage()));
				}
			}};
			System.out.println("Карта боевых действий:");
			printCurrentMapAndState();
			System.out.print("Выбери героя для хода по номеру:");
			inputHero = checkAnswer(new Scanner(System.in).nextLine(), heroCheckList);
			System.out.print("Выбери действие: Передвижение - 1, атака - 2:");
			inputAction = checkAnswer(new Scanner(System.in).nextLine(), actionCheck);
			inputActionNum = Integer.parseInt(inputAction);
			if (inputActionNum == 2) {
				ArrayList<Unit> attackableUnitsList = game.checkHeroAttackableList(inputHero);
				if (attackableUnitsList.isEmpty()) {
					System.out.println("Герой " + game.getUnitByMapImage(inputHero).getName() + " сейчас никого атаковать не может!");
					continue;
				}
				else if (attackableUnitsList.size() == 1) {
					System.out.println("Атаковать можешь только одного: " + attackableUnitsList.getFirst().getMapImage() +
							") " + attackableUnitsList.getFirst().getName() + "\nАтакуешь?");
					inputAnswer = checkAnswer(new Scanner(System.in).nextLine().toLowerCase(), answerCheckList);
					if (Objects.equals(inputAnswer, "да")) {
						game.makeAttack(inputHero, inputAnswer);
					}
					continue;
				}
				else {
					attackableUnitsIndexList = new ArrayList<>(){{
					for (Unit attackableUnit: attackableUnitsList) {
						add(removeAscii(attackableUnit.getMapImage()));
					}
				}};
					System.out.println("Aтаковать можешь таких героев врага:");
					for (Unit attackableUnit : attackableUnitsList) {
							System.out.println(attackableUnit.getMapImage() + ")" + attackableUnit.getName());
						}
					System.out.println("Кого атакуешь? Напиши номер:");
					inputAnswer = checkAnswer(new Scanner(System.in).nextLine().toLowerCase(), attackableUnitsIndexList);
					game.makeAttack(inputHero, inputAnswer);
				}
			}
			else if (inputActionNum == 1) {
				System.out.print("Напиши координаты, куда хочешь сходить - x y:");
				inputAnswer = new Scanner(System.in).nextLine();
				inputCoords.addAll(Arrays.asList(inputAnswer.split(" ")));
				while (true) {
					if (inputCoords.size() != 2) {
						System.out.println("Ты что-то написал не так! Напиши координаты еще раз:");
					} else if (isNotNumeric(inputCoords.getFirst()) || isNotNumeric(inputCoords.get(1))) {
						System.out.println("Ты написал не числа! Напиши координаты еще раз:");
					}
					else {
						xMoveCoord = Integer.parseInt(inputCoords.getFirst()) - 1;
						yMoveCoord = Integer.parseInt(inputCoords.get(1)) - 1;
						checkRes = game.checkHeroMoveAbility(inputHero, xMoveCoord, yMoveCoord);
						if (checkRes == 1) {
							System.out.println("Герои могут делать ход только в обычную клетку!");
						}
						else if (checkRes == 2) {
							System.out.println("Герой " + game.getUnitByMapImage(inputHero).getName() + " не может пойти в эту клетку!");
						}
						else {
							game.makeMove(inputHero, xMoveCoord, yMoveCoord);
							break;
						}
					}
					inputAnswer = new Scanner(System.in).nextLine();
					inputCoords.clear();
					inputCoords.addAll(Arrays.asList(inputAnswer.split(" ")));
				}
			}
			else {
				System.out.println("Что-то пошло не так...");
				continue;
			}
			System.out.println("Результат твоего хода:");
			printCurrentMapAndState();
			if (game.endOfGame()) {
				System.out.println(ANSI_GREEN + "Ты выиграл!" + ANSI_RESET);
				gamerWin = true;
			}
			secondGamerActionString = game.secondGamerMove();
			System.out.println("Результат хода твоего противника:");
			System.out.println(secondGamerActionString);
		}
		if (!gamerWin) {
			System.out.println(ANSI_RED + "Ты проиграл!" + ANSI_RESET);
		}
	}
}
