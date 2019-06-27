/**==========================================================================
 * Project Name : Smart Editor
 * Published by : YOOK DONGHYUN (aomorikaitou93@gmail.com)
 * ==========================================================================
 * # This editor provides you to below features
 * - open, save the text file (ver 1.0)
 * - search keywords (ver 1.0)
 * - count the number of words (ver 1.0)
 * - count the number of characters (ver 1.0)
 * - make a report which contains frequency of each word (ver 1.0)
 * ==========================================================================
 * Version Control
 * --------------------------------------------------------------------------
 * 2019-06-26 : create Smart Editor ver 1.0
 * 2019-06-27 : Korean encoding problem solved
 * ==========================================================================*/

package editor;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.JTextArea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SmartEditor extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JFrame frame;
	private JPanel panel;
	private JScrollPane scrollPane;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnFind;
	private JMenu mnAnalyze;
	private JMenuItem mnItemOpen;
	private JMenuItem mnItemSave;
	private JMenuItem mnItemExit;
	private JMenuItem mnItemFind;
	private JMenuItem mnItemWordCount;
	private JMenuItem mnItemCharCount;
	private JMenuItem mnItemWordFreq;
	private JTextArea textArea;
	
	private String text;
	private String pattern;
	private BruteForce bf;

	/** Launch the application */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					SmartEditor window = new SmartEditor();
					window.frame.setVisible(true);
				} 
				catch (Exception e) { e.printStackTrace(); }
			}
		});
	}

	/** Create the application */
	public SmartEditor() { initialize(); }

	/** Initialize the contents of the frame */
	private void initialize() 
	{
		this.frame = new JFrame();
		this.frame.setTitle("No Title - Smart Editor");
		this.frame.setBounds(100, 100, 700, 500);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.menuBar = new JMenuBar();
		this.frame.setJMenuBar(this.menuBar);
		
		/**======================= File Menu Tab =============================*/
		this.mnFile = new JMenu("File");
		this.mnFile.setFont(new Font("Nanum Gothic", Font.BOLD, 16));
		this.menuBar.add(this.mnFile);
		
		/* Open Menu */
		this.mnItemOpen = new JMenuItem("Open");
		this.mnItemOpen.setFont(new Font("Nanum Gothic", Font.PLAIN, 14));
		this.mnItemOpen.addActionListener(this); // adding event handler
		this.mnFile.add(this.mnItemOpen);
		
		/* Save Menu */
		this.mnItemSave = new JMenuItem("Save");
		this.mnItemSave.setFont(new Font("Nanum Gothic", Font.PLAIN, 14));
		this.mnItemSave.addActionListener(this);
		this.mnFile.add(this.mnItemSave);

		/* Exit Menu */
		this.mnItemExit = new JMenuItem("Exit");
		this.mnItemExit.setFont(new Font("Nanum Gothic", Font.PLAIN, 14));
		this.mnItemExit.addActionListener(this);
		this.mnFile.add(this.mnItemExit);
		
		/**======================= Find Menu Tab =============================*/
		this.mnFind = new JMenu("Find");
		this.mnFind.setFont(new Font("Nanum Gothic", Font.BOLD, 16));
		this.menuBar.add(this.mnFind);
		
		/* Find Menu */
		this.mnItemFind = new JMenuItem("Find");
		this.mnItemFind.setFont(new Font("Nanum Gothic", Font.PLAIN, 14));
		this.mnItemFind.addActionListener(this);
		this.mnFind.add(this.mnItemFind);
		
		/**======================= Analyze Menu Tab =============================*/
		this.mnAnalyze = new JMenu("Analyze");
		this.mnAnalyze.setFont(new Font("Nanum Gothic", Font.BOLD, 16));
		this.menuBar.add(this.mnAnalyze);
		
		/* Word Count Menu */
		this.mnItemWordCount = new JMenuItem("Word Count");
		this.mnItemWordCount.setFont(new Font("Nanum Gothic", Font.PLAIN, 14));
		this.mnItemWordCount.addActionListener(this);
		this.mnAnalyze.add(this.mnItemWordCount);
		
		/* Character Count Menu */
		this.mnItemCharCount = new JMenuItem("Character Count");
		this.mnItemCharCount.setFont(new Font("Nanum Gothic", Font.PLAIN, 14));
		this.mnItemCharCount.addActionListener(this);
		this.mnAnalyze.add(this.mnItemCharCount);
		
		/* Word Frequency Menu */
		this.mnItemWordFreq = new JMenuItem("Word Frequency");
		this.mnItemWordFreq.setFont(new Font("Nanum Gothic", Font.PLAIN, 14));
		this.mnItemWordFreq.addActionListener(this);
		this.mnAnalyze.add(this.mnItemWordFreq);
		
		/**======================= Text Area =============================*/
		this.panel = new JPanel();
		this.panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.frame.getContentPane().add(this.panel, BorderLayout.CENTER);
		this.panel.setLayout(new BorderLayout(0, 0));
		
		this.scrollPane = new JScrollPane();
		this.panel.add(scrollPane);
		
		/* Text Area */
		this.textArea = new JTextArea();
		this.textArea.setFont(new Font("Nanum Gothic", Font.PLAIN, 16));
		this.scrollPane.setViewportView(textArea);
		this.textArea.setLineWrap(true);
		this.textArea.setColumns(50);
	}

	@Override
	public void actionPerformed(ActionEvent e) // all the actions will be handled in here
	{ 
		/**======================= File Menu Tab =============================*/
		/* Open Menu */
		if(e.getSource() == mnItemOpen) // e.getSource(): identify where the action happened
		{
			JFileChooser fc = new JFileChooser(); // can select file from dialog box
			FileNameExtensionFilter filter = new FileNameExtensionFilter("txt","txt"); // set file format
	        fc.setFileFilter(filter); // apply file format
			int returnVal = fc.showOpenDialog(SmartEditor.this); // belong dialog to program
			this.frame.setTitle(fc.getSelectedFile().getName() + " - Smart Editor"); // set title
			
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				textArea.setText("");
				File file = fc.getSelectedFile();
				
				try 
				{
					BufferedReader reader  =  new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8")); // korean encoding
					
					String line = null;
					while((line = reader.readLine()) != null)
						textArea.append(line + "\n");
					reader.close();
				} 
				catch (FileNotFoundException e1) { e1.printStackTrace(); } 
				catch (IOException e1) { e1.printStackTrace(); }
			}
		}
		
		/* Save Menu */
		if(e.getSource() == mnItemSave)
		{
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("txt","txt"); // set file format
	        fc.setFileFilter(filter); // apply file format
			int returnVal = fc.showSaveDialog(SmartEditor.this);
			this.frame.setTitle(fc.getSelectedFile().getName() + " - Smart Editor"); // set title
			
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();
				try 
				{
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					writer.write(textArea.getText());
					writer.flush();
					writer.close();
				} 
				catch (IOException e1) { e1.printStackTrace(); }
			}
		}
		
		/* Exit Menu */
		if(e.getSource() == mnItemExit)
			this.frame.dispose(); // target of object needs to be frame object
		
		/**======================= Find Menu Tab =============================*/
		/* Find Menu */
		if(e.getSource() == mnItemFind)
		{
			text = textArea.getText();
			pattern = (String) JOptionPane.showInputDialog(frame, "Find : ", "Keyword Finder", 1);

			bf = new BruteForce(text, pattern);

			Highlighter h = textArea.getHighlighter();
			h.removeAllHighlights();

			ArrayList<Integer> arr = bf.patternMatch(text, pattern);
			for (int i = 0; i < arr.size(); i++) 
			{
				if (((Integer) arr.get(0)).intValue() == -1)
					JOptionPane.showMessageDialog(null, "No Match is found", "Error", 0);
				
				int pos = ((Integer) arr.get(i)).intValue();
				try 
				{
					h.addHighlight(pos, pos + pattern.length(), DefaultHighlighter.DefaultPainter);
				} 
				catch (BadLocationException ex) 
				{
					System.err.println(ex);
				}
			}
		}
		
		/**======================= Analyze Menu Tab =============================*/
		/* Word Count Menu */
		if(e.getSource() == mnItemWordCount)
		{
			String targetStr = textArea.getText(); // target string from textArea
			String[] words = targetStr.split("\\W"); // parse by using regular expression
			int count = words.length; // count the number of words
			
			JOptionPane.showMessageDialog(frame, "This text consists of " + count + " words" , "Word Counter", JOptionPane.PLAIN_MESSAGE);
		}
		
		/* Character Count Menu */
		if(e.getSource() == mnItemCharCount)
		{
			String targetStr = textArea.getText(); // target string from textArea
			int count = targetStr.length(); // count the number of characters
			
			JOptionPane.showMessageDialog(frame, "This text consists of " + count + " characters" , "Character Counter", JOptionPane.PLAIN_MESSAGE);
		}
		
		/* Word Frequency Menu */
		if(e.getSource() == mnItemWordFreq)
		{
			String targetStr = textArea.getText(); // target string from textArea
			String[] words = targetStr.split("\\W"); // parse by using space
			
			HashMap<String, Integer> word_dictionary = new HashMap<String, Integer>(); // for word frequency counting
			for(String word : words)
			{
				if(word_dictionary.containsKey(word)) // if word already exists
					word_dictionary.replace(word, (int) word_dictionary.get(word), (int) word_dictionary.get(word) + 1); // increase count
				else // if word does not exist
					word_dictionary.put(word, 1); // create pair
			}
			
			Set<String> keySet = word_dictionary.keySet(); // extract key sets
			StringBuffer result = new StringBuffer(""); // for result string
			for(String key : keySet) // create result
				result.append(key + " : " + word_dictionary.get(key) + " counts\n");
			
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("txt","txt"); // set file format
	        fc.setFileFilter(filter); // apply file format
			int returnVal = fc.showSaveDialog(SmartEditor.this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();
				try 
				{
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					writer.append(result);
					writer.flush();
					writer.close();
					
					JOptionPane.showMessageDialog(frame, "Successfully saved result file!" , "Notice", JOptionPane.PLAIN_MESSAGE);
				} 
				catch (IOException e1) { e1.printStackTrace(); }
			}
		}
	}
}