package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;;

/**
 * A label containing a timer for elapsed game time
 * @author yonatan
 *
 */
@SuppressWarnings("serial")
class TimerJLabel extends JLabel{

	private Timer t;
	private long lastResumeTimeInMilli;
	private long elapsedTimeLastPause = 0;
	private boolean isBlink = false;

	TimerJLabel() {

		this.setHorizontalAlignment(CENTER);
		this.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/k-timer-icon.png")));
		this.setFont(new Font(getFont().getName(), Font.PLAIN, 20));

		t = new Timer(500, new clockListener());
		t.setInitialDelay(0);
	}

	void start() {
		resume();
		t.start();
	}

	void pause() {
		elapsedTimeLastPause = calcElapsedMilli();
		isBlink = true;		
	}
	
	void resume() {
		lastResumeTimeInMilli = new Date().getTime();
		isBlink = false;
	}
	
	String getElapsedTimeText() {
		long elapsed = calcElapsedMilli();
		long hr = TimeUnit.MILLISECONDS.toHours(elapsed);
		long min = TimeUnit.MILLISECONDS.toMinutes(elapsed - TimeUnit.HOURS.toMillis(hr));
		long sec = TimeUnit.MILLISECONDS.toSeconds(elapsed - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
		return String.format("%02d:%02d:%02d", hr,min,sec);				
	}

	long calcElapsedMilli() {
		return (new Date().getTime() - lastResumeTimeInMilli) + elapsedTimeLastPause;
	}
	
	
	void killTimer() {
		t.stop();
	}
	
	private class clockListener implements ActionListener {
		private boolean isForeground = false;
		private Color bg = TimerJLabel.this.getBackground();
		private Color fg = TimerJLabel.this.getForeground();
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!isBlink) { //regular mode
				if (isForeground) { // return to show label if needed
					TimerJLabel.this.setForeground(fg);
					isForeground = false;
				}
				TimerJLabel.this.setText(getElapsedTimeText());
			}
			else { // blinking
				if (!isForeground) { // text color is different color than background color
					TimerJLabel.this.setForeground(bg);
					isForeground = true;
				}
				else { //text is in background color, unseen
					TimerJLabel.this.setForeground(fg);
					isForeground = false;
				}
			}
		}

	}

}
