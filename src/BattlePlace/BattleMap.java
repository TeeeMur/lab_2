package BattlePlace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class BattleMap implements Serializable {
	int sizeX, sizeY;

	private static final int maxSizeX = 20;
	private static final int maxSizeY = 20;
	private static final int minSizeX = 6;
	private static final int minSizeY = 6;

	private int maxUnitsOnLine;
	private final String[][] battleMapMatrix;
	private static final ArrayList<String> defaultFields = new ArrayList<>(Arrays.asList("▓", "#", "@", "!"));
	private ArrayList<String> mapBasicFields = defaultFields;

	public BattleMap(int mapSizeX, int mapSizeY, int difficulty) {
		sizeX = mapSizeX;
		sizeY = mapSizeY;
		maxUnitsOnLine = sizeX - 2;
		battleMapMatrix = new String[mapSizeY][mapSizeX];
		ArrayList<String> fieldsToChoose = new ArrayList<>(defaultFields);
		for (int i = difficulty; i < 6 + sizeX + sizeY - 28; i++) {
			fieldsToChoose.add(mapBasicFields.getFirst());
		}
		for (int j = 0; j < mapSizeX; j++) {
			battleMapMatrix[0][j] = mapBasicFields.getFirst();
		}
		for (int j = 0; j < mapSizeX; j++) {
			battleMapMatrix[mapSizeY - 1][j] = mapBasicFields.getFirst();
		}
		for (int i = 1; i < mapSizeY - 1; i++) {
			for (int j = 0; j < mapSizeX; j++) {
				battleMapMatrix[i][j] = fieldsToChoose.get((int) (Math.random() * fieldsToChoose.size()));
			}
		}
	}

	public BattleMap(String[][] inputBattleMapMatrix, ArrayList<String> basicFields) {
		mapBasicFields = new ArrayList<>(basicFields);
		maxUnitsOnLine = sizeX - 2;
		sizeX = inputBattleMapMatrix[0].length;
		sizeY = inputBattleMapMatrix.length;
		battleMapMatrix = new String[sizeY][sizeX];
		System.arraycopy(inputBattleMapMatrix, 0, battleMapMatrix, 0, sizeY);
	}

	public String[] getBattleMapLine(int index) {
		return battleMapMatrix[index];
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getSizeX() {
		return sizeX;
	}

	public String getFieldByPosition(int xCoord, int yCoord) {
		return battleMapMatrix[yCoord][xCoord];
	}

	public static ArrayList<String> getDefaultFields() {
		return defaultFields;
	}

	public ArrayList<String> getMapBasicFields() {
		return mapBasicFields;
	}

	public void placeSmth(String smth, int xCoord, int yCoord) {
		battleMapMatrix[yCoord][xCoord] = smth;
	}

	public static int getMaxSizeX() {return maxSizeX;}
	public static int getMaxSizeY() {return maxSizeY;}
	public static int getMinSizeX() {return minSizeX;}
	public static int getMinSizeY() {return minSizeY;}

	public int getMaxUnitsOnLine() {
		return maxUnitsOnLine;
	}
}
