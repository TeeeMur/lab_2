package Buildings;

import GameSubjects.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Hotel implements Buildable {
	public static final String NAME = "Отель";
	private int level;
	private final int maxLevel;
	private final String costType;
	public static final String PAYMENT_STRING = "Платеж";
	public static final String RESIDENTS_COUNT_STRING = "Жители";
	public static final int PAYMENT = 50;

	private final ArrayList<HashMap<String, Integer>> upgrades = new ArrayList<>() {{
		add(new HashMap<>() {{
			put(Game.BUILDING_UPPER_STRING, 0);
			put(Game.BUILDING_COST_STRING, 0);
		}});
		add(new HashMap<>() {{
			put(Game.BUILDING_UPPER_STRING, 1);
			put(Game.BUILDING_COST_STRING, 80);
		}});
		add(new HashMap<>() {{
			put(Game.BUILDING_UPPER_STRING, 4);
			put(Game.BUILDING_COST_STRING, 180);
		}});
		add(new HashMap<>() {{
			put(Game.BUILDING_UPPER_STRING, 8);
			put(Game.BUILDING_COST_STRING, 500);
		}});
		add(new HashMap<>() {{
			put(Game.BUILDING_UPPER_STRING, 10);
			put(Game.BUILDING_COST_STRING, 800);
		}});
		add(new HashMap<>() {{
			put(Game.BUILDING_UPPER_STRING, 12);
			put(Game.BUILDING_COST_STRING, 1000);
		}});
		add(new HashMap<>() {{
			put(Game.BUILDING_UPPER_STRING, 15);
			put(Game.BUILDING_COST_STRING, 1800);
		}});
	}};

	public Hotel() {
		this.level = 0;
		this.maxLevel = upgrades.size() - 1;
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
		return upgrades.get(level).get(Game.BUILDING_UPPER_STRING);
	}

	@Override
	@Deprecated
	public int getBuildingUpper(String type) {
		if (Objects.equals(type, PAYMENT_STRING)) {
			return PAYMENT;
		}
		return upgrades.get(level).get(Game.BUILDING_UPPER_STRING);
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
