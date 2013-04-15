/*
 * Image Controller class
 */

package ImageSystem;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

import ImageUtils.*;

public class ImageCtrl {
    //... The Controller needs to interact with both the Model and View.
    private ImageModel m_model;
    private ImageView m_view;
    
    //========================================================== constructor
    /** Constructor */
    public ImageCtrl(ImageModel model, ImageView view) {
        m_model = model;
        m_view  = view;
        
        //... Add listeners to the view.
        m_view.addQuitListener(new QuitListener());
        m_view.addLoadListener(new LoadListener());
        m_view.addSaveListener(new SaveListener());
        m_view.addGrayListener(new GrayListener());
        m_view.addBiDirectlyListener(new BiDirectlyListener());
    }
    
    class QuitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }//end inner class QuitListener

    class LoadListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //Set up the file chooser.
            if (m_view.fc == null) {
                m_view.fc = new JFileChooser();
     
                //Add a custom file filter and disable the default
                //(Accept All) file filter.
                m_view.fc.addChoosableFileFilter(new ImageFilter());
                m_view.fc.setAcceptAllFileFilterUsed(false);
     
                //Add the preview pane.
                m_view.fc.setAccessory(new ImagePreview(m_view.fc));
            }
     
            //Show it.
            int returnVal = m_view.fc.showDialog(m_view,
                                          "Attach");
     
            //Process the results.
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                m_view.inputImage = m_view.fc.getSelectedFile();
                m_model.readPPM(m_view.inputImage);
                m_view.imageLabel.setIcon(new ImageIcon(m_model.img));
                m_view.frame.setSize(m_model.getW(), m_model.getH()+70);
            } else {
                // cancel case
            }
     
            //Reset the file chooser for the next time it's shown.
            m_view.fc.setSelectedFile(null);
        }
    }

    class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (m_model.img != null) {
                int returnVal = m_view.fcs.showSaveDialog(m_view);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = m_view.fcs.getSelectedFile();
                    m_model.write2PPM(file);
                } else {
                    // cancel case
                }
            }
        }
    }

    class GrayListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            m_model.convertToGray();
            m_view.imageLabel.setIcon(new ImageIcon(m_model.img));
        }
    }

    class BiDirectlyListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            m_model.convertToBiDirectly();
            m_view.imageLabel.setIcon(new ImageIcon(m_model.img));
        }
    }
}