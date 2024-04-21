package Buildings;

import java.io.Serializable;

public interface Buildable extends Serializable {

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
