package Bots;

import BattlePlace.BattleMap;
import GameSubjects.Game;
import GameSubjects.GameBattle;
import Units.Chernomor;
import Units.Unit;

import java.util.*;

public class Bot {

	int botDifficulty;
	final int PARAM_STEAL_COUNT = 1;
	Random random = new Random();

	private final ArrayList<String> doubleAttackersIndexList;

	public Bot(ArrayList<Unit> botUnitsArray, HashMap<String, ArrayList<String>> unitsTyping,
			   HashMap<String, ArrayList<Integer>> unitsSpecsMap, HashMap<String, HashMap<String, Float>> unitTypesPenalties,
			   BattleMap battleMap, int difficulty) {
		botDifficulty = difficulty;
		int randomType, choiceUnit, unitNameCounter;
		String unitName, unitSpecName;
		ArrayList<String> botUnitsNames = new ArrayList<>();
		String[] specNameSplit;
		ArrayList<String> choiceTypes = new ArrayList<>(unitsTyping.keySet());
		int botUnitsCount = (int) (battleMap.getSizeX() * (0.5f + (float) difficulty / 15f));
		if (difficulty == 5) {
			botUnitsCount -= 2;
		}
		if (difficulty == 0) {
			int i = 0;
			boolean stop = false;
			for (String type : unitsTyping.keySet()) {
				for (String name : unitsTyping.get(type)) {
					botUnitsNames.add(name + " " + "1");
					i++;
					stop = (i == botUnitsCount);
					if (stop) {
						break;
					}
				}
				if (stop) {
					break;
				}
			}
		} else {
			for (int i = 0; i < botUnitsCount; i++) {
				unitNameCounter = 1;
				randomType = random.nextInt(choiceTypes.size());
				choiceUnit = random.nextInt(unitsTyping.get(choiceTypes.get(randomType)).size());
				unitName = unitsTyping.get(choiceTypes.get(randomType)).get(choiceUnit) + " " + unitNameCounter;
				while (botUnitsNames.contains(unitName)) {
					unitNameCounter += 1;
					unitName = unitsTyping.get(choiceTypes.get(randomType)).get(choiceUnit) + " " + unitNameCounter;
				}
				botUnitsNames.add(unitName);
			}
		}
		botUnitsNames.sort(Comparator.naturalOrder());
		for (int i = 0; i < botUnitsNames.size(); i++) {
			specNameSplit = botUnitsNames.get(i).split(" ");
			unitSpecName = specNameSplit[0];
			String type = "";
			if (specNameSplit.length == 3) {
				unitSpecName = unitSpecName + " " + specNameSplit[1];
			}
			for (String unitType : choiceTypes) {
				if (unitsTyping.get(unitType).contains(unitSpecName)) {
					type = unitType;
					break;
				}
			}
			botUnitsArray.add(new Unit(GameBattle.ANSI_YELLOW + (i + 1) + GameBattle.ANSI_RESET,
					GameBattle.ANSI_YELLOW + botUnitsNames.get(i) + GameBattle.ANSI_RESET,
					unitsSpecsMap.get(unitSpecName),
					unitTypesPenalties.get(type)
			));
		}
		if (botDifficulty == 5) {
			int botUnitsArraySize = botUnitsArray.size();
			botUnitsArray.add(new Chernomor(((Integer) (botUnitsArraySize + 1)).toString(), battleMap.getPenalties().get(GameBattle.getUnitsTypes().getFirst())));
		}
		doubleAttackersIndexList = new ArrayList<>();
		if (difficulty > 0) {
			for (int i = 0; i < difficulty - 2; i++) {
				int a = random.nextInt(botUnitsNames.size());
				while (doubleAttackersIndexList.contains(GameBattle.ANSI_YELLOW + botUnitsNames.get(a) + GameBattle.ANSI_RESET)) {
					a = random.nextInt(botUnitsNames.size());
				}
				doubleAttackersIndexList.add(GameBattle.ANSI_YELLOW + botUnitsNames.get(a) + GameBattle.ANSI_RESET);
			}
		}

	}

	public boolean ableStealParamIndex(ArrayList<Unit> enemyUnitsArray, int paramIndex, int gamerUnitIndexInArray) {
		Unit victim = enemyUnitsArray.get(gamerUnitIndexInArray);
		return victim.getParamByIndex(paramIndex) > 1;
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
	//--------------------------------------------------------------------------------
	// stealer stealing something - 4
	//(if there's no resources, stealer steals someone's params like movement range)
	//STEALER STEALING RESOURCES:
	// 1st digit - number of stealer hero of the bot
	// 2nd digit - type of resources (5 - elixir, 6 - gold)
	// 3rd digit - count of resources
	//STEALER STEALING PARAMS:
	// 1st digit - number of stealer hero of the bot
	// 2nd digit - index of param (0-4)
	// 3fd digit - count of param
	// 4th digit - index of the hero that loses some count of param
	//STEALER STEALING UNITS:
	// 1st digit - number of stealer hero of the bot
	// 2nd digit - type of action (7 - stealing enemy's unit)
	// 3rd digit - index of stealed unit
	//	the whole number is in the 16-digit number system
	public int botMove(ArrayList<Unit> botUnitsArray, ArrayList<Unit> enemyUnitsArray, BattleMap battleMap, ArrayList<ArrayList<Integer>> existingPortals,
					   Game game) {
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
			if (breakAttackChoice) {
				break;
			}
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
				returnInteger = coordsList.get(1).get(1) * (int) Math.pow(16, 5) +
						coordsList.get(1).get(0) * (int) Math.pow(16, 4) +
						coordsList.get(0).get(1) * (int) Math.pow(16, 3) +
						coordsList.get(0).get(0) * (int) Math.pow(16, 2) +
						actingBotUnitIndex * 16 + 3;
				return returnInteger;
			}
		}
		if (Objects.equals(actingUnit.getName(), Game.STEALER)) {
			float acting = random.nextFloat();
			if (acting >= 0.5f) {
				if (game.getResource(Game.GOLD) != 0) {
					int elixirCount = game.getResource(Game.ELIXIR);
					game.spendResource((int) (elixirCount * 0.1), Game.ELIXIR);
					returnInteger = (int) (elixirCount * 0.1) * (int) Math.pow(16, 3) +
							5 * (int) Math.pow(16, 2) +
							actingBotUnitIndex * 16 + 4;
					return returnInteger;
				}
				if (game.getResource(Game.ELIXIR) != 0) {
					int goldCount = game.getResource(Game.GOLD);
					game.spendResource((int) (goldCount * 0.1), Game.GOLD);
					returnInteger = (int) (goldCount * 0.1) * (int) Math.pow(16, 3) +
							6 * (int) Math.pow(16, 2) +
							actingBotUnitIndex * 16 + 4;
					return returnInteger;
				}
				ArrayList<Integer> enemyUnitIndexes = new ArrayList<>() {{
					for (int i = 0; i < enemyUnitsArray.size(); i++) {
						add(i);
					}
				}};
				while (!enemyUnitIndexes.isEmpty()) {
					int enemyUnitIndex = enemyUnitIndexes.get(random.nextInt(enemyUnitIndexes.size()));
					ArrayList<Integer> loyalParamIndexes = new ArrayList<>(Arrays.asList(1, 2, 4));
					while (!loyalParamIndexes.isEmpty()) {
						int stealParamIndex = loyalParamIndexes.get(random.nextInt(loyalParamIndexes.size()));
						if (ableStealParamIndex(enemyUnitsArray,
								stealParamIndex, enemyUnitIndex)) {
							returnInteger = enemyUnitIndex * (int) Math.pow(16, 4) +
									PARAM_STEAL_COUNT * (int) Math.pow(16, 3) +
									stealParamIndex * (int) Math.pow(16, 2) +
									actingBotUnitIndex * 16 + 4;
							return returnInteger;
						} else {
							loyalParamIndexes.remove((Integer) stealParamIndex);
						}
					}
					enemyUnitIndexes.remove((Integer) (enemyUnitIndex));
				}
				int enemyUnitIndexToSteal = random.nextInt(enemyUnitsArray.size());
				return enemyUnitIndexToSteal * (int) Math.pow(16, 3) +
						7 * (int) Math.pow(16, 2) +
						actingBotUnitIndex * 16 + 4;
			}
		}
		int maxUnitMovePoints = actingUnit.getMovePoints();
		int xCoordMove = actingUnit.getxCoord();
		int yCoordMove = actingUnit.getyCoord() - random.nextInt(maxUnitMovePoints);
		int attempts = 0;
		while (!actingUnit.canMove(xCoordMove, yCoordMove, battleMap) ||
				!Objects.equals(battleMap.getFieldByPosition(xCoordMove, yCoordMove), battleMap.getMapBasicFields().getFirst())) {
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
