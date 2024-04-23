package Buildings;

import GameSubjects.Game;
import java.util.ArrayList;
import java.util.HashMap;

public class Market implements Buildable {
	public static final String NAME = "Рынок";
	private int level;
	private final int maxLevel;
	private final String costType;

	private final ArrayList<HashMap<String, Integer>> upgrades = new ArrayList<>() {{
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 100);
				put(Game.BUILDING_COST_STRING, 0);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 25);
				put(Game.BUILDING_COST_STRING, 100);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 20);
				put(Game.BUILDING_COST_STRING, 300);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 12);
				put(Game.BUILDING_COST_STRING, 650);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 8);
				put(Game.BUILDING_COST_STRING, 1200);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 5);
				put(Game.BUILDING_COST_STRING, 1400);
			}});
		}};

	public Market() {
		this.level = 0;
		this.maxLevel = upgrades.size();
		this.costType = Game.GOLD;
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
		return upgrades.get(level).getOrDefault(Game.BUILDING_UPPER_STRING, 0);
	}

	@Override
	@Deprecated
	public int getBuildingUpper(String type) {
		return upgrades.get(level).getOrDefault(Game.BUILDING_UPPER_STRING, 0);
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
	@Deprecated
	public int getUpgradeCost(String upgradeType) {
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
