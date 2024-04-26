package Buildings;

import GameSubjects.Game;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Building implements Buildable {

	private final String name;
	private int level;
	private final int maxLevel;
	private final String costType;

	private final ArrayList<HashMap<String, Integer>> upgrades = new ArrayList<>();

	Building(String name, ArrayList<Integer> costs, ArrayList<Integer> uppers, String costType) {
		this.costType = costType;
		this.name = name;
		this.level = 0;
		this.maxLevel = uppers.size() - 1;
		for (int i = 0; i < uppers.size(); i++) {
			int finalI = i;
			upgrades.add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, uppers.get(finalI));
				put(Game.BUILDING_COST_STRING, costs.get(finalI));
			}});
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public int getLevel(String type) {
		return level;
	}

	@Override
	public int getMaxLevel() {
		return maxLevel;
	}

	@Override
	public void upgradeBuilding() {
		if (level < maxLevel) {
			level += 1;
		}
	}

	@Override
	public void upgradeBuilding(String upgradeType) {
		if (level < maxLevel) {
			level += 1;
		}
	}

	@Override
	public int getBuildingUpper() {
		return upgrades.get(level).get(Game.BUILDING_UPPER_STRING);
	}

	@Override
	public int getBuildingUpper(String type) {
		return upgrades.get(level).getOrDefault(Game.BUILDING_UPPER_STRING, 0);
	}

	@Override
	public int getUpgradeCost() {
		int cost;
		if (level < maxLevel) {
			cost = upgrades.get(level + 1).get(Game.BUILDING_COST_STRING);
		} else {
			cost = 0;
		}
		return cost;
	}

	@Override
	@Deprecated
	public int getUpgradeCost(String upgradeType) {
		int cost;
		if (level < maxLevel) {
			cost = upgrades.get(level + 1).get(Game.BUILDING_COST_STRING);
		} else {
			cost = 0;
		}
		return cost;
	}

	@Override
	public String getCostType() {
		return costType;
	}
}
