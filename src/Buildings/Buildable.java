package Buildings;

public interface Buildable {

	String getName();

	int getLevel();

	int getLevel(String type);

	int getMaxLevel();

	void upgradeBuilding();

	void upgradeBuilding(String upgradeType);

	int getBuildingUpper();

	int getBuildingUpper(String type);

	int getUpgradeCost();

	int getUpgradeCost(String upgradeType);

	String getCostType();
}
