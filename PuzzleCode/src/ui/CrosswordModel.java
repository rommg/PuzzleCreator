package ui;

public class CrosswordModel {

	static int calculateScore(int difficulty, long timeElapsed, Integer usedHints) {
		int score =  (1000 + difficulty * 200 +  -(int )Math.round(timeElapsed / 1000) - (int)Math.round(usedHints * 1.5));
		score = (score > 0) ? score : 0;
		
		return score;
	}
}
