package Units;

import GameSubjects.GameBattle;

import java.util.*;

import BattlePlace.BattleMap;

public class Chernomor extends Unit{

	Random random = new Random();

	public Chernomor(String num, HashMap<String, Float> chernomorPenatlies) {
		super(GameBattle.ANSI_YELLOW + num + GameBattle.ANSI_RESET, GameBattle.ANSI_YELLOW + "Черномор" + GameBattle.ANSI_RESET, new ArrayList<>(Arrays.asList(45, 5, 3, 3, 4, 10)),
				chernomorPenatlies);
	}

	private boolean portalCreateCondition(ArrayList<ArrayList<Integer>> existingPortals, BattleMap battleMap, int xStartCoord, int yStartCoord, int xEndCoord, int yEndCoord) {
		for (ArrayList<Integer> eachPortal: existingPortals) {
			if (xStartCoord == eachPortal.getFirst() && yStartCoord == eachPortal.get(1) ||
					xStartCoord == eachPortal.get(2) && yStartCoord == eachPortal.get(3) ||
					yEndCoord == eachPortal.getFirst() && yEndCoord == eachPortal.get(1) ||
					xEndCoord == eachPortal.get(2) && yEndCoord == eachPortal.get(3)
			) {
				return false;
			}
		}
		return Objects.equals(battleMap.getFieldByPosition(xStartCoord, yStartCoord), battleMap.getMapBasicFields().getFirst()) &&
				Objects.equals(battleMap.getFieldByPosition(xEndCoord, yEndCoord), battleMap.getMapBasicFields().getFirst()) &&
				(Math.sqrt(Math.pow((xEndCoord - xStartCoord), 2) + Math.pow((yEndCoord - yStartCoord), 2)) >= 2);
	}

	public ArrayList<ArrayList<Integer>> createPortal(BattleMap battleMap, ArrayList<ArrayList<Integer>> existingPortals) {
		int xStartCoord = random.nextInt(battleMap.getSizeX());
		int yStartCoord = random.nextInt(battleMap.getSizeY());
		int xEndCoord = random.nextInt(battleMap.getSizeX());
		int yEndCoord = random.nextInt(battleMap.getSizeY());
		while (!portalCreateCondition(existingPortals, battleMap, xStartCoord, yStartCoord, xEndCoord, yEndCoord)
		) {
			xStartCoord = random.nextInt(battleMap.getSizeX());
			yStartCoord = random.nextInt(battleMap.getSizeY());
			xEndCoord = random.nextInt(battleMap.getSizeX());
			yEndCoord = random.nextInt(battleMap.getSizeY());
		}
		int finalXStartCoord = xStartCoord;
		int finalYStartCoord = yStartCoord;
		int finalXEndCoord = xEndCoord;
		int finalYEndCoord = yEndCoord;
		return new ArrayList<>() {{
			add(new ArrayList<>() {{add(finalXStartCoord);add(finalYStartCoord);}});
			add(new ArrayList<>() {{add(finalXEndCoord);add(finalYEndCoord);}});
		}};
	}
}
