package swipe5;
import java.awt.Point;

import static java.lang.System.out;

import javax.swing.SwingWorker;

import swipe5.SwipePanel.CircleButton;

public class ButtonSpawner extends Thread {
	private CircleButton myButton;
	private SwipePanel myPanel;
	private int currSize;
	private Point centrePt;
	
	private static int animationRate = swipe5.SwipePanel.animationFrameRate;
	private static int buttonSize = swipe5.SwipePanel.buttonSize;
	
	public ButtonSpawner() {
		myButton = null;
		myPanel = null;
	}
	
	public ButtonSpawner(CircleButton button, SwipePanel swipePanel) {
		this.myButton = button;
		this.myPanel = swipePanel;
		currSize = buttonSize - 40;
		centrePt = new Point(myButton.x + buttonSize / 2, myButton.y + buttonSize / 2);
		myButton.width = currSize;
		myButton.height = currSize;
	}
	
	public void run() {
		while (currSize != buttonSize) {
			
			int sizeDiff = Math.abs(buttonSize - currSize);
			
			if (sizeDiff < animationRate) {
				currSize = buttonSize;
				// break and set to full
			}
			
			int sizeChange = sizeDiff / animationRate;
			currSize += sizeChange;
			
			
			int halfway;
			halfway = currSize / 2;
			myButton.x = centrePt.x - halfway;
			myButton.y = centrePt.y - halfway;
			myButton.width = currSize;
			myButton.height = currSize;

			myPanel.repaint();
			
			try {
				sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		//out.println("finish spawning "+ myButton.myChar);
	}

	public void setButton(CircleButton circleButton) {
		this.myButton = circleButton;
	}

	
}
