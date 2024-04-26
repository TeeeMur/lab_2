package Gamers;

import java.util.Scanner;

public class Gamer {
	Scanner sc = new Scanner(System.in);

	public String input() {
		return sc.nextLine();
	}

	public String inputOneWord() {
		return sc.nextLine().toLowerCase().split(" ")[0];
	}

}
