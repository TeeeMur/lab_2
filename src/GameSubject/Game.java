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

	public Game(int difficulty) {
		battleMap = new BattleMap(15, 15, difficulty);
		secondGamer = new Bot(secondGamerUnitsArray, unitsTyping,
				unitsSpecsMap, unitTypesPenalties, battleMap.getBasicFields(), difficulty);
		fillWallet(battleMap.getSizeX(), battleMap.getSizeY(), difficulty);
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

	public String colorByType(int unitsTyping) {
		return switch (unitsTyping) {
			case (0) -> ANSI_GREEN;
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

	public Unit getUnitByMapImage(String mapImage) {
		mapImage = removeAscii(mapImage);
		for (Unit eachUnit: gamerUnitsArray) {
			if (Objects.equals(removeAscii(eachUnit.getMapImage()), mapImage)) {
				return eachUnit;
			}
		}
		return null;
	}

	public boolean endOfGame() {
		return gamerUnitsArray.isEmpty() || secondGamerUnitsArray.isEmpty();
	}

	private String removeAscii(String str) {
		if (str.length() >= 5) {
			return str.substring(5, 6);
		}
		return str;
	}

	public void makeMove(String inputHeroNum, int xCoord, int yCoord) {
		Unit actionUnit = getUnitByMapImage(inputHeroNum);
		replaceUnitInMap(actionUnit, xCoord, yCoord);
	}

	public int checkHeroMoveAbility(String inputHeroNum, int xCoord, int yCoord) {
		Unit moveHero = getUnitByMapImage(inputHeroNum);
		if (!Objects.equals(battleMap.getFieldByPosition(xCoord, yCoord), battleMap.getBasicFields().getFirst())) {
			return 1;
		} else if (!moveHero.canMove(xCoord, yCoord, battleMap)) {
			return 2;
		} else {
			return 0;
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
		Unit attackUnit = getUnitByMapImage(inputHeroNum);
		ArrayList<Unit> returnList = new ArrayList<>();
		for (Unit attackableUnit: secondGamerUnitsArray) {
			assert attackUnit != null;
			if (attackUnit.canAttack(attackableUnit)) {
				returnList.add(attackableUnit);
			}
		}
		return returnList;
	}

	public void makeAttack(String attackHeroIndex, String attackableHeroIndex) {
		Unit attackableUnit = getUnitByMapImage(removeAscii(attackableHeroIndex));
		Unit attackUnit = getUnitByMapImage(removeAscii(attackHeroIndex));
		attackableUnit.getDamage(attackUnit.getAttackPoints());
		if (attackableUnit.checkDeath()) {
			battleMap.placeSmth(battleMap.getBasicFields().getFirst(),attackableUnit.getxCoord(), attackableUnit.getyCoord());
			secondGamerUnitsArray.remove(attackableUnit);
		}
	}

	public String secondGamerMove() throws InterruptedException {
		int secondGamerMove, secondGamerAct,
				secondGamerFirstArg, secondGamerSecondArg, secondGamerThirdArg;
		secondGamerMove = secondGamer.botMove(secondGamerUnitsArray, gamerUnitsArray, battleMap);
		secondGamerAct = secondGamerMove / (16 * 16 * 16);
		secondGamerFirstArg = (secondGamerMove % (16 * 16 * 16)) / (16 * 16);
		secondGamerSecondArg = secondGamerMove % 256 / 16;
		secondGamerThirdArg = secondGamerMove % 16;
		String secondGamerActionString;
		switch (secondGamerAct) {
			case (1):
				Unit attackableUnit = gamerUnitsArray.get(secondGamerSecondArg);
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
				return secondGamerActionString;
			case (2):
				Unit selectedUnit = secondGamerUnitsArray.get(secondGamerFirstArg);
				replaceUnitInMap(selectedUnit, secondGamerSecondArg, secondGamerThirdArg);
				secondGamerActionString = selectedUnit.getName() + " переместился на поле (" +
						(secondGamerSecondArg + 1) + "; " + (secondGamerThirdArg + 1) + ").";
				return secondGamerActionString;
			default:
				return "Бот не сумел сходить...";
		}
	}
}
