package GameSubjects;

import Buildings.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Game implements Serializable {

	HashMap<String, String> mapPaths;
	private final HashMap<String, Integer> resources;
	public static final String GOLD = "Золото";
	public static final String ELIXIR = "Эликсир";
	public static final String BUILDING_UPPER_STRING = "upper";
	public static final String BUILDING_COST_STRING = "cost";

	private final HashMap<String, Buildable> buildings = new HashMap<>() {{
		put(Building.HEALER.getName(), Building.HEALER);
		put(Building.BLACKSMITH_HOUSE.getName(), Building.BLACKSMITH_HOUSE);
		put(Building.ARSENAL.getName(), Building.ARSENAL);
		put(Academy.NAME, new Academy());
		put(Hotel.NAME, new Hotel());
		put(Market.NAME, new Market());
		put(Tavern.NAME, new Tavern());
	}};

	private final HashMap<String, HashMap<String, ArrayList<Integer>>> academyUnits;

	public Game(int firstGold, int firstElixir) {
		resources = new HashMap<>() {{
			put(GOLD, firstGold);
			put(ELIXIR, firstElixir);
		}};
		mapPaths = new HashMap<>();
		academyUnits = new HashMap<>() {{
			for (String type : GameBattle.getUnitsTypes()) {
				put(type, new HashMap<>());
			}
		}};
	}

	public void addUnit(String type, String name, ArrayList<Integer> specs) {
		if (academyUnits.size() == 3 || specs.size() != 6 || !academyUnits.containsKey(type)) {
			return;
		}
		academyUnits.get(type).put(name, specs);
	}

	public void deleteUnit(String type, String name, ArrayList<Integer> specs) {
		if (academyUnits.size() == 3 || specs.size() != 6 || !academyUnits.containsKey(type)
		|| !academyUnits.get(type).containsKey(name)) {
			return;
		}
		academyUnits.get(type).remove(name);
	}

	public HashMap<String, HashMap<String, ArrayList<Integer>>> getAcademyUnits() {
		return academyUnits;
	}

	public void createNewGameBuildingUpper(String buildingName) {
		if (buildings.get(buildingName).getLevel() != 0) {
			return;
		}
		buildings.get(buildingName).upgradeBuilding();
	}

	public void upgradeGameBuilding(String buildingName) {
		buildings.get(buildingName).upgradeBuilding();
	}

	public void addMapPath(String name, String mapPath) {
		mapPaths.put(name, mapPath);
	}

	public void removeMapPath(String name) {
		mapPaths.remove(name);
	}

	public HashMap<String, String> getMapPaths() {
		return mapPaths;
	}

	public HashMap<String, Buildable> getBuildings() {
		return buildings;
	}

	public Set<String> getBuildingsNames() {
		return buildings.keySet();
	}

	public void addElixir(int count) {
		if (count < 0) {
			return;
		}
		int pre = resources.getOrDefault(ELIXIR, 0);
		resources.put(ELIXIR, pre + count);
	}

	public void addGold(int count) {
		if (count < 0) {
			return;
		}
		int pre = resources.getOrDefault(GOLD, 0);
		resources.put(GOLD, pre + count);
	}

	public void spendElixir(int count) {
		int pre = resources.get(ELIXIR);
		if (count > 0 | pre < count) {
			return;
		}
		resources.put(ELIXIR, pre - count);
	}

	public void spendGold(int count) {
		int pre = resources.get(GOLD);
		if (count > 0 | pre < count) {
			return;
		}
		resources.put(GOLD, pre - count);
	}

	public int getElixir() {
		return resources.get(ELIXIR);
	}

	public int getGold() {
		return resources.get(GOLD);
	}

}
