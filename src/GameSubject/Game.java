/*
	ИГРА РАССЧИТАНА МАКСИМУМ НА 9 ГЕРОЕВ С КАЖДОЙ СТОРОНЫ
 */

package GameSubject;

import BattlePlace.BattleMap;
import Bots.Bot;
import Units.Unit;

import java.util.*;

public class Game {

	public static final String CONSOLE_FLUSH = "\033[H\033[2J";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_CYAN = "\u001B[36m";

	private int wallet;
	private final BattleMap battleMap;
	private final Bot secondGamer;

	private final ArrayList<Unit> gamerUnitsArray = new ArrayList<>();
	private final ArrayList<Unit> secondGamerUnitsArray = new ArrayList<>();

	private final ArrayList<ArrayList<String>> unitsTyping = new ArrayList<>() {{
		add(new ArrayList<>(Arrays.asList("Мечник", "Копьеносец", "Топорщик")));
		add(new ArrayList<>(Arrays.asList("Тяжелый лучник", "Легкий лучник", "Арбалетчик")));
		add(new ArrayList<>(Arrays.asList("Рыцарь", "Кирасир", "Конный лучник")));
	}};
	private final HashMap<String, ArrayList<Integer>> unitsSpecsMap = new HashMap<>() {{
		//Foot units
		put(unitsTyping.getFirst().getFirst(), new ArrayList<>(Arrays.asList(50, 5, 1, 8, 3, 10)));
		put(unitsTyping.getFirst().get(1), new ArrayList<>(Arrays.asList(30, 3, 3, 3, 3, 15)));
		put(unitsTyping.getFirst().get(2), new ArrayList<>(Arrays.asList(45, 9, 1, 3, 4, 20)));
		//Bow units
		put(unitsTyping.get(1).getFirst(), new ArrayList<>(Arrays.asList(30, 6, 5, 8, 2, 15)));
		put(unitsTyping.get(1).get(1), new ArrayList<>(Arrays.asList(25, 3, 3, 4, 4, 19)));
		put(unitsTyping.get(1).get(2), new ArrayList<>(Arrays.asList(40, 7, 6, 3, 2, 23)));
		//Horse units
		put(unitsTyping.get(2).getFirst(), new ArrayList<>(Arrays.asList(30, 5, 1, 3, 6, 20)));
		put(unitsTyping.get(2).get(1), new ArrayList<>(Arrays.asList(50, 2, 1, 7, 5, 23)));
		put(unitsTyping.get(2).get(2), new ArrayList<>(Arrays.asList(25, 3, 3, 2, 5, 25)));
	}};

	private final ArrayList<ArrayList<Float>> unitTypesPenalties = new ArrayList<>() {{
		add(new ArrayList<>(Arrays.asList(1f, 1.5f, 2f, 1.2f)));
		add(new ArrayList<>(Arrays.asList(1f, 1.5f, 2f, 1.2f)));
		add(new ArrayList<>(Arrays.asList(1f, 2.2f, 1.2f, 1.5f)));
	}};

	public Game(int gameDifficulty) {
		battleMap = new BattleMap(15, 15, gameDifficulty);
		secondGamer = new Bot(secondGamerUnitsArray, unitsTyping,
				unitsSpecsMap, unitTypesPenalties, battleMap.getBasicFields(), gameDifficulty);
		fillWallet(battleMap.getSizeX(), battleMap.getSizeY(), gameDifficulty);
		placeUnitsIntoMap(secondGamerUnitsArray, battleMap.getSizeY() - 1);
	}
	private static boolean isNotNumeric(String str) {
		try {
			Integer.parseInt(str);
			return false;
		} catch (NumberFormatException e) {
			return true;
		}
	}

	private void fillWallet(int sizeX, int sizeY, int difficulty) {
		wallet = 75;
		for (int i = 0; i < 6 - difficulty; i++) {
			wallet += (sizeX + sizeY) / 2;
		}
	}

	public void setGamerUnits(HashMap<String, Integer> purchasedUnitsMap) {
		String tempUnitName;
		String tempPurchasedName;
		String unitMapImage;
		int order = 0;
		int a;
		for (String purchasedUnitName : purchasedUnitsMap.keySet()) {
			int countToBuy = purchasedUnitsMap.get(purchasedUnitName);
			for (Integer j = 0; j < countToBuy; j++) {
				tempPurchasedName = purchasedUnitName.substring(0, 1).toUpperCase() + purchasedUnitName.substring(1);
				for (a = 0; a < unitsTyping.size(); a++) {
					if (unitsTyping.get(a).contains(tempPurchasedName)) {
						break;
					}
				}
				tempUnitName = colorByType(a) + tempPurchasedName + " " + (j + 1) + ANSI_RESET;
				order++;
				unitMapImage = colorByType(a) + order + ANSI_RESET;
				gamerUnitsArray.add(new Unit(unitMapImage, tempUnitName, unitsSpecsMap.get(tempPurchasedName), unitTypesPenalties.get(a),
						battleMap.getBasicFields()));
			}
		}
		placeUnitsIntoMap(gamerUnitsArray, 0);
	}

	public int getWallet() {
		return wallet;
	}

	private String colorByType(int unitsTyping) {
		return switch (unitsTyping) {
			case (0) -> ANSI_CYAN;
			case (1) -> ANSI_RED;
			case (2) -> ANSI_BLUE;
			default -> ANSI_RESET;
		};
	}

	private void placeUnitsIntoMap(ArrayList<Unit> unitsToPlace, int row) {
		Unit eachUnitToPlace;
		int xCoord;
		ArrayList<Integer> placeCoords = new ArrayList<>() {{
			for (int i = 7; i >= 0; i--) {
				add(i);
			}
			int j = 0;
			for (int i = 1; i < 14; i += 2) {
				add(i, 8 + j);
				j++;
			}
		}};
		for (int i = 0; i < unitsToPlace.size(); i++) {
			eachUnitToPlace = unitsToPlace.get(i);
			xCoord = placeCoords.get(i);
			battleMap.placeSmth(eachUnitToPlace.getMapImage(), xCoord, row);
			eachUnitToPlace.move(xCoord, row);
		}
	}

	private void replaceUnitInMap(Unit moveUnit, int xMoveCoord, int yMoveCoord) {
		battleMap.placeSmth(battleMap.getBasicFields().getFirst(), moveUnit.getxCoord(), moveUnit.getyCoord());
		battleMap.placeSmth(moveUnit.getMapImage(), xMoveCoord, yMoveCoord);
		moveUnit.move(xMoveCoord, yMoveCoord);
	}

	public void game() throws InterruptedException {
		String inputHero, inputAction, inputAnswer;
		String secondGamerActionString;
		boolean canAttackAnyone;
		ArrayList<String> inputCoords = new ArrayList<>();
		ArrayList<String> attackableUnitsIndexListToString = new ArrayList<>();
		ArrayList<Integer> attackableUnitsIndexList = new ArrayList<>();
		Unit selectedUnit, attackableUnit;
		int inputHeroNum, xMoveCoord, yMoveCoord, secondGamerMove, secondGamerAct,
				secondGamerFirstArg, secondGamerSecondArg, secondGamerThirdArg;
		System.out.println("Вперед, игра началась!");
		ArrayList<String> heroCheck = new ArrayList<>() {{
			for (int i = 0; i < gamerUnitsArray.size(); i++) {
				add(String.format("%d", i + 1));
			}
		}};
		ArrayList<String> actionCheck = new ArrayList<>() {{
			for (int i = 0; i < 2; i++) {
				add(String.format("%d", i + 1));
			}
		}};
		ArrayList<String> answerCheck = new ArrayList<>() {{
			add("да");
			add("нет");
		}};
		placeUnitsIntoMap(gamerUnitsArray, 0);
		placeUnitsIntoMap(secondGamerUnitsArray, battleMap.getSizeY() - 1);
		while (!gamerUnitsArray.isEmpty() && !secondGamerUnitsArray.isEmpty()) {
			inputCoords.clear();
			canAttackAnyone = false;
			printCurrentMapAndState();
			System.out.println("Выбери героя для хода по номеру:");
			inputHero = checkAnswer(new Scanner(System.in).nextLine(), heroCheck);
			if (isNotNumeric(inputHero)) {
				System.out.println("Ты, похоже, написал не номер героя...");
				continue;
			}
			else {
				inputHeroNum = (Integer.parseInt(inputHero));
				if ((inputHeroNum > gamerUnitsArray.size()) || (inputHeroNum <= 0)) {
					System.out.println("Ты, похоже, написал не тот номер...");
					continue;
				}
				else {
					selectedUnit = gamerUnitsArray.get(inputHeroNum - 1);
				}
			}
			System.out.println("Выбери действие: Передвижение - 1, атака - 2:");
			inputAction = checkAnswer(new Scanner(System.in).nextLine(), actionCheck);
			if (Objects.equals(inputAction, "2")) {
				attackableUnitsIndexList.clear();
				attackableUnitsIndexListToString.clear();
				for (int i = 0; i < secondGamerUnitsArray.size(); i++) {
					if (selectedUnit.canAttack(secondGamerUnitsArray.get(i))) {
						attackableUnitsIndexList.add(i);
						canAttackAnyone = true;
					}
				}
				if (!canAttackAnyone) {
					System.out.println("Твой герой " + gamerUnitsArray.get(Integer.parseInt(inputHero) - 1).getName() +
							" не может никого атаковать, ни до кого не достает!");
					continue;
				}
				else {
					if (attackableUnitsIndexList.size() == 1) {
						System.out.println("Атаковать можешь только одного: " + secondGamerUnitsArray.get(attackableUnitsIndexList.getFirst()).getName() +
								"\nАтакуешь?");
						inputAnswer = checkAnswer(new Scanner(System.in).nextLine().toLowerCase(), answerCheck);
						if (Objects.equals(inputAnswer, "да")) {
							attackableUnit = secondGamerUnitsArray.get(attackableUnitsIndexList.getFirst());
							attackableUnit.getDamage(selectedUnit.getAttackPoints());
							if (attackableUnit.checkDeath()) {
								battleMap.placeSmth(battleMap.getBasicFields().getFirst(), attackableUnit.getxCoord(), attackableUnit.getyCoord());
								secondGamerUnitsArray.remove(attackableUnit);
							}
						}
						else continue;
					}
					else {
						System.out.println("Aтаковать можешь таких героев врага:");
						for (Integer integer : attackableUnitsIndexList) {
							System.out.println(secondGamerUnitsArray.get(integer).getMapImage() + ")" + secondGamerUnitsArray.get(integer).getName());
						}
						System.out.println("Кого атакуешь? Напиши номер:");
						for (Integer i: attackableUnitsIndexList) {
								attackableUnitsIndexListToString.add(String.format("%d", i + 1  ));
							}
						inputAnswer = checkAnswer(new Scanner(System.in).nextLine().toLowerCase(), attackableUnitsIndexListToString);
						attackableUnit = secondGamerUnitsArray.get(Integer.parseInt(inputAnswer) - 1);
						attackableUnit.getDamage(selectedUnit.getAttackPoints());
						if (attackableUnit.checkDeath()) {
							battleMap.placeSmth(battleMap.getBasicFields().getFirst(),attackableUnit.getxCoord(), attackableUnit.getyCoord());
							secondGamerUnitsArray.remove(attackableUnit);
						}
					}
				}
			} else if (Objects.equals(inputAction, "1")) {
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
						break;
					}
					inputAnswer = new Scanner(System.in).nextLine();
					inputCoords.clear();
					inputCoords.addAll(Arrays.asList(inputAnswer.split(" ")));
				}
				if (!Objects.equals(battleMap.getFieldByPosition(xMoveCoord, yMoveCoord), battleMap.getBasicFields().getFirst())) {
					System.out.println("Герои могут делать ход только в обычную клетку!");
					continue;
				}
				else if (!selectedUnit.canMove(xMoveCoord, yMoveCoord, battleMap)) {
					System.out.println("Герой " + selectedUnit.getName() + " не может пойти в эту клетку!");
					continue;
				}
				else {
					replaceUnitInMap(selectedUnit, xMoveCoord, yMoveCoord);
				}
			}
			else {
				System.out.println("Похоже, ты что-то не так написал!");
				continue;
			}
			System.out.println("Результат твоего хода:");
			if (secondGamerUnitsArray.isEmpty()) {
				System.out.println(ANSI_GREEN + "Ты выиграл!" + ANSI_RESET);
				System.exit(0);
			}
			printCurrentMapAndState();
			secondGamerMove = secondGamer.botMove(secondGamerUnitsArray, gamerUnitsArray, battleMap);
			secondGamerAct = secondGamerMove / (16 * 16 * 16);
			secondGamerFirstArg = (secondGamerMove % (16 * 16 * 16)) / (16 * 16);
			secondGamerSecondArg = secondGamerMove % 256 / 16;
			secondGamerThirdArg = secondGamerMove % 16;
			switch (secondGamerAct) {
				case(1):
					attackableUnit = gamerUnitsArray.get(secondGamerSecondArg);
					attackableUnit.getDamage(
							secondGamerUnitsArray.get(secondGamerFirstArg).getAttackPoints());
					secondGamerActionString = secondGamerUnitsArray.get(secondGamerFirstArg).getName() + " атаковал твоего героя " +
							attackableUnit.getName() + " и нанес " + attackableUnit.getAttackPoints() + " урона.";
					if (attackableUnit.checkDeath()) {
						battleMap.placeSmth(battleMap.getBasicFields().getFirst(), attackableUnit.getxCoord(), attackableUnit.getyCoord());
						gamerUnitsArray.remove(attackableUnit);
						secondGamerActionString = secondGamerUnitsArray.get(secondGamerFirstArg).getName() + " атаковал твоего героя " +
							attackableUnit.getName() + " и убил его ";
					}
					break;
				case(2):
					selectedUnit = secondGamerUnitsArray.get(secondGamerFirstArg);
					replaceUnitInMap(selectedUnit, secondGamerSecondArg, secondGamerThirdArg);
					secondGamerActionString = selectedUnit.getName() + " переместился на поле (" +
							secondGamerSecondArg + "; " + secondGamerThirdArg + ").";
					break;
				default:
					continue;
			}
			System.out.println("Результат хода твоего противника:");
			System.out.println(secondGamerActionString);
			printCurrentMapAndState();
		}
		System.out.println("Ты проиграл((");
	}

	public boolean endOfGame() {
		return gamerUnitsArray.isEmpty() || secondGamerUnitsArray.isEmpty();
	}

	public boolean makeMove(String inputHeroNum, int moveType, int firstArg, int secondArg) {
		Unit actionUnit = null;
		switch (moveType) {
			case (1):
				for (Unit moveUnit: gamerUnitsArray) {
					if (Objects.equals(inputHeroNum, moveUnit.getMapImage())) {
						actionUnit = moveUnit;
						break;
					}
				}
				if (Objects.isNull(actionUnit)) {
					return false;
				}

				break;
			case(2):
				break;
			default:
				return false;
		}
	}

	public BattleMap getBattleMap() {
		return battleMap;
	}

	public ArrayList<ArrayList<Float>> getUnitTypesPenalties() {
		return unitTypesPenalties;
	}

	public ArrayList<ArrayList<String>> getUnitsTyping() {
		return unitsTyping;
	}

	public HashMap<String, ArrayList<Integer>> getUnitsSpecsMap() {
		return unitsSpecsMap;
	}

	public ArrayList<Unit> getGamerUnitsArray() {
		return gamerUnitsArray;
	}

	public ArrayList<Unit> getSecondGamerUnitsArray() { return secondGamerUnitsArray; }

	public ArrayList<Unit> checkHeroAttackableList(String inputHeroNum) {
		Unit attackUnit = null;
		ArrayList<Unit> returnList = new ArrayList<>();
		for (Unit eachUnit: gamerUnitsArray) {
			if (Objects.equals(ANSI_RESET + eachUnit.getMapImage(), inputHeroNum)) {
				attackUnit = eachUnit;
			}
		}
		for (Unit attackableUnit: secondGamerUnitsArray) {
			assert attackUnit != null;
			if (attackUnit.canAttack(attackableUnit)) {
				returnList.add(attackableUnit);
			}
		}
		return returnList;
	}

	public boolean checkHeroMoveAbility(String inputHeroNum, int xCoord, int yCoord) {

	}
}
