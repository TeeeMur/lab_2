package GameInterfaces;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import BattlePlace.BattleMap;
import GameSubjects.Game;
import GameSubjects.GameBattle;
import Gamers.Gamer;
import Units.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import static java.lang.Math.*;

class GameBattleInterfaceTest {

	static Game game;
	static Gamer gamer;
	static Random rand;

	@BeforeAll
	static void setUpBeforeClass() {
		game = new Game(500, 500);
		gamer = new Gamer();
		rand = new Random();
	}

	@Test
	void testGameBattleGamerWins() {
		boolean gamerUnitsArrayEmpty = false;
		boolean secondGamerUnitsArrayEmpty = true;
		boolean endOfGame = true;
		for (int i = 1; i < 5; i++) {
			GameBattle gameBattle = new GameBattle(i, game);
			HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
				put("Копьеносец", gameBattle.getBattleMap().getMaxUnitsOnLine());
			}};
			gameBattle.setGamerUnits(purchasedGamerUnits);
			while (!gameBattle.getSecondGamerUnitsArray().isEmpty()) {
				gameBattle.makeAttack(false, gameBattle.getGamerUnitsArray().getFirst().getMapImage(),
						gameBattle.getSecondGamerUnitsArray().getFirst().getMapImage());
			}
			gamerUnitsArrayEmpty = gamerUnitsArrayEmpty || gameBattle.getGamerUnitsArray().isEmpty();
			secondGamerUnitsArrayEmpty = secondGamerUnitsArrayEmpty && gameBattle.getSecondGamerUnitsArray().isEmpty();
			endOfGame = endOfGame && gameBattle.endOfGame();
		}
		Assertions.assertFalse(gamerUnitsArrayEmpty);
		Assertions.assertTrue(secondGamerUnitsArrayEmpty);
		Assertions.assertTrue(endOfGame);
	}

	@Test
	void testGameBattleBotWins() {
		boolean gamerUnitsArrayEmpty = false;
		boolean secondGamerUnitsArrayEmpty = true;
		boolean endOfGame = true;
		for (int i = 1; i < 5; i++) {
			GameBattle gameBattle = getGameBattle(i);
			while (!gameBattle.getGamerUnitsArray().isEmpty()) {
				gameBattle.secondGamerMove();
			}
			gamerUnitsArrayEmpty = gamerUnitsArrayEmpty || gameBattle.getGamerUnitsArray().isEmpty();
			secondGamerUnitsArrayEmpty = secondGamerUnitsArrayEmpty && gameBattle.getSecondGamerUnitsArray().isEmpty();
			endOfGame = endOfGame && gameBattle.endOfGame();
			System.out.println(i + " test passed");
		}
		Assertions.assertFalse(secondGamerUnitsArrayEmpty);
		Assertions.assertTrue(gamerUnitsArrayEmpty);
		Assertions.assertTrue(endOfGame);
	}

	private static GameBattle getGameBattle(int i) {
		GameBattle gameBattle = new GameBattle(i, game);
		HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
			for (String type : GameBattle.getUnitsTyping().keySet()) {
				String name = GameBattle.getUnitsTyping().get(type).get(rand.nextInt(GameBattle.getUnitsTyping().get(type).size()));
				while (Objects.equals(name, GameBattle.getUnitsTyping().get(GameBattle.getUnitsTypes().getFirst()).get(1))) {
					name = GameBattle.getUnitsTyping().get(type).get(rand.nextInt(GameBattle.getUnitsTyping().get(type).size()));
				}
				put(name, 1);
			}
		}};
		gameBattle.setGamerUnits(purchasedGamerUnits);
		return gameBattle;
	}

	@Test
	void testPenalties() {
		boolean mustNotMove = false;
		for (int a = 0; a < 5; a++) {
			String[][] battleMapMatrix = new String[15][15];
			for (int i = 0; i < battleMapMatrix.length; i++) {
				if (i == 1) {
					for (int j = 0; j < battleMapMatrix[0].length; j++) {
						battleMapMatrix[i][j] = BattleMap.getDefaultFields().get(rand.nextInt(BattleMap.getDefaultFields().size()));
					}
				} else {
					for (int j = 0; j < battleMapMatrix[i].length; j++) {
						battleMapMatrix[i][j] = BattleMap.getDefaultFields().getFirst();
					}
				}
			}
			HashMap<String, HashMap<String, ArrayList<Integer>>> templateUnitsMap = new HashMap<>() {{
				for (String type : GameBattle.getUnitsTypes()) {
					put(type, new HashMap<>());
				}
			}};
			BattleMap battleMap = new BattleMap(battleMapMatrix, BattleMap.getDefaultFields(), BattleMap.getDefaultPenalties());
			GameBattle gameBattle = new GameBattle(game, 5,
					0, 0, 0, 0, 0, battleMap,
					templateUnitsMap, templateUnitsMap);
			HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
				for (String type : GameBattle.getUnitsTyping().keySet()) {
					for (String name : GameBattle.getUnitsTyping().get(type)) {
						put(name, 1);
					}
				}
			}};
			gameBattle.setGamerUnits(purchasedGamerUnits);
			GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
			gameBattleInterface.setGameBattle(gameBattle);
			gameBattleInterface.printCurrentMapAndState();
			for (int i = 0; i < gameBattle.getGamerUnitsArray().size(); i++) {
				Unit moveUnit = gameBattle.getGamerUnitsArray().get(i);
				float lastMovePoints = moveUnit.getLastMovePoints(moveUnit.getxCoord(), moveUnit.getyCoord() + 2, battleMap);
				float mustHaveLastMovePoints = moveUnit.getMovePoints() -
						moveUnit.getPenalty(battleMap.getFieldByPosition(moveUnit.getxCoord(), moveUnit.getyCoord() + 1)) -
						moveUnit.getPenalty(battleMap.getFieldByPosition(moveUnit.getxCoord(), moveUnit.getyCoord() + 2));
				mustNotMove = mustNotMove || (lastMovePoints != mustHaveLastMovePoints);
			}
		}
		Assertions.assertFalse(mustNotMove);
	}

	@Test
	void testAttackDistance() {
		boolean mustAttack = true;
		boolean canAttack;
		boolean checkAttack;
		float weakAttackDistanceAdd;
		GameBattle gameBattle = new GameBattle(3, game);
		HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
			put("Арбалетчик", 4);
			put("Мечник", 3);
		}};
		gameBattle.setGamerUnits(purchasedGamerUnits);
		GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
		gameBattleInterface.setGameBattle(gameBattle);
		for (int i = 0; i < gameBattle.getGamerUnitsArray().size(); i++) {
			Unit attackUnit = gameBattle.getGamerUnitsArray().get(i);
			weakAttackDistanceAdd = 0.1f;
			gameBattle.makeMove(attackUnit.getMapImage(), attackUnit.getxCoord(), 10 - i);
			if (attackUnit.getName().contains("Мечник")) {
				gameBattle.makeMove(attackUnit.getMapImage(), attackUnit.getxCoord(), 17 - i);
			}
			if (attackUnit.getAttackDistance() == 1) {
				weakAttackDistanceAdd = 0.5f;
			}
			gameBattleInterface.printCurrentMapAndState();
			for (int j = 0; j < gameBattle.getSecondGamerUnitsArray().size(); j++) {
				int xEnemy = gameBattle.getSecondGamerUnitsArray().get(j).getxCoord(), yEnemy = gameBattle.getSecondGamerUnitsArray().get(j).getyCoord();
				double distance = sqrt(pow((attackUnit.getxCoord() - xEnemy), 2) + pow((attackUnit.getyCoord() - yEnemy), 2));
				canAttack = attackUnit.canAttack(gameBattle.getSecondGamerUnitsArray().get(j));
				checkAttack = distance <= (attackUnit.getAttackDistance() + weakAttackDistanceAdd);
				mustAttack = mustAttack && (canAttack == checkAttack);
			}
		}
		Assertions.assertTrue(mustAttack);
	}

	@Test
	void testAttackSuccess() {
		boolean attacksEquals = true;
		GameBattle gameBattle = new GameBattle(3, game);
		HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
			for (String type : GameBattle.getUnitsTyping().keySet()) {
				for (String name : GameBattle.getUnitsTyping().get(type)) {
					put(name, 1);
				}
			}
		}};
		gameBattle.setGamerUnits(purchasedGamerUnits);
		GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
		gameBattleInterface.setGameBattle(gameBattle);
		gameBattleInterface.printCurrentMapAndState();
		for (int i = 0; i < gameBattle.getGamerUnitsArray().size(); i++) {
			Unit attackUnit = gameBattle.getGamerUnitsArray().get(i);
			gameBattle.makeMove(attackUnit.getMapImage(), attackUnit.getxCoord(), 13);
			int healthPlusDefenseBeforeAttack = gameBattle.getSecondGamerUnitsArray().get(i).getHealthPoints() +
					gameBattle.getSecondGamerUnitsArray().get(i).getDefensePoints();
			int attackPoints = attackUnit.getAttackPoints();
			boolean kill = gameBattle.makeAttack(false, attackUnit.getMapImage(), gameBattle.getSecondGamerUnitsArray().get(i).getMapImage());
			int healthPlusDefenseAfterAttack = gameBattle.getSecondGamerUnitsArray().get(i).getHealthPoints() +
					gameBattle.getSecondGamerUnitsArray().get(i).getDefensePoints();
			if (attackPoints >= healthPlusDefenseBeforeAttack) {
				attacksEquals = attacksEquals && kill;
			} else {
				attacksEquals = attacksEquals && (healthPlusDefenseAfterAttack == (healthPlusDefenseBeforeAttack - attackPoints));
			}
			gameBattleInterface.printCurrentMapAndState();
		}
		Assertions.assertTrue(attacksEquals);
	}

	@Test
	void testMoveAbility() {
		boolean testMove = true;
		boolean canMove;
		GameBattle gameBattle = new GameBattle(3, game);
		HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
			put("Копьеносец", 4);
		}};
		gameBattle.setGamerUnits(purchasedGamerUnits);
		GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
		gameBattleInterface.setGameBattle(gameBattle);
		gameBattleInterface.printCurrentMapAndState();
		for (int i = -3; i < gameBattle.getBattleMap().getSizeX() + 5; i++) {
			for (int j = -3; j < gameBattle.getBattleMap().getSizeY() + 5; j++) {
				Unit moveUnit = gameBattle.getGamerUnitsArray().getFirst();
				canMove = (i >= 0 && j >= 0 && i <= 14 && j <= 14 &&
						Objects.equals(gameBattle.getBattleMap().getFieldByPosition(i, j), gameBattle.getBattleMap().getMapBasicFields().getFirst()));
				int move = gameBattle.checkHeroMoveAbility(moveUnit.getMapImage(), i, j);
				testMove = testMove && (canMove == (move == 0));
				System.out.println(i + " " + j + " " + canMove + " " + move + " " + testMove + ";");
			}
		}
		Assertions.assertTrue(testMove);
	}

	@Test
	void testUnitDeath() {
		boolean testDeath = true;
		GameBattle gameBattle = new GameBattle(3, game);
		HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
			for (String type : GameBattle.getUnitsTyping().keySet()) {
				for (String name : GameBattle.getUnitsTyping().get(type)) {
					put(name, 1);
				}
			}
		}};
		gameBattle.setGamerUnits(purchasedGamerUnits);
		GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
		gameBattleInterface.setGameBattle(gameBattle);
		Unit victimUnit = gameBattle.getSecondGamerUnitsArray().getFirst();
		int xCheck = victimUnit.getxCoord(), yCheck = victimUnit.getyCoord();
		while (victimUnit.getHealthPoints() > 0) {
			gameBattle.makeAttack(false, gameBattle.getGamerUnitsArray().getFirst().getMapImage(),
					victimUnit.getMapImage());
		}
		for (int i = 0; i < gameBattle.getSecondGamerUnitsArray().size(); i++) {
			if (Objects.equals(gameBattle.getSecondGamerUnitsArray().get(i).getName(), victimUnit.getName())) {
				testDeath = false;
				break;
			}
		}
		testDeath = testDeath && Objects.equals(gameBattle.getBattleMap().getFieldByPosition(xCheck, yCheck), gameBattle.getBattleMap().getMapBasicFields().getFirst());
		victimUnit = gameBattle.getGamerUnitsArray().getFirst();
		xCheck = victimUnit.getxCoord();
		yCheck = victimUnit.getyCoord();
		while (victimUnit.getHealthPoints() > 0) {
			gameBattle.makeAttack(true, gameBattle.getSecondGamerUnitsArray().getFirst().getMapImage(),
					victimUnit.getMapImage());
		}
		for (int i = 0; i < gameBattle.getGamerUnitsArray().size(); i++) {
			if (Objects.equals(gameBattle.getGamerUnitsArray().get(i).getName(), victimUnit.getName())) {
				testDeath = false;
				break;
			}
		}
		testDeath = testDeath && Objects.equals(gameBattle.getBattleMap().getFieldByPosition(xCheck, yCheck), gameBattle.getBattleMap().getMapBasicFields().getFirst());
		gameBattleInterface.printCurrentMapAndState();
		Assertions.assertTrue(testDeath);
	}

	@Test
	void testUnitDefensePoints() {
		boolean testDefensePoints = true;
		GameBattle gameBattle = new GameBattle(5, game);
		HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
			for (String type : GameBattle.getUnitsTyping().keySet()) {
				for (String name : GameBattle.getUnitsTyping().get(type)) {
					put(name, 1);
				}
			}
		}};
		gameBattle.setGamerUnits(purchasedGamerUnits);
		GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
		gameBattleInterface.setGameBattle(gameBattle);
		gameBattleInterface.printCurrentMapAndState();
		for (int i = 0; i < gameBattle.getGamerUnitsArray().size(); i++) {
			Unit victimUnit = gameBattle.getSecondGamerUnitsArray().get(i);
			int defensePointsBeforeAttack = victimUnit.getDefensePoints();
			int healthPointsBeforeAttack = victimUnit.getHealthPoints();
			int attack = gameBattle.getGamerUnitsArray().get(i).getAttackPoints();
			gameBattle.makeAttack(false, gameBattle.getGamerUnitsArray().get(i).getMapImage(), victimUnit.getMapImage());
			int defensePointsAfterAttack = victimUnit.getDefensePoints();
			int healthPointsAfterAttack = victimUnit.getHealthPoints();
			testDefensePoints = testDefensePoints && ((defensePointsBeforeAttack - defensePointsAfterAttack) <= attack) &&
					((attack >= healthPointsBeforeAttack + defensePointsBeforeAttack) && victimUnit.checkDeath() ||
							(attack < healthPointsBeforeAttack + defensePointsBeforeAttack) && healthPointsAfterAttack > 0);
		}
		gameBattleInterface.printCurrentMapAndState();
		Assertions.assertTrue(testDefensePoints);
	}

	@Test
	void testPurchaseUnits() {
		boolean testPurchaseUnits = true;
		HashMap<String, Integer> purchasedGamerUnits, gameBattleUnits;
		for (int att = 0; att < 10; att++) {
			GameBattle gameBattle = new GameBattle(5, game);
			purchasedGamerUnits = new HashMap<>();
			gameBattleUnits = new HashMap<>();
			int unitsCount = 0;
			rand = new Random();
			int finalUnitsCount = rand.nextInt(gameBattle.getBattleMap().getMaxUnitsOnLine());
			while (unitsCount < finalUnitsCount) {
				int typeIndex = rand.nextInt(GameBattle.getUnitsTyping().size());
				int unitByTypeIndex = rand.nextInt(GameBattle.getUnitsTyping().get(GameBattle.getUnitsTypes().get(typeIndex)).size());
				int purchaseCount = rand.nextInt(finalUnitsCount - unitsCount + 1);
				if (purchaseCount == 0) {
					continue;
				}
				purchasedGamerUnits.put(GameBattle.getUnitsTyping().get(GameBattle.getUnitsTypes().get(typeIndex)).get(unitByTypeIndex), purchaseCount);
				unitsCount += purchaseCount;
			}
			gameBattle.setGamerUnits(purchasedGamerUnits);
			for (int i = 0; i < gameBattle.getGamerUnitsArray().size(); i++) {
				String[] nameParts = GameBattleInterface.removeAscii(gameBattle.getGamerUnitsArray().get(i).getName()).split(" ");
				String unitName;
				if (nameParts.length > 2) {
					unitName = nameParts[0] + " " + nameParts[1];
				} else {
					unitName = nameParts[0];
				}
				if (!gameBattleUnits.containsKey(unitName)) {
					gameBattleUnits.put(unitName, 1);
				} else {
					gameBattleUnits.put(unitName, gameBattleUnits.get(unitName) + 1);
				}
			}
			for (String unitName : gameBattleUnits.keySet()) {
				testPurchaseUnits = testPurchaseUnits && purchasedGamerUnits.containsKey(unitName) &&
						Objects.equals(purchasedGamerUnits.get(unitName), gameBattleUnits.get(unitName));
			}
			System.out.println(purchasedGamerUnits);
			System.out.println(gameBattleUnits);
		}
		Assertions.assertTrue(testPurchaseUnits);
	}

	@Test
	void testBotActions() {
		boolean testBotActions = true;
		GameBattle gameBattle = new GameBattle(3, game);
		HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
			put("Мечник", 4);
		}};
		gameBattle.setGamerUnits(purchasedGamerUnits);
		GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
		gameBattleInterface.setGameBattle(gameBattle);
		Unit gamerMoveUnit = gameBattle.getGamerUnitsArray().getFirst();
		gameBattle.makeMove(gamerMoveUnit.getMapImage(), 7, 5);
		gameBattleInterface.printCurrentMapAndState();
		int attackDistanse = 0;
		Unit botMoveUnit = gameBattle.getSecondGamerUnitsArray().getFirst();
		for (int i = 0; i < gameBattle.getSecondGamerUnitsArray().size(); i++) {
			if (gameBattle.getSecondGamerUnitsArray().get(i).getAttackDistance() > attackDistanse) {
				botMoveUnit = gameBattle.getSecondGamerUnitsArray().get(i);
				attackDistanse = botMoveUnit.getAttackDistance();
			}
		}
		while (!botMoveUnit.canAttack(gamerMoveUnit)) {
			int healthPlusDefensePointsOfGamerUnit = gamerMoveUnit.getDefensePoints() + gamerMoveUnit.getHealthPoints();
			gameBattle.secondGamerMove();
			testBotActions = testBotActions && (gamerMoveUnit.getDefensePoints() == healthPlusDefensePointsOfGamerUnit);
		}
		if (botMoveUnit.canAttack(gamerMoveUnit)) {
			int previousSize = gameBattle.getGamerUnitsArray().size();
			while (gameBattle.getGamerUnitsArray().size() == previousSize) {
				int previousGamerUnitHealthPlusDefensePoints = gamerMoveUnit.getHealthPoints() + gamerMoveUnit.getDefensePoints();
				gameBattle.secondGamerMove();
				testBotActions = testBotActions && (previousGamerUnitHealthPlusDefensePoints > gamerMoveUnit.getHealthPoints() + gamerMoveUnit.getDefensePoints());
			}
		}
		Assertions.assertTrue(testBotActions);
	}

	@Test
	void testIndividualTaskLR2() {
		boolean testTask = true;
		GameBattle gameBattle = new GameBattle(5, game);
		HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
			put("Копьеносец", 5);
		}};
		gameBattle.setGamerUnits(purchasedGamerUnits);
		GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
		gameBattleInterface.setGameBattle(gameBattle);
		while (gameBattle.getSecondGamerUnitsArray().size() != 2) {
			gameBattle.makeAttack(false, gameBattle.getGamerUnitsArray().getFirst().getMapImage(),
					gameBattle.getSecondGamerUnitsArray().getFirst().getMapImage());
		}
		int att = 300;
		String portalImageMustBe = gameBattle.getPortalsColoringArray().getFirst() +
				gameBattle.getBattleMap().getMapBasicFields().getFirst() + GameBattle.ANSI_RESET;
		while (att > 0) {
			ArrayList<Integer> secondGamerMoveDescription = gameBattle.secondGamerMove();
			if (secondGamerMoveDescription.getFirst() == 3) {
				break;
			}
			att--;
		}
		if (att == 0) {
			testTask = false;
		}
		ArrayList<Integer> portalCoords = new ArrayList<>() {{
			for (int i = 0; i < 4; i++) {
				add(gameBattle.getPortalsArray().getFirst().get(i));
			}
		}};
		gameBattleInterface.printCurrentMapAndState();
		Unit moveUnit = gameBattle.getGamerUnitsArray().getFirst();
		for (int i = 0; i < 4; i += 2) {
			testTask = testTask && (Objects.equals(gameBattle.getBattleMap().getFieldByPosition(portalCoords.get(i),
					portalCoords.get(i + 1)), portalImageMustBe));
		}
		gameBattle.makeMove(moveUnit.getMapImage(),
				portalCoords.get(0), portalCoords.get(1));
		testTask = testTask && (Objects.equals(gameBattle.getBattleMap().getFieldByPosition(portalCoords.get(2),
				portalCoords.get(3)), moveUnit.getMapImage())) &&
				(Objects.equals(gameBattle.getBattleMap().getFieldByPosition(portalCoords.get(0),
						portalCoords.get(1)), portalImageMustBe)) && (moveUnit.getxCoord() == portalCoords.get(2) &&
				moveUnit.getyCoord() == portalCoords.get(3));
		gameBattle.makeMove(moveUnit.getMapImage(), 0, 0);
		testTask = testTask && (Objects.equals(gameBattle.getBattleMap().getFieldByPosition(portalCoords.get(2),
				portalCoords.get(3)), portalImageMustBe)) &&
				(Objects.equals(gameBattle.getBattleMap().getFieldByPosition(portalCoords.get(0),
						portalCoords.get(1)), portalImageMustBe));
		gameBattle.makeMove(moveUnit.getMapImage(),
				portalCoords.get(2), portalCoords.get(3));
		testTask = testTask && (Objects.equals(gameBattle.getBattleMap().getFieldByPosition(portalCoords.get(0),
				portalCoords.get(1)), moveUnit.getMapImage())) &&
				(Objects.equals(gameBattle.getBattleMap().getFieldByPosition(portalCoords.get(2),
						portalCoords.get(3)), portalImageMustBe)) && (moveUnit.getxCoord() == portalCoords.get(0) &&
				moveUnit.getyCoord() == portalCoords.get(1));
		Assertions.assertTrue(testTask);
	}

	@Test
	void testBattleMapDisplay() {
		String mustBeOutput = "1 2 3 4 5 6 7 8 9 101112131415 X\r\n" +
				"1  ▓ ▓ ▓ \u001B[34m9\u001B[0m \u001B[34m7\u001B[0m \u001B[32m5\u001B[0m \u001B[32m3\u001B[0m \u001B[31m1\u001B[0m \u001B[31m2\u001B[0m \u001B[32m4\u001B[0m \u001B[34m6\u001B[0m \u001B[31m8\u001B[0m ▓ ▓ ▓   Твои герои:                                              Герои твоего врага:\r\n" +
				"2  ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | \u001B[31m1\u001B[0m)\u001B[31mЛегкий лучник 1\u001B[0m - Здоровье:\u001B[32m25\u001B[0m; Защита:\u001B[34m4\u001B[0m; Атака:\u001B[31m3\u001B[0m;|  | \u001B[33m1\u001B[0m)\u001B[33mАрбалетчик 1\u001B[0m - Здоровье:\u001B[32m40\u001B[0m; Защита:\u001B[34m3\u001B[0m; Атака:\u001B[31m7\u001B[0m;|     \r\n" +
				"3  ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | Дальность атаки:3; Дальность перемещения:4 |          | Дальность атаки:6; Дальность перемещения:2 |          \r\n" +
				"4  ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | \u001B[31m2\u001B[0m)\u001B[31mАрбалетчик 1\u001B[0m - Здоровье:\u001B[32m40\u001B[0m; Защита:\u001B[34m3\u001B[0m; Атака:\u001B[31m7\u001B[0m;|     | \u001B[33m2\u001B[0m)\u001B[33mКопьеносец 1\u001B[0m - Здоровье:\u001B[32m100\u001B[0m; Защита:\u001B[34m100\u001B[0m; Атака:\u001B[31m100\u001B[0m;|\r\n" +
				"5  ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | Дальность атаки:6; Дальность перемещения:2 |          | Дальность атаки:100; Дальность перемещения:100 |      \r\n" +
				"6  ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | \u001B[32m3\u001B[0m)\u001B[32mТопорщик 1\u001B[0m - Здоровье:\u001B[32m45\u001B[0m; Защита:\u001B[34m3\u001B[0m; Атака:\u001B[31m9\u001B[0m;|       | \u001B[33m3\u001B[0m)\u001B[33mЛегкий лучник 1\u001B[0m - Здоровье:\u001B[32m25\u001B[0m; Защита:\u001B[34m4\u001B[0m; Атака:\u001B[31m3\u001B[0m;|  \r\n" +
				"7  ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | Дальность атаки:1; Дальность перемещения:4 |          | Дальность атаки:3; Дальность перемещения:4 |          \r\n" +
				"8  ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | \u001B[32m4\u001B[0m)\u001B[32mМечник 1\u001B[0m - Здоровье:\u001B[32m50\u001B[0m; Защита:\u001B[34m8\u001B[0m; Атака:\u001B[31m5\u001B[0m;|         | \u001B[33m4\u001B[0m)\u001B[33mМечник 1\u001B[0m - Здоровье:\u001B[32m50\u001B[0m; Защита:\u001B[34m8\u001B[0m; Атака:\u001B[31m5\u001B[0m;|         \r\n" +
				"9  ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | Дальность атаки:1; Дальность перемещения:3 |          | Дальность атаки:1; Дальность перемещения:3 |          \r\n" +
				"10 ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | \u001B[32m5\u001B[0m)\u001B[32mКопьеносец 1\u001B[0m - Здоровье:\u001B[32m100\u001B[0m; Защита:\u001B[34m100\u001B[0m; Атака:\u001B[31m100\u001B[0m;|| \u001B[33m5\u001B[0m)\u001B[33mРыцарь 1\u001B[0m - Здоровье:\u001B[32m30\u001B[0m; Защита:\u001B[34m3\u001B[0m; Атака:\u001B[31m5\u001B[0m;|         \r\n" +
				"11 ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | Дальность атаки:100; Дальность перемещения:100 |      | Дальность атаки:1; Дальность перемещения:6 |          \r\n" +
				"12 ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | \u001B[34m6\u001B[0m)\u001B[34mКирасир 1\u001B[0m - Здоровье:\u001B[32m50\u001B[0m; Защита:\u001B[34m7\u001B[0m; Атака:\u001B[31m2\u001B[0m;|        | \u001B[33m6\u001B[0m)\u001B[33mТопорщик 1\u001B[0m - Здоровье:\u001B[32m45\u001B[0m; Защита:\u001B[34m3\u001B[0m; Атака:\u001B[31m9\u001B[0m;|       \r\n" +
				"13 ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | Дальность атаки:1; Дальность перемещения:5 |          | Дальность атаки:1; Дальность перемещения:4 |          \r\n" +
				"14 ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓ ▓   | \u001B[34m7\u001B[0m)\u001B[34mКонный лучник 1\u001B[0m - Здоровье:\u001B[32m25\u001B[0m; Защита:\u001B[34m2\u001B[0m; Атака:\u001B[31m3\u001B[0m;|  | \u001B[33m7\u001B[0m)\u001B[33mТяжелый лучник 1\u001B[0m - Здоровье:\u001B[32m30\u001B[0m; Защита:\u001B[34m8\u001B[0m; Атака:\u001B[31m6\u001B[0m;| \r\n" +
				"15 ▓ ▓ ▓ ▓ \u001B[33m7\u001B[0m \u001B[33m5\u001B[0m \u001B[33m3\u001B[0m \u001B[33m1\u001B[0m \u001B[33m2\u001B[0m \u001B[33m4\u001B[0m \u001B[33m6\u001B[0m ▓ ▓ ▓ ▓   | Дальность атаки:3; Дальность перемещения:5 |          | Дальность атаки:5; Дальность перемещения:2 |          \n" +
				"Y                                  | \u001B[31m8\u001B[0m)\u001B[31mТяжелый лучник 1\u001B[0m - Здоровье:\u001B[32m30\u001B[0m; Защита:\u001B[34m8\u001B[0m; Атака:\u001B[31m6\u001B[0m;| \r\n" +
				"                                   | Дальность атаки:5; Дальность перемещения:2 |          \r\n" +
				"                                   | \u001B[34m9\u001B[0m)\u001B[34mРыцарь 1\u001B[0m - Здоровье:\u001B[32m30\u001B[0m; Защита:\u001B[34m3\u001B[0m; Атака:\u001B[31m5\u001B[0m;|         \r\n" +
				"                                   | Дальность атаки:1; Дальность перемещения:6 |";
		PrintStream standardOut = System.out;
		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStreamCaptor));
		GameBattle gameBattle = new GameBattle(0, game);
		HashMap<String, Integer> purchasedGamerUnits = new HashMap<>() {{
			for (String type : GameBattle.getUnitsTyping().keySet()) {
				for (String name : GameBattle.getUnitsTyping().get(type)) {
					put(name, 1);
				}
			}
		}};
		gameBattle.setGamerUnits(purchasedGamerUnits);
		GameBattleInterface gameBattleInterface = new GameBattleInterface(gamer, game);
		gameBattleInterface.setGameBattle(gameBattle);
		gameBattleInterface.printCurrentMapAndState();
		String output = outputStreamCaptor.toString().trim();
		System.setOut(standardOut);
		Assertions.assertEquals(output, mustBeOutput);
	}


}