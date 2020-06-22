package zad1;

import java.awt.EventQueue;

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			new Chat("Patryk");
			new Chat("Micha³");
		});
	}
}
