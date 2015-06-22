package swipe5;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.List;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.UIManager;

import static java.lang.System.out;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JInternalFrame;
import javax.swing.JTextField;

import java.awt.Font;

import javax.swing.SwingConstants;

import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import java.awt.dnd.DropTarget;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;



import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;
public class Application {

	private JFrame frmApplication;
	private ArrayList<String> words;
	private SwipePanel panelPlay;
	private String currentWord;
	private JInternalFrame frameHelp;
	private JInternalFrame frameMain;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.frmApplication.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Application() {
		initialize();
		AnimationManager.myPanel = panelPlay;
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		panelPlay = new SwipePanel();
		frmApplication = new JFrame();
		frmApplication.setTitle("Swipe5 \u00A9 2015 Pheng Taing");
		frmApplication.setBounds(100, 100, 632, 465);
		frmApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmApplication.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JDesktopPane desktopPane = new JDesktopPane();
		frmApplication.getContentPane().add(desktopPane, BorderLayout.CENTER);
		desktopPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panelTop = new JPanel();
		desktopPane.add(panelTop, BorderLayout.NORTH);
		panelTop.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panelTop.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnStartGame = new JButton("Start Game");
		btnStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (! panelPlay.gameTimer.isRunning()) panelPlay.gameBegin();
			}
		});
		panel_1.add(btnStartGame);
		
		JPanel panelBottom = new JPanel();
		desktopPane.add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new BorderLayout(0, 0));
		
		JToggleButton bttnHelp = new JToggleButton("Help");
		bttnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (bttnHelp.isSelected()){
					frameHelp.show();
					frameMain.hide();
				} else {
					frameHelp.hide();
					frameMain.show();
				}
			}
		});
		panelBottom.add(bttnHelp, BorderLayout.WEST);
		JButton btnVisitMe = new JButton("Visit My Page");
		btnVisitMe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			            desktop.browse(new URI("http://bit.ly/pixport"));
			        } catch (Exception e) { e.printStackTrace(); }
			    }
			}
		});
		btnVisitMe.setToolTipText("");
		panelBottom.add(btnVisitMe, BorderLayout.EAST);
		
		JPanel panelBottomMiddle = new JPanel();
		panelBottom.add(panelBottomMiddle, BorderLayout.CENTER);
		panelBottomMiddle.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblDropInputFile = new JLabel("Drop Text File Here");
		lblDropInputFile.setForeground(Color.WHITE);
		panelBottomMiddle.add(lblDropInputFile);
		
		DropFileListener myDragDropListener = new DropFileListener(lblDropInputFile, panelPlay);
		
		JPanel panelBody = new JPanel();
		desktopPane.add(panelBody, BorderLayout.CENTER);
		panelBody.setLayout(null);
		
		
		frameMain = new JInternalFrame("Play Swipe5");
		frameMain.setBounds(50, 6, 534, 348);
		panelBody.add(frameMain);
		
		
		panelPlay.setBackground(new Color(255, 228, 225));
		frameMain.getContentPane().add(panelPlay, BorderLayout.CENTER);
		panelPlay.setLayout(null);
		
		frameHelp = new JInternalFrame("Help");
		frameHelp.setBounds(19, 6, 592, 324);
		panelBody.add(frameHelp);
		
		frameHelp.setResizable(true);
		frameHelp.setClosable(true);
		frameHelp.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frameHelp.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JScrollPane scrHowToPlay = new JScrollPane();
		tabbedPane.addTab("How to Play", null, scrHowToPlay, null);
		
		JTextArea txaHowToPlay = new JTextArea();
		txaHowToPlay.setWrapStyleWord(true);
		txaHowToPlay.setText("Form a five letter word by swiping clockwise or anti-clockwise. The word could start from any letter and go in either direction.\r\n\r\nControl: use your mouse to click and drag over the five letters to create a word.\r\n\r\nIf the word is incorrect, the letters will move to make another arrangement and the definition will be displayed.\r\n\r\nYou can also load words from a file by dragging it into the application. Only words that contain 5 letters are accepted, definitions are optional (add a space before the definition). One word and definition per line.");
		txaHowToPlay.setLineWrap(true);
		txaHowToPlay.setEditable(false);
		scrHowToPlay.setViewportView(txaHowToPlay);
		
		JScrollPane scrAbout = new JScrollPane();
		tabbedPane.addTab("About Swipe5", null, scrAbout, null);
		
		JTextArea txaAbout = new JTextArea();
		txaAbout.setWrapStyleWord(true);
		txaAbout.setText("The concept for Swipe5 was inspired by Thomas Hall's \"Four Letters\".\r\nButton and colour designs were inspired from \"Four Letters\".\r\n\r\nSeaGlass is the LookAndFeel used in this Java application.\r\n\r\nSwipe5 \u00A9 2015 Pheng Taing\r\nView more of my work at https://github.com/aleang");
		txaAbout.setLineWrap(true);
		txaAbout.setEditable(false);
		scrAbout.setViewportView(txaAbout);
		frameHelp.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent arg0) {
				bttnHelp.setSelected(false);
				frameHelp.hide();
				frameMain.show();
			}
		});
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frmApplication.getSize();
		frmApplication.setLocation((d.width - frameSize.width) / 2, (d.height - frameSize.height) / 2);
		
		frameMain.setVisible(true);
		new DropTarget(frmApplication, myDragDropListener);
	}
}
