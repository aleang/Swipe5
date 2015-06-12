package swipe5;
import java.awt.Point;

import static java.lang.System.out;

import javax.swing.SwingWorker;

import swipe5.SwipePanel.CircleButton;

public class ButtonMover extends Thread {
	Point myNewPosition;
	CircleButton myButton;
	SwipePanel myPanel;
	int numOfSkipping;
	private int btnPointIndex;
	
	static int animationFrameRate = swipe5.SwipePanel.animationFrameRate;
	static Point[] PTS = swipe5.SwipePanel.PTS;

	public ButtonMover(CircleButton myButton, int numOfSkipping, SwipePanel swipePanel) {
		this.myButton = myButton;
		this.myPanel = swipePanel;
		this.btnPointIndex = myButton.ptIndex;
		
		this.numOfSkipping = numOfSkipping;
	}
	
	public void run() {
		keepSkipping: while (numOfSkipping != 0) {
			//out.println(numOfSkipping);
			int newIndex = Integer.MAX_VALUE;
			if (numOfSkipping > 0) {
				newIndex = (btnPointIndex + 5 + 1) % 5;
			} else {
				newIndex = (btnPointIndex + 5 - 1) % 5;
			}
			btnPointIndex = myButton.ptIndex = newIndex;
			myNewPosition = PTS[btnPointIndex];
			
			
			int moveAmountX, moveAmountY;
			int xDifference, yDifference;
			xDifference = myNewPosition.x - myButton.x;
			yDifference = myNewPosition.y - myButton.y;
			moveAmountX = xDifference / animationFrameRate;
			moveAmountY = yDifference / animationFrameRate;
			
			keepMoving: while (! hasReachedDestination()) {
				
				xDifference = myNewPosition.x - myButton.x;
				yDifference = myNewPosition.y - myButton.y;
				
				if (Math.abs(xDifference) < animationFrameRate && 
						Math.abs(yDifference) < animationFrameRate) {
					// almost there
					myButton.x = myNewPosition.x;
					myButton.y = myNewPosition.y;
				} else {
					//myButton.x += xDifference / animationRate;
					//myButton.y += yDifference / animationRate;
					myButton.x += moveAmountX;
					myButton.y += moveAmountY;
				}
				
				myPanel.repaint();
				
				try {
					sleep(Math.abs(numOfSkipping) > 1 ? 25 :50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} // end of keepMoving
			if (numOfSkipping > 0) {
				numOfSkipping--;
			} else {
				numOfSkipping++;
			}

		}// end of keepSkipping
	
		myPanel.resetAnswerField();
	}

	private boolean hasReachedDestination() {
		return myButton.x == myNewPosition.x && myButton.y == myNewPosition.y;
	}
	
}
