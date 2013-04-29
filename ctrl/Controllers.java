/*
 * Image Controller class
 */

package ctrl;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

import java.io.*;

import ImageUtils.*;
import models.*;
import views.*;

public class Controllers {
	//... The Controller needs to interact with both the Model and View.
	private ImageModel m_model;
	private Views m_view;
	private ImageModel output;
	private TextModel text_model;
	
	//========================================================== constructor
	/** Constructor */
	public Controllers(ImageModel model, Views view, TextModel text_model) {
		m_model = model;
		m_view  = view;
		output = new ImageModel();
		this.text_model = text_model;
		
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
		m_view.addLZWEncodingListener(new LZWEncodingListener());
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
			if (m_view.getFC() == null) {
				m_view.setFC(new JFileChooser());
	 
				//Add a custom file filter and disable the default
				//(Accept All) file filter.
				m_view.getFC().addChoosableFileFilter(new ImageFilter());
				m_view.getFC().setAcceptAllFileFilterUsed(false);
	 
				//Add the preview pane.
				m_view.getFC().setAccessory(new ImagePreview(m_view.getFC()));
			}
	 
			//Show it.
			int returnVal = m_view.getFC().showDialog(m_view,
										  "Attach");
	 
			//Process the results.
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File inputFile = m_view.getFC().getSelectedFile();
				if (Utils.getExtension(inputFile).equals("ppm") || Utils.getExtension(inputFile).equals("png")
					|| Utils.getExtension(inputFile).equals("jpg") || Utils.getExtension(inputFile).equals("jpeg")
					|| Utils.getExtension(inputFile).equals("gif")) {
					m_view.getTextMenu().setEnabled(false);
					m_view.getImageMenu().setEnabled(true);
					m_view.setInputImage(m_view.getFC().getSelectedFile());
					output.readPPM(m_view.getInputImage());
					m_model.readPPM(m_view.getInputImage());
					m_view.getImageLabel().setIcon(new ImageIcon(m_model.getImg()));
					m_view.getImageLabel().setText("");
					m_view.getInput().setViewportView(new JScrollPane(m_view.getImageLabel()));
					m_view.getInput().updateUI();
					m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
					m_view.getOutputLabel().setText("");
					m_view.getOutput().setViewportView(new JScrollPane(m_view.getOutputLabel()));
				} else if (Utils.getExtension(inputFile).equals("txt")) {
					m_view.getTextMenu().setEnabled(true);
					m_view.getImageMenu().setEnabled(false);

					m_view.setTextArea(new JTextArea());
					m_view.setOutputText(new JTextArea());

					text_model.readFile(inputFile);
					
					m_view.getTextArea().append(text_model.getMessage());
					m_view.getOutputText().append(text_model.getMessage());
					
					m_view.getInput().setViewportView(new JScrollPane(m_view.getTextArea()));
					m_view.getOutput().setViewportView(new JScrollPane(m_view.getOutputText()));
				}
			} else {
				// cancel case
			}
	 
			//Reset the file chooser for the next time it's shown.
			m_view.getFC().setSelectedFile(null);

			m_view.updatePanel();
		}
	}

	class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (m_model.getImg() != null) {
				int returnVal = m_view.getFCS().showSaveDialog(m_view);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = m_view.getFCS().getSelectedFile();
					output.write2PPM(file);
				} else {
					// cancel case
				}
			}
		}
	}

	class GrayListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output = new ImageModel(m_model.getFile());
			output.convertToGray();
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
		}
	}

	class BiDirectlyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output = new ImageModel(m_model.getFile());
			output.convertToBiDirectly();
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
		}
	}

	class BiErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output = new ImageModel(m_model.getFile());
			output.convertToBiError("floyd");
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
		}
	}

	class BiErrorBellListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output = new ImageModel(m_model.getFile());
			output.convertToBiError("bell");
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
		}
	}

	class BiErrorStuckiListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output = new ImageModel(m_model.getFile());
			output.convertToBiError("stucki");
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
		}
	}

	class QuadErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			output = new ImageModel(m_model.getFile());
			output.convertToQuadError();
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
		}
	}

	class UCQListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// remove the uncessary tabs
			int tabCount = m_view.getMainPanel().getTabCount();
			if (tabCount > 2) {
				for (int i = 2; i < tabCount; i ++)
					m_view.getMainPanel().remove(2);
			}


			output = new ImageModel(m_model.getFile());
			output.convertTo8BitUCQ();

			// Use a textarea to display the table
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.getLookUpTable().keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				for (int i = 0; i < 3; i ++)
					tableArea.append(output.getLookUpTable().get(index)[i] + "\t");

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);

			// display index image
			
			JLabel label2 = new JLabel(new ImageIcon(output.getIndexImg()));
			m_view.getMainPanel().add(logScrollPane, "Look Up Table");
			m_view.getMainPanel().add(label2, "Index Image");

			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
		}
	}

	class MCQListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// remove the uncessary tabs
			int tabCount = m_view.getMainPanel().getTabCount();
			if (tabCount > 2) {
				for (int i = 2; i < tabCount; i ++)
					m_view.getMainPanel().remove(2);
			}

			output = new ImageModel(m_model.getFile());
			output.convertTo8BitMCQ();

			// Use a textarea to display the table
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.getLookUpTableMedian().keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\tFrequency\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				tableArea.append(output.getLookUpTableMedian().get(index).getRmean() + "\t");
				tableArea.append(output.getLookUpTableMedian().get(index).getGmean() + "\t");
				tableArea.append(output.getLookUpTableMedian().get(index).getBmean() + "\t");

				tableArea.append("" + output.getLookUpTableMedian().get(index).getHistogram().size());

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);

			// display index image
			JLabel label2 = new JLabel(new ImageIcon(output.getIndexImg()));

			m_view.getMainPanel().add(logScrollPane, "Look Up Table");
			m_view.getMainPanel().add(label2, "Index Image");

			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class MCQErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// remove the uncessary tabs
			int tabCount = m_view.getMainPanel().getTabCount();
			if (tabCount > 2) {
				for (int i = 2; i < tabCount; i ++)
					m_view.getMainPanel().remove(2);
			}

			output = new ImageModel(m_model.getFile());
			output.convertTo8BitMCQError("floyd");

			// Use a textarea to display the table
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.getLookUpTableMedian().keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\tFrequency\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				tableArea.append(output.getLookUpTableMedian().get(index).getRmean() + "\t");
				tableArea.append(output.getLookUpTableMedian().get(index).getGmean() + "\t");
				tableArea.append(output.getLookUpTableMedian().get(index).getBmean() + "\t");

				tableArea.append("" + output.getLookUpTableMedian().get(index).getHistogram().size());

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);

			// display index image
			JLabel label2 = new JLabel(new ImageIcon(output.getIndexImg()));
			
			m_view.getMainPanel().add(logScrollPane, "Look Up Table");
			m_view.getMainPanel().add(label2, "Index Image");

			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class MCQBellListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// remove the uncessary tabs
			int tabCount = m_view.getMainPanel().getTabCount();
			if (tabCount > 2) {
				for (int i = 2; i < tabCount; i ++)
					m_view.getMainPanel().remove(2);
			}

			output = new ImageModel(m_model.getFile());
			output.convertTo8BitMCQError("bell");

			// Use a textarea to display the table
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.getLookUpTableMedian().keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\tFrequency\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				tableArea.append(output.getLookUpTableMedian().get(index).getRmean() + "\t");
				tableArea.append(output.getLookUpTableMedian().get(index).getGmean() + "\t");
				tableArea.append(output.getLookUpTableMedian().get(index).getBmean() + "\t");

				tableArea.append("" + output.getLookUpTableMedian().get(index).getHistogram().size());

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);
			
			// display index image
			JLabel label2 = new JLabel(new ImageIcon(output.getIndexImg()));
			
			m_view.getMainPanel().add(logScrollPane, "Look Up Table");
			m_view.getMainPanel().add(label2, "Index Image");

			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class MCQStuckiListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// remove the uncessary tabs
			int tabCount = m_view.getMainPanel().getTabCount();
			if (tabCount > 2) {
				for (int i = 2; i < tabCount; i ++)
					m_view.getMainPanel().remove(2);
			}

			output = new ImageModel(m_model.getFile());
			output.convertTo8BitMCQError("floyd");

			// Use a textarea to display the table
			JTextArea tableArea = new JTextArea(30,40);

			// print the table to the textarea
			Iterator<Integer> iter = output.getLookUpTableMedian().keySet().iterator();

			tableArea.append("Look Up Table\n");
			tableArea.append("index\tR\tG\tB\tFrequency\n");
			tableArea.append("--------------------------------------------------------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				tableArea.append(output.getLookUpTableMedian().get(index).getRmean() + "\t");
				tableArea.append(output.getLookUpTableMedian().get(index).getGmean() + "\t");
				tableArea.append(output.getLookUpTableMedian().get(index).getBmean() + "\t");

				tableArea.append("" + output.getLookUpTableMedian().get(index).getHistogram().size());

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);
			
			// display index image
			JLabel label2 = new JLabel(new ImageIcon(output.getIndexImg()));
			
			m_view.getMainPanel().add(logScrollPane, "Look Up Table");
			m_view.getMainPanel().add(label2, "Index Image");

			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class LZWEncodingListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// pop out dialog to ask for the size of the dictionary
			String sizeString = "";

			while (sizeString.isEmpty() || !sizeString.matches("[0-9]+")) {
				sizeString = JOptionPane.showInputDialog("Please input the size of the dictionary");
			}

			Integer size = Integer.valueOf(sizeString);

			// remove the uncessary tabs
			int tabCount = m_view.getMainPanel().getTabCount();
			if (tabCount > 2) {
				for (int i = 2; i < tabCount; i ++)
					m_view.getMainPanel().remove(2);
			}

			String result = text_model.lzwEncoding(text_model.getMessage(), size);

			m_view.getOutputText().setText(result);

			JTextArea tableArea = new JTextArea();

			Iterator<Integer> iter = text_model.getLzwTable().keySet().iterator();

			tableArea.append("LZW Table\n");
			tableArea.append("index\tvalue\n");
			tableArea.append("---------------------------\n");

			while (iter.hasNext()) {
				Integer index = iter.next();
				tableArea.append(index + "\t");

				tableArea.append(text_model.getLzwTable().get(index));

				tableArea.append("\n");
			}

			JScrollPane logScrollPane = new JScrollPane(tableArea);

			double compressionRatio = (double) text_model.getSizeAfterEncoded() / text_model.getSize();

			JTextArea ratio = new JTextArea("Compression Ratio: " + compressionRatio);

			JScrollPane ratioPane = new JScrollPane(ratio);

			m_view.getMainPanel().add(logScrollPane, "LZW Table");
			m_view.getMainPanel().add(ratioPane, "Compression Ratio");
		}
	}
}