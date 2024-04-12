package GameSubjects;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

	private final String GOLD = "Золото";
	private final String ELIXIR = "Эликсир";

	private final HashMap<String, Integer> resources;
	private final ArrayList<String> buildingsNames = new ArrayList<>() {{
		add("Дом лекаря");
		add("Таверна");
		add("Кузница");
		add("Арсенал");
		add("Академия");
		add("Рынок");
		add("Ремесленная мастерская");
	}};
	private final HashMap<String, Integer> buildings;

	Game() {
		buildings = new HashMap<>();
		resources = new HashMap<>() {{
			put(GOLD, 0);
			put(ELIXIR, 0);
		}};
	}

	public int createNewBuilding(String buildingName) {
		if (!buildingsNames.contains(buildingName) || buildings.containsKey(buildingName)) {
			return 1;
		}
		buildings.put(buildingName, 1);
		return 0;
	}
	public int upgradeBuilding(String buildingName) {
		if (!buildingsNames.contains(buildingName) || buildings.containsKey(buildingName)) {
			return 1;
		}
		buildings.put(buildingName, 1);
		return 0;
	}

	HashMap<String, Integer> getBuildings() {
		return buildings;
	}

	public void addElixir(int count) {
		if (count < 0) {
			return;
		}
		int pre = resources.get(ELIXIR);
		resources.put(ELIXIR, pre + count);
	}

	public void addGold(int count) {
		if (count < 0) {
			return;
		}
		int pre = resources.get(GOLD);
		resources.put(GOLD, pre + count);
	}

	public int spendElixir(int count) {
		int pre = resources.get(ELIXIR);
		if (count > 0 | pre < count) {
			return 1;
		}
		resources.put(ELIXIR, pre - count);
		return 0;
	}

	public int spendGold(int count) {
		int pre = resources.get(GOLD);
		if (count > 0 | pre < count) {
			return 1;
		}
		resources.put(GOLD, pre - count);
		return 0;
	}

	public int getElixir() {
		return resources.get(ELIXIR);
	}

	public int getGold() {
		return resources.get(GOLD);
	}
}
