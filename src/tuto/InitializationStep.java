package tuto;

import View.VisualisationPanel.XRadioButton;
import main.MainTutorial;

public class InitializationStep extends AbstractTutoStep {

	@Override
	public void stepInitialization() {
		
		/* Disable all the components that can be used by the user */
		getSelectionPanel().jb_process_extraction.setEnabled(false);
		getSelectionPanel().jb_sim_score_fileChooser.setEnabled(false);
		getSelectionPanel().jb_sim_editor.setEnabled(false);
		getSelectionPanel().jb_tool.setEnabled(false);
		getSelectionPanel().jtf_gap_score.setEnabled(false);
		getSelectionPanel().jtf_desired_nb_of_alignments.setEnabled(false);
		getSelectionPanel().jtf_sim_scores.setEnabled(false);
		getSelectionPanel().jtf_K.setEnabled(false);
		getSelectionPanel().jtf_maxSim.setEnabled(false);
		
		for(XRadioButton xrb: getVisualisationPanel().rButtons){
			xrb.setEnabled(false);
		}
		
		MainTutorial.nextStep();
	}

	@Override
	public void stepFinalization() {}

	@Override
	public String description() {
		return "Initialization of the tutorial";
	}

	@Override
	public String instructions() {
		return "Please wait";
	}

	@Override
	public String resultsComment() {
		return null;
	}

}
