package Buildings;

import GameSubjects.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public enum Building implements Buildable {

	HEALER ("Лекарь",
			new ArrayList<>(Arrays.asList(0, 100, 350, 750, 1000)),
			new ArrayList<>(Arrays.asList(0, 6, 15, 18, 22)), Game.ELIXIR),
	BLACKSMITH_HOUSE ("Кузница",
			new ArrayList<>(Arrays.asList(0, 150, 400, 800, 1100)),
			new ArrayList<>(Arrays.asList(0, 7, 12, 18, 24)), Game.GOLD),
	ARSENAL ("Арсенал",
			new ArrayList<>(Arrays.asList(0, 100, 300, 750, 1000)),
			new ArrayList<>(Arrays.asList(0, 5, 12, 16, 20)), Game.GOLD);

	private final String name;
	private int level;
	private final int maxLevel;
	private final String costType;

	private final ArrayList<HashMap<String, Integer>> upgrades;

	Building(String name, ArrayList<Integer> costs, ArrayList<Integer> uppers, String costType) {
		this.costType = costType;
		this.name = name;
		this.level = 0;
		this.maxLevel = uppers.size();
		this.upgrades = new ArrayList<>() {{
			for (int i = 0; i < uppers.size(); i++) {
				int finalI = i;
				add(new HashMap<>(){{
					put(Game.BUILDING_UPPER_STRING, uppers.get(finalI));
					put(Game.BUILDING_COST_STRING, costs.get(finalI));
				}});
			}
		}};
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
	public int getBuildingUpper() {
		return upgrades.get(level).get(Game.BUILDING_UPPER_STRING);
	}

	@Override
	public int getBuildingUpper(String type) {
		return 0;
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
