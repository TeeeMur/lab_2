package Buildings;

import GameSubjects.Game;

import java.util.ArrayList;
import java.util.Arrays;

public class BlacksmithHouse extends Building{
	public static final String NAME = "Кузница";

	public BlacksmithHouse() {
		super(NAME,
			new ArrayList<>(Arrays.asList(0, 150, 400, 800, 1100)),
			new ArrayList<>(Arrays.asList(0, 7, 12, 18, 24)), Game.GOLD);
	}
}
