/*
	ИГРА РАССЧИТАНА МАКСИМУМ НА 9 ГЕРОЕВ С КАЖДОЙ СТОРОНЫ
 */

package GameSubjects;

import BattlePlace.BattleMap;
import Bots.Bot;
import Buildings.Hostel;
import Units.Unit;

import java.util.*;

public class GameBattle {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_CYAN = "\u001B[36m";

	private final int wallet;
	private final BattleMap battleMap;
	private final Bot secondGamer;
	private final Game game;

	private final ArrayList<Unit> gamerUnitsArray = new ArrayList<>();
	private final ArrayList<Unit> secondGamerUnitsArray = new ArrayList<>();
	private final ArrayList<String> portalsColoringArray = new ArrayList<>() {{
		add(ANSI_YELLOW);
		add(ANSI_GREEN);
		add(ANSI_BLUE);
		add(ANSI_CYAN);
	}};
	private final ArrayList<ArrayList<Integer>> portalsArray = new ArrayList<>();

	private static final ArrayList<String> unitsTypes = new ArrayList<>() {{
		add("Пешие");
		add("Лучники");
		add("Конные");
	}};

	private static final HashMap<String, ArrayList<String>> unitsTyping = new HashMap<>() {{
		put(unitsTypes.getFirst(), new ArrayList<>(Arrays.asList("Мечник", "Копьеносец", "Топорщик")));
		put(unitsTypes.get(1), new ArrayList<>(Arrays.asList("Тяжелый лучник", "Легкий лучник", "Арбалетчик")));
		put(unitsTypes.get(2), new ArrayList<>(Arrays.asList("Рыцарь", "Кирасир", "Конный лучник")));
	}};

	private static final HashMap<String, ArrayList<Integer>> defaultUnitsSpecsMap = new HashMap<>() {{
		//Foot units
		put(unitsTyping.get(unitsTypes.getFirst()).getFirst(), new ArrayList<>(Arrays.asList(50, 5, 1, 8, 3, 10)));
		put(unitsTyping.get(unitsTypes.getFirst()).get(1), new ArrayList<>(Arrays.asList(100, 100, 100, 100, 100, 5)));
		put(unitsTyping.get(unitsTypes.getFirst()).get(2), new ArrayList<>(Arrays.asList(45, 9, 1, 3, 4, 20)));
		//Bow units
		put(unitsTyping.get(unitsTypes.get(1)).getFirst(), new ArrayList<>(Arrays.asList(30, 6, 5, 8, 2, 15)));
		put(unitsTyping.get(unitsTypes.get(1)).get(1), new ArrayList<>(Arrays.asList(25, 3, 3, 4, 4, 19)));
		put(unitsTyping.get(unitsTypes.get(1)).get(2), new ArrayList<>(Arrays.asList(40, 7, 6, 3, 2, 23)));
		//Horse units
		put(unitsTyping.get(unitsTypes.get(2)).getFirst(), new ArrayList<>(Arrays.asList(30, 5, 1, 3, 6, 20)));
		put(unitsTyping.get(unitsTypes.get(2)).get(1), new ArrayList<>(Arrays.asList(50, 2, 1, 7, 5, 23)));
		put(unitsTyping.get(unitsTypes.get(2)).get(2), new ArrayList<>(Arrays.asList(25, 3, 3, 2, 5, 25)));
	}};

	private final HashMap<String, ArrayList<Integer>> unitsSpecsMap = new HashMap<>() {{
		for (String unitName: defaultUnitsSpecsMap.keySet()) {
			put(unitName, defaultUnitsSpecsMap.get(unitName));
		}
	}};

	private final HashMap<String, Integer> hostelUnitsLastMoves = new HashMap<>();

	private final HashMap<String, HashMap<String, Float>> unitTypesPenalties;

	public GameBattle(Game game, int difficulty, int healthPercentUpper, int attackPercentUpper, int defensePercentUpper,
					  int penaltyDecrease, int movePercentUpper,
					  BattleMap battleMap, HashMap<String, HashMap<String, ArrayList<Integer>>> addUnitsMap,
					   HashMap<String, HashMap<String, ArrayList<Integer>>> hostelUnitsMap) {
		this.game = game;
		this.battleMap = battleMap;
		float penaltyDowner = 1f - (float)penaltyDecrease / 100f;
		unitTypesPenalties = this.battleMap.getPenalties();
		wallet = fillWallet(battleMap.getSizeX(), battleMap.getSizeY(), difficulty);
		secondGamer = new Bot(secondGamerUnitsArray, unitsTyping,
				unitsSpecsMap, unitTypesPenalties, battleMap, difficulty);
		for (String unitType : unitTypesPenalties.keySet()) {
			for (String field : unitTypesPenalties.get(unitType).keySet()) {
				float prePenalty = unitTypesPenalties.get(unitType).getOrDefault(field, 1.1f);
				unitTypesPenalties.get(unitType).put(field, prePenalty * penaltyDowner);
			}
		}
		for (String unitType: addUnitsMap.keySet()) {
			for (String name: addUnitsMap.get(unitType).keySet()) {
				unitsSpecsMap.put(name, addUnitsMap.get(unitType).get(name));
				unitsTyping.get(unitType).add(name);
			}
		}
		for (String unitName: unitsSpecsMap.keySet()) {
			unitsSpecsMap.get(unitName).set(0, (int)(unitsSpecsMap.get(unitName).getFirst() * (1f + (float)healthPercentUpper / 100f)));
			unitsSpecsMap.get(unitName).set(1, (int)(unitsSpecsMap.get(unitName).get(1) * (1f + (float)attackPercentUpper / 100f)));
			unitsSpecsMap.get(unitName).set(3, (int)(unitsSpecsMap.get(unitName).get(3) * (1f + (float)defensePercentUpper / 100f)));
			unitsSpecsMap.get(unitName).set(4, (int) (unitsSpecsMap.get(unitName).get(4) * (1 + (float) movePercentUpper / 100f)));
		}
		if (hostelUnitsMap.get(unitsTypes.getFirst()).containsKey(Game.STEALER)) {
			secondGamerUnitsArray.add(new Unit(
					ANSI_YELLOW + (secondGamerUnitsArray.size() + 1) + ANSI_RESET,
					ANSI_YELLOW + Game.STEALER + ANSI_RESET,
					hostelUnitsMap.get(unitsTypes.getFirst()).get(Game.STEALER),
					unitTypesPenalties.get(unitsTypes.getFirst())
			));
		}
		else {
			for (String hostelUnitType: hostelUnitsMap.keySet()) {
				for (String hostelUnitName: hostelUnitsMap.get(hostelUnitType).keySet()) {
					hostelUnitsLastMoves.put(hostelUnitName, Hostel.MOVES_COUNT);
				}
			}
		}
		placeUnitsIntoMap(secondGamerUnitsArray, battleMap.getSizeY() - 1);
	}

	public GameBattle(int difficulty, Game game) {
		battleMap = new BattleMap(15, 15, difficulty);
		if (difficulty == 0) {
			wallet = fillWallet(battleMap.getSizeX(), battleMap.getSizeY(), 3);
		}
		else {
			wallet = fillWallet(battleMap.getSizeX(), battleMap.getSizeY(), difficulty);
		}
		secondGamer = new Bot(secondGamerUnitsArray, unitsTyping,
				unitsSpecsMap, battleMap.getPenalties(), battleMap, difficulty);
		unitTypesPenalties = battleMap.getPenalties();
		placeUnitsIntoMap(secondGamerUnitsArray, battleMap.getSizeY() - 1);
		this.game = game;
	}

	public static int maxWallet() {
		return fillWallet(BattleMap.getMaxSizeX(), BattleMap.getMaxSizeY(), 5);
	}

	private static int fillWallet(int sizeX, int sizeY, int difficulty) {
		int newWallet = 75;
		for (int i = 0; i < 6 - difficulty; i++) {
			newWallet += (sizeX + sizeY) / 2;
		}
		return newWallet;
	}

	public void setGamerUnits(HashMap<String, Integer> purchasedUnitsMap) {
		String tempUnitName;
		String tempPurchasedName;
		String unitMapImage;
		int order = 0;
		if (!hostelUnitsLastMoves.containsKey(Game.STEALER)) {
			for (String unitName: hostelUnitsLastMoves.keySet()) {
				purchasedUnitsMap.put(unitName, 1);
			}
		}
		for (String purchasedUnitName : purchasedUnitsMap.keySet()) {
			int countToBuy = purchasedUnitsMap.get(purchasedUnitName);
			for (Integer j = 0; j < countToBuy; j++) {
				tempPurchasedName = purchasedUnitName.substring(0, 1).toUpperCase() +
						purchasedUnitName.substring(1);
				String unitPurchasedType = "";
				String hostelUnitNameSubstring = purchasedUnitName.substring(0, purchasedUnitName.length() - 2);
				for (String unitType : unitsTypes) {
					if (hostelUnitsLastMoves.containsKey(purchasedUnitName)) {
						if (unitsTyping.get(unitType).contains(hostelUnitNameSubstring)) {
							unitPurchasedType = unitType;
							break;
						}
					}
					if (unitsTyping.get(unitType).contains(tempPurchasedName)) {
						unitPurchasedType = unitType;
						break;
					}
				}
				if (Objects.equals(unitPurchasedType, "")) {
					for (String unitType: game.getAcademyUnits().keySet()) {
						if (game.getAcademyUnits().get(unitType).containsKey(purchasedUnitName)) {
							unitPurchasedType = unitType;
							break;
						}
					}
				}
				String color;
				ArrayList<Integer> specsList;
				if (hostelUnitsLastMoves.containsKey(purchasedUnitName)) {
					color = GameBattle.ANSI_CYAN;
					specsList = unitsSpecsMap.get(hostelUnitNameSubstring);
					int hostelUnitLastMoves = hostelUnitsLastMoves.remove(purchasedUnitName);
					hostelUnitsLastMoves.put((tempPurchasedName + " " + (j + 1)), hostelUnitLastMoves);
				}
				else if (unitsSpecsMap.containsKey(tempPurchasedName)){
					color = colorByType(unitPurchasedType);
					specsList = unitsSpecsMap.get(tempPurchasedName);
				}
				else {
					color = colorByType(unitPurchasedType);
					specsList = game.getAcademyUnits().get(unitPurchasedType).get(purchasedUnitName);
				}
				tempUnitName = color + tempPurchasedName + " " + (j + 1) + ANSI_RESET;
				order++;
				unitMapImage = color + order + ANSI_RESET;
				gamerUnitsArray.add(new Unit(unitMapImage, tempUnitName, specsList,
						unitTypesPenalties.get(unitPurchasedType)));
			}
		}
		placeUnitsIntoMap(gamerUnitsArray, 0);
	}

	public int getWallet() {
		return wallet;
	}

	public static String colorByType(String unitType) {
		if (Objects.equals(unitType, unitsTypes.getFirst())) {
			return ANSI_GREEN;
		}
		else if (Objects.equals(unitType, unitsTypes.get(1))) {
			return ANSI_RED;
		}
		else if (Objects.equals(unitType, unitsTypes.get(2))) {
			return ANSI_BLUE;
		}
		return ANSI_RESET;
	}

	private void placeUnitsIntoMap(ArrayList<Unit> unitsToPlace, int row) {
		Unit eachUnitToPlace;
		int xCoord;
		int xSizeIsEven = Math.abs(battleMap.getSizeX() % 2 - 1);
		ArrayList<Integer> placeCoords = new ArrayList<>() {{
			for (int i = battleMap.getSizeX() / 2 - xSizeIsEven; i >= 0; i--) {
				add(i);
			}
			int j = 0;
			for (int i = 1; i < battleMap.getSizeX(); i += 2) {
				add(i, battleMap.getSizeX() / 2 + 1 + j - xSizeIsEven);
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
		String placeField = battleMap.getMapBasicFields().getFirst();
		int isPortal = checkFieldIsPortal(moveUnit.getxCoord(), moveUnit.getyCoord());
		if (isPortal != -1) {
			placeField = portalsColoringArray.get(isPortal) + battleMap.getMapBasicFields().getFirst() + ANSI_RESET;
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
		int strLength = str.length();
		if (strLength >= 5) {
			return str.substring(5, strLength - 4);
		}
		return str;
	}

	//false - common, true - with portal
	public boolean makeMove(String inputHeroMapImage, int xCoord, int yCoord) {
		Unit actionUnit = getUnitByMapImage(inputHeroMapImage, false);
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

	public int checkHeroMoveAbility(String inputHeroMapImage, int xCoord, int yCoord) {
		Unit moveHero = getUnitByMapImage(inputHeroMapImage, false);
		ArrayList<String> mapBasicFields = battleMap.getMapBasicFields();
		if (xCoord < 0 || xCoord >= 15 || yCoord < 0 || yCoord >= 15) {
			return 3;
		} else if (!Objects.equals(removeAscii(battleMap.getFieldByPosition(xCoord, yCoord)), mapBasicFields.getFirst())) {
			return 1;
		} else if (!moveHero.canMove(xCoord, yCoord, battleMap)) {
			return 2;
		} else {
			for (ArrayList<Integer> eachPortal: portalsArray) {
				if (eachPortal.getFirst() == xCoord && eachPortal.get(1) == yCoord &&
						!Objects.equals(removeAscii(battleMap.getFieldByPosition(eachPortal.get(2), eachPortal.get(3))), mapBasicFields.getFirst()) ) {
					return 4;
				}
				if (eachPortal.get(2) == xCoord && eachPortal.get(3) == yCoord &&
						!Objects.equals(removeAscii(battleMap.getFieldByPosition(eachPortal.getFirst(), eachPortal.get(1))), mapBasicFields.getFirst())) {
					return 4;
				}
			}
			return 0;
		}
	}

	public BattleMap getBattleMap() {
		return battleMap;
	}

	public HashMap<String, HashMap<String, Float>> getUnitTypesPenalties() {
		return unitTypesPenalties;
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
	// return: true - kill, false - other
	public boolean makeAttack(boolean side, String attackHeroImage, String attackableHeroImage) {
		boolean kill = false;
		Unit attackableUnit = getUnitByMapImage(removeAscii(attackableHeroImage), !side);
		Unit attackUnit = getUnitByMapImage(removeAscii(attackHeroImage), side);
		attackableUnit.getDamage(attackUnit.getAttackPoints());
		if (attackableUnit.checkDeath()) {
			kill = true;
			if (!side && Objects.equals(removeAscii(attackableUnit.getName()), "Черномор")) {
				int xPortalCoord;
				int yPortalCoord;
				for (ArrayList<Integer> portal : portalsArray) {
					for (int i = 0; i < 3; i += 2) {
						xPortalCoord = portal.get(i);
						yPortalCoord = portal.get(i + 1);
						if (Objects.equals(removeAscii(battleMap.getFieldByPosition(xPortalCoord, yPortalCoord)), battleMap.getMapBasicFields().getFirst())) {
							battleMap.placeSmth(battleMap.getMapBasicFields().getFirst(), xPortalCoord,
									yPortalCoord);
						}
					}

				}
				portalsArray.clear();
			}
			battleMap.placeSmth(battleMap.getMapBasicFields().getFirst(), attackableUnit.getxCoord(),
					attackableUnit.getyCoord());
			if (side) {
				gamerUnitsArray.remove(attackableUnit);
			}
			else {
				secondGamerUnitsArray.remove(attackableUnit);
			}
		}
		return kill;
	}

	// number:  2AB945
	// indexes: 543210
	// Scheme: 0 digit - type of action:
	//---------------------------------------------------------------------------------
	// double attack - 0
	//DOUBLE ATTACK:
	//	1st digit - index of the ATTACKING hero of the bot,
	//	2nd digit is the index of the ATTACKED hero of the opponent bot,
	//	3rd digit is attackPoints for first enemy(0 if he is killed)
	//	4th digit is the index of the SECOND ATTACKED hero of the opponent bot
	//	5th digit is attackPoints for second enemy (0 if he is killed)
	//---------------------------------------------------------------------------------
	// attack - 1
	//ATTACK:
	//	1st digit - index of the ATTACKING hero of the bot,
	//	2nd digit is the index of the ATTACKED hero of the bot's opponent,
	//	3rd digit is attackPoints for enemy(0 if he is killed)
	//	other digits - nothing
	//---------------------------------------------------------------------------------
	// movement - 2
	//MOVING:
	//	1st digit is the index of the hero that is being MOVED,
	//	2nd digit is the X coordinate
	//	3rd digit is the Y coordinate
	//	4th digit is marker for portal moving
	//---------------------------------------------------------------------------------
	// chernomor portal creating - 3
	//CHERNOMOR PORTAL CREATING:
	//	1st digit - number of chernomor hero of the bot
	//	2nd digit - xStartCoord
	//	3rd digit - yStartCoord
	//	4th digit - xEndCoord
	//	5th digit - yEndCoord
	//---------------------------------------------------------------------------------
	// stealer stealing something - 4
	//if there's no resources, stealer steals someone's params like movement range
	//if there's no resources and params, they can steal enemy's units
	//STEALER STEALING RESOURCES:
	// 1st digit - number of stealer hero of the bot
	// 2nd digit - type of resources
	// 3rd digit - count of resources
	//STEALER STEALING PARAMS:
	// 1st digit - number of stealer hero of the bot
	// 2nd digit - index of param (0-4)
	// 3fd digit - count of param
	// 4th digit - index of the hero that loses some count of param
	//STEALER STEALING UNITS:
	// 1st digit - number of stealer hero of the bot
	// 2nd digit - type of action (7 - stealing enemy's unit)
	// 3rd digit - index of stealed unit in bot's array
	//---------------------------------------------------------------------------------
	//THE WHOLE NUMBER IS IN 16-DIGIT NUMBER SYSTEM

	public ArrayList<Integer> secondGamerMove() {
		int secondGamerMove = secondGamer.botMove(secondGamerUnitsArray, gamerUnitsArray, battleMap, portalsArray, game);
		ArrayList<Integer> moveParams = new ArrayList<>(){{
			for (int i = 0; i < 6; i++) {
				add((secondGamerMove / ((int)Math.pow(16, i)) % 16));
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
					hostelUnitsLastMoves.remove(removeAscii(attackableUnit.getName()));
					battleMap.placeSmth(battleMap.getMapBasicFields().getFirst(), attackableUnit.getxCoord(),
							attackableUnit.getyCoord());
					gamerUnitsArray.remove(attackableUnit);
					returnList.set(3, 0);
					if (Objects.equals(moveParams.get(2), moveParams.get(3))) {
						returnList.set(5, 0);
					}
				}
				if (!Objects.equals(moveParams.get(2), moveParams.get(3)) && secondAttackableUnit.checkDeath()) {
					battleMap.placeSmth(battleMap.getMapBasicFields().getFirst(), secondAttackableUnit.getxCoord(),
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
					hostelUnitsLastMoves.remove(removeAscii(attackableUnit.getName()));
					battleMap.placeSmth(battleMap.getMapBasicFields().getFirst(), attackableUnit.getxCoord(),
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
			case (4):
				returnList.set(0, 4);
				returnList.set(1, moveParams.get(1));
				if (moveParams.get(2) >= 0 && moveParams.get(2) <= 4) {
					stealParamByCount(moveParams.get(2), moveParams.get(3), moveParams.get(4));
					for (int i = 2; i < 5; i++) {
						returnList.set(i, moveParams.get(i));
					}
					return returnList;
				}
				else if (moveParams.get(2) == 5 || moveParams.get(2) == 6) {
					returnList.set(2, moveParams.get(2));
					returnList.set(3, moveParams.get(3));
					String typeOfResources;
					if (moveParams.get(2) == 6){
						typeOfResources = Game.GOLD;
					}
					else {
						typeOfResources = Game.ELIXIR;
					}
					game.spendResource(moveParams.get(3), typeOfResources);
					return returnList;
				}
				else {
					Unit stealedUnit = gamerUnitsArray.remove((int)moveParams.get(3));
					String newMapImage = ANSI_YELLOW + (secondGamerUnitsArray.size() + 1) + ANSI_RESET;
					String newUnitName = "_" + stealedUnit.getName() + "_";
					if (hostelUnitsLastMoves.containsKey(stealedUnit.getName())) {
						int lastMoves = hostelUnitsLastMoves.remove(stealedUnit.getName());
						hostelUnitsLastMoves.put(newUnitName, lastMoves);
					}
					stealedUnit.setMapImage(newMapImage);
					stealedUnit.setName(newUnitName);
					secondGamerUnitsArray.add(stealedUnit);
					for (int i = 0; i < battleMap.getSizeX(); i++) {
						for (int j = battleMap.getSizeY() - 1; j >= battleMap.getSizeY() / 2; j--) {
							if (Objects.equals(battleMap.getFieldByPosition(i, j), battleMap.getMapBasicFields().getFirst())){
								battleMap.placeSmth(newMapImage, i, j);
								stealedUnit.setxCoord(i);
								stealedUnit.setyCoord(j);
								for (int k = 0; k < 2; k++) {
									returnList.set(k, moveParams.get(k));
								}
								returnList.set(3, secondGamerUnitsArray.size() - 1);
								return returnList;
							}
						}
					}
				}
				break;
			default:
				break;
		}
		for (int i = 0; i < portalsArray.size(); i++) {
			if (portalsArray.get(i).getLast() == 1) {
				if (Objects.equals(removeAscii(battleMap.getFieldByPosition(portalsArray.get(i).getFirst(), portalsArray.get(i).get(1))),
						battleMap.getMapBasicFields().getFirst())) {
					battleMap.placeSmth(battleMap.getMapBasicFields().getFirst(),
						portalsArray.get(i).getFirst(), portalsArray.get(i).get(1));
				}
				if (Objects.equals(removeAscii(battleMap.getFieldByPosition(portalsArray.get(i).get(2), portalsArray.get(i).get(3))),
						battleMap.getMapBasicFields().getFirst())) {
					battleMap.placeSmth(battleMap.getMapBasicFields().getFirst(),
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

	public ArrayList<String> hostelMoveModification() {
		hostelUnitsLastMoves.replaceAll((n, v) -> hostelUnitsLastMoves.get(n) - 1);
		ArrayList<String> unitNamesToRemove = new ArrayList<>();
		for (String unitName : hostelUnitsLastMoves.keySet()) {
			if (hostelUnitsLastMoves.get(unitName) == 0) {
				for (int i = 0; i < gamerUnitsArray.size(); i++) {
					if (Objects.equals(removeAscii(gamerUnitsArray.get(i).getName()), unitName)) {
						battleMap.placeSmth(battleMap.getMapBasicFields().getFirst(), gamerUnitsArray.get(i).getxCoord(),
							gamerUnitsArray.get(i).getyCoord());
						unitNamesToRemove.add(gamerUnitsArray.remove(i).getName());
					}
				}
				for (int i = 0; i < secondGamerUnitsArray.size(); i++) {
					if (Objects.equals(removeAscii(secondGamerUnitsArray.get(i).getName()), unitName)) {
						battleMap.placeSmth(battleMap.getMapBasicFields().getFirst(), secondGamerUnitsArray.get(i).getxCoord(),
								secondGamerUnitsArray.get(i).getyCoord());
						unitNamesToRemove.add(secondGamerUnitsArray.remove(i).getName());
					}
				}
			}
		}
		for (String unitName : unitNamesToRemove) {
			hostelUnitsLastMoves.remove(unitName);
		}
		return unitNamesToRemove;
	}

	public HashMap<String, Integer> getHostelUnitsLastMoves() {
		return hostelUnitsLastMoves;
	}

	public Bot getSecondGamer() {
		return secondGamer;
	}

	public ArrayList<ArrayList<Integer>> getPortalsArray() {
		return portalsArray;
	}

	public static HashMap<String, ArrayList<String>> getUnitsTyping() {
		return unitsTyping;
	}

	public static ArrayList<String> getUnitsTypes() {
		return unitsTypes;
	}

	public static HashMap<String, ArrayList<Integer>> getDefaultUnitsSpecsMap() {
		return defaultUnitsSpecsMap;
	}

	private void stealParamByCount(int paramIndex, int stealCount, int gamerUnitIndexInArray) {
		if (paramIndex >= 5) {
			return;
		}
		Unit victim = gamerUnitsArray.get(gamerUnitIndexInArray);
		int previousParam = victim.getParamByIndex(paramIndex);
		victim.setParamByIndex(paramIndex,previousParam - stealCount);
	}

	public int checkFieldIsPortal(int xCoord, int yCoord) {
		for (int i = 0; i < portalsArray.size(); i++) {
			if (xCoord == portalsArray.get(i).getFirst() && yCoord == portalsArray.get(i).get(1) ||
			xCoord == portalsArray.get(i).get(2) && yCoord == portalsArray.get(i).get(3)) {
				return i;
			}
		}
		return -1;
	}

	public ArrayList<String> getPortalsColoringArray() {
		return portalsColoringArray;
	}

}
