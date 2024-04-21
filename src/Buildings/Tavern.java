package Buildings;

import GameSubjects.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Tavern implements Buildable, Serializable {
	public static final String PENALTY_TYPE = "PENALTY";
	public static final String MOVE_TYPE = "MOVE";
	public static final String NAME = "Таверна";
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
				put(Game.BUILDING_UPPER_STRING, 5);
				put(Game.BUILDING_COST_STRING, 250);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 10);
				put(Game.BUILDING_COST_STRING, 600);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 15);
				put(Game.BUILDING_COST_STRING, 800);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 18);
				put(Game.BUILDING_COST_STRING, 1100);
			}});
		}};

	public Tavern() {
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
		return moveLevel + penaltyLevel;
	}

	@Override
	public int getLevel(String type) {
		if (type.equals(MOVE_TYPE)) {
			return moveLevel;
		}
		else if (type.equals(PENALTY_TYPE)) {
			return penaltyLevel;
		}
		return -1;
	}

	@Override
	public int getMaxLevel() {
		return maxLevel;
	}

	@Override
	@Deprecated
	public void upgradeBuilding() {
	}

	@Override
	public void upgradeBuilding(String upgradeType) {
		if (Objects.equals(upgradeType, PENALTY_TYPE) && penaltyLevel != maxLevel) {
			penaltyLevel++;
		}
		else if (Objects.equals(upgradeType, MOVE_TYPE) && moveLevel != maxLevel) {
			moveLevel++;
		}
	}

	@Override
	@Deprecated
	public int getBuildingUpper() {
		return -1;
	}

	@Override
	public int getBuildingUpper(String upperType) {
		if (Objects.equals(upperType, PENALTY_TYPE)) {
			return upgrades.get(penaltyLevel).get(Game.BUILDING_UPPER_STRING);
		}
		else if (Objects.equals(upperType, MOVE_TYPE)){
			return upgrades.get(moveLevel).get(Game.BUILDING_UPPER_STRING);
		}
		else {
			return -1;
		}
	}

	@Override
	public int getUpgradeCost() {
		if (moveLevel < penaltyLevel && moveLevel < maxLevel) {
			return upgrades.get(moveLevel + 1).get(Game.BUILDING_COST_STRING);
		}
		else if (penaltyLevel < maxLevel){
			return upgrades.get(penaltyLevel + 1).get(Game.BUILDING_COST_STRING);
		}
		return -1;
	}

	@Override
	public int getUpgradeCost(String upgradeType) {
		if (Objects.equals(upgradeType, PENALTY_TYPE) && penaltyLevel < maxLevel) {
			return upgrades.get(penaltyLevel + 1).get(Game.BUILDING_COST_STRING);
		}
		else if (Objects.equals(upgradeType, MOVE_TYPE) && moveLevel < maxLevel) {
			return upgrades.get(moveLevel + 1).get(Game.BUILDING_COST_STRING);
		}
		else {
			return -1;
		}
	}

	@Override
	public String getCostType() {
		return costType;
	}
}
