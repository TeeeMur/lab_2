package GameSubjects;

import Buildings.*;

import java.io.Serializable;
import java.util.*;

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

	public void upgradeGameBuilding(String buildingName, String type) {
		if (Objects.equals(buildingName, Tavern.NAME)) {
			buildings.get(Tavern.NAME).upgradeBuilding(type);
		}
		else {
			buildings.get(buildingName).upgradeBuilding();
		}
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

	public void addResource(int count, String type) {
		if (!resources.containsKey(type) || count < 0) {
			return;
		}
		resources.put(type, resources.get(type) + count);
	}

	public void spendResource(int count, String type) {
		if (!resources.containsKey(type) || count > resources.get(type)) {
			return;
		}
		resources.put(type, resources.get(type) - count);
	}

	public int getResource(String type) {
		return resources.get(type);
	}

	public HashMap<String, Integer> getResources() {
		HashMap<String, Integer> res = new HashMap<>();
		for (String type : resources.keySet()) {
			res.put(type, resources.get(type));
		}
		return res;
	}

	public static int calculateCost(int health, int attack, int attackDistance, int defense, int move) {
		return health + attack + attackDistance + defense + move + 12;
	}

}
