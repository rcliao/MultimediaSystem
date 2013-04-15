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
		m_view.addBiErrorListener(new BiErrorListener());
		m_view.addBiErrorBellListener(new BiErrorBellListener());
		m_view.addQuadListener(new QuadErrorListener());
		m_view.add8BitUCQListener(new UCQListener());
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

	class BiErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			m_model.convertToBiError();
			m_view.imageLabel.setIcon(new ImageIcon(m_model.img));
		}
	}

	class BiErrorBellListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			m_model.convertToBiErrorBell();
			m_view.imageLabel.setIcon(new ImageIcon(m_model.img));
		}
	}

	class QuadErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			m_model.convertToQuadError();
			m_view.imageLabel.setIcon(new ImageIcon(m_model.img));
		}
	}

	class UCQListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			m_model.convertTo8BitUCQ();

			// Use a textarea to display the table
			JFrame frame = new JFrame();
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = m_model.lookUpTable.keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				for (int i = 0; i < 3; i ++)
					tableArea.append(m_model.lookUpTable.get(index)[i] + "\t");

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);
			frame.add(logScrollPane, BorderLayout.CENTER);
			frame.setTitle("Look Up Table");
			frame.pack();
			frame.setVisible(true);

			// display index image
			JFrame frame2 = new JFrame();
			JLabel label2 = new JLabel(new ImageIcon(m_model.indexImg));
			frame2.add(label2, BorderLayout.CENTER);
			frame2.setTitle("Index Image");
			frame2.pack();
			frame2.setVisible(true);

			m_view.imageLabel.setIcon(new ImageIcon(m_model.img));
		}
	}
}