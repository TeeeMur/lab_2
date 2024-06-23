package BattlePlace;

import GameSubjects.GameBattle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BattleMap implements Serializable {
	int sizeX, sizeY;

	private static final int maxSizeX = 16;
	private static final int maxSizeY = 16;
	private static final int minSizeX = 6;
	private static final int minSizeY = 6;

	private final int maxUnitsOnLine;
	private final String[][] battleMapMatrix;
	private static final ArrayList<String> DEFAULT_FIELDS = new ArrayList<>(Arrays.asList("▓", "#", "@", "!"));
	private ArrayList<String> mapBasicFields = DEFAULT_FIELDS;
	private final HashMap<String, HashMap<String, Float>> penalties;
	private static final HashMap<String, HashMap<String, Float>> DEFAULT_PENALTIES = new HashMap<>(){{
		put(GameBattle.getUnitsTypes().getFirst(), new HashMap<>(){{
			put(DEFAULT_FIELDS.getFirst(), 1f);
			put(DEFAULT_FIELDS.get(1), 1.5f);
			put(DEFAULT_FIELDS.get(2), 2f);
			put(DEFAULT_FIELDS.get(3), 1.2f);
		}});
		put(GameBattle.getUnitsTypes().get(1), new HashMap<>(){{
			put(DEFAULT_FIELDS.getFirst(), 1f);
			put(DEFAULT_FIELDS.get(1), 1.8f);
			put(DEFAULT_FIELDS.get(2), 2.2f);
			put(DEFAULT_FIELDS.get(3), 1f);
		}});
		put(GameBattle.getUnitsTypes().get(2), new HashMap<>(){{
			put(DEFAULT_FIELDS.getFirst(), 1f);
			put(DEFAULT_FIELDS.get(1), 2.2f);
			put(DEFAULT_FIELDS.get(2), 1.2f);
			put(DEFAULT_FIELDS.get(3), 1.5f);
		}});
	}};

	public BattleMap(int mapSizeX, int mapSizeY, int difficulty) {
		penalties = DEFAULT_PENALTIES;
		sizeX = mapSizeX;
		sizeY = mapSizeY;
		maxUnitsOnLine = mapSizeX - 3;
		battleMapMatrix = new String[mapSizeY][mapSizeX];
		ArrayList<String> fieldsToChoose = new ArrayList<>(DEFAULT_FIELDS);
		for (int i = difficulty; i < 6 + sizeX + sizeY - 28; i++) {
			fieldsToChoose.add(mapBasicFields.getFirst());
		}
		for (int i = 0; i < mapSizeY; i++) {
			for (int j = 0; j < mapSizeX; j++) {
				battleMapMatrix[i][j] = fieldsToChoose.getFirst();
			}
		}
		if (difficulty != 0) {
			for (int i = 1; i < mapSizeY - 1; i++) {
				for (int j = 0; j < mapSizeX; j++) {
					battleMapMatrix[i][j] = fieldsToChoose.get((int) (Math.random() * fieldsToChoose.size()));
				}
			}
		}

	}

	public BattleMap(String[][] inputBattleMapMatrix, ArrayList<String> basicFields, HashMap<String, HashMap<String, Float>> penalties) {
		mapBasicFields = new ArrayList<>(basicFields);
		this.penalties = penalties;
		sizeX = inputBattleMapMatrix[0].length;
		sizeY = inputBattleMapMatrix.length;
		maxUnitsOnLine = sizeX - 2;
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
		return DEFAULT_FIELDS;
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

	public HashMap<String, HashMap<String, Float>> getPenalties() {
		return penalties;
	}

	public static HashMap<String, HashMap<String, Float>> getDefaultPenalties() {
		return DEFAULT_PENALTIES;
	}
}
