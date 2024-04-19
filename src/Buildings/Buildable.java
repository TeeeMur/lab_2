package Buildings;

public interface Buildable {

	String getName();

	int getLevel();

	int getMaxLevel();

	void upgradeBuilding();

	int getBuildingUpper();

	int getBuildingUpper(String type);

	int getUpgradeCost();

	String getCostType();
}
