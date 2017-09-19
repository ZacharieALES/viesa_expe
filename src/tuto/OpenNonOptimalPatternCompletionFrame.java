package tuto;

public class OpenNonOptimalPatternCompletionFrame extends AbstractTutoStep {

	@Override
	public void stepInitialization() {}

	@Override
	public void stepFinalization() {}

	@Override	
	public String description() {
		return "You are now familiar with almost all the features of this software (all but one).<br><br>"
				+ "In this experiment your objective will be to use VIESA to find interesting alignments.<br>"
				+ "To do so you will have to iteratively adapt the value of the parameters.<br>"
				+ "After each extraction you may or may not evaluate some of the obtained alignments (or even all of them if you want).<br><br>"
				+ "Once you consider that all the interesting alignments of the corpus have been covered, you can move on to the final step.<br>"
				+ "This last step concerns the alignments that you have evaluated relevant but non optimal.";
	}
	
	@Override
	public String instructions() {
		return "In the menu of the software window click on the item \"Action\" > \"Complete non optimal relevant patterns\"<br>"
				+ "(this item is only available if at least one alignment has been evaluated \"relevant but non optimal\", so you may need to change at least one evaluation to go to the next step)";
	}

	@Override
	public String resultsComment() {
		return null;
	}

}
