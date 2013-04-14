/*******************************************************
 Java GUI display
 
 *******************************************************/

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
 
/*
 * This class exists solely to show you what menus look like.
 * It has no menu-related event handling.
 */
public class ImageView extends JPanel implements ActionListener {
    static private String newline = "\n";

    // initiated all gui variables
    JTextArea output;
    JScrollPane scrollPane;
    JFileChooser fc;
    JFileChooser fcs = new JFileChooser();
    JMenuItem menuItemLoad, menuItemSave, menuItemQuit, menuItemGrey,
    			menuItemBiDirectly, menuItemBiErrorDiff, menuItemQuadDirectly,
    			menuItemQuadErrorDiff, menuItemUCQ, menuItemMCQ;
    JMenu fileMenu, imageMenu, submenuBiScale, submenuQuadScale, submenu8Bits;
    JLabel imageLabel;

    // init the java frame
    static JFrame frame;

    // image function and image file
    ImageModel ImageModel = new ImageModel();
    File inputImage;

    // constructor
    public ImageView() {

    }

    public ImageView(ImageModel model) {
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
        menuItemGrey = new JMenuItem("Grey Scale",
                                 KeyEvent.VK_G);
        menuItemGrey.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        imageMenu.add(menuItemGrey);

        // a submenu for bi-scale
        submenuBiScale = new JMenu("Bi-Scale(Black & White)");
        submenuBiScale.setMnemonic(KeyEvent.VK_B);
 
        menuItemBiDirectly = new JMenuItem("Directly");
        submenuBiScale.add(menuItemBiDirectly);
 
        menuItemBiErrorDiff = new JMenuItem("Error-Diffusion");
        submenuBiScale.add(menuItemBiErrorDiff);
        imageMenu.add(submenuBiScale);

        // a submenu for quad-scale
        submenuQuadScale = new JMenu("Quad-Scale(Black & White)");
        submenuQuadScale.setMnemonic(KeyEvent.VK_B);
 
        menuItemQuadDirectly = new JMenuItem("Directly");
        submenuQuadScale.add(menuItemQuadDirectly);
 
        menuItemQuadErrorDiff = new JMenuItem("Error-Diffusion");
        submenuQuadScale.add(menuItemQuadErrorDiff);
        imageMenu.add(submenuQuadScale);

        // seperate from black white to colored
        imageMenu.addSeparator();

        // a submenu from 8-Bits
        submenu8Bits = new JMenu("8-Bits");
        submenu8Bits.setMnemonic(KeyEvent.VK_B);
 
        menuItemUCQ = new JMenuItem("Uniform Color Quantization");
        submenu8Bits.add(menuItemUCQ);
 
        menuItemMCQ = new JMenuItem("Median Color Quantization");
        submenu8Bits.add(menuItemMCQ);
        imageMenu.add(submenu8Bits);

        return menuBar;
    }

    public void addSaveListener(ActionListener action) {
        menuItemSave.addActionListener(action);
    }

    public void addLoadListener(ActionListener action) {
        menuItemLoad.addActionListener(action);
    }

    public void addQuitListener(ActionListener action) {
        menuItemQuit.addActionListener(action);
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
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
 
      	imageLabel = new JLabel();
      	imageLabel.setVisible(true);
 
        //Add the text area to the content pane.
        contentPane.add(imageLabel, BorderLayout.CENTER);
 
        return contentPane;
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