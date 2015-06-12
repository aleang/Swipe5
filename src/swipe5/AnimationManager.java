package swipe5;
import static java.lang.System.out;

public class AnimationManager extends Thread{
	
	private Thread[] myThreads;
	public static SwipePanel myPanel;
	
	public AnimationManager(Thread[] t, SwipePanel m) {
		this.myThreads = t;
		this.myPanel = m;
	}
	
	public void run() {
		// remove listener when animation is happening!
		myPanel.removeMouseListener(myPanel);
		myPanel.removeMouseMotionListener(myPanel);
		
		for (Thread th: myThreads) {
			th.start();
		}

		for (Thread th: myThreads) {
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// user can now click and do anything after animation
		myPanel.addMouseListener(myPanel);
		myPanel.addMouseMotionListener(myPanel);
	}
}
