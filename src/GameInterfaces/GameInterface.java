package GameInterfaces;

import BattlePlace.BattleMap;
import GameSubjects.GameBattle;
import Gamers.Gamer;
import Units.Unit;

import java.util.*;

import static GameInterfaces.InputChecker.checkAnswer;
import static GameInterfaces.InputChecker.isNotNumeric;
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
	private final String colorUnitStateFormat = ANSI_YELLOW + ANSI_RESET + ANSI_YELLOW + ANSI_RESET + ANSI_GREEN + ANSI_RESET + ANSI_BLUE + ANSI_RESET + ANSI_RED + ANSI_RESET;
	GameBattle gameBattle;
	Gamer gamer;

	public GameInterface(Gamer gamer) {
		this.gamer = gamer;
	}
	public int startGameInterface() {
		System.out.println("Привет!");
		System.out.print("Хочешь загрузиться с сохраненного файла - введи 1, начать новую игру - введи 2, " +
				"создать новую карту - введи 3:");
		return Integer.parseInt(checkAnswer(gamer, new Scanner(System.in).nextLine().split(" ")[0], new ArrayList<>(Arrays.asList("1", "2", "3"))));
	}

	public int newGameBattle() {
		System.out.println("Добро пожаловать в игру Bauman's gate!");
		System.out.print("Выбери сложность игры от 1 до 5:");
		ArrayList<String> checkList = new ArrayList<>() {{
			for (int i = 1; i < 6; i++) {
				add(String.format("%d", i));
			}
		}};
		String answ = gamer.input();
		return Integer.parseInt(checkAnswer(gamer, answ, checkList));
	}

	public void setGame(GameBattle gameBattle) {
		this.gameBattle = gameBattle;
	}



	private void printExampleOfGame() {
		System.out.println("Каким образом играть? Сначала по номеру выбери героя, потом действие, и назначение действия, например:");
		ArrayList<String> exampleUnitStateList = getCurrentUnitsState(gameBattle.getGamerUnitsArray());
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
		int sum = 0, purchaseUnitCount, unitsCount = 0;
		boolean answ;
		HashMap<String, Integer> hasPurchasedStringMap = new HashMap<>(),
				unitsLowerNamePrices = new HashMap<>() {{
			for (String unitSpecName: gameBattle.getUnitsSpecsMap().keySet()) {
				put(unitSpecName.toLowerCase(), gameBattle.getUnitsSpecsMap().get(unitSpecName).getLast());
			}
		}};
		String purchaseUnitName;
		printGamerUnitsArrayChoice();
		String writeYourChoiceString = """
				Напиши в строку, каких и сколько героев хочешь взять, например: Мечник 2""";
		String writeAndAnswerChoiceString = writeYourChoiceString + "\n" +
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
			System.out.println("У тебя осталось " + ANSI_RED + (gameBattle.getWallet() - sum) + ANSI_RESET + " денежек в кошельке");
			purchaseStringList = new ArrayList<>(Arrays.asList(gamer.input().toLowerCase().split(" "))); // строка "Герой Кол-во" из ввода
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
						purchaseStringList.add(gamer.input().split(" ")[0]);
						break;
					}
					break;
				case (1):
					if (unitsLowerNamePrices.containsKey(purchaseStringList.getFirst())) {
						System.out.println("Похоже, ты не указал количество воинов! Укажи здесь:");
						purchaseStringList.add(gamer.input().split(" ")[0]);
						break;
					}
					if (answerCheckList.contains(purchaseStringList.getFirst())) {
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
					unitsCount = 0;
					System.out.println("Набор юнитов начинается заново!");
					printGamerUnitsArrayChoice();
					System.out.println(writeYourChoiceString);
					continue;
				} else if (Objects.equals(purchaseStringList.getFirst(), "да")) {
					if (hasPurchasedStringMap.isEmpty()) {
						System.out.println("Ты берешь пустой набор героев, так нельзя!");
						continue;
					} else {
						gameBattle.setGamerUnits(hasPurchasedStringMap);
						System.out.println("Твой набор героев:");
						for (Unit unit : gameBattle.getGamerUnitsArray()) {
							System.out.println(unit.getName());
						}
						return;
					}
				}
			} else if (unitsLowerNamePrices.containsKey(purchaseStringList.getFirst()) && !isNotNumeric(purchaseStringList.getLast())) {
				purchaseUnitCount = Integer.parseInt(purchaseStringList.getLast());
				purchaseUnitName = purchaseStringList.getFirst();
				if (gameBattle.getWallet() - sum - unitsLowerNamePrices.get(purchaseUnitName) *  purchaseUnitCount < 0) {
					System.out.println("У тебя не хватает денег на такую покупку, выбирай еще раз!");
					continue;
				}
				if (unitsCount + purchaseUnitCount > 9) {
					System.out.println("Больше 9 юнитов купить нельзя!");
					continue;
				}
				unitsCount += purchaseUnitCount;
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
			if (gameBattle.getWallet() - sum < min(unitsLowerNamePrices.values())) {
				System.out.println("У тебя осталось недостаточно денег для покупки героев.");
				System.out.println("Итого твой выбор:");
				for (String unitName : hasPurchasedStringMap.keySet()) {
					System.out.println(unitName + ": " + hasPurchasedStringMap.get(unitName));
				}
				System.out.println("Покупаем такой набор? Напиши Да или Нет:");
				String purchaseConfirmation = checkAnswer(gamer, gamer.input().toLowerCase().split(" ")[0], answerCheckList);
				if (Objects.equals(purchaseConfirmation, "да")) {
					gameBattle.setGamerUnits(hasPurchasedStringMap);
					System.out.println("Твой набор героев:");
					for (Unit unit : gameBattle.getGamerUnitsArray()) {
						System.out.println(unit.getName());
					}
					return;
				}
				else {
					hasPurchasedStringMap.clear();
					sum = 0;
					unitsCount = 0;
					System.out.println("Набор юнитов начинается заново!");
					printGamerUnitsArrayChoice();
					System.out.println(writeYourChoiceString);
				}
			}
		}
	}

	private void printGamerUnitsArrayChoice() {
		BattleMap battleMap = gameBattle.getBattleMap();
		HashMap<String, ArrayList<Float>> unitTypesPenalties = gameBattle.getUnitTypesPenalties();
		HashMap<String, ArrayList<String>> unitsTyping = gameBattle.getUnitsTyping();
		HashMap<String, ArrayList<Integer>> unitsSpecsMap = gameBattle.getUnitsSpecsMap();
		ArrayList<String> unitsTypes = gameBattle.getUnitsTypes();
		System.out.println("Для покупки у тебя есть на выбор 9 бойцов, внимательно изучи их характеристики и выбери, кого и сколько ты купишь:");
		String divider = "+------+-----------------+----------+-------+-----------------+--------+---------------+-----------+\n";
		String columnNames = "|   №  |     Название    | Здоровье | Атака | Дальность атаки | Защита |  Перемещение  | Стоимость |\n";
		String footColumnName = "|      |            " + ANSI_GREEN + unitsTypes.getFirst() + ANSI_RESET + "           |   " +
				formattedTypeOfUnitsColumnName(battleMap.getMapBasicFields(), unitTypesPenalties.get(unitsTypes.getFirst())) + "      |\n";
		String archerColumnName = "|      |           " + ANSI_RED + unitsTypes.get(1) + ANSI_RESET + "          |   " +
				formattedTypeOfUnitsColumnName(battleMap.getMapBasicFields(), unitTypesPenalties.get(unitsTypes.get(1))) + "      |\n";
		String horseColumnName = "|      |           " + ANSI_BLUE + unitsTypes.get(2) + ANSI_RESET + "         |   " +
				formattedTypeOfUnitsColumnName(battleMap.getMapBasicFields(), unitTypesPenalties.get(unitsTypes.get(2))) + "      |\n";
		String[] typeColumnNames = {footColumnName, archerColumnName, horseColumnName};
		System.out.print(divider);
		Object[] tempSpecs = new Object[8];
		int iter = 1;
		for (int i = 0; i < unitsTypes.size(); i++) {
			System.out.print(typeColumnNames[i]);
			System.out.print(divider);
			System.out.print(columnNames);
			System.out.print(divider);
			for (int j = 0; j < unitsTyping.get(unitsTypes.get(i)).size(); j++) {
				tempSpecs[0] = iter++;
				tempSpecs[1] = unitsTyping.get(unitsTypes.get(i)).get(j);
				for (int a = 2; a < 8; a++) {
					tempSpecs[a] = unitsSpecsMap.get(unitsTyping.get(unitsTypes.get(i)).get(j)).get(a - 2);
				}
				System.out.format(specsAlignmentByType(unitsTypes.get(i)), tempSpecs);
				System.out.print(divider);
			}
		}
	}

	private String specsAlignmentByType(String unitsType) {
		String color = gameBattle.colorByType(unitsType);
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
		String addString;
		int maxLength = 0;
		int thisLength;
		for (Unit unit : unitsList) {
			addString = "| " + unit.getMapImage() + ")" + unit.getName() + " - " + "Здоровье:" +
					ANSI_GREEN + unit.getHealthPoints() + ANSI_RESET + "; Защита:" + ANSI_BLUE + unit.getDefensePoints() + ANSI_RESET +
					"; Атака:" + ANSI_RED + unit.getAttackPoints() + ANSI_RESET + ";|";
			thisLength = (addString).length();
			if (thisLength > maxLength) {
				maxLength = thisLength;
			}
			result.add(addString);
			result.add("| Дальность атаки:" + unit.getAttackDistance() + "; Дальность перемещения:" + unit.getMovePoints() + " |");
		}
		for (int i = 0; i < result.size(); i++) {
			addString = result.get(i);
			if (i % 2 == 0) {
				result.set(i, addString + " ".repeat(maxLength - addString.length()));
			}
			else {
				result.set(i, addString + " ".repeat(maxLength - addString.length() - colorUnitStateFormat.length()));
			}
		}
		return result;
	}

	private void printCurrentMapAndState() {
		ArrayList<String> currentGamerUnitsStateLines = getCurrentStateLines();
		int minLines = Math.min(gameBattle.getBattleMap().getSizeY(), currentGamerUnitsStateLines.size());
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
		int a = gameBattle.getBattleMap().getSizeY() - currentGamerUnitsStateLines.size();
		if (a > 0) {
			for (int i = minLines; i < minLines + a; i++) {
				printBattleMapLine(i);
				System.out.println();
			}
			System.out.println("Y");
		} else if (a < 0) {
			System.out.print("Y");
			String emptyString = " ".repeat(gameBattle.getBattleMap().getSizeX() * 2 + 4);
			for (int i = minLines; i < minLines - a; i++) {
				if (i == minLines) {
					System.out.print(emptyString);
				}
				else {
					System.out.print(emptyString + " ");
				}
				System.out.println(currentGamerUnitsStateLines.get(i));
			}
		}
	}

	private ArrayList<String> getCurrentStateLines() {
		ArrayList<String> gamerUnitsArray = getCurrentUnitsState(gameBattle.getGamerUnitsArray());
		int maxFirstColumnLength = 0;
		for (String eachGamerUnitState: gamerUnitsArray) {
			if (eachGamerUnitState.length() > maxFirstColumnLength) {
				maxFirstColumnLength = eachGamerUnitState.length();
			}
		}
		maxFirstColumnLength -= colorUnitStateFormat.length();
		ArrayList<String> gamerUnitsStateLines = new ArrayList<>() {{
			add("Твои герои:");
			addAll(gamerUnitsArray);
		}};
		ArrayList<String> secondUnitsStateLines = new ArrayList<>() {{
			add("Герои твоего врага:");
			addAll(getCurrentUnitsState(gameBattle.getSecondGamerUnitsArray()));
		}};
		return getStrings(gamerUnitsStateLines, secondUnitsStateLines, maxFirstColumnLength);
	}

	private ArrayList<String> getStrings(ArrayList<String> gamerUnitsStateLines, ArrayList<String> secondUnitsStateLines, int maxFirstColumnLength) {
		ArrayList<String> currentGamerUnitsStateLines = new ArrayList<>();
		int minUnitsStateLines = Math.min(gamerUnitsStateLines.size(), secondUnitsStateLines.size());
		String adder = " ".repeat(57 - gamerUnitsStateLines.getFirst().length());
		currentGamerUnitsStateLines.add(gamerUnitsStateLines.getFirst() + adder + secondUnitsStateLines.getFirst());
		for (int i = 1; i < minUnitsStateLines; i++) {
			currentGamerUnitsStateLines.add(gamerUnitsStateLines.get(i) + secondUnitsStateLines.get(i));
		}
		if (gamerUnitsStateLines.size() > secondUnitsStateLines.size()) {
			for (int i = minUnitsStateLines; i < gamerUnitsStateLines.size(); i++) {
				currentGamerUnitsStateLines.add(gamerUnitsStateLines.get(i));
			}
		}
		else if (gamerUnitsStateLines.size() < secondUnitsStateLines.size()) {
			for (int i = minUnitsStateLines; i < secondUnitsStateLines.size(); i++) {
				adder = " ".repeat(maxFirstColumnLength);
				currentGamerUnitsStateLines.add(adder + secondUnitsStateLines.get(i));
			}
		}
		return currentGamerUnitsStateLines;
	}

	//descriptionList must be like:
	//get(0) - action type: attack double - 0, attack - 1, move - 2;
	//get(1) - actionHero index in secondGamerUnitsArray;
	//get(2) - action description: if attack - attackedHero index in
	//gamerUnitsArray, else - xCoord
	//get(3) - action description: if attack - attackPoints for first enemy (0 if he is killed), else - yCoord
	//get(4) - action description: if attack - secondAttackedHero index in gamerUnitsArray, else -
	//moving type: common - 0, by portal - 1
	//get(5) - action description: if attack - attackPoints for second enemy (0 if he is killed), else - nothing
	private String parseBotMoveDescription(ArrayList<String> attackedHeroNames, ArrayList<Integer> descriptionList) {
		String actionHeroName = gameBattle.getSecondGamerUnitsArray().get(descriptionList.get(1)).getName();
		String returnString, attackedHeroName, secondAttackedHeroName;
		switch (descriptionList.get(0)) {
			case (0):
				attackedHeroName = attackedHeroNames.get(descriptionList.get(2));
				secondAttackedHeroName = attackedHeroNames.get(descriptionList.get(4));
				if (Objects.equals(descriptionList.get(2), descriptionList.get(4))) {
					if (descriptionList.get(3) != 0) {
					returnString = actionHeroName + " два раза атаковал твоего героя " +
							attackedHeroName + " и нанес " + (descriptionList.get(3) * 2) + " урона.";
					} else {
						returnString = actionHeroName + " атаковал твоего героя " +
								attackedHeroName + " и убил его второй атакой!";
					}
					return returnString;
				} else {
					if (descriptionList.get(3) != 0) {
						returnString = actionHeroName + " атаковал твоего героя " +
								attackedHeroName + " и нанес " + descriptionList.get(3) + " урона.";
					} else {
						returnString = actionHeroName + " убил твоего героя " + attackedHeroName;
					}
					if (descriptionList.get(5) != 0) {
						returnString += "\nА еще..." + actionHeroName + " атаковал твоего героя " +
								secondAttackedHeroName + " и нанес " + descriptionList.get(3) + " урона.";
					} else {
						returnString += "\nА еще..." + actionHeroName + " убил твоего героя " + secondAttackedHeroName + "!";
					}
				}
				return returnString;
			case (1):
				attackedHeroName = attackedHeroNames.get(descriptionList.get(2));
				if (descriptionList.get(3) != 0) {
					returnString = actionHeroName + " атаковал твоего героя " +
						attackedHeroName + " и нанес " + descriptionList.get(3) + " урона.";
				}
				else {
					returnString = actionHeroName + " убил твоего героя " + attackedHeroName;
				}
				return returnString;
			case (2):
				String portalMoved = "";
				if (descriptionList.get(4) != 0) {
					portalMoved = "\nC помощью портала, созданного черномором!";
				}
				returnString = actionHeroName + " переместился на поле (" +
						(descriptionList.get(2) + 1) + "; " + (descriptionList.get(3) + 1) + ")." + portalMoved;
				return returnString;
			case (3):
				returnString = "Черномор создал портал с началом в поле (" + (descriptionList.get(2) + 1) + ";" +
						(descriptionList.get(3) + 1) + ") и концом в поле (" + (descriptionList.get(4) + 1) + ";" +
						(descriptionList.get(5) + 1) + ").";
				return returnString;
			default:
				return "Бот что-то сделал...";
		}
	}

	private void printBattleMapLine(int i) {
		String[] battleLine;
		System.out.format("%d ", i + 1);
		if (i < 9) {
			System.out.print(" ");
		}
		battleLine = gameBattle.getBattleMap().getBattleMapLine(i);
		for (int j = 0; j < gameBattle.getBattleMap().getSizeX(); j++) {
			System.out.print(battleLine[j] + " ");
		}
	}

	private String removeAscii(String str) {
		if (str.length() >= 5) {
			return str.substring(5, 6);
		}
		return str;
	}

	public void gaming(){
		int xMoveCoord, yMoveCoord, checkRes,
				inputActionNum;
		String inputHero, inputAction, inputAnswer, secondGamerActionString;
		ArrayList<String> heroCheckList, attackableUnitsIndexList, inputCoords;
		ArrayList<String> actionCheck = new ArrayList<>() {{
			for (int i = 1; i < 3; i++) {
				add(String.format("%d", i));
			}
		}};
		printExampleOfGame();
		while (!gameBattle.endOfGame()) {
			inputCoords = new ArrayList<>();
			heroCheckList = new ArrayList<>() {{
				for (int i = 0; i < gameBattle.getGamerUnitsArray().size(); i++) {
					add(removeAscii(gameBattle.getGamerUnitsArray().get(i).getMapImage()));
				}
			}};
			System.out.println("Порталы: " + gameBattle.getPortalsArray());
			System.out.println("Кто умеет делать двойную атаку:");
			for (String eachUnitName: gameBattle.getSecondGamer().getDoubleAttackersIndexList()) {
				System.out.println(eachUnitName);
			}
			System.out.println("Карта боевых действий:");
			printCurrentMapAndState();
			System.out.print("Выбери героя для хода по номеру:");
			inputHero = checkAnswer(gamer, gamer.input().split(" ")[0], heroCheckList);
			System.out.print("Выбери действие: Передвижение - 1, атака - 2:");
			inputAction = checkAnswer(gamer, gamer.input().split(" ")[0], actionCheck);
			inputActionNum = Integer.parseInt(inputAction);
			if (inputActionNum == 2) {
				ArrayList<Unit> attackableUnitsList = gameBattle.checkHeroAttackableList(false, inputHero);
				if (attackableUnitsList.isEmpty()) {
					System.out.println("Герой " + gameBattle.getUnitByMapImage(inputHero, false).getName() + " сейчас никого атаковать не может!");
					continue;
				}
				else if (attackableUnitsList.size() == 1) {
					System.out.println("Атаковать можешь только одного: " + attackableUnitsList.getFirst().getMapImage() +
							") " + attackableUnitsList.getFirst().getName() + "\nАтакуешь? Напиши да/нет");
					inputAnswer = checkAnswer(gamer, gamer.input().toLowerCase().split(" ")[0], answerCheckList);
					if (Objects.equals(inputAnswer, "да")) {
						gameBattle.makeAttack(false, inputHero, attackableUnitsList.getFirst().getMapImage());
					}
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
					inputAnswer = checkAnswer(gamer, gamer.input().toLowerCase().split(" ")[0], attackableUnitsIndexList);
					gameBattle.makeAttack(false, inputHero, inputAnswer);
				}
			}
			else if (inputActionNum == 1) {
				System.out.print("Напиши координаты, куда хочешь сходить - x y:");
				inputAnswer = gamer.input();
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
						checkRes = gameBattle.checkHeroMoveAbility(inputHero, xMoveCoord, yMoveCoord);
						if (checkRes == 1) {
							System.out.println("Герои могут делать ход только в обычную клетку!");
						}
						else if (checkRes == 2) {
							System.out.println("Герой " + gameBattle.getUnitByMapImage(inputHero, false).getName() + " не может пойти в эту клетку!");
						}
						else if (checkRes == 3) {
							System.out.println("Такой клетки нет!!!");
						}
						else if (checkRes == 4) {
							System.out.println("Эта клетка принадлежит порталу, а этот портал занят!");
						}
						else {
							boolean portalMoving = gameBattle.makeMove(inputHero, xMoveCoord, yMoveCoord);
							if (portalMoving) {
								System.out.println("Твой герой попал в портал!");
							}
							break;
						}
					}
					inputAnswer = gamer.input();
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
			if (gameBattle.endOfGame()) {
				System.out.println(ANSI_GREEN + "Ты выиграл!" + ANSI_RESET);
				return;
			}
			System.out.println("Ход бота...");
			ArrayList <String> gamerUnitsNames = new ArrayList<>() {{
				for (Unit gamerUnit: gameBattle.getGamerUnitsArray()) {
					add(gamerUnit.getName());
				}
			}};
			secondGamerActionString = parseBotMoveDescription(gamerUnitsNames, gameBattle.secondGamerMove());
			System.out.println("Результат хода твоего противника:");
			System.out.println(secondGamerActionString);
		}
		System.out.println(ANSI_RED + "Ты проиграл!" + ANSI_RESET);
	}
}
