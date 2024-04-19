package Buildings;

import GameSubjects.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Tavern implements Buildable{
	public static final String PENALTY_TYPE = "PENALTY";
	public static final String MOVE_TYPE = "MOVE";
	public static final String NAME = "Таверна";
	private int level;
	private int moveLevel;
	private int penaltyLevel;
	private final int maxLevel;
	private final String costType;

	private final ArrayList<HashMap<String, Integer>> upgrades = new ArrayList<>() {{
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 0);
				put(Game.BUILDING_COST_STRING, 0);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 250);
				put(Game.BUILDING_COST_STRING, 5);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 600);
				put(Game.BUILDING_COST_STRING, 10);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 800);
				put(Game.BUILDING_COST_STRING, 15);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 1100);
				put(Game.BUILDING_COST_STRING, 18);
			}});
		}};

	public Tavern() {
		level = 0;
		maxLevel = upgrades.size();
		costType = Game.ELIXIR;
		moveLevel = 0;
		penaltyLevel = 0;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public int getMaxLevel() {
		return maxLevel;
	}

	@Override
	@Deprecated
	public void upgradeBuilding() {
	}

	public void upgradeBuilding(String upgradeType) {//исправить: сделать так, чтобы и move, и penalty
		//можно было улучшать до максимального уровня
		if (level < maxLevel) {
			level += 1;
			if (Objects.equals(upgradeType, PENALTY_TYPE)) {
				penaltyLevel += 1;
			}
			else {
				moveLevel += 1;
			}
		}
	}

	@Override
	@Deprecated
	public int getBuildingUpper() {
		return upgrades.get(level).get(Game.BUILDING_UPPER_STRING);
	}

	public int getBuildingUpper(String upperType) {
		if (Objects.equals(upperType, PENALTY_TYPE)) {
			return upgrades.get(penaltyLevel).get(Game.BUILDING_UPPER_STRING);
		}
		else if (Objects.equals(upperType, MOVE_TYPE)){
			return upgrades.get(moveLevel).get(Game.BUILDING_UPPER_STRING);
		}
		else {
			return 0;
		}
	}

	@Override
	public int getUpgradeCost() {
		int cost;
		if (level < maxLevel) {
			cost = upgrades.get(level + 1).get(Game.BUILDING_COST_STRING);
		}
		else {
			cost = 0;
		}
		return cost;
	}

	@Override
	public String getCostType() {
		return costType;
	}
}
