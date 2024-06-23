package Units;

import BattlePlace.BattleMap;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.signum;

public class Unit {

	protected String mapImage;
	protected String name;
	protected int healthPoints;
	protected int defensePoints;
	protected int attackPoints;
	protected int attackDistance;
	protected int movePoints;
	protected final int costPoints;
	protected int xCoord;
	protected int yCoord;

	protected HashMap<String, Float> penalty;

	public Unit(String image, String unitName, ArrayList<Integer> unitParams, HashMap<String, Float> unitTypePenalties) {
		mapImage = image;
		name = unitName;
		healthPoints = unitParams.getFirst();
		attackPoints = unitParams.get(1);
		attackDistance = unitParams.get(2);
		defensePoints = unitParams.get(3);
		movePoints = unitParams.get(4);
		costPoints = unitParams.get(5);
		penalty = new HashMap<>();
		for (String key : unitTypePenalties.keySet()) {
			penalty.put(key, unitTypePenalties.get(key));
		}
	}

	public String getMapImage() {
		return mapImage;
	}

	public void setMapImage(String mapImage) {
		this.mapImage = mapImage;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final float getPenalty(String field) {
		return penalty.get(field);
	}

	public final int getParamByIndex(int index) {
		return switch (index) {
			case (0) -> getHealthPoints();
			case (1) -> getAttackPoints();
			case (2) -> getAttackDistance();
			case (3) -> getDefensePoints();
			case (4) -> getMovePoints();
			default -> 0;
		};
	}

	public final void setParamByIndex(int index, int value) {
		switch (index) {
			case (0):
				healthPoints = value;
				break;
			case (1):
				attackPoints = value;
				break;
			case (2):
				attackDistance = value;
				break;
			case (3):
				defensePoints = value;
				break;
			case (4):
				movePoints = value;
				break;
			default:
				break;
		}
	}

	public final int getMovePoints() {
		return movePoints;
	}

	public final int getAttackDistance() {
		return attackDistance;
	}

	public final int getAttackPoints() {
		return attackPoints;
	}

	public final int getHealthPoints() {
		return healthPoints;
	}

	public final int getDefensePoints() {
		return defensePoints;
	}

	public final int getxCoord() { return xCoord; }

	public final void setxCoord(int xCoord) { this.xCoord = xCoord; }

	public final int getyCoord() { return yCoord; }

	public final void setyCoord(int yCoord) { this.yCoord = yCoord; }

	public boolean checkDeath() {
		return healthPoints <= 0;
	}

	public final void getDamage(int damage) {
		int healthDamage = damage;
		if (healthDamage > defensePoints) {
			healthDamage -= defensePoints;
			defensePoints = 0;
			healthPoints -= healthDamage;
		}
		else {
			defensePoints -= healthDamage;
		}
	}

	public final boolean canAttack(Unit enemy) {
		double weakAttackDistanceAdd = 0.1f;
		if (attackDistance == 1) {
			weakAttackDistanceAdd = 0.5f;
		}
		int xEnemy = enemy.getxCoord(), yEnemy = enemy.getyCoord();
		double distance = sqrt(pow((xCoord - xEnemy), 2) + pow((yCoord - yEnemy), 2));
		return distance <= attackDistance + weakAttackDistanceAdd;
	}

	public void move(int xCoordMoved, int yCoordMoved) {
		xCoord = xCoordMoved;
		yCoord = yCoordMoved;
	}

	public float getLastMovePoints(int xCoordMoved, int yCoordMoved, BattleMap battlePlace) {
		int xDistance = xCoord - xCoordMoved;
		int yDistance = yCoord - yCoordMoved;
		int xMove = -(int)signum(xDistance); // if -1 then move left, if 1 then move right
		int yMove = -(int)signum(yDistance); // if -1 then move up, if 1 then move down
		float lastMovePoints = (float)movePoints;
		float movePointPenalty;
		boolean secondDiagonal = false;
		int xNextPoint = xCoord;
		int yNextPoint = yCoord;
		while ((xDistance != 0) && (yDistance != 0)) {
			xNextPoint = xNextPoint + xMove;
			yNextPoint = yNextPoint + yMove;
			movePointPenalty = penalty.getOrDefault(battlePlace.getFieldByPosition(xNextPoint, yNextPoint), 1f);
			lastMovePoints -= movePointPenalty;
			if (secondDiagonal) {
				lastMovePoints -= movePointPenalty;
			}
			secondDiagonal = !secondDiagonal;
			xDistance += xMove;
			yDistance += yMove;
		}
		while (xDistance != 0) {
			xNextPoint = xNextPoint + xMove;
			movePointPenalty = penalty.getOrDefault(battlePlace.getFieldByPosition(xNextPoint, yCoord), 1f);
			lastMovePoints -= movePointPenalty;
			xDistance += xMove;
		}
		while (yDistance != 0) {
			yNextPoint = yNextPoint + yMove;
			movePointPenalty = penalty.getOrDefault(battlePlace.getFieldByPosition(xCoord, yNextPoint), 1f);
			lastMovePoints -= movePointPenalty;
			yDistance += yMove;
		}
		return lastMovePoints;
	}

	public boolean canMove(int xCoordMoved, int yCoordMoved, BattleMap battlePlace) {
		if (xCoordMoved >= 15 || xCoordMoved < 0 || yCoordMoved >= 15 || yCoordMoved < 0) {
			return false;
		}
		return getLastMovePoints(xCoordMoved, yCoordMoved, battlePlace) >= 0;
	}
}
