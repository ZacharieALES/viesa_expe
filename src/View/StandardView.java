//Copyright (C) 2012 Zacharie ALES and Rick MORITZ
//
//This file is part of Viesa.
//
//Viesa is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Viesa is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with Viesa.  If not, see <http://www.gnu.org/licenses/>.

package View;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Controler.StandardViewControler;
import clustering.AbstractClusteringMethod;
import clustering.ClusteringSolution;
import exception.AbstractException;
import experesults.CloseSoftwareAction;
import experesults.NonOptimalPatternManagement;
import extraction.PositiveScoreTable;
import extraction.SABRE;
import main.MainTutorial;
import model.AnnotatedArray;
import model.Corpus;
import model.CorpusObserver;
import model.SABREObserver;
import net.miginfocom.swing.MigLayout;
import tuto.OpenNonOptimalPatternCompletionFrame;

@SuppressWarnings("serial")
public class StandardView extends JFrame implements CorpusObserver,
SABREObserver, AWTEventListener {

	public SelectionPanel jf_s = null;
	private StandardViewControler svc;
	private JTabbedPane tab = new JTabbedPane();
	private JDialogProgressBar jdpb;
	
	public JLabel jlTuto1; 
	public JLabel jlTutoStepDescription;
	public JLabel jlTutoStepInstructions;


	public boolean isCtrlPressed = false;

	private boolean processClusteringAfterExtraction = false;

	//	public static int execution = -1;

	private StandardView() {

		
		try{
		System.setProperty("org.graphstream.ui.renderer",
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");


		try {
			//			for(int i = 0 ; i < UIManager.getInstalledLookAndFeels().length ; ++i)
			//				System.out.println(UIManager.getInstalledLookAndFeels()[i].getClassName());
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		} 

		Toolkit tk = Toolkit.getDefaultToolkit();
		tk.addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);

		svc = new StandardViewControler();
		jf_s = new SelectionPanel(this);

		System.setProperty("org.graphstream.ui.renderer",
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		this.setTitle("Viesa");

		if(MainTutorial.IS_TUTO)
			this.setSize(650, 900);
		else
			this.setSize(650, 700);

		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		//		if (Toolkit.getDefaultToolkit().getImage(
		//				getClass().getClassLoader().getResource("./img/icon.png")) == null)
		// System.out.println("found it");

		// this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("./img/icon.png")));
		//			this.setIconImage((new ImageIcon(getClass().getClassLoader()
		//					.getResource("img/icon.png"))).getImage());

		this.add(tab, BorderLayout.CENTER);

		tab.add("Data selection", jf_s);
		tab.add("Result visualization", VisualisationPanel.getInstance());

		/* Change the window when changing the tab */
		tab.addChangeListener(new ChangeListener() {

			// This method is called whenever the selected tab changes
			public void stateChanged(ChangeEvent evt) {
				JTabbedPane pane = (JTabbedPane) evt.getSource();

				switch (pane.getSelectedIndex()) {
				case 1:
					StandardView.this.setSize(900, 700);
					break;
				default:
					StandardView.this.setSize(650, 700);
				}
			}
		});

		/* Confirm the closing */
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				StandardView.this.toFront();

				int reponse = JOptionPane.showConfirmDialog(StandardView.this,
						"Do you really want to quit the application ?",
						"Confirmation", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (reponse == JOptionPane.YES_OPTION) {
					if(Corpus.getCorpus().results != null){
						Corpus.getCorpus().results.addAction(new CloseSoftwareAction());
						Corpus.getCorpus().results.serialiseXMLFile();
					}
				}

				StandardView.this.dispose();
			}
		});

		JMenuBar bar = new JMenuBar();
		//		JMenuItem action = new  JMenuItem("Action");
		//		JMenuItem save = new  JMenuItem("Save");

		JMenu menu = new JMenu("Action");

		JMenuItem endEvaluation = new  JMenuItem("Complete non optimal relevant patterns");
		bar.add(menu);

		menu.add(endEvaluation);
		this.setJMenuBar(bar);

		endEvaluation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				/* If there are some non optimal patterns */
				if(Corpus.getCorpus().results != null && !MainTutorial.IS_TUTO && Corpus.getCorpus().results.getRelevantNonOptimalAlignments().size() > 0
						|| MainTutorial.results != null && MainTutorial.IS_TUTO && MainTutorial.results.getRelevantNonOptimalAlignments().size() > 0
						){

					/* If it is not a tuto or if it is and we are at the proper step */
					if(!MainTutorial.IS_TUTO 
							|| MainTutorial.getCurrentStep() != null
							&& MainTutorial.getCurrentStep() instanceof OpenNonOptimalPatternCompletionFrame){

						/* Go to the next step if it is a tuto */
						if(MainTutorial.IS_TUTO)
							MainTutorial.nextStep();

						/* Open the window */
						new NonOptimalPatternManagement();


						/* Close the main window */
						StandardView.this.dispose();
					}
					else
						JOptionPane.showMessageDialog(StandardView.this,
								"We will soon see this feature in a next step");

				}
				else{
					String isOverMessage = "<html>It seems that there is currently no pattern with the label: relevant but non optimal.<br>If you clicked on this menu by mistake you can just close this pop-up.<br><br>However, if you finished the evaluation of this corpus thank for your help!<br>"
							+ "If it is the last or only corpus that you had to evaluate, you can close the application.<br>"
							+ "Don't forget to send your folder named \"results\" (which should be in the jar file folder) at the following address: zacharie.ales@univ-avignon.fr</html>";

					if(!MainTutorial.IS_TUTO)
						Corpus.getCorpus().results.serialiseXMLFile();

					JOptionPane.showMessageDialog(StandardView.this,
							isOverMessage);
				}

				if(!MainTutorial.IS_TUTO){
					/* Save the last results */
					Corpus.getCorpus().results.serialiseXMLFile();
				}
			}
		});


		if(MainTutorial.IS_TUTO){

			JPanel jpTuto = new JPanel(new MigLayout("", "[]", "[][]10[][]"));

			jlTutoStepDescription = new JLabel();
			jlTutoStepInstructions = new JLabel();

			jlTuto1 = new JLabel("<html><b>Description (step " + (MainTutorial.currentStepId) + "/" + (MainTutorial.lSteps.size() - 1) + ")</b></html>");
			JLabel jlTuto2 = new JLabel("<html><b>How to go to the next step</b></html>");

			jpTuto.add(jlTuto1, "wrap");
			jpTuto.add(jlTutoStepDescription, "wrap");
			jpTuto.add(jlTuto2, "wrap");
			jpTuto.add(jlTutoStepInstructions);

			this.add(jpTuto, BorderLayout.SOUTH);


		}


		//		this.setmenu
		setVisible(true);
	
		}catch(Exception e){e.printStackTrace();}

	}

	private static class StandardViewHolder {
		private final static StandardView sv = new StandardView();
	}

	public static StandardView getInstance() {
		return StandardViewHolder.sv;
	}

	public void openSelectionFrame() {

		if (jf_s == null)
			jf_s = new SelectionPanel(this);
		else
			jf_s.setVisible(true);
	}

	public void updatePatterns() {
	}

	public void updateClusters(ClusteringSolution cs) {

		VisualisationPanel.getInstance().updateClusters(cs);
	}

	public void updateSABREParam() {
		// jf_s.setMinScore(svc.getCorpus().getScoreMin());
		jf_s.updateSABREParameters(SABRE.getInstance().getParam());
	}

	public StandardViewControler getControler() {
		return svc;
	}

	public void loadFile(File f) {
		try {

			FileInputStream file = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(file);

			Corpus c = (Corpus) ois.readObject();
			ois.close();

			c.addObserver(StandardView.this);

			// TODO revoir le processus de mise à jour

			StandardView.this.setTitle(f.getName());

			/*
			 * Set the path if the similarity matrix has an empty path (possible
			 * in older versions)
			 */
			if (c.getAnnotationSimilarities().getPath() == null)
				c.getAnnotationSimilarities().setPath("");

			System.out.println(c.getPatternSize());

			// svc.getCorpus().getClusteringSolution().clear();
			//
			// String id = "d1_36";
			//
			// importHardClusteringSolution("corpus_a_evaluer/ap_" + id +
			// ".txt", "Affinity propagation");
			// importHierarchicalClusteringSolution("corpus_a_evaluer/chameleon_"
			// + id + ".txt", "Chameleon");
			// importHierarchicalClusteringSolution("corpus_a_evaluer/rock_" +
			// id + ".txt", "ROCK");
			// importHierarchicalClusteringSolution("corpus_a_evaluer/usc_" + id
			// + ".txt", "SC - Jordan & Weiss");
			// importHierarchicalClusteringSolution("corpus_a_evaluer/sm_" + id
			// + ".txt", "SC - Shi & Malik");
			// importHierarchicalClusteringSolution("corpus_a_evaluer/jw_" + id
			// + ".txt", "USC");
			// importHierarchicalClusteringSolution("corpus_a_evaluer//sl_" + id
			// + ".txt", "Single-Link");

			// StringBuffer buff = new StringBuffer();
			// for( int i = 0 ; i < c.getPatterns().size() ; i++ ){
			// for( int j = 0 ; j <= i ; j++){
			// Pattern p1 = c.getPatterns().get(i);
			// Pattern p2 = c.getPatterns().get(j);
			// buff.append(c.dissimilarity(p1, p2));
			// if(j!=i)
			// buff.append(" ");
			// }
			// buff.append("\n");
			// }
			//
			// System.out.println(buff.toString());
			//
			// try
			// {
			//
			// String outputFileDiss = "data/Resultats/result-" +
			// DateString.date() +"-dissimilarity.txt";
			// FileWriter fwDiss = new FileWriter(outputFileDiss, true);
			// BufferedWriter outputDiss = new BufferedWriter(fwDiss);
			// outputDiss.write(buff.toString());
			// outputDiss.close();
			// }
			// catch(IOException ioe){
			// System.out.print("Erreur : ");
			// ioe.printStackTrace();
			// }

			for (AnnotatedArray aa : Corpus.getCorpus().getAA())
				updateAddAA(aa);

			if (Corpus.getCorpus().getPatternSize() > 0)
				updatePatterns();

			for(ClusteringSolution cs : Corpus.getCorpus().getClusteringSolutions())
				updateClusters(cs);

			endOfClusteringProcess();

			updateSABREParameters();

			tab.setSelectedIndex(1);

			// CorpusAlignment corpus = svc.getCorpus();
			//
			// StringBuffer buff = new StringBuffer();
			// for( int i = 0 ; i < corpus.getPatterns().size() ; i++ ){
			// for( int j = 0 ; j <= i ; j++){
			// Pattern p1 = corpus.getPatterns().get(i);
			// Pattern p2 = corpus.getPatterns().get(j);
			// buff.append(corpus.dissimilarity(p1, p2));
			// if(j!=i)
			// buff.append(" ");
			// }
			// buff.append("\n");
			// }
			//
			// System.out.println(buff.toString());
			//
			// try
			// {
			//
			// String outputFileDiss = "data/Resultats/result-" +
			// DateString.date() +"-dissimilarity.txt";
			// FileWriter fwDiss = new FileWriter(outputFileDiss, true);
			// BufferedWriter outputDiss = new BufferedWriter(fwDiss);
			// outputDiss.write(buff.toString());
			// outputDiss.close();
			// }
			// catch(IOException ioe){
			// System.out.print("Erreur : ");
			// ioe.printStackTrace();
			// }
			//
		} catch (java.io.IOException e2) {
			e2.printStackTrace();
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}

	}

	public JTabbedPane getTab() {
		return tab;
	}

	@Override
	public void abstractException(AbstractException e) {
		JOptionPane
		.showMessageDialog(
				this, e.defaultMessage(), "Warning",
				JOptionPane.WARNING_MESSAGE);
	}

	public void endOfClusteringProcess() {
		jdpb.setVisible(false);
		VisualisationPanel.getInstance().updateEndOfClusteringProcess();
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		if(Corpus.getCorpus().getClusteringSolutions().size() > 0)
			tab.setSelectedIndex(1);

	}

	public void cancelProcess() {

		endOfClusteringProcess();
	}



	public void process_extraction_and_clustering() {

		VisualisationPanel.getInstance().reinitialize();
		jdpb = new JDialogProgressBar(this);
		jdpb.showThis();
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

		this.processClusteringAfterExtraction = true;
		svc.executeExtraction();
	}

	public void process_extraction() {

		//		String executionCountFileName = "data/results/executionCount.txt";
		//		/* Get the execution number */
		//		if(execution == -1){
		//			
		//			File executionCount = new File(executionCountFileName);
		//			
		//			if(executionCount.exists()){
		//				
		//				try{
		//					  InputStream  ips=new FileInputStream(executionCount);
		//					  InputStreamReader ipsr = new InputStreamReader(ips);
		//					  BufferedReader br = new BufferedReader(ipsr);
		//					  String ligne=br.readLine();
		//					  if(ligne != null)
		//					    execution = Integer.parseInt(ligne);
		//					  
		//					  br.close();
		//					}catch(Exception e){
		//					  System.out.println(e.toString());
		//					}
		//			}
		//			
		//		}
		//
		//		
		//		execution ++;
		//		
		//		/* Set the execution number */
		//		 try{
		//		     FileWriter fw = new
		//		     FileWriter(executionCountFileName, false);
		//		     BufferedWriter output = new BufferedWriter(fw);
		//
		//		     //on peut utiliser plusieurs fois la methode write
		//		     output.write(execution + "\n");
		//		     output.flush();
		//		     output.close();
		//		 }
		//		 catch(IOException ioe){
		//		     ioe.printStackTrace();
		//		 }

		VisualisationPanel.getInstance().reinitialize();
		jdpb = new JDialogProgressBar(this);
		jdpb.showThis();
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

		this.processClusteringAfterExtraction = false;
		svc.executeExtraction();
	}

	@Override
	public void endOfExtractionProcess() {

		if (this.processClusteringAfterExtraction) {
			svc.executeClustering(createClusteringMethods());
		} else {
			jdpb.setVisible(false);
			tab.setSelectedIndex(1);
		}
	}

	public ArrayList<AbstractClusteringMethod> createClusteringMethods(){

		ArrayList<AbstractClusteringMethod> clusteringToPerform = new ArrayList<AbstractClusteringMethod>();
		return clusteringToPerform;
	}

	@Override
	public void updateScoreSimilarities(PositiveScoreTable p) {
		jf_s.updateScoreSimilarities(p);
	}

	@Override
	public void updateSABREParameters() {
		jf_s.updateSABREParameters(SABRE.getInstance().getParam());
	}

	public int getClusterNb() {
		return jf_s.getClusterNb();
	}

	@Override
	public void updateAddAA(AnnotatedArray aa) {

		jf_s.addAA(aa);
		VisualisationPanel.getInstance().addAA(aa);

	}

	@Override
	public void updateRemoveAA(int id) {

		jf_s.removeAA(id);
		VisualisationPanel.getInstance().removeAA(id);

	}

	@Override
	public void update(Observable arg0, Object arg1) {
	}

	@Override
	public void updateSwingWorker(SwingWorker<?, ?> sw) {
		jdpb.setSwingWorker(sw);
	}

	@Override
	public void updateMaxSimilarity(double d) {
		jf_s.updateMaxSimilarity(d);
	}

	@Override
	public void removeClusteringSolution(int i) {
		VisualisationPanel.getInstance().removeCS(i);
	}

	@Override
	public void clearClusters() {
		VisualisationPanel.getInstance().reinitialize();
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
			else if (keyEvent.getID() == KeyEvent.KEY_PRESSED){

				if(tab.getSelectedIndex() == 1)
					VisualisationPanel.getInstance().keyPressed(keyEvent);
			}
		}

	}

	public boolean isCtrlPressed(){
		return isCtrlPressed;
	}


	//	public void log(String s){
	//		 String file = "data/results/log.txt";
	//		 try{
	//			 
	//			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	//			 
	//		     FileWriter fw = new FileWriter(file, true);
	//		     BufferedWriter output = new BufferedWriter(fw);
	//
	//		     output.write(sdf.format(new Date()) + " " + s  +"\n");
	//		     output.flush();
	//		     output.close();
	//		 }
	//		 catch(IOException ioe){
	//		     System.out.print("Error : ");
	//		     ioe.printStackTrace();
	//		 }
	//
	//
	//	}

}
