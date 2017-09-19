package main;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import View.StandardView;
import exception.InvalidArgumentsToCreateAnAAColumnFormat;
import exception.UndefinedColumnFormatException;
import experesults.Action;
import experesults.CancelColumnsChangesAction;
import experesults.ChangeDesiredNumberOfAlignments;
import experesults.ChangeGapScoreAction;
import experesults.ExperienceResults;
import experesults.StartSoftwareAction;
import extraction.PositiveScoreTable;
import extraction.SABRE;
import extraction.SABREParameter;
import model.Corpus;

public class MainCogniSismef {

	public static void run(){



		Main.hasHeader = true;
		PositiveScoreTable st;

		Main.scoreFolder = "data/Cogni-CISMEF";
		st = new PositiveScoreTable(Main.getLatestScoreTablePath());

		//		String fo_path = "data/Cogni-CISMEF/dialogues/01";		
		String fo_path = Main.scoreFolder + "/csv";		
		//		String fo_path = "data/Cogni-CISMEF/dialogues/test";					

		Corpus.getCorpus().setAnnotationSimilarities(st);



		ArrayList<Integer> al_comment = new ArrayList<Integer>();
		ArrayList<Integer> al_annot = new ArrayList<Integer>();



		//			al_comment.add(0);
		//			al_comment.add(1);
		//			al_comment.add(2);
		//			al_annot.add(3);
		//			al_comment.add(4);
		//			al_comment.add(5);
		//			al_annot.add(6);
		//			al_comment.add(7);
		//			al_comment.add(8);
		//			al_annot.add(9);
		//			al_annot.add(10);
		//			al_comment.add(11);

		al_comment.add(0);
		al_comment.add(1);
		al_annot.add(2);
		al_annot.add(3);
		al_annot.add(4);
		al_annot.add(5);
		//			al_comment.add(6);
		//			al_comment.add(7);
		//			al_comment.add(8);
		//			al_comment.add(9);
		//			al_comment.add(10);
		//			al_comment.add(11);

		try {
			Corpus.getCorpus().initialAnnotationColumns = al_annot;
			Corpus.getCorpus().initialCommentColumns = al_comment;

			Corpus.getCorpus().setColumnFormat(al_comment, al_annot, null);
			Corpus.getCorpus().setAnnotationSimilarities(st);
			Corpus.getCorpus().add(fo_path, Main.hasHeader);

			/* Launch a JOptionPane in a new thread (in order not to stop the current thread */
			new Thread(){

				@Override
				public void run(){							
					JOptionPane.showOptionDialog(null, "Loading previous results (if any)","Please wait", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
				}

			}.start();

			int latestDesiredNumberOfAlignments = 25;
			double latestGap = 10;


			Corpus.getCorpus().results = new ExperienceResults("CogniCisMef", "results", fo_path, latestDesiredNumberOfAlignments, latestGap, Corpus.getCorpus().getAA(), al_comment, al_annot);


			if(Corpus.getCorpus().results.getActions() != null){
				
				/* Get the latest value of some parameters by browsing all the actions performed by the user */
				for(Action a: Corpus.getCorpus().results.getActions()){

					if(a instanceof ChangeDesiredNumberOfAlignments){
						ChangeDesiredNumberOfAlignments cdnoa = (ChangeDesiredNumberOfAlignments)a;
						latestDesiredNumberOfAlignments = cdnoa.getNewNumber();
					}
					else if(a instanceof ChangeGapScoreAction){
						ChangeGapScoreAction cgsa = (ChangeGapScoreAction)a;
						latestGap = cgsa.getNewScore();
					}

				}
			}

			SABRE.getInstance().setParam(new SABREParameter(latestGap, latestGap/2)); 
			Corpus.getCorpus().setDesiredNumberOfAlignments(latestDesiredNumberOfAlignments);
			
			new Thread(){

				@Override
				public void run(){			
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					JOptionPane.getRootFrame().dispose();  				
				}

			}.start();

			StandardView sv = StandardView.getInstance();
			Corpus.getCorpus().addObserver(sv);
			SABRE.getInstance().addObserver(sv);
			Corpus.getCorpus().results.addAction(new StartSoftwareAction());

		} catch (UndefinedColumnFormatException e) {e.printStackTrace();} catch (InvalidArgumentsToCreateAnAAColumnFormat e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
