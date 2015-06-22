package swipe5;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.GridLayout;

import javax.swing.JTextArea;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalExclusionType;

import javax.swing.SwingConstants;

public class MessageDialog extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public MessageDialog(String message) {
		this("", message);
	}
	public MessageDialog(String title, String message) {
		setResizable(false);
		MessageDialog md = this;
		try {
			UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(title);
		setBounds(100, 100, 338, 119);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		setLocation((d.width - frameSize.width) / 2, (d.height - frameSize.height) / 2);
		
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				md.setVisible(false);
			}
		});
		btnOk.setBounds(224, 57, 106, 30);
		contentPane.add(btnOk);
		
		JTextArea txaMessage = new JTextArea();
		txaMessage.setEditable(false);
		txaMessage.setLineWrap(true);
		txaMessage.setWrapStyleWord(true);
		txaMessage.setBounds(6, 6, 324, 44);
		contentPane.add(txaMessage);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{txaMessage, contentPane, btnOk}));
		txaMessage.setText(message);
		setVisible(true);
	}
}
