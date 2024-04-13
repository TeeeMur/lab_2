package Bots;

import BattlePlace.BattleMap;
import GameBattleSubjects.GameBattle;
import Units.Chernomor;
import Units.Unit;

import java.util.*;

public class Bot {

	int botDifficulty;
	Random random = new Random();

	private final ArrayList<String> doubleAttackersIndexList;

	public Bot(ArrayList<Unit> botUnitsArray, ArrayList<ArrayList<String>> unitsTyping,
			   HashMap<String, ArrayList<Integer>> unitsSpecsMap, ArrayList<ArrayList<Float>> unitTypesPenalties,
			   ArrayList<String> mapBasicFields, int difficulty) {
		botDifficulty = difficulty;
		int choiceType, choiceUnit, unitNameCounter;
		String unitName, unitSpecName;
		ArrayList<String> botUnitsNames = new ArrayList<>();
		String[] specNameSplit;
		for (int i = 0; i < 3; i++) {
			unitNameCounter = 1;
			choiceType = random.nextInt(unitsTyping.size());
			choiceUnit = random.nextInt(unitsTyping.get(choiceType).size());
			unitName = unitsTyping.get(choiceType).get(choiceUnit) + " " + unitNameCounter;
			while (botUnitsNames.contains(unitName)) {
				unitNameCounter += 1;
				unitName = unitsTyping.get(choiceType).get(choiceUnit) + " " + unitNameCounter;
			}
			botUnitsNames.add(unitName);
		}
		botUnitsNames.sort(Comparator.naturalOrder());
		int type = 0;
		for (int i = 0; i < botUnitsNames.size(); i++) {
			specNameSplit = botUnitsNames.get(i).split(" ");
			unitSpecName = specNameSplit[0];
			if (specNameSplit.length == 3) {
				unitSpecName = unitSpecName + " " + specNameSplit[1];
			}
			for (int j = 0; j < unitsTyping.size(); j++) {
				if (unitsTyping.get(j).contains(unitSpecName)) {
					type = j;
					break;
				}
			}
			botUnitsArray.add(new Unit(GameBattle.ANSI_YELLOW + (i + 1) + GameBattle.ANSI_RESET,
					GameBattle.ANSI_YELLOW + botUnitsNames.get(i) + GameBattle.ANSI_RESET,
					unitsSpecsMap.get(unitSpecName),
					unitTypesPenalties.get(type),
					mapBasicFields
			));
		}
		if (botDifficulty == 5) {
			botUnitsArray.add(new Chernomor(((Integer)(botUnitsArray.size() + 1)).toString(), mapBasicFields));
		}
		doubleAttackersIndexList = new ArrayList<>();
		for (int i = 0; i < difficulty - 2; i++) {
			int a = random.nextInt(botUnitsNames.size());
			while (doubleAttackersIndexList.contains(GameBattle.ANSI_YELLOW + botUnitsNames.get(a) + GameBattle.ANSI_RESET)) {
				a = random.nextInt(botUnitsNames.size());
			}
			doubleAttackersIndexList.add(GameBattle.ANSI_YELLOW + botUnitsNames.get(a) + GameBattle.ANSI_RESET);
		}
	}

	// number:  2AB945
	// indexes: 543210
	// Scheme: 0 digit - type of action (double attack - 0/ attack - 1/ movement - 2/ chernomor portal creating - 3),
	//DOUBLE ATTACK:
	//	1st digit - index of the ATTACKING hero of the bot,
	//	2nd digit is the index of the ATTACKED hero of the opponent bot,
	//	3rd digit is the index of the SECOND ATTACKED hero of the opponent bot
	//ATTACK:
	//	1st digit - index of the ATTACKING hero of the bot,
	//	2nd digit is the index of the ATTACKED hero of the opponent bot,
	//	other digits - nothing
	//MOVING:
	//	1st digit is the index of the hero that is being MOVED,
	//	2nd digit is the X coordinate
	//	3rd digit is the Y coordinate
	//	other digits - nothing
	//CHERNOMOR PORTAL CREATING:
	//	1st digit - number of chernomor hero of the bot
	//	2nd digit - xStartCoord
	//	3rd digit - yStartCoord
	//	4th digit - xEndCoord
	//	5th digit - yEndCoord
	//	the whole number is in the 16-digit number system
	public int botMove(ArrayList<Unit> botUnitsArray, ArrayList<Unit> enemyUnitsArray, BattleMap battleMap, ArrayList<ArrayList<Integer>> existingPortals) {
		int actingBotUnitIndex;
		int attackedEnemyUnitIndex = 0;
		int secondAttackedEnemyUnitIndex;
		int returnInteger = 0;
		boolean breakAttackChoice = false;
		ArrayList<Integer> attackableUnitsIndexList = new ArrayList<>();
		for (actingBotUnitIndex = 0; actingBotUnitIndex < botUnitsArray.size(); actingBotUnitIndex++) {
			for (int i = 0; i < enemyUnitsArray.size(); i++) {
				if (botUnitsArray.get(actingBotUnitIndex).canAttack(enemyUnitsArray.get(i))) {
					if (!breakAttackChoice) {
						attackedEnemyUnitIndex = i;
						returnInteger = i * 16 * 16 + actingBotUnitIndex * 16 + 1;
						breakAttackChoice = true;
					}
					attackableUnitsIndexList.add(i);
				}
			}
			if (breakAttackChoice) {break;}
		}
		if (breakAttackChoice) {
			if (doubleAttackersIndexList.contains(botUnitsArray.get(actingBotUnitIndex).getName())) {
				int attackedEnemyUnitHealth = enemyUnitsArray.get(attackedEnemyUnitIndex).getHealthPoints();
				int attackedEnemyUnitDefense = enemyUnitsArray.get(attackedEnemyUnitIndex).getDefensePoints();
				if (attackableUnitsIndexList.size() > 1) {
					returnInteger += (attackableUnitsIndexList.get(1) * 16 * 16 * 16 - 1);
					return returnInteger;
				} else if (attackedEnemyUnitHealth + attackedEnemyUnitDefense - botUnitsArray.get(actingBotUnitIndex).getAttackPoints() <= 0) {
					for (secondAttackedEnemyUnitIndex = 0; secondAttackedEnemyUnitIndex < enemyUnitsArray.size(); secondAttackedEnemyUnitIndex++) {
						if (botUnitsArray.get(actingBotUnitIndex).canAttack(enemyUnitsArray.get(secondAttackedEnemyUnitIndex)) &&
							secondAttackedEnemyUnitIndex != attackedEnemyUnitIndex) {
							returnInteger += (secondAttackedEnemyUnitIndex * 16 * 16 * 16 - 1);
							break;
						}
					}
				} else {
					secondAttackedEnemyUnitIndex = attackedEnemyUnitIndex;
					returnInteger += secondAttackedEnemyUnitIndex * 16 * 16 * 16 - 1;
				}
			}
			return returnInteger;
		}
		actingBotUnitIndex = random.nextInt(botUnitsArray.size());
		Unit actingUnit = botUnitsArray.get(actingBotUnitIndex);
		if (actingUnit instanceof Chernomor) {
			float acting = random.nextFloat();
			if (acting >= 0.5f) {
				ArrayList<ArrayList<Integer>> coordsList = ((Chernomor) actingUnit).createPortal(battleMap, existingPortals);
				returnInteger = coordsList.get(1).get(1) * (int)Math.pow(16, 5) +
						coordsList.get(1).get(0) * (int)Math.pow(16, 4) +
						coordsList.get(0).get(1) * (int)Math.pow(16, 3) +
						coordsList.get(0).get(0) * (int)Math.pow(16, 2) +
						actingBotUnitIndex * 16 + 3;
				return returnInteger;
			}
		}
		int maxUnitMovePoints = actingUnit.getMovePoints();
		int xCoordMove = actingUnit.getxCoord();
		int yCoordMove = actingUnit.getyCoord() - random.nextInt(maxUnitMovePoints);
		int attempts = 0;
		while (!actingUnit.canMove(xCoordMove, yCoordMove, battleMap) ||
				!Objects.equals(battleMap.getFieldByPosition(xCoordMove, yCoordMove), battleMap.getBasicFields().getFirst())) {
			xCoordMove = actingUnit.getxCoord() + random.nextInt(3);
			yCoordMove = actingUnit.getyCoord() - random.nextInt(maxUnitMovePoints);
			while (xCoordMove < 0 || xCoordMove > 14) {
					xCoordMove = actingUnit.getxCoord() + random.nextInt(3);
				}
			while (yCoordMove < 0 || yCoordMove > 14) {
				yCoordMove = actingUnit.getyCoord() + random.nextInt(maxUnitMovePoints);
			}
			attempts++;
			if (attempts > 10) {
				actingBotUnitIndex = random.nextInt(botUnitsArray.size());
				actingUnit = botUnitsArray.get(actingBotUnitIndex);
				maxUnitMovePoints = actingUnit.getMovePoints();
				xCoordMove = actingUnit.getxCoord();
				yCoordMove = actingUnit.getyCoord() - random.nextInt(maxUnitMovePoints);
				attempts = 0;
			}
		}
		returnInteger = yCoordMove * 16 * 16 * 16 + xCoordMove * 16 * 16 + actingBotUnitIndex * 16 + 2;
		return returnInteger;
	}

	public ArrayList<String> getDoubleAttackersIndexList() {
		return doubleAttackersIndexList;
	}

}
