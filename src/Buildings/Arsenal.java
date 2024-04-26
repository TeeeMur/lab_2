package Buildings;

import GameSubjects.Game;

import java.util.ArrayList;
import java.util.Arrays;

public class Arsenal extends Building {
	public static final String NAME = "Арсенал";

	public Arsenal() {
		super(NAME,
			new ArrayList<>(Arrays.asList(0, 100, 300, 750, 1000)),
			new ArrayList<>(Arrays.asList(0, 5, 12, 16, 20)), Game.GOLD);
	}

}
