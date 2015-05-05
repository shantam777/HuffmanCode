import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.*;



class TextAreaOutputStream
extends OutputStream {

	// *************************************************************************************************
	// INSTANCE MEMBERS
	// *************************************************************************************************

	private byte[] oneByte; // array for write(int val);
	private Appender appender; // most recent action

	public TextAreaOutputStream(JTextArea txtara) {
		this(txtara, 1000);
	}

	public TextAreaOutputStream(JTextArea txtara, int maxlin) {
		if (maxlin < 1) {
			throw new IllegalArgumentException("TextAreaOutputStream maximum lines must be positive (value=" + maxlin + ")");
		}
		oneByte = new byte[1];
		appender = new Appender(txtara, maxlin);
	}

	/** Clear the current console text area. */
	public synchronized void clear() {
		if (appender != null) {
			appender.clear();
		}
	}

	public synchronized void close() {
		appender = null;
	}

	public synchronized void flush() {}

	public synchronized void write(int val) {
		oneByte[0] = (byte) val;
		write(oneByte, 0, 1);
	}

	public synchronized void write(byte[] ba) {
		write(ba, 0, ba.length);
	}

	public synchronized void write(byte[] ba, int str, int len) {
		if (appender != null) {
			appender.append(bytesToString(ba, str, len));
		}
	}


	static private String bytesToString(byte[] ba, int str, int len) {
		try {
			return new String(ba, str, len, "UTF-8");
		} catch (UnsupportedEncodingException thr) {
			return new String(ba, str, len);
		}
	}

	// *************************************************************************************************
	// STATIC MEMBERS
	// *************************************************************************************************

	static class Appender
	implements Runnable {
		private final JTextArea textArea;
		private final int maxLines; // maximum lines allowed in text area
		private final LinkedList < Integer > lengths; // length of lines within text area
		private final List < String > values; // values waiting to be appended

		private int curLength; // length of current line
		private boolean clear;
		private boolean queue;

		Appender(JTextArea txtara, int maxlin) {
			textArea = txtara;
			maxLines = maxlin;
			lengths = new LinkedList < Integer > ();
			values = new ArrayList < String > ();

			curLength = 0;
			clear = false;
			queue = true;
		}

		synchronized void append(String val) {
			values.add(val);
			if (queue) {
				queue = false;
				EventQueue.invokeLater(this);
			}
		}

		synchronized void clear() {
			clear = true;
			curLength = 0;
			lengths.clear();
			values.clear();
			if (queue) {
				queue = false;
				EventQueue.invokeLater(this);
			}
		}

		// MUST BE THE ONLY METHOD THAT TOUCHES textArea!
		public synchronized void run() {
			if (clear) {
				textArea.setText("");
			}
			for (String val: values) {
				curLength += val.length();
				if (val.endsWith(EOL1) || val.endsWith(EOL2)) {
					if (lengths.size() >= maxLines) {
						textArea.replaceRange("", 0, lengths.removeFirst());
					}
					lengths.addLast(curLength);
					curLength = 0;
				}
				textArea.append(val);
			}
			values.clear();
			clear = false;
			queue = true;
		}

		static private final String EOL1 = "\n";
		static private final String EOL2 = System.getProperty("line.separator", EOL1);
	}

} /* END PUBLIC CLASS */

abstract class HuffmanTree implements Comparable < HuffmanTree > {
	public final int frequency; // the frequency of this tree
	public HuffmanTree(int freq) {
		frequency = freq;
	}

	// compares on the frequency
	public int compareTo(HuffmanTree tree) {
		return frequency - tree.frequency;
	}
}

class HuffmanLeaf extends HuffmanTree {
	public final char value; // the character this leaf represents

	public HuffmanLeaf(int freq, char val) {
		super(freq);
		value = val;
	}
}

class HuffmanNode extends HuffmanTree {
	public final HuffmanTree left, right; // subtrees

	public HuffmanNode(HuffmanTree l, HuffmanTree r) {
		super(l.frequency + r.frequency);
		left = l;
		right = r;
	}
}

class HuffmanElement{
		public static char[] leafVal = new char[100];
		public static String[] prefix = new String[100];
	}

public class HuffmanCode {
	// input is an array of frequencies, indexed by character code
	public static String encodedString;
	public static int index;
	public static int i,j;
	public static HuffmanElement temp;
	public static HuffmanTree buildTree(int[] charFreqs) {
		PriorityQueue < HuffmanTree > trees = new PriorityQueue < HuffmanTree > ();
		// initially, we have a forest of leaves
		// one for each non-empty character
		for (int i = 0; i < charFreqs.length; i++)
		if (charFreqs[i] > 0) trees.offer(new HuffmanLeaf(charFreqs[i], (char) i));

		assert trees.size() > 0;
		// loop until there is only one tree left
		while (trees.size() > 1) {
			// two trees with least frequency
			HuffmanTree a = trees.poll();
			HuffmanTree b = trees.poll();

			// put into new node and re-insert into queue
			trees.offer(new HuffmanNode(a, b));
		}
		return trees.poll();
	}

	public static void printCodes(HuffmanTree tree, StringBuffer prefix) {
		assert tree != null;
		if (tree instanceof HuffmanLeaf) {
			HuffmanLeaf leaf = (HuffmanLeaf) tree;

			// print out character, frequency, and code for this leaf (which is just the prefix)
			System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);
			temp.leafVal[i++] = leaf.value;
			temp.prefix[j++]=prefix.toString();

		} else if (tree instanceof HuffmanNode) {
			HuffmanNode node = (HuffmanNode) tree;

			// traverse left
			prefix.append('0');
			printCodes(node.left, prefix);
			prefix.deleteCharAt(prefix.length() - 1);

			// traverse right
			prefix.append('1');
			printCodes(node.right, prefix);
			prefix.deleteCharAt(prefix.length() - 1);
		}
	}

	public static void main(String[] args) {
		// NEW FRAME 
		Frame newframe = new Frame("Huffman Coding");
		newframe.setSize(300, 200);
		newframe.setLayout(null);
		newframe.setVisible(true);

		//NEW LABEL
		Label msglabel = new Label();
		msglabel.setText("Open the file to generate a Huffman tree.");
		msglabel.setBounds(20, 50, 250, 20);
		newframe.add(msglabel);



		String cwd = System.getProperty("user.dir");
		final JFileChooser jfc = new JFileChooser(cwd);

		Button filebutton = new Button("Open file for compression");
		filebutton.setBounds(50, 130, 200, 30);
		newframe.add(filebutton);



		//WINDOW CLOSE EVENT
		newframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				System.exit(0);
			}
		});
		filebutton.addActionListener(new ActionListener() {
			String str = null;
			String line = null;
			StringBuilder contents = new StringBuilder();
			public void actionPerformed(ActionEvent e) {
				if (jfc.showOpenDialog(newframe) != JFileChooser.APPROVE_OPTION) return;
				File f = jfc.getSelectedFile();
				FileReader fr;
				try {
					fr = new FileReader(f.toString());
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					throw new RuntimeException(e2);
				}
				BufferedReader input = new BufferedReader(fr);
				try {
					while ((line = input.readLine()) != null) {
						contents.append(line);
					}
				} catch (IOException ex) {
					System.out.println("ERROR : Input error!");
				}
				str = contents.toString();
				try {
					fr.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						newframe.setCursor(Cursor.
						getPredefinedCursor(
						Cursor.DEFAULT_CURSOR));

					}
				});
				newframe.dispose();
				JFrame newopframe = new JFrame();
				newopframe.add(new JLabel(" Output"), BorderLayout.NORTH);
				newopframe.setSize(1024, 768);
				JTextArea ta = new JTextArea();
				TextAreaOutputStream taos = new TextAreaOutputStream(ta, 60);
				PrintStream ps = new PrintStream(taos);
				System.setOut(ps);
				System.setErr(ps);
				newopframe.add(new JScrollPane(ta));
				newopframe.setVisible(true);
				newopframe.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent windowevent) {
						System.exit(0);
					}
				});
				if (str.length() == 0) {
					newopframe.setSize(400, 170);
					System.out.println("ERROR : Blank String Entered! Press close button to exit.");
				} else {
					System.out.println(str);
					// we will assume that all our characters will have
					// code less than 256, for simplicity
					int[] charFreqs = new int[256];
					// read each character and record the frequencies
					for (char c: str.toCharArray())
					charFreqs[c]++;

					// build tree
					HuffmanTree tree = buildTree(charFreqs);
					encodedString="";
					index=0;
					i=0;
					j=0;
					Character[] charObjectArray = str.chars().mapToObj(c -> (char)c).toArray(Character[]::new);
					// print out results
					System.out.println("SYMBOL\tFREQUENCY\tHUFFMAN CODE");
					printCodes(tree, new StringBuffer());
					int k,l;
					int testlen=0;
					while(testlen<=charObjectArray.toString().length())
					{
						k=0;
						l=0;
						while(k<=i&&l<=j)
						{
							if(charObjectArray[index]==temp.leafVal[k])
							{
								encodedString = encodedString.concat(temp.prefix[l].toString());
								break;
							}
							k++;
							l++;
						}
						index++;
						testlen++;
					}
					System.out.println("\n\nEncoded String : " + encodedString);
				}
			}
		});
	}
}