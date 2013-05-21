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

/** Controller class to connect view and models */
public class Controllers {
	//... The Controller needs to interact with both the Model and View.
	private ImageModel m_model;
	private Views m_view;
	private ImageModel output, output2, output3, output4;
	private TextModel text_model;
	private JPEGImage jpegImage;
	
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
		m_view.addAliasingListener(new AliasingListener());
		m_view.addCircleListener(new CircleListener());
		m_view.addHuffmanListener(new HuffmanListener());
		m_view.addResizeListener(new ResizeListener());
		m_view.addDeResizeListener(new DeResizeLisetner());
		m_view.addColorTransformListener(new ColorTransformListener());
		m_view.addDCTListener(new DCTListener());
	}

	/**
	 * Clean all the tabs after nth tab
	 * 
	 * @param n index of tab + 1
	 */
	public void cleanTabs(int n) {
		// remove the uncessary tabs
		int tabCount = m_view.getMainPanel().getTabCount();
		if (tabCount > n) {
			for (int i = n; i < tabCount; i ++)
				m_view.getMainPanel().remove(n);
		}
	}
	
	//====================== Action Listeners
	class QuitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	class LoadListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			cleanTabs(2);

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

					m_view.getMainPanel().setTitleAt(0, "Input Image");
					m_view.getMainPanel().setTitleAt(1, "Output Image");
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

					m_view.getMainPanel().setTitleAt(0, "Input Text");
					m_view.getMainPanel().setTitleAt(1, "Output Text");
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
			if (m_view.getMainPanel().getSelectedIndex() == 1) {
				if (output.getImg() != null) {
					int returnVal = m_view.getFCS().showSaveDialog(m_view);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = m_view.getFCS().getSelectedFile();
						output.write2PPM(file);
					} else {
						// cancel case
					}
				}
			} else if (m_view.getMainPanel().getSelectedIndex() == 2) {
				if (output2.getImg() != null) {
					int returnVal = m_view.getFCS().showSaveDialog(m_view);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = m_view.getFCS().getSelectedFile();
						output2.write2PPM(file);
					} else {
						// cancel case
					}
				}
			} else if (m_view.getMainPanel().getSelectedIndex() == 3) {
				if (output3.getImg() != null) {
					int returnVal = m_view.getFCS().showSaveDialog(m_view);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = m_view.getFCS().getSelectedFile();
						output3.write2PPM(file);
					} else {
						// cancel case
					}
				}
			} else if (m_view.getMainPanel().getSelectedIndex() == 4) {
				if (output4.getImg() != null) {
					int returnVal = m_view.getFCS().showSaveDialog(m_view);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = m_view.getFCS().getSelectedFile();
						output4.write2PPM(file);
					} else {
						// cancel case
					}
				}
			}
		}
	}

	class GrayListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			output = new ImageModel(m_model.getFile());
			output.convertToGray();
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			m_view.getMainPanel().setTitleAt(1, "Gray Scale");
		}
	}

	class BiDirectlyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			output = new ImageModel(m_model.getFile());
			output.convertToBiDirectly();
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			m_view.getMainPanel().setTitleAt(1, "1-Bit Level Image (Directly)");
		}
	}

	class BiErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			output = new ImageModel(m_model.getFile());
			output.convertToBiError("floyd");
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			m_view.getMainPanel().setTitleAt(1, "1-Bit Level Image (Error Diffusion(Floyd))");
		}
	}

	class BiErrorBellListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			output = new ImageModel(m_model.getFile());
			output.convertToBiError("bell");
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			m_view.getMainPanel().setTitleAt(1, "1-Bit Level Image (Error Diffusion(Bell))");
		}
	}

	class BiErrorStuckiListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			output = new ImageModel(m_model.getFile());
			output.convertToBiError("stucki");
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			m_view.getMainPanel().setTitleAt(1, "1-Bit Level Image (Error Diffusion(Stucki))");
		}
	}

	class QuadErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			output = new ImageModel(m_model.getFile());
			output.convertToQuadError();
			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));

			m_view.getMainPanel().setTitleAt(1, "2-Bit Level Image (Error Diffusion)");
		}
	}

	class UCQListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

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
			m_view.getMainPanel().setTitleAt(1, "Uniform Color Quantization");
		}
	}

	class MCQListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

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

			m_view.getMainPanel().setTitleAt(1, "Median Cut Algorithm");

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class MCQErrorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

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
			m_view.getMainPanel().setTitleAt(1, "Median Cut Algorithm (Floyd)");

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class MCQBellListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

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
			m_view.getMainPanel().setTitleAt(1, "Median Cut Algorithm (Bell)");

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class MCQStuckiListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

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
			m_view.getMainPanel().setTitleAt(1, "Median Cut Algorithm (Stucki)");

			// write to file
			output.writeToFile("LUT.txt");
		}
	}

	class LZWEncodingListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			// pop out dialog to ask for the size of the dictionary
			String sizeString = "";

			while (sizeString.isEmpty() || !sizeString.matches("[0-9]+") || Integer.valueOf(sizeString) > 256) {
				sizeString = JOptionPane.showInputDialog("Please input the size of the dictionary");
			}

			// if user doesnt input value, default to be max size 256
			Integer size = 256;

			if (sizeString != null)
				size = Integer.valueOf(sizeString);

			String result = text_model.lzwEncoding(m_view.getTextArea().getText(), size);

			m_view.getOutputText().setText(result);

			m_view.getMainPanel().setTitleAt(1, "LZW Encoding");

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

			String decodedMessage = text_model.lzwDecoding(text_model.getLzwTable(), result, size);

			JTextArea decodedTextArea = new JTextArea("Decoded Message: \n" + decodedMessage);

			JScrollPane decodedPane = new JScrollPane(decodedTextArea);

			JScrollPane logScrollPane = new JScrollPane(tableArea);

			double compressionRatio = (double) text_model.getSizeAfterEncoded() / text_model.getSize();

			JTextArea ratio = new JTextArea("Compression Ratio: \n" + compressionRatio);

			JScrollPane ratioPane = new JScrollPane(ratio);

			m_view.getMainPanel().add(logScrollPane, "LZW Table");
			m_view.getMainPanel().add(ratioPane, "Compression Ratio");
			m_view.getMainPanel().add(decodedPane, "Decoded Message");
		}
	}

	class AliasingListener implements ActionListener {
		public Integer inputDialog(String message, Integer defaultValue) {
			String mString = "";

			while (mString.isEmpty() || !mString.matches("[0-9]+")) {
				mString = JOptionPane.showInputDialog(message);
			}

			// if user doesnt input value, default is 1
			Integer m = defaultValue;

			if (mString != null)
				m = Integer.valueOf(mString);

			return m;
		}

		public boolean powerOf2(int number) {
			if (number == 0)
				return false;
			while (number % 2 == 0) {
				number /= 2;
			}
			if (number > 1)
				return false;
			else
				return true;
		}

		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			Integer k = 0;

			while (k > 16 || !powerOf2(k)) {
				k = inputDialog("Please input k (sampling routine)", 1);
			}

			output = new ImageModel(m_model.getW(), m_model.getH());
			output.setImg(m_model.getImg());
			output2 = new ImageModel(m_model.getW(), m_model.getH());
			output2.setImg(m_model.getImg());
			output3 = new ImageModel(m_model.getW(), m_model.getH());
			output3.setImg(m_model.getImg());
			output4 = new ImageModel(m_model.getW(), m_model.getH());
			output4.setImg(m_model.getImg());

			output.subSampling(k, "default");
			output2.subSampling(k, "average");
			output3.subSampling(k, "filter1");
			output4.subSampling(k, "filter2");

			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
			m_view.getOutputLabel().setText("");
			m_view.getOutput().setViewportView(new JScrollPane(m_view.getOutputLabel()));
			m_view.getMainPanel().setTitleAt(1, "No filter");

			JLabel outputImage2 = new JLabel(new ImageIcon(output2.getImg()));
			JLabel outputImage3 = new JLabel(new ImageIcon(output3.getImg()));
			JLabel outputImage4 = new JLabel(new ImageIcon(output4.getImg()));

			JScrollPane outputPane2 = new JScrollPane(outputImage2);
			JScrollPane outputPane3 = new JScrollPane(outputImage3);
			JScrollPane outputPane4 = new JScrollPane(outputImage4);

			m_view.getMainPanel().add(outputPane2, "Average");
			m_view.getMainPanel().add(outputPane3, "Filter 1");
			m_view.getMainPanel().add(outputPane4, "Filter 2");
		}
	}

	class CircleListener implements ActionListener {
		public Integer inputDialog(String message, Integer defaultValue) {
			String mString = "";

			while (mString.isEmpty() || !mString.matches("[0-9]+")) {
				mString = JOptionPane.showInputDialog(message);
			}

			// if user doesnt input value, default is 1
			Integer m = defaultValue;

			if (mString != null)
				m = Integer.valueOf(mString);

			return m;
		}

		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			// pop out dialog to ask for the value of the N
			Integer m = 0;

			m = inputDialog("Please input M (the thickness of the circle)", 1);

			// pop out dialog to ask for the value of the M
			Integer n = 0;

			n = inputDialog("Please input N (the radius step of the circle)", 5);

			m_model.createCircleImage(m, n);
			output.createCircleImage(m, n);

			m_view.getImageLabel().setIcon(new ImageIcon(m_model.getImg()));
			m_view.getImageLabel().setText("");
			m_view.getInput().setViewportView(new JScrollPane(m_view.getImageLabel()));

			m_view.getOutputLabel().setIcon(new ImageIcon(output.getImg()));
			m_view.getOutputLabel().setText("");
			m_view.getOutput().setViewportView(new JScrollPane(m_view.getOutputLabel()));

			m_view.getMainPanel().setTitleAt(1, "Circle_"+m+"_"+n);
			m_view.getMainPanel().setTitleAt(0, "Circle_"+m+"_"+n);
		}
	}

	class HuffmanListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(1);

			String inputMessage = m_view.getTextArea().getText();

			int[] counts = text_model.getCharacterFrequency(inputMessage);
			Tree huffmanTree = text_model.getHuffmanTree(counts);
			String[] codes = text_model.getCode(huffmanTree.getRoot());

			char[] chars = inputMessage.toCharArray();

			JTextArea huffmanTable = new JTextArea();
			JTextArea huffmanResult = new JTextArea();

			huffmanTable.append("Huffman table to visualize the huffman node\n");
			huffmanTable.append("ASCII Code\tCharacter\tFrequency\tCode\n");

			for (int i = 0; i < codes.length; i ++)
				if (counts[i] != 0)
					huffmanTable.append("" + i + "\t" + (char) i + "\t" + counts[i] + "\t" + codes[i] + "\n");

			for (char character: chars) {
				huffmanResult.append(codes[(int)character] + " ");
			}

			JScrollPane resultPane = new JScrollPane(huffmanResult);
			JScrollPane tablePane = new JScrollPane(huffmanTable);

			m_view.getMainPanel().add(resultPane, "Decoded Message");
			m_view.getMainPanel().add(tablePane, "Huffman Table");
		}
	}

	class ResizeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			jpegImage = new JPEGImage(m_model.getFile());
			jpegImage.resize();
			m_view.getOutputLabel().setIcon(new ImageIcon(jpegImage.getImg()));

			m_view.getMainPanel().setTitleAt(1, "Resize");
		}
	}

	class DeResizeLisetner implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			jpegImage.deResize();

			m_view.getOutputLabel().setIcon(new ImageIcon(jpegImage.getImg()));

			m_view.getMainPanel().setTitleAt(1, "De-Resize");
		}
	}

	class ColorTransformListener implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			cleanTabs(2);

			jpegImage = new JPEGImage(m_model.getFile());

			jpegImage.resize();

			jpegImage.colorTransAndSubsample();
			jpegImage.invColorTransAndSuperSample();

			jpegImage.deResize();

			m_view.getOutputLabel().setIcon(new ImageIcon(jpegImage.getImg()));

			m_view.getMainPanel().setTitleAt(1, "Color Transform and De-Color-Transform");
		}
	}

	class DCTListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cleanTabs(2);

			jpegImage = new JPEGImage(m_model.getFile());

			jpegImage.resize();
			jpegImage.colorTransAndSubsample();
			jpegImage.dctEncoding();

			jpegImage.dctDecoding();
			jpegImage.invColorTransAndSuperSample();
			jpegImage.deResize();

			m_view.getOutputLabel().setIcon(new ImageIcon(jpegImage.getImg()));

			m_view.getMainPanel().setTitleAt(1, "Color Transform and De-Color-Transform");
		}
	}
}