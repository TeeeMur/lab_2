package GameInterfaces;

import Gamers.Gamer;

import java.util.ArrayList;

public class InputChecker {

	public static final ArrayList<String> ANSWER_CHECK_LIST = new ArrayList<>() {{
			add("да");
			add("нет");
		}};

	public static boolean isNotNumeric(String str) {
		try {
			Float.parseFloat(str);
			return false;
		} catch (NumberFormatException e) {
			return true;
		}
	}

	public static String checkAnswer(Gamer gamer, String answ, ArrayList<String> checkList) {
		int exitCounter = 1;
		while (!checkList.contains(answ.toLowerCase())) {
			if (exitCounter++ > 6) {
				System.out.println("Ты слишком долго пытался ввести ответ...");
				System.exit(0);
			}
			System.out.print("Ты ввел что-то не то, введи еще раз:");
			answ = gamer.input().toLowerCase().split(" ")[0];
		}
		return answ;
	}
}
