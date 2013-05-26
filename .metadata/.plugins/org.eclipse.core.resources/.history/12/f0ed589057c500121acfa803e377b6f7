package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

class TimerWidget extends JLabel{

	private Timer t;

	TimerWidget(ActionListener clockListener) {

		this.setHorizontalAlignment(CENTER);
		//this.setIcon((Icon) new ImageIcon(this.getResource("/resources/k-timer.png")));

		t = new Timer(1000, clockListener);
		t.setInitialDelay(0);
	}

	void start() {
		t.start();
	}

	void pause() {
		t.stop();
	}

	void reset() {
		t.restart();
	}
}
