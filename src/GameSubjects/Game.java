package GameSubjects;

import BattlePlace.BattleMap;
import Buildings.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game implements Serializable {

	HashMap<String, String> mapPaths;
	private final HashMap<String, Integer> resources;
	public static final String GOLD = "Золото";
	public static final String ELIXIR = "Эликсир";
	public static final String BUILDING_UPPER_STRING = "upper";
	public static final String BUILDING_COST_STRING = "cost";
	public static final String STEALER = "Вор";
	LocalDate dateOfLastSession;
	Logger logger = Logger.getLogger(Game.class.getName());

	private final HashMap<String, Buildable> buildings = new HashMap<>() {{
		put(Healer.NAME, new Healer());
		put(BlacksmithHouse.NAME, new BlacksmithHouse());
		put(Arsenal.NAME, new Arsenal());
		put(Academy.NAME, new Academy());
		put(Hotel.NAME, new Hotel());
		put(Market.NAME, new Market());
		put(Tavern.NAME, new Tavern());
		put(Hostel.NAME, new Hostel());
	}};

	private final HashMap<String, HashMap<String, ArrayList<Integer>>> academyUnits;
	private HashMap<String, HashMap<String, ArrayList<Integer>>> hostelUnits;

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
		dateOfLastSession = LocalDate.now();
		hostelUnits = new HashMap<>() {{
			for (String type : GameBattle.getUnitsTypes()) {
				put(type, new HashMap<>());
			}
		}};
	}

	public void addUnit(String type, String name, ArrayList<Integer> specs) {
		if (getAcademyUnitsSize() == buildings.get(Academy.NAME).getBuildingUpper() || specs.size() != 6 || !academyUnits.containsKey(type)) {
			return;
		}
		academyUnits.get(type).put(name, specs);
	}

	public void deleteUnit(String name) {
		boolean found = false;
		String unitType = "";
		for (String type : academyUnits.keySet()) {
			if (academyUnits.get(type).containsKey(name)) {
				found = true;
				unitType = type;
				break;
			}
		}
		if (academyUnits.isEmpty() || !found) {
			return;
		}
		academyUnits.get(unitType).remove(name);
	}

	public HashMap<String, HashMap<String, ArrayList<Integer>>> getAcademyUnits() {
		return academyUnits;
	}

	public HashMap<String, HashMap<String, ArrayList<Integer>>> getHostelUnits(int difficulty) {
		Random r = new Random();
		hostelUnits = new HashMap<>() {{
			for (String type : GameBattle.getUnitsTypes()) {
				put(type, new HashMap<>());
			}
		}};
		if (buildings.get(Hostel.NAME).getLevel() == 0) {
			return hostelUnits;
		}
		if (r.nextFloat() < 0.5 && difficulty >= 4) {
			HashMap<String, HashMap<String, ArrayList<Integer>>> returnHostelUnits = new HashMap<>();
			returnHostelUnits.put(GameBattle.getUnitsTypes().getFirst(), new HashMap<>() {{
				put(STEALER, new ArrayList<>(Arrays.asList(70, 2, 4, 2, 3, 0)));
			}});
			return returnHostelUnits;
		}
		else {
			for (int i = 0; i < buildings.get(Hostel.NAME).getBuildingUpper(); i++) {
				String hostelUnitType = GameBattle.getUnitsTypes().get(r.nextInt(GameBattle.getUnitsTypes().size()));
				int hostelUnitTypeCount = GameBattle.getUnitsTyping().get(hostelUnitType).size();
				String hostelUnitName = GameBattle.getUnitsTyping().get(hostelUnitType).get(r.nextInt(hostelUnitTypeCount));
				while (hostelUnits.get(hostelUnitType).containsKey(hostelUnitName + "_H")) {
					hostelUnitType = GameBattle.getUnitsTypes().get(r.nextInt(GameBattle.getUnitsTypes().size()));
					hostelUnitTypeCount = GameBattle.getUnitsTyping().get(hostelUnitType).size();
					hostelUnitName = GameBattle.getUnitsTyping().get(hostelUnitType).get(r.nextInt(hostelUnitTypeCount));
				}
				hostelUnits.get(hostelUnitType).put(hostelUnitName + "_H", GameBattle.getDefaultUnitsSpecsMap().get(hostelUnitName));
			}
			return hostelUnits;
		}
	}

	public void upgradeGameBuilding(String buildingName, String type) {
		if (Objects.equals(buildingName, Tavern.NAME)) {
			buildings.get(Tavern.NAME).upgradeBuilding(type);
			logger.log(Level.WARNING, "У здания " + buildingName + " улучшен параметр " +
					type + " до уровня " + buildings.get(buildingName).getLevel());
		}
		if (buildings.get(buildingName).getUpgradeCost(type) > resources.get(buildings.get(buildingName).getCostType())) {
			logger.log(Level.SEVERE, "Попытка улучшить здание " + buildingName + " при нехватке ресурса " + type + " для его улучшения." +
					" Количество ресурса " + type + ": " + resources.get(buildings.get(buildingName).getCostType()) + ", стоимость улучшения: " + buildings.get(buildingName).getUpgradeCost(type) + ".");
			return;
		}
		else {
			buildings.get(buildingName).upgradeBuilding();
		}
		if (buildings.get(buildingName).getLevel() == 1) {
			logger.log(Level.INFO, "Построено здание: " + buildingName);
		}
		else {
			logger.log(Level.INFO, "Здание " + buildingName + " улучшено до " + buildings.get(buildingName).getLevel() + " уровня.");
		}
	}

	public void addMapPath(String name, String mapPath) {
		mapPaths.put(name, mapPath);
	}

	public void removeMapPath(String name) {
		mapPaths.remove(name);
	}

	public HashMap<String, String> getMapPaths() {
		GameManager<BattleMap> gameManager = new GameManager<>();
		ArrayList<String> mapPathsToDelete = new ArrayList<>();
		for (String mapPathName: mapPaths.keySet()) {
			if (gameManager.getGameItemByFilename(mapPaths.get(mapPathName)) == null) {
				mapPathsToDelete.add(mapPathName);
			}
		}
		for (String mapPathName: mapPathsToDelete) {
			mapPaths.remove(mapPathName);
		}
		return mapPaths;
	}

	public HashMap<String, Buildable> getBuildings() {
		HashMap<String, Buildable> returnBuildings = new HashMap<>();
		for (String buildingName: buildings.keySet()) {
			if (buildings.get(buildingName).getLevel() != 0) {
				returnBuildings.put(buildingName, buildings.get(buildingName));
			}
		}
		return returnBuildings;
	}

	public HashMap<String, Buildable> getDefaultBuildings() {
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

	public static int calculateUnitCost(int health, int attack, int attackDistance, int defense, int move) {
		return health + attack + attackDistance + defense + move + 12;
	}

	public int refreshResourcesFromHotel(String type) {
		if (buildings.get(Hotel.NAME).getLevel() == 0) {
			return 0;
		}
		LocalDate localDate = LocalDate.now();
		boolean newDate = localDate.getYear() > dateOfLastSession.getYear() ||
				localDate.getDayOfYear() > dateOfLastSession.getDayOfYear();
		if (resources.containsKey(type) && newDate) {
			int days = (localDate.getYear() - dateOfLastSession.getYear()) * 365 +
					localDate.getDayOfYear() - dateOfLastSession.getDayOfYear();
			int count = buildings.get(Hotel.NAME).getBuildingUpper() * Hotel.PAYMENT * days;
			resources.put(type, resources.get(type) + count);
			dateOfLastSession = LocalDate.now();
			return count;
		}
		return 0;
	}

	private int getAcademyUnitsSize() {
		int count = 0;
		for (String unitType: academyUnits.keySet()) {
			count += academyUnits.get(unitType).size();
		}
		return count;
	}

}
