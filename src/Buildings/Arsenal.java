package Buildings;

import java.util.ArrayList;

public class Arsenal implements Building {
	private final String name = "Арсенал";
	private int level = 0;

	private final ArrayList<Integer> upgrades = new ArrayList<>() {{
		add(10);
		add(15);
		add(25);
	}};

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
		return 3;
	}

	@Override
	public void upgradeBuilding() {
		if (level < 4) {
			level += 1;
		}
	}
}
