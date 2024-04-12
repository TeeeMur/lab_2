package GameSubjects;

import Gamers.Gamer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GameManager<T> {
	public boolean saveGameItemToDirectory(T gameItem, String absolutePath) {
		try {
			ObjectOutputStream fileByDirectory = new ObjectOutputStream(new FileOutputStream(absolutePath));
			fileByDirectory.writeObject(gameItem);
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	public T getGameItemByFilename(String absolutePath){
		try {
			FileInputStream fileByDirectory = new FileInputStream(absolutePath);
			@SuppressWarnings("unchecked")
			T gameItem = (T) (new ObjectInputStream(fileByDirectory).readObject());
			return gameItem;
		} catch (ClassCastException | IOException | ClassNotFoundException ex) {
			return null;
		}
	}
	public boolean deleteGameBattleByName(String absolutePath){
		File mapFile = new File(absolutePath);
		if (mapFile.exists() && mapFile.isFile()) {
			return mapFile.delete();
		}
		else { return false;}
	}

	public static String[][] inputBattleMap(Gamer gamer, int sizeX, int sizeY) {
		ArrayList<ArrayList<String>> resultArrayList = new ArrayList<>();
		while (true) {
			String line = gamer.input();
			if (!line.isEmpty()) {
				resultArrayList.add(new ArrayList<>(Arrays.asList(line.split(" "))));
			}
			else {break;}
		}
		int firstLength = resultArrayList.getFirst().size();
		if (firstLength != sizeX || resultArrayList.size() != sizeY) {
			return null;
		}
		for (int i = 1; i < resultArrayList.size(); i++) {
			if (resultArrayList.get(i).size() != firstLength) {
				return null;
			}
		}
		String[][] result = new String[sizeY][sizeX];
		for (int i = 0; i < sizeY; i++) {
			for (int j = 0; j < sizeX; j++) {
				result[j][i] = resultArrayList.get(j).get(i);
			}
		}
		return result;
	}
}
