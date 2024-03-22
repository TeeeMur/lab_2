package BattlePlace;

import java.util.ArrayList;
import java.util.Arrays;

public class BattleMap {
	int sizeX, sizeY;
	private final String[][] battleMapMatrix;
	private final ArrayList<String> fieldsToChoose;

	private final ArrayList<String> basicFields = new ArrayList<>(Arrays.asList("▓", "#", "@", "!"));

	public BattleMap(int mapSizeX, int mapSizeY, int difficulty) {
		sizeX = mapSizeX;
		sizeY = mapSizeY;
		battleMapMatrix = new String[mapSizeY][mapSizeX];
		fieldsToChoose = new ArrayList<>();
		fillFieldsArray(difficulty);
		for (int j = 0; j < mapSizeX; j++) {
			battleMapMatrix[0][j] = basicFields.getFirst();
		}
		for (int j = 0; j < mapSizeX; j++) {
			battleMapMatrix[mapSizeY - 1][j] = basicFields.getFirst();
		}
		for (int i = 1; i < mapSizeY - 1; i++) {
			for (int j = 0; j < mapSizeX; j++) {
				battleMapMatrix[i][j] = fieldsToChoose.get((int) (Math.random() * fieldsToChoose.size()));
			}
		}
	}

	private void fillFieldsArray(int difficulty) {
		fieldsToChoose.addAll(basicFields);
		for (int i = difficulty; i < 6 + sizeX + sizeY - 28; i++) {
			fieldsToChoose.add(basicFields.getFirst());
		}
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

	public ArrayList<String> getBasicFields() {
		return basicFields;
	}

	public void placeSmth(String smth, int xCoord, int yCoord) {
		battleMapMatrix[yCoord][xCoord] = smth;
	}
}
