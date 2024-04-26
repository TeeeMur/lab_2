package Buildings;

import GameSubjects.Game;

import java.util.ArrayList;
import java.util.Arrays;

public class Healer extends Building {
	public static final String NAME = "Лекарь";

	public Healer() {
		super(NAME,
				new ArrayList<>(Arrays.asList(0, 100, 350, 750, 1000)),
				new ArrayList<>(Arrays.asList(0, 6, 15, 18, 22)), Game.ELIXIR);
	}
}
