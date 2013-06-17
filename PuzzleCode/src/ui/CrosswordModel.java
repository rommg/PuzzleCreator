package ui;

public class CrosswordModel {

	static int calculateScore(int difficulty, long timeElapsed, Integer usedHints) {
		return (1000 + difficulty * 200 +  -(int )Math.round(timeElapsed / 1000) - (int)Math.round(usedHints * 1.5)); 
	}
}
