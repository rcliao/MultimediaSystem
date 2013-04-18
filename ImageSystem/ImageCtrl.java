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
	private ImageModel output;
	
	//========================================================== constructor
	/** Constructor */
	public ImageCtrl(ImageModel model, ImageView view) {
		m_model = model;
		m_view  = view;
		output = new ImageModel();
		
		//... Add listeners to the view.
		m_view.addQuitListener(new QuitListener());
		m_view.addLoadListener(new LoadListener());
		m_view.addSaveListener(new SaveListener());
		m_view.addGrayListener(new GrayListener());
		m_view.addBiDirectlyListener(new BiDirectlyListener());
		m_view.addBiErrorListener(new BiErrorListener());
		m_view.addBiErrorBellListener(new BiErrorBellListener());
		m_view.addBiErrorStuckiListener(new BiErrorStuckiListener());
		m_view.addQuadListener(new QuadErrorListener());
		m_view.add8BitUCQListener(new UCQListener());
		m_view.add8BitMCQListener(new MCQListener());
		m_view.add8BitMCQErrorListener(new MCQErrorListener());
		m_view.add8BitMCQBellListener(new MCQBellListener());
		m_view.add8BitMCQStuckiListener(new MCQStuckiListener());
	}
	
	//====================== Action Listeners
	class QuitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

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
				output.readPPM(m_view.inputImage);
				m_model.readPPM(m_view.inputImage);
				m_view.imageLabel.setIcon(new ImageIcon(m_model.img));
				m_view.outputLabel.setIcon(new ImageIcon(output.img));
				m_view.frame.setSize(m_model.getW()*2+20, m_model.getH()+110);
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
					output.write2PPM(file);
				} else {
					// cancel case
				}
			}
		}
	}

	class GrayListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertToGray();
			m_view.outputLabel.setIcon(new ImageIcon(output.img));
		}
	}

	class BiDirectlyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertToBiDirectly();
			m_view.outputLabel.setIcon(new ImageIcon(output.img));
		}
	}

	class BiErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertToBiError("floyd");
			m_view.outputLabel.setIcon(new ImageIcon(output.img));
		}
	}

	class BiErrorBellListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertToBiError("bell");
			m_view.outputLabel.setIcon(new ImageIcon(output.img));
		}
	}

	class BiErrorStuckiListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertToBiError("stucki");
			m_view.outputLabel.setIcon(new ImageIcon(output.img));
		}
	}

	class QuadErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertToQuadError();
			m_view.outputLabel.setIcon(new ImageIcon(output.img));
		}
	}

	class UCQListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertTo8BitUCQ();

			// Use a textarea to display the table
			JFrame frame = new JFrame();
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.lookUpTable.keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				for (int i = 0; i < 3; i ++)
					tableArea.append(output.lookUpTable.get(index)[i] + "\t");

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);
			frame.add(logScrollPane, BorderLayout.CENTER);
			frame.setTitle("Look Up Table");
			frame.pack();
			frame.setVisible(true);

			// display index image
			JFrame frame2 = new JFrame();
			JLabel label2 = new JLabel(new ImageIcon(output.indexImg));
			frame2.add(label2, BorderLayout.CENTER);
			frame2.setTitle("Index Image");
			frame2.pack();
			frame2.setVisible(true);

			m_view.outputLabel.setIcon(new ImageIcon(output.img));
		}
	}

	class MCQListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertTo8BitMCQ();

			// Use a textarea to display the table
			JFrame frame = new JFrame();
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.lookUpTableMedian.keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\tFrequency\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				tableArea.append(output.lookUpTableMedian.get(index).rmean + "\t");
				tableArea.append(output.lookUpTableMedian.get(index).gmean + "\t");
				tableArea.append(output.lookUpTableMedian.get(index).bmean + "\t");

				tableArea.append("" + output.lookUpTableMedian.get(index).histogram.size());

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);
			frame.add(logScrollPane, BorderLayout.CENTER);
			frame.setTitle("Look Up Table");
			frame.pack();
			frame.setVisible(true);

			// display index image
			JFrame frame2 = new JFrame();
			JLabel label2 = new JLabel(new ImageIcon(output.indexImg));
			frame2.add(label2, BorderLayout.CENTER);
			frame2.setTitle("Index Image");
			frame2.pack();
			frame2.setVisible(true);

			m_view.outputLabel.setIcon(new ImageIcon(output.img));

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class MCQErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertTo8BitMCQError("floyd");

			// Use a textarea to display the table
			JFrame frame = new JFrame();
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.lookUpTableMedian.keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\tFrequency\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				tableArea.append(output.lookUpTableMedian.get(index).rmean + "\t");
				tableArea.append(output.lookUpTableMedian.get(index).gmean + "\t");
				tableArea.append(output.lookUpTableMedian.get(index).bmean + "\t");

				tableArea.append("" + output.lookUpTableMedian.get(index).histogram.size());

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);
			frame.add(logScrollPane, BorderLayout.CENTER);
			frame.setTitle("Look Up Table");
			frame.pack();
			frame.setVisible(true);

			// display index image
			JFrame frame2 = new JFrame();
			JLabel label2 = new JLabel(new ImageIcon(output.indexImg));
			frame2.add(label2, BorderLayout.CENTER);
			frame2.setTitle("Index Image");
			frame2.pack();
			frame2.setVisible(true);

			m_view.outputLabel.setIcon(new ImageIcon(output.img));

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class MCQBellListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertTo8BitMCQError("bell");

			// Use a textarea to display the table
			JFrame frame = new JFrame();
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.lookUpTableMedian.keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\tFrequency\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				tableArea.append(output.lookUpTableMedian.get(index).rmean + "\t");
				tableArea.append(output.lookUpTableMedian.get(index).gmean + "\t");
				tableArea.append(output.lookUpTableMedian.get(index).bmean + "\t");

				tableArea.append("" + output.lookUpTableMedian.get(index).histogram.size());

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);
			frame.add(logScrollPane, BorderLayout.CENTER);
			frame.setTitle("Look Up Table");
			frame.pack();
			frame.setVisible(true);

			// display index image
			JFrame frame2 = new JFrame();
			JLabel label2 = new JLabel(new ImageIcon(output.indexImg));
			frame2.add(label2, BorderLayout.CENTER);
			frame2.setTitle("Index Image");
			frame2.pack();
			frame2.setVisible(true);

			m_view.outputLabel.setIcon(new ImageIcon(output.img));

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class MCQStuckiListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output.convertTo8BitMCQError("floyd");

			// Use a textarea to display the table
			JFrame frame = new JFrame();
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.lookUpTableMedian.keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\tFrequency\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				tableArea.append(output.lookUpTableMedian.get(index).rmean + "\t");
				tableArea.append(output.lookUpTableMedian.get(index).gmean + "\t");
				tableArea.append(output.lookUpTableMedian.get(index).bmean + "\t");

				tableArea.append("" + output.lookUpTableMedian.get(index).histogram.size());

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);
			frame.add(logScrollPane, BorderLayout.CENTER);
			frame.setTitle("Look Up Table");
			frame.pack();
			frame.setVisible(true);

			// display index image
			JFrame frame2 = new JFrame();
			JLabel label2 = new JLabel(new ImageIcon(output.indexImg));
			frame2.add(label2, BorderLayout.CENTER);
			frame2.setTitle("Index Image");
			frame2.pack();
			frame2.setVisible(true);

			m_view.outputLabel.setIcon(new ImageIcon(output.img));

			// write to file
			output.writeToFile("LUT.txt");
		}
	}
}