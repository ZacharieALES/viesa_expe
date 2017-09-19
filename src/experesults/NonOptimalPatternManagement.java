package experesults;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import View.AAExpertEditableTable;
import main.MainTutorial;
import model.Alignment;
import model.Corpus;
import net.miginfocom.swing.MigLayout;
import tuto.AbstractTutoStep;
import tuto.CompleteNonOptimalPatterns;
import tuto.EditSimilarityCombobox;
import tuto.EditSimilarityTable;

public class NonOptimalPatternManagement extends JFrame implements AWTEventListener{

	private static final long serialVersionUID = 8230191127948616315L;

	private AAExpertEditableTable table1 = new AAExpertEditableTable();
	private AAExpertEditableTable table2 = new AAExpertEditableTable();

	private JLabel jl_o1 = new JLabel(": to use it");
	private JLabel jl_o2 = new JLabel(": to use it");

	private int displayedId = 0;

	private JButton jbNext, jbPrevious;
	private JButton jbNextUnedited, jbPreviousUnedited;

	private JLabel jlDisplayed = new JLabel();
	private JLabel jlEdited = new JLabel();

	private String isOverTutoMessage = "<html>The tutorial is over. <br>"
			+ "You are now ready to use VIESA to its full potential!<br><br>"
			+ "If you have any question regarding the evaluation or the software please feel free to contact me: zacharie.ales@univ-avignon.fr</html>";

	private String isOverMessage = "<html>Job done. <br>Thanks for your help!<br><br>Don't forget to send your folder named \"results\" (which should be in the jar file folder) at the following address: zacharie.ales@univ-avignon.fr</html>";
	private boolean isCtrlPressed = false, isShiftPressed = false;



	ExperienceResults results = null;

	public NonOptimalPatternManagement(){

		JPanel mainPanel;

		if(MainTutorial.IS_TUTO)
			mainPanel = new JPanel(new MigLayout("fill", "[center, 25%][center, 25%][center, 25%][center, 25%]", "[shrink][grow][shrink][]"));
		else
			mainPanel = new JPanel(new MigLayout("fill,debug", "[center, 25%][center, 25%][center, 25%][center, 25%]", "[shrink][grow][shrink]"));

		if(Corpus.getCorpus().results != null)
			results = Corpus.getCorpus().results;
		else if(MainTutorial.results != null)
			results = MainTutorial.results;

		/* Confirm the closing */
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {


				NonOptimalPatternManagement.this.toFront();

				/* If all the non optimal alignments have been edited */
				if(results != null && nbOfEditedAlignments() == results.getRelevantNonOptimalAlignments().size()){

					String message = isOverMessage;
					
					if(MainTutorial.IS_TUTO)
						message = isOverTutoMessage;
					else
						results.serialiseXMLFile();

					JOptionPane.showMessageDialog(NonOptimalPatternManagement.this,
							message);

					NonOptimalPatternManagement.this.dispose();
					System.exit(0);					
				}
				else{

					int reponse = JOptionPane.showConfirmDialog(NonOptimalPatternManagement.this,
							"Do you really want to quit the application ?",
							"Confirmation", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);

					if (reponse == JOptionPane.YES_OPTION) {

						if(!MainTutorial.IS_TUTO)
							results.serialiseXMLFile();

						NonOptimalPatternManagement.this.dispose();
						System.exit(0);
					}

				}


			}
		});

		table1.setColor(new Color(0,158,206));
		table2.setColor(new Color(156,207,49));

		table1.emptyTable();
		table2.emptyTable();

		jbPrevious = new JButton("Previous alignment (Ctrl + Left)");
		jbNext = new JButton("Next alignment (Ctrl + Right)");

		jbPreviousUnedited = new JButton("Previous unedited alignment (Ctrl + Shift + Left)");
		jbNextUnedited = new JButton("Next unedited alignment (Ctrl + Shift + Right)");

		jbNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setNextAlignment();
			}

		});
		jbPrevious.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				setPreviousAlignment();

			}

		});

		jbNextUnedited.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setNextUneditedAlignment();
			}

		});
		jbPreviousUnedited.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				setPreviousUneditedAlignment();

			}

		});

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {

				/* If the number of edited alignments may have changed */
				if(nbOfExpertSelectedAnnotationsForCurrentAlignment() < 2){

					new Thread(){

						@Override
						public void run(){

							SwingUtilities.invokeLater(new Runnable(){
								@Override
								public void run(){
									if(results != null)
										jlEdited.setText("Number of edited alignments: " + nbOfEditedAlignments() + " / " + results.getRelevantNonOptimalAlignments().size());
								}
							});
						}
					}.run();
				}
			}
		};

		jlEdited.setText("Number of edited alignments: " + nbOfEditedAlignments() + " / " + results.getRelevantNonOptimalAlignments().size());


		table1.addMouseListener(ma);
		table2.addMouseListener(ma);


		Toolkit tk = Toolkit.getDefaultToolkit();
		tk.addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		
		JLabel jlLegendAdded = new JLabel(" Added ");
		jlLegendAdded.setOpaque(true);
		jlLegendAdded.setBackground(new Color(160, 160, 236));
		
		JLabel jlLegendRemoved = new JLabel(" Removed ");
		jlLegendRemoved.setOpaque(true);
		jlLegendRemoved.setBackground(new Color(255, 255, 0));
		
		JLabel jlLegendNotIn = new JLabel(" Not in the pattern ");
		jlLegendNotIn.setOpaque(true);
		jlLegendNotIn.setBackground(new Color(255, 255, 255));
		
		JLabel jlLegendIn = new JLabel(" In the pattern ");
		jlLegendIn.setOpaque(true);
		jlLegendIn.setBackground(new Color(237, 174, 174));

		JPanel jpLegendColor = new JPanel(new MigLayout("", "[][][][]", "[]"));
		jpLegendColor.add(jlLegendNotIn);
		jpLegendColor.add(jlLegendIn);
		jpLegendColor.add(jlLegendAdded);
		jpLegendColor.add(jlLegendRemoved);

		jpLegendColor.setBorder(BorderFactory.createTitledBorder("Background color legend"));
		
		mainPanel.add(jl_o1, "spanx 2");
		mainPanel.add(jl_o2, "spanx 2, wrap");
		mainPanel.add(table1.getJSP(), "spanx 2, grow");
		mainPanel.add(table2.getJSP() ,"spanx 2, grow, wrap");
		mainPanel.add(jpLegendColor, "spany 2");
		mainPanel.add(jlDisplayed);
		mainPanel.add(jbPrevious);
		mainPanel.add(jbNext, "wrap");
		mainPanel.add(jlEdited);
		mainPanel.add(jbPreviousUnedited);
		mainPanel.add(jbNextUnedited, "wrap");

		if(MainTutorial.IS_TUTO 
				&& MainTutorial.getCurrentStep() != null
				&& (MainTutorial.getCurrentStep() instanceof CompleteNonOptimalPatterns)
				){

			AbstractTutoStep ads = MainTutorial.getCurrentStep();

			JPanel jpTuto = new JPanel(new MigLayout("", "[]", "[][]10[][]"));

			JLabel jlTutoStepDescription = new JLabel();
			JLabel jlTutoStepInstructions = new JLabel();

			JLabel jlTuto1 = new JLabel("<html><b>Description (step " + (MainTutorial.currentStepId) + "/" + (MainTutorial.lSteps.size()-1)	 + ")</b></html>");
			JLabel jlTuto2 = new JLabel("<html><b>How to go to the next step</b></html>");

			jlTutoStepDescription.setText("<html>" + ads.description() + "</html>");
			jlTutoStepInstructions.setText("<html>" + ads.instructions() + "</html>");

			jpTuto.add(jlTuto1, "wrap");
			jpTuto.add(jlTutoStepDescription, "wrap");
			jpTuto.add(jlTuto2, "wrap");
			jpTuto.add(jlTutoStepInstructions);

			mainPanel.add(jpTuto, "spanx 4");

		}


		this.setTitle("Complete non optimal relevant patterns");
		this.setSize(650, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH );

		if(results != null && results.getRelevantNonOptimalAlignments().size() > 0){
			displayedId = 0;
			displayAlignment();
		}

		this.setContentPane(mainPanel);
		this.setVisible(true);

	}

	public int nbOfExpertSelectedAnnotationsForCurrentAlignment(){
		int result = 0;
		if(table1.getPattern() != null){

			if(table1.getPattern().expertAddedCoordinates != null)
				result += table1.getPattern().expertAddedCoordinates.size();

			if(table1.getPattern().expertRemovedCoordinates != null)
				result += table1.getPattern().expertRemovedCoordinates.size();
		}

		if(table2.getPattern() != null){

			if(table2.getPattern().expertAddedCoordinates != null)
				result += table2.getPattern().expertAddedCoordinates.size();

			if(table2.getPattern().expertRemovedCoordinates != null)
				result += table2.getPattern().expertRemovedCoordinates.size();
		}

		return result;
	}

	public int nbOfEditedAlignments(){

		int nbOfEditedAlignments = 0;
		if(results != null)
			for(Alignment a: results.getRelevantNonOptimalAlignments())
				if(a.getP1().isEditedByExpert() || a.getP2().isEditedByExpert())
					nbOfEditedAlignments++;

		return nbOfEditedAlignments;
	}



	public void displayAlignment(){

		if(results != null){
			jlDisplayed.setText("Alignment currently displayed: " + (displayedId+1) + " / " + results.getRelevantNonOptimalAlignments().size());
			Alignment a = results.getRelevantNonOptimalAlignments().get(displayedId);

			StringBuffer labelText1 = new StringBuffer();
			StringBuffer labelText2 = new StringBuffer();

			labelText1.append(a.getP1().getOriginalAA().getFileName());
			labelText2.append(a.getP2().getOriginalAA().getFileName());

			/* If p is not already displayed in the main Table */
			if(a.getP1() != table1.getPattern()){

				if(a.getP1().getCoordinates().size() > 0)
					table1.setPattern(a.getP1());
				else
					table1.setAA(a.getP1().getOriginalAA());

				jl_o1.setText(labelText1.toString());

			}

			/* If p is not already displayed in the main Table */
			if(a.getP2() != table2.getPattern()){

				if(a.getP2().getCoordinates().size() > 0)
					table2.setPattern(a.getP2());
				else
					table2.setAA(a.getP2().getOriginalAA());

				jl_o2.setText(labelText2.toString());
			}
		}


	}



	protected void setPreviousUneditedAlignment() {

		int newId = -1;
		int i = 1;

		if(results != null)
			while(newId == -1 && i < results.getRelevantNonOptimalAlignments().size()){

				if(displayedId == 0)
					displayedId = results.getRelevantNonOptimalAlignments().size() - 1;
				else
					displayedId--;

				Alignment a = results.getRelevantNonOptimalAlignments().get(displayedId);

				if(!a.isEditedByExpert())
					newId = displayedId;

				i++;

			}

		if(newId != -1){
			displayedId = newId;
			displayAlignment();
		}
		else{
			if(!MainTutorial.IS_TUTO){
				results.serialiseXMLFile();

				JOptionPane.showMessageDialog(this,
						isOverMessage);
			}
			else
				JOptionPane.showMessageDialog(this,
						isOverTutoMessage);

			NonOptimalPatternManagement.this.dispose();
			System.exit(0);
		}

	}


	protected void setNextUneditedAlignment() {

		int newId = -1;
		int i = 1;
		if(results != null)
			while(newId == -1 && i < results.getRelevantNonOptimalAlignments().size()){

				if(displayedId == results.getRelevantNonOptimalAlignments().size() - 1)
					displayedId = 0;
				else
					displayedId++;

				Alignment a = results.getRelevantNonOptimalAlignments().get(displayedId);

				if(!a.isEditedByExpert())
					newId = displayedId;

				i++;

			}

		if(newId != -1){
			displayedId = newId;
			displayAlignment();
		}
		else{
			if(!MainTutorial.IS_TUTO){
				Corpus.getCorpus().results.serialiseXMLFile();

				JOptionPane.showMessageDialog(this,
						isOverMessage);
			}
			else
				JOptionPane.showMessageDialog(this,
						isOverTutoMessage);

			NonOptimalPatternManagement.this.dispose();
			System.exit(0);
		}

	}

	protected void setPreviousAlignment() {
		if(results != null)
			if(displayedId == 0)
				displayedId = results.getRelevantNonOptimalAlignments().size() - 1;
			else
				displayedId--;

		displayAlignment();

	}


	private void setNextAlignment() {
		if(results != null)

			if(displayedId == results.getRelevantNonOptimalAlignments().size() - 1)
				displayedId = 0;
			else
				displayedId++;

		displayAlignment();
	}




	@Override
	public void eventDispatched(AWTEvent event) {

		if (event instanceof KeyEvent) {
			KeyEvent keyEvent = (KeyEvent) event;
			if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
				if (keyEvent.getID() == KeyEvent.KEY_PRESSED)
					isCtrlPressed = true;
				else if (keyEvent.getID() == KeyEvent.KEY_RELEASED)
					isCtrlPressed = false;
			}
			else if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
				if (keyEvent.getID() == KeyEvent.KEY_PRESSED)
					isShiftPressed = true;
				else if (keyEvent.getID() == KeyEvent.KEY_RELEASED)
					isShiftPressed = false;
			}
			else if (keyEvent.getID() == KeyEvent.KEY_PRESSED){

				if(isCtrlPressed)
					if(isShiftPressed){
						if(keyEvent.getKeyCode() == KeyEvent.VK_LEFT)
							jbPreviousUnedited.doClick();
						else if(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT)
							jbNextUnedited.doClick();
					}
					else
						if(keyEvent.getKeyCode() == KeyEvent.VK_LEFT)
							jbPrevious.doClick();
						else if(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT)
							jbNext.doClick();
			}
		}

	}

}
