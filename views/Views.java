/*******************************************************
 Java GUI display
 
 *******************************************************/

package views;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

import models.*;
 
/*
 * This class exists solely to show you what menus look like.
 * It has no menu-related event handling.
 */
public class Views extends JPanel implements ActionListener {
	static private String newline = "\n";

	// initiated all gui variables
	private JTextArea output;
	private JScrollPane scrollPane;
	private JFileChooser fc;
	private JFileChooser fcs = new JFileChooser();
	private JMenuItem menuItemLoad, menuItemSave, menuItemQuit, menuItemGray,
				menuItemBiDirectly, menuItemBiErrorDiff, menuItemQuadErrorDiff,
				menuItemUCQ, menuItemMCQ, menuItemBiErrorDiffBell,
				menuItemBiErrorDiffStucki, menuItemMCQError, menuItemMCQBell,
				menuItemMCQStucki;
	private JMenu fileMenu, imageMenu, submenuBiScale, submenu8Bits;
	private JLabel imageLabel, outputLabel;
	private JTabbedPane mainPanel;
	private JPanel container, optionPanel;

	// init the java frame
	static JFrame frame;

	// image function and image file
	private ImageModel ImageModel = new ImageModel();
	private File inputImage;

	// constructor
	public Views() {

	}

	public Views(ImageModel model) {
		ImageModel = model;

		// set up gui
		createAndShowGUI();
	}
 
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
 
		// Create the menu bar.
		menuBar = new JMenuBar();
 
		// Build the first menu. -> File functions
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription(
				"The only fileMenu in this program that has fileMenu items");
		menuBar.add(fileMenu);
 
		// a group of JMenuItems for file functions
		menuItemLoad = new JMenuItem("Load File",
								 KeyEvent.VK_L);
		menuItemLoad.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_L, ActionEvent.ALT_MASK));
		menuItemLoad.getAccessibleContext().setAccessibleDescription(
				"This will load the image file");
		fileMenu.add(menuItemLoad);

		menuItemSave = new JMenuItem("Save File",
								 KeyEvent.VK_S);
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItemSave.getAccessibleContext().setAccessibleDescription(
				"This will save the image file");
		fileMenu.add(menuItemSave);

		menuItemQuit = new JMenuItem("Quit",
								 KeyEvent.VK_Q);
		menuItemQuit.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		menuItemQuit.getAccessibleContext().setAccessibleDescription(
				"This should quit the program");
		fileMenu.add(menuItemQuit);
 
		// Build second menu in the menu bar. -> Image Functions
		imageMenu = new JMenu("Image");
		imageMenu.getAccessibleContext().setAccessibleDescription(
				"This imageMenu contains all the image functions");
		menuBar.add(imageMenu);

		// a group of JMenuItems for Images
		menuItemGray = new JMenuItem("Gray Scale",
								 KeyEvent.VK_G);
		menuItemGray.getAccessibleContext().setAccessibleDescription(
				"This doesn't really do anything");
		imageMenu.add(menuItemGray);

		// a submenu for bi-scale
		submenuBiScale = new JMenu("Bi-Scale(Black & White)");
		submenuBiScale.setMnemonic(KeyEvent.VK_B);
 
		menuItemBiDirectly = new JMenuItem("Directly");
		submenuBiScale.add(menuItemBiDirectly);
 
		menuItemBiErrorDiff = new JMenuItem("Error-Diffusion(Floyd)");
		submenuBiScale.add(menuItemBiErrorDiff);

		menuItemBiErrorDiffBell = new JMenuItem("Error-Diffusion(Bell)");
		submenuBiScale.add(menuItemBiErrorDiffBell);

		menuItemBiErrorDiffStucki = new JMenuItem("Error-Diffusion(Stucki)");
		submenuBiScale.add(menuItemBiErrorDiffStucki);
		imageMenu.add(submenuBiScale);

		// a group of JMenuItems for Images
		menuItemQuadErrorDiff = new JMenuItem("Quad-level",
								 KeyEvent.VK_G);
		menuItemQuadErrorDiff.getAccessibleContext().setAccessibleDescription(
				"This doesn't really do anything");
		imageMenu.add(menuItemQuadErrorDiff);

		// seperate from black white to colored
		imageMenu.addSeparator();

		// a submenu from 8-Bits
		submenu8Bits = new JMenu("8-Bits");
		submenu8Bits.setMnemonic(KeyEvent.VK_B);
 
		menuItemUCQ = new JMenuItem("Uniform Color Quantization");
		submenu8Bits.add(menuItemUCQ);
 
		menuItemMCQ = new JMenuItem("Median Color Quantization");
		submenu8Bits.add(menuItemMCQ);

		menuItemMCQError = new JMenuItem("Median Color Quantization(Error-Diffusion(Floyd))");
		submenu8Bits.add(menuItemMCQError);

		menuItemMCQBell = new JMenuItem("Median Color Quantization(Error-Diffusion(Bell))");
		submenu8Bits.add(menuItemMCQBell);

		menuItemMCQStucki = new JMenuItem("Median Color Quantization(Error-Diffusion(Stucki))");
		submenu8Bits.add(menuItemMCQStucki);

		imageMenu.add(submenu8Bits);

		return menuBar;
	}

	/*
	 Getter/ Setters
	 */
	public JFileChooser getFC() {
		return fc;
	}

	public void setFC(JFileChooser fc) {
		this.fc = fc;
	}

	public JFileChooser getFCS() {
		return fcs;
	}

	public void setFCS(JFileChooser fcs) {
		this.fcs = fcs;
	}

	public File getInputImage() {
		return inputImage;
	}

	public void setInputImage(File inputImage) {
		this.inputImage = inputImage;
	}

	public JLabel getImageLabel() {
		return imageLabel;
	}

	public void setImageLabel(JLabel imageLabel) {
		this.imageLabel = imageLabel;
	}

	public JLabel getOutputLabel() {
		return outputLabel;
	}

	public void setOutputLabel(JLabel outputLabel) {
		this.outputLabel = outputLabel;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public JTabbedPane getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JTabbedPane mainPanel) {
		this.mainPanel = mainPanel;
	}

	public JPanel getContainer() {
		return container;
	}

	public void setContainer(JPanel container) {
		this.container = container;
	}

	public JPanel getOptionPanel() {
		return optionPanel;
	}

	public void setOptionPanel(JPanel optionPanel) {
		this.optionPanel = optionPanel;
	}

	/*
	 Add events to button
	 */
	public void addSaveListener(ActionListener action) {
		menuItemSave.addActionListener(action);
	}

	public void addLoadListener(ActionListener action) {
		menuItemLoad.addActionListener(action);
	}

	public void addQuitListener(ActionListener action) {
		menuItemQuit.addActionListener(action);
	}

	public void addGrayListener(ActionListener action) {
		menuItemGray.addActionListener(action);
	}

	public void addBiDirectlyListener(ActionListener action) {
		menuItemBiDirectly.addActionListener(action);
	}

	public void addBiErrorListener(ActionListener action) {
		menuItemBiErrorDiff.addActionListener(action);
	}

	public void addBiErrorBellListener(ActionListener action) {
		menuItemBiErrorDiffBell.addActionListener(action);
	}

	public void addBiErrorStuckiListener(ActionListener action) {
		menuItemBiErrorDiffStucki.addActionListener(action);
	}

	public void addQuadListener(ActionListener action) {
		menuItemQuadErrorDiff.addActionListener(action);
	}

	public void add8BitUCQListener(ActionListener action) {
		menuItemUCQ.addActionListener(action);
	}

	public void add8BitMCQListener(ActionListener action) {
		menuItemMCQ.addActionListener(action);
	}

	public void add8BitMCQErrorListener(ActionListener action) {
		menuItemMCQError.addActionListener(action);
	}

	public void add8BitMCQBellListener(ActionListener action) {
		menuItemMCQBell.addActionListener(action);
	}

	public void add8BitMCQStuckiListener(ActionListener action) {
		menuItemMCQStucki.addActionListener(action);
	}

	public void actionPerformed(ActionEvent e) {
		// refresh the panel everytime a button is clicked
		updatePanel();
	}

	public void updatePanel() {
		revalidate();
		repaint();
	}
 
	public Container createContentPane() {
		//Create the content-pane-to-be.
		container = new JPanel(new BorderLayout());

		optionPanel = new JPanel(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));

		mainPanel = new JTabbedPane();
		mainPanel.setOpaque(true);
 
		imageLabel = new JLabel();
		imageLabel.setVisible(true);

		outputLabel = new JLabel();
		outputLabel.setVisible(true);
 
		//Add the text area to the content pane.
		mainPanel.add(imageLabel, "Input Image");
		mainPanel.add(outputLabel, "Output Image");

		container.add(mainPanel, BorderLayout.CENTER);
 
		return container;
	}
 
	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("CS 451 Multi-Media System - Homework1");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
		//Create and set up the content pane.
		frame.setJMenuBar(this.createMenuBar());
		frame.setContentPane(this.createContentPane());
 
		//Display the window.
		frame.setSize(450, 260);
		frame.setVisible(true);
	}
}