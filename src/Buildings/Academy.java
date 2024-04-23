package Buildings;

import GameSubjects.Game;
import java.util.ArrayList;
import java.util.HashMap;

public class Academy implements Buildable {
	public static final String NAME = "Академия";
	private int level;
	private final int maxLevel;
	private final String costType;

	private final ArrayList<HashMap<String, Integer>> upgrades = new ArrayList<>() {{
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 0);
				put(Game.BUILDING_COST_STRING, 0);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 1);
				put(Game.BUILDING_COST_STRING, 800);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 2);
				put(Game.BUILDING_COST_STRING, 1500);
			}});
			add(new HashMap<>() {{
				put(Game.BUILDING_UPPER_STRING, 3);
				put(Game.BUILDING_COST_STRING, 2400);
			}});
		}};

	public Academy() {
		this.level = 0;
		this.maxLevel = upgrades.size();
		this.costType = Game.ELIXIR;
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
	@Deprecated
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
