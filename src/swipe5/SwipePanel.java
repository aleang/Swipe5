package swipe5;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.out;

import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

public class SwipePanel extends JPanel implements MouseListener, MouseMotionListener {
	private boolean isButtonTouched[], selectionIsClockwise, isTimerDisplayedRed, isTimerDisplayedBlack;
	private int lastBtnSelected, gameOverBlinkCounter, currentScore;
	private double timeLeft, bonusNewTime;
	private String wordCreated, currentWord, currentDefinition;	
	private CircleButton buttons[];
	private ButtonMover[] btnMovers;
	private ButtonSpawner[] btnSpawners;
	private Color border, fill, textCol, glow;
	private Font myFont;
	private ColourPack myColourPack[], myPack;
	private JTextField txfWord;
	private JTextArea txaDefinition;
	private JScrollPane scrollPane;
	private AnimationManager am;
	
	public static Timer gameTimer, redDisplayer, bonusTimeDisplayer, gameOverDisplayer;
	public static ArrayList<String> wordList;
	public static int animationFrameRate = 5;
	public static int buttonSize = 90;
	static final Point[] PTS = new Point[] {
		new Point(141, 17),
		new Point(228, 80),
		new Point(193,183),
		new Point(87,183),
		new Point(55, 80)
	};
	
	
	public SwipePanel() {
		setBackground(new Color(255, 228, 225));
		setSize(new Dimension(547, 349));
		addMouseListener(this);
		addMouseMotionListener(this);
		
		isTimerDisplayedRed = false;
		timeLeft = 0;
		currentScore = 0;
		myColourPack = getColours();
		myPack = myColourPack[(int)(Math.random()*myColourPack.length)];
		buttons = new CircleButton[] {
				new CircleButton(PTS[0], 0),
				new CircleButton(PTS[1], 1),
				new CircleButton(PTS[2], 2),
				new CircleButton(PTS[3], 3),
				new CircleButton(PTS[4], 4)
		};
		
		btnMovers = new ButtonMover[5];
		btnSpawners = new ButtonSpawner[5];
		gameTimer = new Timer(10, getTimerListener());
		redDisplayer = new Timer(600, getRedTimerListener());
		bonusTimeDisplayer = new Timer(15, getBlackTimerListener());
		gameOverDisplayer = new Timer(100, getGameOverListener());
		border = Color.DARK_GRAY;
		fill = Color.LIGHT_GRAY;
		textCol = Color.WHITE;
		glow = Color.WHITE;

		try {
			myFont = Font.createFont( Font.PLAIN, 
				getClass().getResourceAsStream("/resources/ClearSans-Regular.ttf") );
			
		} catch (FontFormatException e){
			myFont = Font.getFont("Arial");
			out.println(e.getMessage());
		} catch (Exception e) {
			out.println(e.getMessage());
		} finally {
			myFont = myFont.deriveFont(55.0f);
		}
		
		setLayout(null);
		txfWord = new JTextField();
		txfWord.setHorizontalAlignment(SwingConstants.CENTER);
		txfWord.setFont(myFont.deriveFont(25f));
		txfWord.setFocusable(false);
		txfWord.setFocusTraversalKeysEnabled(false);
		txfWord.setEditable(false);
		txfWord.setColumns(10);
		txfWord.setAutoscrolls(false);
		txfWord.setBounds(357, 91, 139, 45);
		add(txfWord);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(357, 166, 139, 113);
		add(scrollPane);
		
		txaDefinition = new JTextArea();
		txaDefinition.setWrapStyleWord(true);
		txaDefinition.setLineWrap(true);
		txaDefinition.setEditable(false);
		scrollPane.setViewportView(txaDefinition);
		
		JLabel label = new JLabel("Definition");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(357, 148, 139, 18);
		add(label);
		
		try {
			getFileData("/resources/swipe5words.txt", true);
		} catch (Exception e) {
			out.println(e.getMessage());
		}
		
	}

	/* ========================================================================================
		Action Performed methods for timer ticks (timer animations)
	   ======================================================================================== */
	private ActionListener getTimerListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (timeLeft <= 0) {
					gameTimer.stop();
					gameOver();
				}
				else  timeLeft -= 0.05;	
				repaint();
			}
		};
	}
	private ActionListener getRedTimerListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				isTimerDisplayedRed = false;
				redDisplayer.stop();
				repaint();
			}
		};
	}

	private ActionListener getBlackTimerListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (timeLeft < bonusNewTime) {
					timeLeft += 0.5;
				} else {
					isTimerDisplayedBlack = false;
					bonusTimeDisplayer.stop();
				}
				repaint();
			}
		};
	}
	private ActionListener getGameOverListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (gameOverBlinkCounter < 6) {
					isTimerDisplayedRed = !isTimerDisplayedRed;
					gameOverBlinkCounter++;
				} else {
					gameOverDisplayer.stop();
					timeLeft = 0;
					new MessageDialog("Game over!", "You got "+currentScore+" right!");
				}
				
				repaint();
			}
		};
	}
	/* ========================================================================================
		GameBegin and GameOver
   	   ======================================================================================== */
	public void gameBegin() {
		for (CircleButton cb: buttons) cb.isTouched = false;
		isTimerDisplayedRed = false;
		timeLeft = 100;
		spawnLetters();
		gameTimer.start();
		currentScore = 0;
	}
	
	public void gameOver() {	
		timeLeft = 100;
		gameOverBlinkCounter = 0;
		gameOverDisplayer.start();
		repaint();
	}
	
	/* ========================================================================================
		Game methods
		madeAnAttempt: 		a guess has been made (after mouse release)
		spawnLetters: 		pick a word, display it and animate the spawn
		shiftLetters:		move the letters along to hint the user
		getClockwiseOrAnti:	return true if selection was clockwise, else false (for animation)
		areNeighbours:		given two int, are they neighbours in an array?
	   ======================================================================================== */

	public void madeAnAttempt(boolean directionClockWise) {
		if (currentWord.equalsIgnoreCase(txfWord.getText())){
			if (wordList.size() == 0) {
				new MessageDialog("No more words left. Will load words from the default file.");
				try {
					getFileData("/resources/swipe5words.txt", true);
				} catch (Exception e) { out.println(e.getMessage());}
			}
			if (gameTimer.isRunning()) currentScore++;
			spawnLetters();
		} else {
			shiftLetters(directionClockWise);
		}
	}

	public void spawnLetters() {
		resetAnswerField();
		resetDefField();
		
		if (gameTimer.isRunning()){
			//timeLeft += 10;
			//if (timeLeft > 100) timeLeft = 100;
			bonusNewTime = timeLeft + 10;
			if (bonusNewTime > 100) timeLeft = 100;
			isTimerDisplayedBlack = true;
			bonusTimeDisplayer.start();
		}
		
		currentWord = wordList.remove((int) (Math.random() * wordList.size()));
		currentDefinition = currentWord.length() > 7 ? currentWord.substring(6) : "";
		currentWord = currentWord.substring(0, 5).toUpperCase();
		
		int start = (int)(Math.random()*5);
		selectionIsClockwise = Math.random() > 0.5;
		if (! selectionIsClockwise) start += 5;
		
		for (int i = 0; i < 5; i++) {
			buttons[(start + (selectionIsClockwise? i : -i)) % 5].myChar = currentWord.charAt(i);
		}
		
		// get a different colour pack while it is still the same as the current
		ColourPack newCP = myColourPack[(int)(Math.random()*myColourPack.length)];
		while (myPack == newCP) newCP = myColourPack[(int)(Math.random()*myColourPack.length)];
		myPack = newCP;
		
		// provide a spawner animator thread to each button
		for (int i = 0; i < 5; i++) {
			btnSpawners[i] = new ButtonSpawner(buttons[i], this);
		}
		am = new AnimationManager(btnSpawners, this);
		am.start();
	}
	
	public void shiftLetters(boolean directionClockWise) {
		// they got it wrong so definition is given as the clue
		txaDefinition.setText(currentDefinition);			
		
		// make timer go red
		isTimerDisplayedRed = true;
		redDisplayer.start();
		
		int numOfSkipping = (int)(Math.random() * 2 + 2) * (directionClockWise ? 1 : -1);
		// numOfSkipping can be -2, -1, 1, 2 (negative means anti-clockwise)
		
		for (int i = 0; i < 5; i++) {
			btnMovers[i] = new ButtonMover(buttons[i], numOfSkipping, this);
		}
		
		am = new AnimationManager(btnMovers, this);
		am.start();
		
	}
	private boolean getClockwiseOrAnti(int currBtn, int lastBtn) {
		if (currBtn >= 0 && currBtn <= 3) {
			return currBtn > lastBtn;
		} else if (currBtn == 4) {
			return lastBtn == 0;
		}
		return false;
	}

	private boolean areNeighbours(int i, int lastBtn) {
		// 0 1 2 3 4 are neighbours, 4 <-> 0 neighbours too
		if (i >= 1 && i <= 3) {
			return Math.abs(i - lastBtn) == 1;
		} else if (i == 0) {
			return lastBtn == 1 || lastBtn == 4;
		} else if (i == 4) {
			return lastBtn == 0 || lastBtn == 3;
		}
		return false;
	}
	/* ========================================================================================
		Mouse and MouseMotion methods for mouse actions
	   ======================================================================================== */

	public void mousePressed(MouseEvent e) {
		wordCreated = "";
		isButtonTouched = new boolean[5];
		for (CircleButton b: buttons) {
			if (b.contains(e.getPoint())) { 
				wordCreated += b.myChar.toString();
				isButtonTouched[getIndex(b)] = b.setTouched();
				txfWord.setText(wordCreated);
				lastBtnSelected = getIndex(b);
				return;
			}
		}
		repaint();
	}
	
	public void mouseReleased(MouseEvent e) { 
		if (txfWord.getText().length() < 1) return;
		
		madeAnAttempt(selectionIsClockwise);
		for (CircleButton b: buttons) {
			b.untouch();
		}
		resetAnswerField();
	}
	
	public void mouseDragged(MouseEvent e) { 
		searchingHoveredButton : 
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i].contains(e.getPoint())) {

				if (isButtonTouched[i]) {
					continue searchingHoveredButton;
				}
				else if (areNeighbours(i, lastBtnSelected)) {
					selectionIsClockwise = getClockwiseOrAnti(i, lastBtnSelected);
					wordCreated += buttons[i].myChar.toString();
					isButtonTouched[i] = buttons[i].setTouched();
					txfWord.setText(wordCreated);
					lastBtnSelected = i;
				}
			}
		}
		repaint();
	}
	public void mouseMoved(MouseEvent e) { 
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].isHighlighted = buttons[i].contains(e.getPoint());
		}
		repaint();
	}
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	

	/* ========================================================================================
		Get, Set, Reset
 	   ======================================================================================= */
	public void resetAnswerField() 			{ txfWord.setText(""); 		}
	public void resetDefField() 			{ txaDefinition.setText("");		}
	public void setTextField(JTextField t) 	{ this.txfWord = t;			}
	public void setTextArea(JTextArea t) 	{ this.txaDefinition = t;			}
	public int getTimerAngle(double time) 	{ return (int) (time * -360.0 / 100); }
	public ColourPack[] getColours() {
		Scanner scan = new Scanner(getClass().getResourceAsStream("/resources/buttonColours.txt"));
		int numberOfCols = scan.nextInt(); scan.nextLine();
		ColourPack[] cp = new ColourPack[numberOfCols];
		
		for (int i = 0; i < cp.length; i++) {
			String line = scan.nextLine();
			String[] bits = line.split("[:,]+");
			cp[i] = new ColourPack(bits);
		}
		return cp;
	}
	public int getIndex(CircleButton cb) {
		for (int i = 0; i < buttons.length; i++) if (buttons[i] == cb) return i;
		return -1;
	}
	public ArrayList<String> getFileData(String filename, boolean fromProject) throws Exception {
		Scanner scan = fromProject ? 
				new Scanner(getClass().getResourceAsStream(filename)) :
				new Scanner(new File(filename));
		
		ArrayList<String> words = new ArrayList<String>();
		while (scan.hasNextLine()) {
			String inputLine = scan.nextLine().trim();
			if (fromProject) words.add(inputLine);
			else if (isEntryValid(inputLine)) words.add(inputLine.toUpperCase());
		}
		scan.close();
		if (words.size() < 1) {
			throw new Exception("File had no valid word");
		} else {
			this.wordList = words;
			if (!fromProject) new MessageDialog("Success", String.format("%d words have been added from %s", words.size(), new File(filename).getName()));
		}
		spawnLetters();
		return words;
	}
	public boolean isEntryValid(String s) {
		if (s.length() >= 5) {
			boolean good = true;
			for (char c: Arrays.copyOfRange(s.toCharArray(), 0, 5)){
				good &= Character.isAlphabetic(c);
			}
			
			return good;
		}
		return false;
	}
	/* ========================================================================================
		Paint Component
	   ======================================================================================= */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// draw Timer circle
		g.setColor(glow);
		g.drawOval(42, 10, 290, 290);
		
		if (timeLeft > 0 ) {
			Graphics2D g2d = (Graphics2D)g;
			
			if (isTimerDisplayedBlack) {
				g.setColor(Color.DARK_GRAY);
				g2d.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2d.drawArc(42, 10, 290, 290, 90, getTimerAngle(bonusNewTime));
			}
			g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			if (isTimerDisplayedRed) g.setColor(Color.RED); 
			else g.setColor(glow);
			
			g2d.drawArc(42, 10, 290, 290, 90, getTimerAngle(timeLeft));
			
			
		}
		
		
		// draw buttons
		for (CircleButton r : buttons) {
			r.draw(g);
		}
	}
	/* ========================================================================================
		End of SwipePanel Class
   	   ======================================================================================= */
	
	/**
	 * CircleButton is a Rectangle subclass with added features for the game
	 * @author Pheng
	 *
	 */
	class CircleButton extends Rectangle {
		Character myChar;
		boolean isTouched, isHighlighted;
		int ptIndex;
		
		public CircleButton(Point xy, int pointIndex) {
			super.x = xy.x;
			super.y = xy.y;
			super.width = buttonSize;
			super.height = buttonSize;
			myChar = ' ';
			myPack = myColourPack[(int)(Math.random()*myColourPack.length)];
			ptIndex = pointIndex;
		}
		
		public boolean setTouched() {
			return (isTouched = true);
		}
		public void untouch() {
			isTouched = false;
			isHighlighted = false;
		}
		
		public void setChar(char c) {
			myChar = c;
		}
		
		public void setColourPack(ColourPack cp) {
			myPack = cp;
		}
		//public void setPoint(Point p) {x = p.x; y = p.y;};
		public Point getPoint() {
			return new Point(x, y);
		}
		public Point getCenter() {
			return new Point(x + width/2, y + height/2);
		}
		public void draw(Graphics g) {
			g.setColor(myPack == null || isTouched ? border : myPack.shadow);
			g.fillOval(x + 2, y + 4, width, height);
			
			//g.drawRect(x, y, width, height);
			
			g.setColor(myPack == null || isTouched ? fill : myPack.face);
			g.fillOval(x, y, width, height);
			
			if (isHighlighted) {
				g.setColor(glow);
				Graphics2D g2d = (Graphics2D)g;
				g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g.drawOval(x, y, width, height);
			}
			
			if (myChar == null) {
				return;
			}
			g.setFont(myFont);
	        FontMetrics fm = g.getFontMetrics();
	        int totalWidth = fm.stringWidth(myChar.toString());
	        
	        // absolutely centred
	        int x = (width / 2) - (totalWidth / 2);
	        int y = (height - fm.getHeight()) / 2 + fm.getAscent(); 
	        //x -= fm.stringWidth(text) / 2;
	        
	        g.setColor(textCol);
	        g.drawString(myChar.toString(), this.x + x, this.y + y - 3);
		}
	}
	class ColourPack {
		Color face, shadow;
		String name;
		
		public ColourPack(String[] bits) {
			this.face	= new Color(num(bits[1]), num(bits[2]), num(bits[3]));
			this.shadow	= new Color(num(bits[4]), num(bits[5]), num(bits[6]));
			this.name = bits[0];
		}
		public int num(String s) {
			return Integer.valueOf(s);
		}
	}
}
