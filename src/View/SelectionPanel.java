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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import experesults.ChangeDesiredNumberOfAlignments;
import experesults.ChangeGapScoreAction;
import extraction.PositiveScoreTable;
import extraction.SABREParameter;
import main.MainTutorial;
import model.AAColumnFormat;
import model.AnnotatedArray;
import model.AnnotationColumn;
import model.Corpus;
import net.miginfocom.swing.MigLayout;
import tuto.EditDesiredNumberOfAlignmentsStep;
import tuto.EditGap;
import tuto.OpenEditSimilarityFrame;
import util.AAReader;

@SuppressWarnings("serial")
public class SelectionPanel extends JPanel{


	private AAList aal = new AAList();
	private AATable aat = new AATable();
	public JButton jb_process_extraction = new JButton("Extract");

	private JLabel jl_corpus = new JLabel("Corpus");
	private JLabel jl_table = new JLabel("Overview");
	public JTextField jtf_gap_score = new JTextField("", 30);
	public JTextField jtf_desired_nb_of_alignments = new JTextField("", 30);
	public JTextField jtf_sim_scores = new JTextField("", 30);
	public JTextField jtf_K = new JTextField("", 30);
	public JTextField jtf_maxSim = new JTextField("", 30);
	public JIconButton jb_sim_score_fileChooser;
	public JIconButton jb_sim_editor;
	public JIconButton jb_tool;
	private StandardView sv;

	//TODO quand on change le type de colonnes,  vérifier les fichiers qui seront perdus et l'indiquer à l'utilisateur

	/** List of string which contain the values of the csv file of the last annotated array which has been added for the current column format 
	 * (null as long as no column format is defined) */
	private List<String[]> lastEntries = null;


	public SelectionPanel(StandardView f_sw){

		sv = f_sw;
		this.setLayout(new MigLayout("fill", "[50%][50%]", ""));		

		aal.getJSP().setPreferredSize(new Dimension(243, 270));
		aal.getJSP().setMinimumSize(new Dimension(243, 270));

		aal.addMouseListener(new MouseListener(){

			public void mouseReleased(MouseEvent arg0){

				ListSelectionModel lsm = aal.getSelectionModel();

				if(!lsm.isSelectionEmpty()){

					int minIndex = lsm.getMinSelectionIndex();

					AnnotatedArray aa = (AnnotatedArray)aal.getModel().getElementAt(minIndex);
					jl_table.setText("Overview: " + aa.getFileName());
					aat.setAA((AnnotatedArray)aal.getModel().getElementAt(minIndex));

				}
			}

			public void mousePressed(MouseEvent arg0){}
			public void mouseExited(MouseEvent arg0){}
			public void mouseEntered(MouseEvent arg0){}
			public void mouseClicked(MouseEvent arg0){}

		});

		aal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		/* *** AnnotatedArrays pane definition *** */
		JHelpButton jb_helpSelectAA = new JHelpButton(sv, "<html>Modify the annotation columns.<br><br>" +
				"There are three possible types for a column of a csv file:<br>" +
				"- unused: the column is not displayed and not used to extract the patterns;<br>" +
				"- comment: the column is displayed but not used to extract the patterns;<br>" +
				"- annotation: the column is used to extract the patterns.</html>");
		jb_tool = new JIconButton("img/tool.png");
		JPanel jp_selectAA = new JPanel(new MigLayout("fill", "[fill]push[][][][]", "[][]"));

		jp_selectAA.add(jl_corpus);
		jp_selectAA.add(jb_tool);
		jp_selectAA.add(jb_helpSelectAA, "wrap");
		jp_selectAA.add(aal.getJSP(), "spanx 6");

		jb_tool.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){		

				boolean readyToDisplayTool = false;

				if(lastEntries != null)
					readyToDisplayTool = true;

				int i = 0;

				while(!readyToDisplayTool && i < Corpus.getCorpus().getAA().size()){

					AAReader reader;
					try {
						reader = new AAReader(Corpus.getCorpus().getAA().get(i).getFullPath());
						lastEntries = reader.read();
					} catch (Exception e1) {}

					++i;
				}


				if(lastEntries != null && Corpus.getCorpus().getAA().size() > 0){

					if(MainTutorial.IS_TUTO)
						MainTutorial.nextStep();
					
					CSVColumnsSelector csvcs = new CSVColumnsSelector(sv, lastEntries);
					AAColumnFormat aacf = csvcs.showThis();

					if(aacf != null)
						Corpus.getCorpus().setAACF(aacf, true);

					if(!MainTutorial.IS_TUTO)
						activateDesactivateSettingsAndProcessButtons();
				}
				else{
					JOptionPane.showMessageDialog(sv, "<html>This button enables to change the column format.<br>(i.e., the choice of the columns of the input files that contain annotations, numerical annotations, comments or that will be ignored).<br><br>To use this button you must first define the format by adding csv files to the corpus.<br><br>If the corpus already contains annotated arrays their csv files cannot be reached. Remove them all and add again the arrays to change the column format.");
				}

			}
		});


		/* *** End * Selected AnnotatedArrays pane *** */


		/* *** Parameters panel definition *** */		
		JHelpButton jb_help_sim_score = new JHelpButton(sv, "<html>(only used if columns of type \"annotation\" are considered)<br>" +
				"In the annotation columns, the similarity between two annotations is equal to 0 if it is not specified in the similarity score table.<br><br>" +
				"The left button enables to change the file from which the similarity are read.<br>" +
				"The right button enables to dynamically edit the scores by adding or removing similarities between couples of annotations.<br><br>" +
				"To define the table, the input csv file should satisfy the following format: <br>- the first line contains headers and will be ignored;<br>- the next lines contain values in its three first columns<br>(1: first annotation, 2: second annotation, 3: similarity of the two annotations, the next columns are ignored).<br><br>Example:<br>Annotation 1, Annotation 2, Similarity, Comment<br>P, P, 2, The similarity between annotations P and P is equal to 2<br>P, Q, 1, The similarity between annotations P and Q is equal to 1<br>... </html>");
		JHelpButton jb_help_K = new JHelpButton(sv, "For each clustering algorithm, the solution which contains this number of clusters will be displayed preferentially.");
		jb_sim_score_fileChooser = new JIconButton("img/folder.png");
		jb_sim_editor = new JIconButton("img/edit.png");

		/* The Text field are disabled as long as Annotated Arrays have not been added to the corpus */
		jtf_maxSim.setEnabled(false);
		jtf_sim_scores.setEnabled(false);
		jtf_gap_score.setEnabled(false);
		jtf_desired_nb_of_alignments.setEnabled(false);
		jtf_K.setEnabled(false);
		jb_sim_score_fileChooser.setEnabled(false);
		jb_sim_editor.setEnabled(false);


		JPanel jp_extraction = new JPanel(new MigLayout("fill", "[fill]", ""));
		jp_extraction.setBorder(BorderFactory.createTitledBorder("1 - Pattern extraction"));
		jtf_sim_scores.setEditable(false);

		jb_sim_score_fileChooser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int returnVal = chooser.showOpenDialog(sv);

				if(returnVal == JFileChooser.APPROVE_OPTION){				
					sv.getControler().setAnnotationSimilarities(chooser.getSelectedFile());
					jb_sim_editor.setEnabled(true);
					//					StandardView.getInstance().log("Set new positive score table: " + chooser.getSelectedFile());
				}
			}
		});



		jb_sim_editor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				try{

					if(MainTutorial.IS_TUTO 
							&& MainTutorial.getCurrentStep() != null
							&& MainTutorial.getCurrentStep() instanceof OpenEditSimilarityFrame
							)
						MainTutorial.nextStep();
						
					PSTEditor psted = new PSTEditor(sv);
					PositiveScoreTable pst = psted.showThis();

					if(pst != null){
						sv.getControler().setAnnotationSimilarities(pst);
						

						if(!MainTutorial.IS_TUTO)
							activateDesactivateSettingsAndProcessButtons();

						//						StandardView.getInstance().log("Confirm the similarity modifications");
					}
					else{
						//						StandardView.getInstance().log("Cancel the similarity modifications");
					}

				} catch (Exception e1) {
					e1.printStackTrace();

				}	
			}
		});

		JLabel jl_gap_score = new JLabel("Gap cost");
		JHelpButton jhb_gap_score = new JHelpButton(sv, "The cost of having a gap of one line between two annotations in a pattern. The higher this cost, the smaller the patterns extracted.");
		jtf_gap_score.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				setDesynchCost(Double.parseDouble(jtf_gap_score.getText())/2.0);

				if(MainTutorial.IS_TUTO && MainTutorial.getCurrentStep() != null && MainTutorial.getCurrentStep() instanceof EditGap){
				
					EditGap cdnas = ((EditGap)MainTutorial.getCurrentStep());
					if(Double.parseDouble(jtf_gap_score.getText()) == cdnas.newParameterValue)
							cdnas.hasValueBeenChanged = true;
					else
						JOptionPane.showMessageDialog(SelectionPanel.this, "Please set the gap cost to " + cdnas.newParameterValue + " before starting the extraction.");
				}

				if(Corpus.getCorpus().results != null && !MainTutorial.IS_TUTO)
					Corpus.getCorpus().results.addAction(new ChangeGapScoreAction(Double.parseDouble(jtf_gap_score.getText())));
				
			}

		});

		jtf_gap_score.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				setDesynchCost(Double.parseDouble(jtf_gap_score.getText())/2.0);	

				if(Corpus.getCorpus().results != null)
					Corpus.getCorpus().results.addAction(new ChangeGapScoreAction(Double.parseDouble(jtf_gap_score.getText())));

			}
		});

		JLabel jl_desired_number_of_alignments = new JLabel("Number of alignments");
		JHelpButton jhb_desired_number_of_alignments = new JHelpButton(sv, "<html><br>Desired number of alignments.<br><br>Let x be this number. The methods will extract the x alignments in the corpus with the best score.<br>More alignments can be obtained if there are several alignments with the lowest score.<br><br>Example:<br>If we seek 4 alignments and we obtain 6 alignments with the following scores: 1, 2, 2, 3, 4, 5.<br>Alignments of score 2, 2, 3, 4, 5 are kept.</html>");
		//		JHelpButton jhb_min_score = new JHelpButton(sv, "Score above which a pattern is found. The lower this score, the higer the number of extracted patterns.");
		jtf_desired_nb_of_alignments.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				setDesiredNumberOfAlignments(Integer.parseInt(jtf_desired_nb_of_alignments.getText()));
				
				if(MainTutorial.IS_TUTO && MainTutorial.getCurrentStep() != null && MainTutorial.getCurrentStep() instanceof EditDesiredNumberOfAlignmentsStep){
					
					EditDesiredNumberOfAlignmentsStep cdnas = ((EditDesiredNumberOfAlignmentsStep)MainTutorial.getCurrentStep());
					if(Integer.parseInt(jtf_desired_nb_of_alignments.getText()) == cdnas.newParameterValue)
							cdnas.hasValueBeenChanged = true;
					else
						JOptionPane.showMessageDialog(SelectionPanel.this, "Please set the desired number of alignments to " + cdnas.newParameterValue + " before starting the extraction.");
				}

				if(Corpus.getCorpus().results != null)
					Corpus.getCorpus().results.addAction(new ChangeDesiredNumberOfAlignments(Integer.parseInt(jtf_desired_nb_of_alignments.getText())));
			}

		});

		jtf_desired_nb_of_alignments.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setDesiredNumberOfAlignments(Integer.parseInt(jtf_desired_nb_of_alignments.getText()));

				if(Corpus.getCorpus().results != null)
					Corpus.getCorpus().results.addAction(new ChangeDesiredNumberOfAlignments(Integer.parseInt(jtf_desired_nb_of_alignments.getText())));
			}
		});

		JPanel jp_gap = new JPanel(new MigLayout("fill", "[fill]push[10]", ""));
		jp_gap.add(jl_gap_score, "wrap");
		jp_gap.add(jtf_gap_score, "growx");
		jp_gap.add(jhb_gap_score, "wrap");
		jp_extraction.add(jp_gap, "growx,wrap");

		JPanel jp_desired_number_of_alignments = new JPanel(new MigLayout("fill", "[fill]push[10]", ""));
		jp_desired_number_of_alignments.add(jl_desired_number_of_alignments, "wrap");
		jp_desired_number_of_alignments.add(jtf_desired_nb_of_alignments, "growx");
		jp_desired_number_of_alignments.add(jhb_desired_number_of_alignments, "wrap");
		jp_extraction.add(jp_desired_number_of_alignments, "growx,wrap");


		JPanel jp_sim_scores = new JPanel(new MigLayout("fill", "[fill]push[10][10]", ""));
		//		jp_temp.setMaximumSize(new Dimension(4000, 30));
		jp_sim_scores.add(new JLabel("Similarity scores"), "wrap");
		jp_sim_scores.add(jtf_sim_scores, "growx");	
		jp_sim_scores.add(jb_sim_score_fileChooser, "");
		jp_sim_scores.add(jb_sim_editor, "");
		jp_sim_scores.add(jb_help_sim_score, "wrap");
		jp_extraction.add(jp_sim_scores, "spanx 2, growx, wrap");
		//		jp_extraction.add(jp_sim_scores, "growx, wrap");



		/* End *** Parameter panel definition */

		/* *** Process pane */
		JPanel jp_process = new JPanel(new MigLayout("", "[][][]"));
		JHelpButton jhb_process = new JHelpButton(sv, "This button starts the extraction process.");
		jp_process.add(jb_process_extraction);
		jp_process.add(jhb_process);

		jb_process_extraction.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				if(MainTutorial.IS_TUTO){
					
					if(MainTutorial.getCurrentStep() != null){
						
						if(MainTutorial.getCurrentStep() instanceof EditDesiredNumberOfAlignmentsStep 
								&& !((EditDesiredNumberOfAlignmentsStep)MainTutorial.getCurrentStep()).hasValueBeenChanged
								){
							JOptionPane.showMessageDialog(SelectionPanel.this, "Please change the desire number of alignments before starting a new extraction");
						}
						else if(MainTutorial.getCurrentStep() instanceof EditGap 
								&& !((EditGap)MainTutorial.getCurrentStep()).hasValueBeenChanged
								){
							JOptionPane.showMessageDialog(SelectionPanel.this, "Please change the gap cost before starting a new extraction");
						}
						else{
							MainTutorial.nextStep();
							sv.process_extraction();
						}
					}
				}
				else
					sv.process_extraction();		
			}
		});

		/* *** End * Process pane */

		this.add(jp_selectAA, "cell 0 0, spany 2, grow");
		this.add(jp_extraction, "cell 1 0, grow");

		this.add(jl_table, "cell 0 2");
		this.add(aat.getJSP(), "cell 0 3, grow, spanx 2");
		this.add(jp_process, "cell 0 4");

	}

	protected void setDesynchCost(double d) {

		sv.getControler().setDesynchCost(d);
		sv.getControler().setGapCost(2*d);	
		//		StandardView.getInstance().log("Set desynchronization cost to " + d);

	}

	protected void setMaxSim(double d) {

		sv.getControler().setMaxSim(d);	

	}

	private void activateDesactivateSettingsAndProcessButtons() {

		if(Corpus.getCorpus().isColumnFormatDefined() && Corpus.getCorpus().getAA().size() > 0){
			jtf_gap_score.setEnabled(true);
			jtf_K.setEnabled(true);
			jtf_desired_nb_of_alignments.setEnabled(true);

			if(jtf_K.getText().equals(""))
				jtf_K.setText("1");

			//			if(Corpus.getCorpus().getAA(0).getNumericalAnnotations().size() > 0){
			//				jtf_maxSim.setEnabled(true);
			//				
			//				if(jtf_maxSim.getText().equals("")){
			//					jtf_maxSim.setText("1");
			//					setMaxSim(1.0);
			//				}
			//			}
			//			else
			jtf_maxSim.setEnabled(false);

			if(Corpus.getCorpus().getAA(0).getAnnotations().size() > 0){
				jtf_sim_scores.setEnabled(true);
				jb_sim_score_fileChooser.setEnabled(true);

				if(AnnotationColumn.pst != null)
					jb_sim_editor.setEnabled(true);


			}
			else{
				jtf_sim_scores.setEnabled(false);
				jb_sim_score_fileChooser.setEnabled(false);
				jb_sim_editor.setEnabled(false);
			}
		}
		else{

			jtf_gap_score.setEnabled(false);
			jtf_K.setEnabled(false);
			jtf_maxSim.setEnabled(false);
			jtf_desired_nb_of_alignments.setEnabled(false);
			jtf_sim_scores.setEnabled(false);
			jb_sim_score_fileChooser.setEnabled(false);
			jb_sim_editor.setEnabled(false);
		}

		jb_process_extraction.setEnabled(isReadyToProcess());

	}

	public void addAA(AnnotatedArray aa){

		aal.addAA(aa);		

		if(aal.getModel().getSize() == 1){
			jl_table.setText("Overview: " + aa.getFileName());
			this.aat.setAA(aa);
		}

		jl_corpus.setText("Corpus (" + aal.getModel().getSize() + ")");

		if(!MainTutorial.IS_TUTO)
			activateDesactivateSettingsAndProcessButtons();
	}

	public void removeAA(int id){

		/* If the aa is displayed in the table, clear the table */
		if(aat.getAA() != null && aal.getAA(id).getFileName().equals(aat.getAA().getFileName())){
			jl_table.setText("Overview");
			aat.emptyTable();
		}

		aal.removeAA(id);


		jl_corpus.setText("Corpus (" + aal.getModel().getSize() + ")");

		if(!MainTutorial.IS_TUTO)
			activateDesactivateSettingsAndProcessButtons();
	}


	// TODO Ajouter une nouvelle tab de visualisation sans graphe et avec 3 tables supplémentaires à la place
	//Quand on click gauche ça met dans première table
	// Quand on click droit ça affiche un menu contextuel pour choisir laquelle des 4 autres tables on veut
	public void setDesiredNumberOfAlignments(int score){

		sv.getControler().setDesiredNumberOfAlignments(score);
		
		if(!MainTutorial.IS_TUTO)
			activateDesactivateSettingsAndProcessButtons();
		//		StandardView.getInstance().log("Set desired number of alignments to " + score);
	}

	public boolean isReadyToProcess(){

		return Corpus.getCorpus().isReadyToProcess();

	}

	public String getFirstCSVFile(File f, boolean goIntoChildrenFolders){

		String result = "";

		if(f.exists()){
			if(f.isDirectory()){
				if(goIntoChildrenFolders)

					/* Set goIntoChildrenFolders to false to ensure that no children folders will be opened */
					result = getFirstCSVFile(f.listFiles(), false);

			}

			else{

				/* Regexp to test that the end of the file end by "csv" */
				java.util.regex.Pattern csvRegexp = java.util.regex.Pattern.compile(".*csv");

				Matcher fileName = csvRegexp.matcher(f.getName());

				if(fileName.matches()){
					result = f.getAbsolutePath();
				}

			}
		}

		return result;

	}

	/**
	 * Returns the path of the first csv file in the list <f> or in the files directly in a folder in <f> 
	 * @param f
	 * @param goIntoNextLevelFolders 
	 * @return
	 */
	public String getFirstCSVFile(File[] f){
		return getFirstCSVFile(f, true);
	}

	/**
	 * Returns the path of the first csv file in the list <f>
	 * @param f List of files
	 * @param goIntoChildrenFolders True if the files in the folders contained in f must be considered
	 * @return
	 */
	private String getFirstCSVFile(File[] f, boolean goIntoChildrenFolders){

		String result = "";
		int i = 0;

		while("".equals(result) && i < f.length){
			result = getFirstCSVFile(f[i], goIntoChildrenFolders);		
			i++;
		}

		return result;

	}

	public void updateScoreSimilarities(PositiveScoreTable p) {
		String[] s= p.getPath().split("/");

		jtf_sim_scores.setText(s[s.length-1]);

		if(!MainTutorial.IS_TUTO)
			activateDesactivateSettingsAndProcessButtons();		
	}

	public void updateSABREParameters(SABREParameter p) {
		jtf_gap_score.setText(((Double)(p.gap_cost)).toString());
		updateDesiredNumberOfAlignments(p.desired_number_of_alignments);

		if(!MainTutorial.IS_TUTO)
			activateDesactivateSettingsAndProcessButtons();	
	}

	public void updateDesiredNumberOfAlignments(int v){
		jtf_desired_nb_of_alignments.setText(((Integer)v).toString());
	}

	public int getClusterNb() {

		int result;

		try {
			result = Integer.parseInt(jtf_K.getText());
		} catch (NumberFormatException nfe) {
			result = -1;
		}

		return result;
	}

	public void updateMaxSimilarity(double d) {
		jtf_maxSim.setText(((Double)d).toString());
	}

}
