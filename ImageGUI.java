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

/* ImageGUI.java requires images/middle.gif. */
 
/*
 * This class exists solely to show you what menus look like.
 * It has no menu-related event handling.
 */
public class ImageGUI extends JPanel implements ActionListener {
    static private String newline = "\n";

    JTextArea output;
    JScrollPane scrollPane;
    JFileChooser fc;
    JMenuItem menuItemLoad, menuItemSave, menuItemQuit, menuItemGrey, menuItemBiDirectly,
                menuItemBiErrorDiff, menuItemQuadDirectly, menuItemQuadErrorDiff, menuItemUCQ, menuItemMCQ;
    JMenu fileMenu, imageMenu, submenuBiScale, submenuQuadScale, submenu8Bits;
 
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
        menuItemLoad.addActionListener(this);
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
        menuItemQuit.addActionListener(this);
        fileMenu.add(menuItemQuit);
 
        // Build second menu in the menu bar. -> Image Functions
        imageMenu = new JMenu("Image");
        imageMenu.setMnemonic(KeyEvent.VK_I);
        imageMenu.getAccessibleContext().setAccessibleDescription(
                "This imageMenu contains all the image functions");
        menuBar.add(imageMenu);

        // a group of JMenuItems for Images
        menuItemGrey = new JMenuItem("Grey Scale",
                                 KeyEvent.VK_G);
        menuItemGrey.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_G, ActionEvent.ALT_MASK));
        menuItemGrey.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        imageMenu.add(menuItemGrey);

        // a submenu for bi-scale
        submenuBiScale = new JMenu("Bi-Scale(Black & White)");
        submenuBiScale.setMnemonic(KeyEvent.VK_B);
 
        menuItemBiDirectly = new JMenuItem("Directly");
        menuItemBiDirectly.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, ActionEvent.ALT_MASK));
        submenuBiScale.add(menuItemBiDirectly);
 
        menuItemBiErrorDiff = new JMenuItem("Error-Diffusion");
        menuItemBiErrorDiff.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));
        submenuBiScale.add(menuItemBiErrorDiff);
        imageMenu.add(submenuBiScale);

        // a submenu for quad-scale
        submenuQuadScale = new JMenu("Quad-Scale(Black & White)");
        submenuQuadScale.setMnemonic(KeyEvent.VK_B);
 
        menuItemQuadDirectly = new JMenuItem("Directly");
        menuItemQuadDirectly.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, ActionEvent.ALT_MASK));
        submenuQuadScale.add(menuItemQuadDirectly);
 
        menuItemQuadErrorDiff = new JMenuItem("Error-Diffusion");
        menuItemQuadErrorDiff.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));
        submenuQuadScale.add(menuItemQuadErrorDiff);
        imageMenu.add(submenuQuadScale);

        // seperate from black white to colored
        imageMenu.addSeparator();

        // a submenu from 8-Bits
        submenu8Bits = new JMenu("8-Bits");
        submenu8Bits.setMnemonic(KeyEvent.VK_B);
 
        menuItemUCQ = new JMenuItem("Uniform Color Quantization");
        menuItemUCQ.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, ActionEvent.ALT_MASK));
        submenu8Bits.add(menuItemUCQ);
 
        menuItemMCQ = new JMenuItem("Median Color Quantization");
        menuItemUCQ.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_M, ActionEvent.ALT_MASK));
        submenu8Bits.add(menuItemMCQ);
        imageMenu.add(submenu8Bits);

        return menuBar;
    }

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == menuItemQuit) {
            System.exit(0);
        //Handle save button action.
        } else if (e.getSource() == menuItemLoad) {
            //Set up the file chooser.
            if (fc == null) {
                fc = new JFileChooser();
     
            	//Add a custom file filter and disable the default
            	//(Accept All) file filter.
                fc.addChoosableFileFilter(new ImageFilter());
                fc.setAcceptAllFileFilterUsed(false);
     
            	//Add the preview pane.
                fc.setAccessory(new ImagePreview(fc));
            }
     
            //Show it.
            int returnVal = fc.showDialog(ImageGUI.this,
                                          "Attach");
     
            //Process the results.
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                System.out.println("Attaching file: " + file.getName()
                           + "." + newline);
            } else {
                System.out.println("Attachment cancelled by user." + newline);
            }
     
            //Reset the file chooser for the next time it's shown.
            fc.setSelectedFile(null);
        }
    }
 
    public Container createContentPane() {
        //Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
 
        //Create a scrolled text area.
        output = new JTextArea(5, 30);
        output.setEditable(false);
        scrollPane = new JScrollPane(output);
 
        //Add the text area to the content pane.
        contentPane.add(scrollPane, BorderLayout.CENTER);
 
        return contentPane;
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ImageGUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        ImageGUI demo = new ImageGUI();
        frame.setJMenuBar(demo.createMenuBar());
        frame.setContentPane(demo.createContentPane());
 
        //Display the window.
        frame.setSize(450, 260);
        frame.setVisible(true);
    }
 
    // testing purpose to display the menu
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}