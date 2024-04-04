/*
	ИГРА РАССЧИТАНА МАКСИМУМ НА 9 ГЕРОЕВ С КАЖДОЙ СТОРОНЫ
 */

package GameSubject;

import BattlePlace.BattleMap;
import Bots.Bot;
import Units.Unit;

import java.util.*;

public class Game {

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
	private final ArrayList<String> portalsColoringArray = new ArrayList<>() {{
		add(ANSI_YELLOW);
		add(ANSI_GREEN);
		add(ANSI_BLUE);
		add(ANSI_CYAN);
	}};
	private final ArrayList<ArrayList<Integer>> portalsArray = new ArrayList<>();

	private final ArrayList<ArrayList<String>> unitsTyping = new ArrayList<>() {{
		add(new ArrayList<>(Arrays.asList("Мечник", "Копьеносец", "Топорщик")));
		add(new ArrayList<>(Arrays.asList("Тяжелый лучник", "Легкий лучник", "Арбалетчик")));
		add(new ArrayList<>(Arrays.asList("Рыцарь", "Кирасир", "Конный лучник")));
	}};
	private final HashMap<String, ArrayList<Integer>> unitsSpecsMap = new HashMap<>() {{
		//Foot units
		put(unitsTyping.getFirst().getFirst(), new ArrayList<>(Arrays.asList(50, 5, 1, 8, 3, 10)));
		put(unitsTyping.getFirst().get(1), new ArrayList<>(Arrays.asList(100, 100, 100, 100, 100, 5)));
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
				tempPurchasedName = purchasedUnitName.substring(0, 1).toUpperCase() +
						purchasedUnitName.substring(1);
				for (a = 0; a < unitsTyping.size(); a++) {
					if (unitsTyping.get(a).contains(tempPurchasedName)) {
						break;
					}
				}
				tempUnitName = colorByType(a) + tempPurchasedName + " " + (j + 1) + ANSI_RESET;
				order++;
				unitMapImage = colorByType(a) + order + ANSI_RESET;
				gamerUnitsArray.add(new Unit(unitMapImage, tempUnitName, unitsSpecsMap.get(tempPurchasedName),
						unitTypesPenalties.get(a),
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
		String placeField = battleMap.getBasicFields().getFirst();
		int isPortal = fieldIsPortal(moveUnit.getxCoord(), moveUnit.getyCoord());
		if (isPortal != -1) {
			placeField = portalsColoringArray.get(isPortal) + battleMap.getBasicFields().getFirst() + ANSI_RESET;
		}
		battleMap.placeSmth(placeField, moveUnit.getxCoord(), moveUnit.getyCoord());
		battleMap.placeSmth(moveUnit.getMapImage(), xMoveCoord, yMoveCoord);
		moveUnit.move(xMoveCoord, yMoveCoord);
	}

	//0 - in gamerUnitsArray, 1 - in secondGamerUnitsArray
	public Unit getUnitByMapImage(String mapImage, boolean side) {
		ArrayList<Unit> returnUnitsArray;
		if (!side) {
			returnUnitsArray = gamerUnitsArray;
		}
		else {
			returnUnitsArray = secondGamerUnitsArray;
		}
		mapImage = removeAscii(mapImage);
		for (Unit eachUnit: returnUnitsArray) {
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

	//false - common, true - with portal
	public boolean makeMove(String inputHeroNum, int xCoord, int yCoord) {
		Unit actionUnit = getUnitByMapImage(inputHeroNum, false);
		boolean portalMoving = false;
		for (ArrayList<Integer> portal : portalsArray) {
			if (Objects.equals(portal.getFirst(), xCoord) &&
					Objects.equals(portal.get(1), yCoord)) {
				xCoord = portal.get(2);
				yCoord = portal.get(3);
				portalMoving = true;
				break;
			} else if (Objects.equals(portal.get(2), xCoord) &&
					Objects.equals(portal.get(3), yCoord)) {
				xCoord = portal.getFirst();
				yCoord = portal.get(1);
				portalMoving = true;
				break;
			}
		}
		replaceUnitInMap(actionUnit, xCoord, yCoord);
		return portalMoving;
	}

	public int checkHeroMoveAbility(String inputHeroNum, int xCoord, int yCoord) {
		Unit moveHero = getUnitByMapImage(inputHeroNum, false);
		if (xCoord < 0 || xCoord >= 15 || yCoord < 0 || yCoord >= 15) {
			return 3;
		} else if (!Objects.equals(removeAscii(battleMap.getFieldByPosition(xCoord, yCoord)), battleMap.getBasicFields().getFirst())) {
			return 1;
		} else if (!moveHero.canMove(xCoord, yCoord, battleMap)) {
			return 2;
		} else {
			for (ArrayList<Integer> eachPortal: portalsArray) {
				if (eachPortal.getFirst() == xCoord && eachPortal.get(1) == yCoord &&
						!Objects.equals(removeAscii(battleMap.getFieldByPosition(eachPortal.get(2), eachPortal.get(3))), battleMap.getBasicFields().getFirst()) ) {
					return 4;
				}
				if (eachPortal.get(2) == xCoord && eachPortal.get(3) == yCoord &&
						!Objects.equals(removeAscii(battleMap.getFieldByPosition(eachPortal.getFirst(), eachPortal.get(1))), battleMap.getBasicFields().getFirst())) {
					return 4;
				}
			}
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


	// side: false - gamer attacks bot, true - bot attacks gamer
	public ArrayList<Unit> checkHeroAttackableList(boolean side, String inputHeroNum) {
		ArrayList<Unit> unitsArray;
		if (side) {
			unitsArray = gamerUnitsArray;
		}
		else {
			unitsArray = secondGamerUnitsArray;
		}
		Unit attackUnit = getUnitByMapImage(inputHeroNum, side);
		ArrayList<Unit> returnList = new ArrayList<>();
		for (Unit attackableUnit: unitsArray) {
			assert attackUnit != null;
			if (attackUnit.canAttack(attackableUnit)) {
				returnList.add(attackableUnit);
			}
		}
		return returnList;
	}

	// side: false - gamer attacks bot, true - bot attacks gamer
	public void makeAttack(boolean side, String attackHeroImage, String attackableHeroImage) {
		Unit attackableUnit = getUnitByMapImage(removeAscii(attackableHeroImage), !side);
		Unit attackUnit = getUnitByMapImage(removeAscii(attackHeroImage), side);
		attackableUnit.getDamage(attackUnit.getAttackPoints());
		if (attackableUnit.checkDeath()) {
			if (!side && Objects.equals(removeAscii(attackableUnit.getName()), "Черномор")) {
				portalsArray.clear();
			}
			battleMap.placeSmth(battleMap.getBasicFields().getFirst(), attackableUnit.getxCoord(),
					attackableUnit.getyCoord());
			secondGamerUnitsArray.remove(attackableUnit);
		}

	}

	// number:  2AB945
	// indexes: 543210
	// Scheme: 0 digit - type of action (double attack - 0/ attack - 1/ movement - 2/ chernomor portal creating - 3),
	//DOUBLE ATTACK:
	//	1st digit - index of the ATTACKING hero of the bot,
	//	2nd digit is the index of the ATTACKED hero of the opponent bot,
	//	3rd digit is attackPoints for first enemy(0 if he is killed)
	//	4th digit is the index of the SECOND ATTACKED hero of the opponent bot
	//	5th digit is attackPoints for second enemy (0 if he is killed)
	//ATTACK:
	//	1st digit - index of the ATTACKING hero of the bot,
	//	2nd digit is the index of the ATTACKED hero of the opponent bot,
	//	3rd digit is attackPoints for first enemy(0 if he is killed)
	//	other digits - nothing
	//MOVING:
	//	1st digit is the index of the hero that is being MOVED,
	//	2nd digit is the X coordinate
	//	3rd digit is the Y coordinate
	//	4th digit is marker for portal moving
	//CHERNOMOR PORTAL CREATING:
	//	1st digit - number of chernomor hero of the bot
	//	2nd digit - xStartCoord
	//	3rd digit - yStartCoord
	//	4th digit - xEndCoord
	//	5th digit - yEndCoord
	//	the whole number is in the 16-digit number system

	public ArrayList<Integer> secondGamerMove() {
		int secondGamerMove = secondGamer.botMove(secondGamerUnitsArray, gamerUnitsArray, battleMap, portalsArray);
		while (secondGamerMove == -1) {
			secondGamerMove = secondGamer.botMove(secondGamerUnitsArray, gamerUnitsArray, battleMap, portalsArray);
		}
		int finalSecondGamerMove = secondGamerMove;
		ArrayList<Integer> moveParams = new ArrayList<>(){{
			for (int i = 0; i < 6; i++) {
				add((finalSecondGamerMove / ((int)Math.pow(16, i)) % 16));
			}
		}};
		ArrayList<Integer> returnList = new ArrayList<>() {{
			for (int i = 0; i < 7; i++) {
				add(0);
			}
		}};
		Unit attackableUnit, secondAttackableUnit, selectedUnit;
		switch (moveParams.getFirst()) {
			case (0):
				attackableUnit = gamerUnitsArray.get(moveParams.get(2));
				secondAttackableUnit = gamerUnitsArray.get(moveParams.get(3));
				attackableUnit.getDamage(
						secondGamerUnitsArray.get(moveParams.get(1)).getAttackPoints());
				secondAttackableUnit.getDamage(
						secondGamerUnitsArray.get(moveParams.get(1)).getAttackPoints());
				returnList.set(0, 0);
				returnList.set(1, moveParams.get(1));
				returnList.set(2, moveParams.get(2));
				returnList.set(3, secondGamerUnitsArray.get(moveParams.get(1)).getAttackPoints());
				returnList.set(4, moveParams.get(3));
				returnList.set(5, secondGamerUnitsArray.get(moveParams.get(1)).getAttackPoints());
				if (attackableUnit.checkDeath()) {
					battleMap.placeSmth(battleMap.getBasicFields().getFirst(), attackableUnit.getxCoord(),
							attackableUnit.getyCoord());
					gamerUnitsArray.remove(attackableUnit);
					returnList.set(3, 0);
					if (Objects.equals(moveParams.get(2), moveParams.get(3))) {
						returnList.set(5, 0);
					}
				}
				if (!Objects.equals(moveParams.get(2), moveParams.get(3)) && secondAttackableUnit.checkDeath()) {
					battleMap.placeSmth(battleMap.getBasicFields().getFirst(), secondAttackableUnit.getxCoord(),
							secondAttackableUnit.getyCoord());
					gamerUnitsArray.remove(secondAttackableUnit);
					returnList.set(5, 0);
				}
				break;
			case (1):
				attackableUnit = gamerUnitsArray.get(moveParams.get(2));
				attackableUnit.getDamage(
						secondGamerUnitsArray.get(moveParams.get(1)).getAttackPoints());
				returnList.set(0, 1);
				returnList.set(1, moveParams.get(1));
				returnList.set(2, moveParams.get(2));
				returnList.set(3, secondGamerUnitsArray.get(moveParams.get(1)).getAttackPoints());
				returnList.set(4, 0);
				returnList.set(5, 0);
				if (attackableUnit.checkDeath()) {
					battleMap.placeSmth(battleMap.getBasicFields().getFirst(), attackableUnit.getxCoord(),
							attackableUnit.getyCoord());
					gamerUnitsArray.remove(attackableUnit);
					returnList.set(3, 0);
				}
				break;
			case (2):
				selectedUnit = secondGamerUnitsArray.get(moveParams.get(1));
				int movedByPortal = 0;
				for (ArrayList<Integer> portal: portalsArray) {
					if (Objects.equals(portal.getFirst(), moveParams.get(2)) &&
							Objects.equals(portal.get(1), moveParams.get(3))) {
						moveParams.set(2, portal.get(2));
						moveParams.set(3, portal.get(3));
						movedByPortal = 1;
					}
					else if (Objects.equals(portal.get(2), moveParams.get(2)) &&
							Objects.equals(portal.get(3), moveParams.get(3))) {
						moveParams.set(2, portal.getFirst());
						moveParams.set(3, portal.get(1));
						movedByPortal = 1;
					}
				}
				replaceUnitInMap(selectedUnit, moveParams.get(2), moveParams.get(3));
				returnList.set(0, 2);
				for (int i = 1; i < 4; i++) {
					returnList.set(i, moveParams.get(i));
				}
				returnList.set(4, movedByPortal);
				returnList.set(5, 0);
				break;
			case (3):
				int colorIndex = portalsArray.size();
				if (colorIndex >= portalsColoringArray.size()) {
					colorIndex = 0;
				}
				portalsArray.add(new ArrayList<>() {{
					for (int i = 2; i < 6; i++) {
						add(moveParams.get(i));
					}
					add(3);
				}});
				battleMap.placeSmth(portalsColoringArray.get(colorIndex) + battleMap.getFieldByPosition(moveParams.get(2), moveParams.get(3)) + ANSI_RESET,
						moveParams.get(2), moveParams.get(3));
				battleMap.placeSmth(portalsColoringArray.get(colorIndex) + battleMap.getFieldByPosition(moveParams.get(4), moveParams.get(5)) + ANSI_RESET,
						moveParams.get(4), moveParams.get(5));
				returnList.set(0, 3);
				for (int i = 1; i < 6; i++) {
					returnList.set(i, moveParams.get(i));
				}
				break;
			default:
				break;
		}
		for (int i = 0; i < portalsArray.size(); i++) {
			if (portalsArray.get(i).getLast() == 1) {
				if (Objects.equals(removeAscii(battleMap.getFieldByPosition(portalsArray.get(i).getFirst(), portalsArray.get(i).get(1))),
						battleMap.getBasicFields().getFirst())) {
					battleMap.placeSmth(battleMap.getBasicFields().getFirst(),
						portalsArray.get(i).getFirst(), portalsArray.get(i).get(1));
				}
				if (Objects.equals(removeAscii(battleMap.getFieldByPosition(portalsArray.get(i).get(2), portalsArray.get(i).get(3))),
						battleMap.getBasicFields().getFirst())) {
					battleMap.placeSmth(battleMap.getBasicFields().getFirst(),
						portalsArray.get(i).get(2), portalsArray.get(i).get(3));
				}
				portalsArray.remove(portalsArray.get(i));
			} else {
				int lastMoves = portalsArray.get(i).getLast();
				portalsArray.get(i).set(4, lastMoves - 1);
			}
		}
		return returnList;
	}

	public Bot getSecondGamer() {
		return secondGamer;
	}

	public ArrayList<ArrayList<Integer>> getPortalsArray() {
		return portalsArray;
	}

	public int fieldIsPortal(int xCoord, int yCoord) {
		for (int i = 0; i < portalsArray.size(); i++) {
			if (xCoord == portalsArray.get(i).getFirst() && yCoord == portalsArray.get(i).get(1) ||
			xCoord == portalsArray.get(i).get(2) && yCoord == portalsArray.get(i).get(3)) {
				return i;
			}
		}
		return -1;
	}
}
