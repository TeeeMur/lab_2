package Bots;

import BattlePlace.BattleMap;
import GameSubject.Game;
import Units.Unit;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bot {

	int botDifficulty;
	Random random = new Random();

	public Bot(ArrayList<Unit> botUnitsArray, ArrayList<ArrayList<String>> unitsTyping,
			   HashMap<String, ArrayList<Integer>> unitsSpecsMap, ArrayList<ArrayList<Float>> unitTypesPenalties,
			   ArrayList<String> mapBasicFields, int difficulty) {
		botDifficulty = difficulty;
		int choiceType, choiceUnit, unitNameCounter;
		String unitName, unitSpecName;
		ArrayList<String> botUnitsNames = new ArrayList<>();
		String[] specNameSplit;
		for (int i = 0; i < 3 + difficulty; i++) {
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
			botUnitsArray.add(new Unit(Game.ANSI_YELLOW + (i + 1) + Game.ANSI_RESET,
					Game.ANSI_YELLOW + botUnitsNames.get(i) + Game.ANSI_RESET,
					unitsSpecsMap.get(unitSpecName),
					unitTypesPenalties.get(type),
					mapBasicFields
			));
		}
	}

	// Схема: 1 разряд - вид действия (передвижение/атака), 2 разряд - номер АТАКУЮЩЕГО героя бота,
	// 3 разряд - номер АТАКУЕМОГО героя противника бота ИЛИ
	// 2 разряд - номер ПЕРЕДВИГАЕМОГО героя, 3 разряд - координата Х, 4 разряд - координата Y
	// все число - в 16-ричной системе счисления
	public int botMove(ArrayList<Unit> botUnitsArray, ArrayList<Unit> enemyUnitsArray, BattleMap battleMap)
			throws InterruptedException {
		System.out.println("Ход бота...");
		TimeUnit.SECONDS.sleep(random.nextInt(3) + 1);
		int actingBotUnitIndex;
		for (actingBotUnitIndex = 0; actingBotUnitIndex < botUnitsArray.size(); actingBotUnitIndex++) {
			for (int attackedEnemyUnit = 0; attackedEnemyUnit < enemyUnitsArray.size(); attackedEnemyUnit++) {
				if (botUnitsArray.get(actingBotUnitIndex).canAttack(enemyUnitsArray.get(attackedEnemyUnit))) {
					return 16 * 16 * 16 + actingBotUnitIndex * 256 + attackedEnemyUnit * 16;
				}
			}
		}
		actingBotUnitIndex = random.nextInt(botUnitsArray.size());
		Unit actingUnit = botUnitsArray.get(actingBotUnitIndex);
		int maxUnitMovePoints = actingUnit.getMovePoints();
		int xCoordMove = actingUnit.getxCoord();
		int yCoordMove = actingUnit.getyCoord() - random.nextInt(maxUnitMovePoints);
		int attempts = 0;
		while (!actingUnit.canMove(xCoordMove, yCoordMove, battleMap) ||
				!Objects.equals(battleMap.getFieldByPosition(xCoordMove, yCoordMove), battleMap.getBasicFields().getFirst())) {
			yCoordMove = actingUnit.getyCoord() - random.nextInt(maxUnitMovePoints);
			attempts++;
			if (attempts > 10) {
				actingBotUnitIndex = random.nextInt(botUnitsArray.size());
				actingUnit = botUnitsArray.get(actingBotUnitIndex);
				maxUnitMovePoints = actingUnit.getMovePoints();
				yCoordMove = actingUnit.getyCoord() - random.nextInt(maxUnitMovePoints);
				attempts = 0;
			}
		}
		return 2 * 16 * 16 * 16 + actingBotUnitIndex * 256 + xCoordMove * 16 + yCoordMove;
	}

}
