package GameSubjects;

import BattlePlace.BattleMap;

import java.io.*;

public class BattleMapObjectSaver {
	public boolean saveMapToDirectory(BattleMap battleMap, String absolutePath) {
		try {
			ObjectOutputStream fileByDirectory = new ObjectOutputStream(new FileOutputStream(absolutePath));
			fileByDirectory.writeObject(battleMap);
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	public BattleMap getMapByName(String absolutePath){
		try {
			FileInputStream fileByDirectory = new FileInputStream(absolutePath);
			return (BattleMap) (new ObjectInputStream(fileByDirectory).readObject());
		} catch (IOException | ClassNotFoundException ex) {
			return null;
		}
	}
	public boolean deleteMapByName(String absolutePath){
		File mapFile = new File(absolutePath);
		if (mapFile.exists() && mapFile.isFile()) {
			return mapFile.delete();
		}
		else { return false;}
	}
}
