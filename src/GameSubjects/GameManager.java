package GameSubjects;

import Gamers.Gamer;

import java.io.*;
import java.util.*;

public class GameManager<T> {
	public boolean saveGameItemToDirectory(T gameItem, String absolutePath) {
		try {
			ObjectOutputStream fileByDirectory = new ObjectOutputStream(new FileOutputStream(absolutePath));
			fileByDirectory.writeObject(gameItem);
			fileByDirectory.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public boolean checkIsDirectory(String absolutePath) {
		File file = new File(absolutePath);
		return file.isDirectory();
	}

	public T getGameItemByFilename(String absolutePath){
		try {
			FileInputStream fileByDirectory = new FileInputStream(absolutePath);
			ObjectInputStream inputStream = new ObjectInputStream(fileByDirectory);
			@SuppressWarnings("unchecked")
			T gameItem = (T) (inputStream.readObject());
			inputStream.close();
			return gameItem;
		} catch (ClassCastException | IOException | ClassNotFoundException ex) {
			return null;
		}
	}

	public static Set<String> getStringMapBasicFields(String[][] map){
		Set<String> mapFields = new HashSet<>();
		for (String[] strings : map) {
			mapFields.addAll(Arrays.asList(strings));
		}
		return mapFields;
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
		if (resultArrayList.isEmpty()) {
			return null;
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
