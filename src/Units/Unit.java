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
	protected final int attackPoints;
	protected final int attackDistance;
	protected final int movePoints;
	protected final int costPoints;
	protected int xCoord;
	protected int yCoord;

	protected HashMap<String, Float> penalty;

	public Unit(String image, String unitName, ArrayList<Integer> unitParams, ArrayList<Float> unitTypePenalties,
				ArrayList<String> mapBasicFields) {
		mapImage = image;
		name = unitName;
		healthPoints = unitParams.getFirst();
		attackPoints = unitParams.get(1);
		attackDistance = unitParams.get(2);
		defensePoints = unitParams.get(3);
		movePoints = unitParams.get(4);
		costPoints = unitParams.get(5);
		penalty = new HashMap<>();
		penalty.put(mapBasicFields.getFirst(), unitTypePenalties.getFirst());
		penalty.put(mapBasicFields.get(1), unitTypePenalties.get(1));
		penalty.put(mapBasicFields.get(2), unitTypePenalties.get(2));
		penalty.put(mapBasicFields.get(3), unitTypePenalties.get(3));
	}

	public String getMapImage() {
		return mapImage;
	}
	public final String getName() {
		return name;
	}
	public final int getCostPoints() {
		return costPoints;
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

	public final int getyCoord() { return yCoord; }
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
		int xEnemy = enemy.getxCoord(), yEnemy = enemy.getyCoord();
		double distance = sqrt(pow((xCoord - xEnemy), 2) + pow((yCoord - yEnemy), 2));
		return distance <= attackDistance;
	}

	public void move(int xCoordMoved, int yCoordMoved) {
		xCoord = xCoordMoved;
		yCoord = yCoordMoved;
	}
	public boolean canMove(int xCoordMoved, int yCoordMoved, BattleMap battlePlace) {
		if (xCoordMoved >= 15 || xCoordMoved < 0 || yCoordMoved >= 15 || yCoordMoved < 0) {
			return false;
		}
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
		return lastMovePoints >= 0f;
	}
}
