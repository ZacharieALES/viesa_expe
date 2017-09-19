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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.sun.glass.events.KeyEvent;

import clustering.Cluster;
import clustering.ClusteringSolution;
import clustering.HardClusteringSolution;
import model.Alignment;
import model.AnnotatedArray;
import model.Corpus;
import model.Pattern;
import model.Pattern.PatternStatus;
import net.miginfocom.swing.MigLayout;

//@SuppressWarnings("serial")
public class VisualisationPanelMotifValidationInsteadOfAlignment extends JPanel implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

//	private static final long serialVersionUID = -6955571429335477410L;
//
//	private List<HardClusteringSolution> csAlignments;
//
//	private ExpeTree expeTreeRelevant = new ExpeTree(PatternStatus.RELEVANT_AND_NOT_OPTIMAL);
//	private ExpeTree expeTreeUnknown = new ExpeTree(PatternStatus.UNKNOWN);
//	private ExpeTree expeTreeIrrelevant = new ExpeTree(PatternStatus.IRRELEVANT);
//
//	private AATable table1 = new AATable();
//	private AATable table2 = new AATable();
//
//	private List<Color> tableColor = new ArrayList<>();
//
//	private JLabel jl_o1 = new JLabel(": to use it");
//	private JLabel jl_o2 = new JLabel(": to use it");
//
//	private JIconButton jb_switchOrientation12 = new JIconButton("img/horizontal2.png");
//
//	private JPanel aaTablePanel12 = new JPanel(new MigLayout("fill", "[50%]0[50%]", "0[]0[grow]0"));
//
//	private JPanel jp_l1 = new JPanel(new MigLayout("fillx", "[][]push[]", "[]"));
//	private JPanel jp_l2 = new JPanel(new MigLayout("fillx", "[][]push", "[]"));
//	//	private JPanel jp_lA = new JPanel(new MigLayout("fillx", "[][][][]push[]", "[]"));
//	//	private JPanel jp_lB = new JPanel(new MigLayout("fillx", "[][][][]push", "[]"));
//
//	private JPanel jp_relevant = new JPanel(new MigLayout("fill", "", ""));
//	private JPanel jp_irrelevant = new JPanel(new MigLayout("fill", "", ""));
//	private JPanel jp_unknown = new JPanel(new MigLayout("fill", "", ""));
//
//	private JButton jbNext, jbPrevious;
//	
//	private JIconButton jib_left12;
//	private JIconButton jib_right12;
//
//	private TitledBorder tb_table12 = BorderFactory.createTitledBorder("Overview");
//	//	private TitledBorder tb_tableAB = BorderFactory.createTitledBorder("Overview");
//
//	private XRadioButton[][] rButtons = new XRadioButton[2][4];
//
//	ArrayList<Alignment> previouslySavedAlignments;
//
//	/**
//	 * If true, table1 and table2 are displayed horizontally otherwise they are displayed vertically
//	 */
//	private boolean isVertical12 = false;
//
//	private VisualisationPanelMotifValidationInsteadOfAlignment(){
//
//		this.setLayout(new MigLayout("fill", "[grow][][]", "0[grow][]"));
//
//		/* *** Cluster list pane */
//		tableColor.add(new Color(0,158,206));
//		tableColor.add(new Color(156,207,49));
//		tableColor.add(new Color(255,158,0));
//		tableColor.add(new Color(206,0,0));
//
//		table1.setColor(tableColor.get(2));
//		table2.setColor(tableColor.get(3));
//
//		table1.emptyTable();
//		table2.emptyTable();
//
//		//TODO voir si utile
//		FocusListener fl = new FocusListener() {
//			@Override public void focusGained(FocusEvent e) {
//				e.getComponent().repaint();
//			}
//			@Override public void focusLost(FocusEvent e) {
//				e.getComponent().repaint();
//			}
//		};
//
//		expeTreeRelevant.addFocusListener(fl);
//		expeTreeUnknown.addFocusListener(fl);
//		expeTreeIrrelevant.addFocusListener(fl);
//
//		expeTreeRelevant.getJSP().setMinimumSize(new Dimension(130, 100));
//		expeTreeUnknown.getJSP().setMinimumSize(new Dimension(130, 100));
//		expeTreeIrrelevant.getJSP().setMinimumSize(new Dimension(130, 100));
//		//		clusterTree.getJSP().setPreferredSize(new Dimension(300, 200));
//		expeTreeRelevant.addMouseListener(new MouseAdapter(){
//
//			public void mouseReleased(MouseEvent arg0){
//
//				int index = expeTreeRelevant.getRowForLocation(arg0.getX(), arg0.getY());
//				displayClusterSelection(expeTreeRelevant, index);
//
//			}	
//
//		});
//
//		expeTreeIrrelevant.addMouseListener(new MouseAdapter(){
//
//			public void mouseReleased(MouseEvent arg0){
//
//				int index = expeTreeIrrelevant.getRowForLocation(arg0.getX(), arg0.getY());
//				displayClusterSelection(expeTreeIrrelevant, index);
//
//			}	
//
//		});
//
//		expeTreeUnknown.addMouseListener(new MouseAdapter(){
//
//			public void mouseReleased(MouseEvent arg0){
//
//				int index = expeTreeUnknown.getRowForLocation(arg0.getX(), arg0.getY());
//				displayClusterSelection(expeTreeUnknown, index);
//
//			}	
//
//		});
//		/* *** End * Cluster list pane */
//
//		TitledBorder tb_unknown = BorderFactory.createTitledBorder("Unevaluated alignments");
//		TitledBorder tb_relevant = BorderFactory.createTitledBorder("Relevant alignments");
//		TitledBorder tb_irrelevant = BorderFactory.createTitledBorder("Irrelevant alignments");
//
//		jp_unknown.setBorder(tb_unknown);
//		jp_unknown.add(expeTreeUnknown.getJSP(), "cell 0 0, grow");
//
//		jp_relevant.setBorder(tb_relevant);
//		jp_relevant.add(expeTreeRelevant.getJSP(), "cell 0 0, grow");
//
//		jp_irrelevant.setBorder(tb_irrelevant);
//		jp_irrelevant.add(expeTreeIrrelevant.getJSP(), "cell 0 0, grow");
//
//		JPanel jp_selection = new JPanel(new MigLayout("fill", "", ""));
//		jp_selection.add(jp_unknown, "cell 0 0, grow");
//		jp_selection.add(jp_relevant, "cell 1 0, grow");
//		jp_selection.add(jp_irrelevant, "cell 2 0, grow");
//
//
//		/* Begin *** adTablePanel */		
//		jb_switchOrientation12.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				isVertical12 = !isVertical12;
//
//				if(isVertical12){
//					jb_switchOrientation12.setImage("img/vertical2.png");
//					changeTableOrientation12(!isVertical12);
//				}
//				else{
//					jb_switchOrientation12.setImage("img/horizontal2.png");
//					changeTableOrientation12(!isVertical12);
//				}
//			}
//		});
//
//		jib_left12 = new JIconButton("img/mouse_left.png");
//		jib_right12 = new JIconButton("img/mouse_right.png");
//
//
//		JPanel aaTablePanelAB = new JPanel(new MigLayout("fill", "[][][][][]", "0[]0[grow]0"));
//		TitledBorder tb_change_status = BorderFactory.createTitledBorder("Pattern status");
//
//		aaTablePanelAB.setBorder(tb_change_status);
//
//		rButtons[0][0] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/unknown.png")), "(A)");
//		rButtons[0][1] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/star2.png")), "(Z)");
//		rButtons[0][2] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/star.png")), "(E)");
//		rButtons[0][3] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/trash.png")), "(R)");
//
//		rButtons[0][0].getRadioButton().setActionCommand("00");
//		rButtons[0][1].getRadioButton().setActionCommand("01");
//		rButtons[0][2].getRadioButton().setActionCommand("02");
//		rButtons[0][3].getRadioButton().setActionCommand("03");
//
//		rButtons[0][0].getRadioButton().addActionListener(this);
//		rButtons[0][1].getRadioButton().addActionListener(this);
//		rButtons[0][2].getRadioButton().addActionListener(this);
//		rButtons[0][3].getRadioButton().addActionListener(this);
//
//		ButtonGroup group1 = new ButtonGroup();
//		group1.add(rButtons[0][0].getRadioButton());
//		group1.add(rButtons[0][1].getRadioButton());
//		group1.add(rButtons[0][2].getRadioButton());
//		group1.add(rButtons[0][3].getRadioButton());
//
//		rButtons[1][0] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/unknown.png")), "(Q)");
//		rButtons[1][1] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/star2.png")), "(S)");
//		rButtons[1][2] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/star.png")), "(D)");
//		rButtons[1][3] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/trash.png")), "(F)");
//
//		rButtons[1][0].getRadioButton().setActionCommand("10");
//		rButtons[1][1].getRadioButton().setActionCommand("11");
//		rButtons[1][2].getRadioButton().setActionCommand("12");
//		rButtons[1][3].getRadioButton().setActionCommand("13");
//
//		rButtons[1][0].getRadioButton().addActionListener(this);
//		rButtons[1][1].getRadioButton().addActionListener(this);
//		rButtons[1][2].getRadioButton().addActionListener(this);
//		rButtons[1][3].getRadioButton().addActionListener(this);
//
//		ButtonGroup group2 = new ButtonGroup();
//		group2.add(rButtons[1][0].getRadioButton());
//		group2.add(rButtons[1][1].getRadioButton());
//		group2.add(rButtons[1][2].getRadioButton());
//		group2.add(rButtons[1][3].getRadioButton());
//
//		aaTablePanelAB.add(new JLabel("Pattern 1"));
//		aaTablePanelAB.add(rButtons[0][0]);
//		aaTablePanelAB.add(rButtons[0][1]);
//		aaTablePanelAB.add(rButtons[0][2]);
//		aaTablePanelAB.add(rButtons[0][3], "wrap");
//
//		aaTablePanelAB.add(new JLabel("Pattern 2"));
//		aaTablePanelAB.add(rButtons[1][0]);
//		aaTablePanelAB.add(rButtons[1][1]);
//		aaTablePanelAB.add(rButtons[1][2]);
//		aaTablePanelAB.add(rButtons[1][3], "wrap");
//
//
//		JPanel jpLegend = new JPanel(new MigLayout("fill", "[][]", "0[]0[]0"));
//		TitledBorder tb_legend = BorderFactory.createTitledBorder("Status legend");
//		jpLegend.setBorder(tb_legend);
//
//		JLabel trash = new JLabel();
//		JLabel star = new JLabel();
//		JLabel star2 = new JLabel();
//		JLabel unknown = new JLabel();
//
//		trash.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/trash.png")));
//		star.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/star.png")));
//		star2.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/star2.png")));
//		unknown.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/unknown.png")));
//
//		trash.setText("Irrelevant pattern");
//		star.setText("Relevant and optimal pattern");
//		star2.setText("Relevant and non optimal pattern");
//		unknown.setText("Unevaluated pattern");
//
//		jpLegend.add(unknown, "wrap");
//		jpLegend.add(star2, "wrap");
//		jpLegend.add(star, "wrap");
//		jpLegend.add(trash);
//		JPanel jpNextPrevious = new JPanel(new MigLayout("fillx"));
//
//		jbPrevious = new JButton("Previous alignment (Ctrl + Left)");
//		jbNext = new JButton("Previous alignment (Ctrl + Right)");
//
//		jbNext.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				setNextAlignment();
//			}
//				
//		});
//		jbPrevious.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//
//				setPreviousAlignment();
//					
//			}
//
//		});
//
//		jpNextPrevious.add(jbPrevious, "grow");
//		jpNextPrevious.add(jbNext, "grow");
//		
//		//		jpNextPrevious.add();
//		//		rButtons[1][1] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/star2.png")), "(S)");
//		//		rButtons[1][2] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/star.png")), "(D)");
//		//		rButtons[1][3] = new XRadioButton(new ImageIcon(ClassLoader.getSystemResource("img/trash.png")
//
//		aaTablePanelAB.setBorder(tb_change_status);
//		//		
//		//		aaTablePanelAB.add(jp_lA, "spanx 2, growx, wrap");
//		//		aaTablePanelAB.add(tableA.getJSP(), "spanx 3, grow, wrap");
//		//		aaTablePanelAB.add(jp_lB, "spanx 2, growx, wrap");
//		//		aaTablePanelAB.add(tableB.getJSP() ,"spanx 3, grow, wrap");
//
//		jp_l1.add(jib_left12);
//		jp_l1.add(jl_o1);
//		jp_l1.add(jb_switchOrientation12);
//		//		jp_l1.setMaximumSize(new Dimension(3000, 10));
//
//		jp_l2.add(jib_right12);
//		jp_l2.add(jl_o2);
//
//		aaTablePanel12.setBorder(tb_table12);
//
//
//		aaTablePanel12.add(jp_l1, "spanx 2, growx, wrap");
//		aaTablePanel12.add(table1.getJSP(), "spanx 3, grow, wrap");
//		aaTablePanel12.add(jp_l2, "spanx 2, growx, wrap");
//		aaTablePanel12.add(table2.getJSP() ,"spanx 3, grow, wrap");
//
//		this.add(jp_selection, "grow");
//		//		this.add(jp_selection, "spany 2, grow");
//		this.add(aaTablePanel12, "spanx, spany4,grow,wrap");
//		this.add(aaTablePanelAB, "wrap");
//		this.add(jpLegend, "wrap");
//		this.add(jpNextPrevious, "");
//
//
//	}
//
//
//	private static class VisualisationPanel2Holder{
//		private final static VisualisationPanelMotifValidationInsteadOfAlignment instance = new VisualisationPanelMotifValidationInsteadOfAlignment();		
//	}
//
//	public static VisualisationPanelMotifValidationInsteadOfAlignment getInstance(){
//		return VisualisationPanel2Holder.instance;
//	}
//
//	public void addAA(AnnotatedArray aa){
//
//	}
//
//	public void removeAA(int id){
//
//	}
//
//	public void displayAlignment(Alignment a){
//
//		StringBuffer labelText1 = new StringBuffer();
//		StringBuffer labelText2 = new StringBuffer();
//
//		int id1 = a.getP1().getIndex();
//		int id2 = a.getP2().getIndex();
//
//		if(id1 >= 0){
//			labelText1.append("pattern ");
//			labelText1.append(a.getP1().getIndex());
//			labelText1.append(" from ");
//
//			switch(a.getP1().status){
//			case UNKNOWN: rButtons[0][0].getRadioButton().setSelected(true); jib_left12.setImage("img/unknown.png"); break;
//			case RELEVANT_AND_NOT_OPTIMAL: rButtons[0][1].getRadioButton().setSelected(true); jib_left12.setImage("img/star2.png"); break;
//			case RELEVANT_AND_OPTIMAL: rButtons[0][2].getRadioButton().setSelected(true); jib_left12.setImage("img/star.png"); break;
//			case IRRELEVANT: rButtons[0][3].getRadioButton().setSelected(true); jib_left12.setImage("img/trash.png"); break;
//			}
//		}
//
//		labelText1.append(a.getP1().getOriginalAA().getFileName());
//
//		if(id2 >= 0){
//			labelText2.append("pattern ");
//			labelText2.append(a.getP2().getIndex());
//			labelText2.append(" from ");
//
//			switch(a.getP2().status){
//			case UNKNOWN: rButtons[1][0].getRadioButton().setSelected(true); jib_right12.setImage("img/unknown.png"); break;
//			case RELEVANT_AND_NOT_OPTIMAL: rButtons[1][1].getRadioButton().setSelected(true); jib_right12.setImage("img/star2.png"); break;
//			case RELEVANT_AND_OPTIMAL: rButtons[1][2].getRadioButton().setSelected(true); jib_right12.setImage("img/star.png"); break;
//			case IRRELEVANT: rButtons[1][3].getRadioButton().setSelected(true); jib_right12.setImage("img/trash.png"); break;
//			}
//		}
//
//		labelText2.append(a.getP2().getOriginalAA().getFileName());
//
//		//			mainLabel = jl_o1;
//		//			mainTable = table1;
//		//			secondaryTable = table2;
//		//			secondLabel = jl_o2;
//		//			indexInRowToColor = 2;
//
//		//		mainPattern = mainTable.getPattern();
//		//		secondaryPattern = secondaryTable.getPattern();
//
//
//		/* If p is not already displayed in the main Table */
//		if(a.getP1() != table1.getPattern()){
//
//			if(a.getP1().getCoordinates().size() > 0)
//				table1.setPattern(a.getP1());
//			else
//				table1.setAA(a.getP1().getOriginalAA());
//
//			jl_o1.setText(labelText1.toString());
//
//			//			Double d = Corpus.getCorpus().safeSimilarity(p, secondaryPattern);
//			//			String s;
//			//
//			//			if(d != null)
//			//				s = "Overview (similarity: " + d + ")";
//			//			else
//			//				s = "Overview";
//
//			//			panel.setBorder(BorderFactory.createTitledBorder(s));
//
//		}
//
//		/* If p is not already displayed in the main Table */
//		if(a.getP2() != table2.getPattern()){
//
//			if(a.getP2().getCoordinates().size() > 0)
//				table2.setPattern(a.getP2());
//			else
//				table2.setAA(a.getP2().getOriginalAA());
//
//			jl_o2.setText(labelText2.toString());
//		}
//
//
//	}
//
//	// TODO enable to display previous and next pattern in a table
//
//	public void displayClusterSelection(ExpeTree tree, int index){
//
//		tree.setSelectionRow(index);
//		tree.scrollPathToVisible(tree.getPathForRow(index));
//
//		if(tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode){
//
//			final DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
//
//			if(cNode.getUserObject() instanceof Cluster){
//				//			   if(arg0.getClickCount() == 2 ){
//				//			   }
//				//			   else if(arg0.getClickCount() == 1)
//				{
//					//				   Alignment a = ((Cluster)cNode.getUserObject()).alignment;
//					//				   table1.setPattern(a.getP1());
//					//				   table2.setPattern(a.getP2());
//
//					//					if(isLeftClick)
//					{			   
//						Alignment a = ((Cluster)cNode.getUserObject()).alignment;
//						displayAlignment(a);
//					}
//					//					else
//					{
//						//					   boolean previous = StandardView.getInstance().isCtrlPressed;
//						//					   StandardView.getInstance().isCtrlPressed = true;					   
//						//						displayClusterSelection(index+1, true, arg0);
//						//						displayClusterSelection(index+2, false, arg0);
//						//						StandardView.getInstance().isCtrlPressed = previous;
//
//
//						//						final Cluster c = (Cluster)cNode.getUserObject();
//
//						//						if(!c.hasBeenSaved){
//						//							JPopupMenu popup = new JPopupMenu();
//						//
//						//							JMenuItem item1 = new JMenuItem("Sélectionner alignement non optimal");
//						//							JMenuItem item2 = new JMenuItem("Sélectionner alignement optimal");
//						//							JMenuItem item3 = new JMenuItem("Sélectionner alignement optimal et particulièrement intéressant");
//						//
//						//							item1.addActionListener(new ActionListener() {
//						//
//						//								@Override
//						//								public void actionPerformed(ActionEvent e) {
//						//									saveAlignment(c, false, false);	
//						//									//			   
//						//
//						//								}
//						//							});
//						//
//						//							item2.addActionListener(new ActionListener() {
//						//
//						//								@Override
//						//								public void actionPerformed(ActionEvent e) {
//						//									saveAlignment(c, true, false);	
//						//								}
//						//							});
//						//
//						//							item3.addActionListener(new ActionListener() {
//						//
//						//								@Override
//						//								public void actionPerformed(ActionEvent e) {
//						//									saveAlignment(c, true, true);	
//						//								}
//						//							});
//						//
//						//
//						//							popup.add(item1);
//						//							popup.add(item2);
//						//							popup.add(item3);
//						//							popup.show(VisualisationPanel.this, arg0.getX(), arg0.getY());
//						//						}
//						//						else
//						//						{
//						//							JPopupMenu menu = new JPopupMenu();
//						//
//						//							JMenuItem item = new JMenuItem("Annuler l'enregistrement de cet alignement");
//						//							item.addActionListener(new ActionListener() {
//						//
//						//								@Override
//						//								public void actionPerformed(ActionEvent e) {
//						//									Pattern p1 = c.get(0);
//						//									Pattern p2 = c.get(1);
//						//
//						//									Corpus.getCorpus().removeSavedAlignment(p1, p2);
//						//									c.hasBeenSaved = false;
//						//									clusterTreeSaved.repaint();
//						//								}
//						//							});
//						//							menu.add(item);
//						//
//						//							menu.show(VisualisationPanel.this, arg0.getX(), arg0.getY());
//						//						}
//
//
//					}
//				}
//
//			}
//		}
//	}
//
//	public void updateClusters(ClusteringSolution cs2){
//
//		if(cs2 != null && cs2 instanceof HardClusteringSolution){
//
//			HardClusteringSolution hcs = (HardClusteringSolution)cs2;
//
//			if(csAlignments == null)
//				csAlignments = new ArrayList<>();
//
//			csAlignments.add(hcs);
//
//			//			try {
//			//	        	HardClusteringSolution hcs2 = (HardClusteringSolution)cs2;
//			//	        	System.out.println(hcs2.getMethodName());
//			//	        	Alignment a = hcs2.getCluster(0).getAlignment();
//			//	        	
//			//	        	System.out.println("a encoder null");
//			//	        	System.out.println(a == null);
//			//				Corpus.getCorpus().encodeToFile(a, "alignment0.xml");
//			//				
//			//				try{
//			//					
//			//					Alignment a2 = (Alignment)Corpus.getCorpus().decodeFromFile("alignment0.xml");
//			//					System.out.println("a desencoder null");
//			//			        System.out.println(a2==null);
//			//			        Pattern p1 = a2.getP1();
//			//			        Pattern p2 = a2.getP2();
//			//			        
//			////			        System.out.println(Corpus.getCorpus().getPattern(0).cAA);
//			////			        System.out.println(p.getCAA());
//			//			        
//			//			        try {
//			//
//			//						AnnotatedArray aa = new AnnotatedArray(p1.getFullPath(), true, Corpus.getCorpus().aacf);
//			//						p1.setAA(aa);
//			//				        table1.setPattern(p1);
//			//				        
//			//						AnnotatedArray aa2 = new AnnotatedArray(p2.getFullPath(), true, Corpus.getCorpus().aacf);
//			//						p2.setAA(aa2);
//			//				        table2.setPattern(p2);
//			//				        
//			//					} catch (CSVSeparatorNotFoundException | InvalidNumberOfColumnsInInputFilesException
//			//							| InvalidCSVFileNumberOfColumnsException | InvalidInputFileException e) {
//			//						// TODO Auto-generated catch block
//			//						e.printStackTrace();
//			//					}
//			//				}catch(Exception e){e.printStackTrace();}
//			//
//			//		        
//			//		        
//			//		        
//			//				
//			//			} catch (IOException e) {
//			//				e.printStackTrace();
//			//			}
//		}
//
//	}
//
//	public void updateEndOfClusteringProcess(){
//
//		if(csAlignments != null){			
//
//			int id = 0;
//			Alignment a = null;
//
//			if(csAlignments.size() > 0){
//				while(a == null && id < csAlignments.get(0).getClusterSet().size()){
//
//					if(csAlignments.get(0).getCluster(id).alignment.getP1().status == PatternStatus.UNKNOWN
//							|| csAlignments.get(0).getCluster(id).alignment.getP2().status == PatternStatus.UNKNOWN)
//						a = csAlignments.get(0).getCluster(id).alignment;
//
//					else
//						id++;
//
//				}
//			}
//
//			if(csAlignments.size() > 1 && a == null){
//
//				id = 0;
//				while(a == null && id < csAlignments.get(1).getClusterSet().size()){
//
//					if(csAlignments.get(1).getCluster(id).alignment.getP1().status == PatternStatus.UNKNOWN
//							|| csAlignments.get(1).getCluster(id).alignment.getP2().status == PatternStatus.UNKNOWN)
//						a = csAlignments.get(1).getCluster(id).alignment;
//
//					else
//						id++;
//
//				}
//			}
//
//			if(csAlignments.size() > 0){
//				expeTreeUnknown.setClusterSet(csAlignments.get(0).getClusterSet(), csAlignments.get(1).getClusterSet());
//				expeTreeRelevant.setClusterSet(csAlignments.get(0).getClusterSet(), csAlignments.get(1).getClusterSet());
//				expeTreeIrrelevant.setClusterSet(csAlignments.get(0).getClusterSet(), csAlignments.get(1).getClusterSet());
//			}
//
//			if(a != null){
//				displayClusterSelection(expeTreeUnknown, 1);
//			}
//			else{
//				if(csAlignments.get(0).getClusterSet().size() > 0){
//					a = csAlignments.get(0).getCluster(0).alignment;
//					if(a.getP1().status != PatternStatus.IRRELEVANT)
//						displayClusterSelection(expeTreeRelevant, 1);
//					else
//						displayClusterSelection(expeTreeIrrelevant, 1);
//				}
//				else{
//					a = csAlignments.get(1).getCluster(0).alignment;
//					if(a.getP1().status != PatternStatus.IRRELEVANT)
//						displayClusterSelection(expeTreeRelevant, 1);
//					else
//						displayClusterSelection(expeTreeIrrelevant, 1);
//				}
//			}
//
//		}
//
//		else{
//			expeTreeUnknown.reinitialize();
//			expeTreeRelevant.reinitialize();
//			expeTreeIrrelevant.reinitialize();
//		}
//
//		jp_relevant.repaint();
//		jp_irrelevant.repaint();
//		jp_unknown.repaint();
//	}
//
//	public boolean contains(JPanel jp, Component c){
//
//		Component[] cs = jp.getComponents();
//		boolean result = false;
//		int i = 0;
//
//		while(!result && i < cs.length){
//
//			if(cs[i] == c)
//				result = true;
//
//			i++;
//		}
//
//		return result;
//	}
//
//	public void reinitialize(){	
//
//		expeTreeRelevant.reinitialize();
//		expeTreeIrrelevant.reinitialize();
//		expeTreeUnknown.reinitialize();
//		table1.emptyTable();
//		table2.emptyTable();
//		tb_table12.setTitle("Overview");
//
//	}
//
//
//	private void changeTableOrientation12(boolean becomeHorizontal){
//
//		aaTablePanel12.removeAll();
//
//		if(becomeHorizontal){
//			aaTablePanel12.add(jp_l1, "spanx 2, growx, wrap");
//			aaTablePanel12.add(table1.getJSP(), "spanx 3, grow, wrap");
//			aaTablePanel12.add(jp_l2, "spanx 2, growx, wrap");
//			aaTablePanel12.add(table2.getJSP() ,"spanx 3, grow, wrap");
//		}
//		else{
//
//			aaTablePanel12.add(jp_l1, "growx");
//			aaTablePanel12.add(jp_l2, "growx, wrap");
//			aaTablePanel12.add(table1.getJSP(), "grow");
//			aaTablePanel12.add(table2.getJSP() ,"grow");
//		}
//
//		this.validate();
//		table1.componentResized(null);	
//		table2.componentResized(null);
//	}
//
//	public void maximize(){
//		//		aaTablePanel.updateUI();
//		table1.componentResized(null);	
//		table2.componentResized(null);
//	}
//
//	private void saveAlignment(Cluster c) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//
//		try {
//
//			if(!c.alignment.getP1().hasBeenSaved && !c.alignment.getP2().hasBeenSaved){
//				String date = sdf.format(new Date());
//				String fileName = "data/results/" + date + "_save_alignment.xml";
//				Corpus.getCorpus().encodeToFile(c.alignment, fileName);
//				StandardView.getInstance().log("Save alignment in file name " + fileName);
//				c.alignment.getP1().hasBeenSaved = true;
//				c.alignment.getP2().hasBeenSaved = true;
//				expeTreeRelevant.repaint();
//				expeTreeIrrelevant.repaint();
//				expeTreeUnknown.repaint();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void removeCS(int i) {
//		if(i < csAlignments.size())
//			csAlignments.remove(i);
//	}
//
//	public class XRadioButton extends JPanel {
//
//		private JRadioButton radioButton;
//		private JLabel label;
//
//		public XRadioButton() {
//			setLayout(new GridBagLayout());
//			add(getRadioButton());
//			add(getLabel());
//		}
//
//		public XRadioButton(Icon icon, String text) {
//			this();
//			setIcon(icon);
//			setText(text);
//		}
//
//		protected JRadioButton getRadioButton() {
//			if (radioButton == null) {
//				radioButton = new JRadioButton();
//			}
//			return radioButton;
//		}
//
//		protected JLabel getLabel() {
//			if (label == null) {
//				label = new JLabel();
//				label.setLabelFor(getRadioButton());
//			}
//			return label;
//		}
//
//		public void addActionListener(ActionListener listener) {
//			getRadioButton().addActionListener(listener);
//		}
//
//		public void removeActionListener(ActionListener listener) {
//			getRadioButton().removeActionListener(listener);
//		}
//
//		public void setText(String text) {
//			getLabel().setText(text);
//		}
//
//		public String getText() {
//			return getLabel().getText();
//		}
//
//		public void setIcon(Icon icon) {
//			getLabel().setIcon(icon);
//		}
//
//		public Icon getIcon() {
//			return getLabel().getIcon();
//		}
//
//	}
//
//
//	/** Returns an ImageIcon, or null if the path was invalid. */
//	protected ImageIcon createImageIcon(String path,
//			String description) {
//		java.net.URL imgURL = getClass().getResource(path);
//		if (imgURL != null) {
//			return new ImageIcon(imgURL, description);
//		} else {
//			System.err.println("Couldn't find file: " + path);
//			return null;
//		}
//	}
//
//	@Override
//	public void actionPerformed(ActionEvent e) {
//
//		if(e.getActionCommand().length() == 2){
//			int i1 = Integer.parseInt(e.getActionCommand().substring(0, 1));
//			int i2 = Integer.parseInt(e.getActionCommand().substring(1, 2));
//
//			/* If it is the first pattern */
//			if(i1 == 0 && table1.getPattern() != null){
//
//				Pattern p = table1.getPattern();
//
//				switch(i2){
//				case 0: p.status = PatternStatus.UNKNOWN; jib_left12.setImage("img/unknown.png");break;
//				case 1: p.status = PatternStatus.RELEVANT_AND_NOT_OPTIMAL; jib_left12.setImage("img/star2.png"); break;
//				case 2: p.status = PatternStatus.RELEVANT_AND_OPTIMAL; jib_left12.setImage("img/star.png"); break;
//				case 3: p.status = PatternStatus.IRRELEVANT; jib_left12.setImage("img/trash.png"); break;
//				}
//
//			}
//
//			/* If it is the second pattern */
//			else if(table2.getParent() != null){
//
//				Pattern p = table2.getPattern();
//
//				switch(i2){
//				case 0: p.status = PatternStatus.UNKNOWN; jib_right12.setImage("img/unknown.png");break;
//				case 1: p.status = PatternStatus.RELEVANT_AND_NOT_OPTIMAL; jib_right12.setImage("img/star2.png");break;
//				case 2: p.status = PatternStatus.RELEVANT_AND_OPTIMAL; jib_right12.setImage("img/star.png");break;
//				case 3: p.status = PatternStatus.IRRELEVANT; jib_right12.setImage("img/trash.png");break;
//				}
//
//			}
//
//			if(csAlignments.size() > 1){
//
//				expeTreeIrrelevant.setClusterSet(csAlignments.get(0).getClusterSet(), csAlignments.get(1).getClusterSet());
//				expeTreeRelevant.setClusterSet(csAlignments.get(0).getClusterSet(), csAlignments.get(1).getClusterSet());
//				expeTreeUnknown.setClusterSet(csAlignments.get(0).getClusterSet(), csAlignments.get(1).getClusterSet());
//
//				// TODO sauvegarder l'info
//			}
//		}
//		else
//			System.out.println("Unknown command: " + e.getActionCommand()	);
//
//		
//	}
//
//	public void keyPressed(java.awt.event.KeyEvent keyEvent) {
//		
//		if(StandardView.getInstance().isCtrlPressed)
//			if(keyEvent.getKeyCode() == KeyEvent.VK_LEFT)
//				jbPrevious.doClick();
//			else if(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT)
//				jbNext.doClick();
//		
//		switch(keyEvent.getKeyCode()){
//		case KeyEvent.VK_A: rButtons[0][0].getRadioButton().doClick(); break;
//		case KeyEvent.VK_Z: rButtons[0][1].getRadioButton().doClick(); break;
//		case KeyEvent.VK_E: rButtons[0][2].getRadioButton().doClick(); break;
//		case KeyEvent.VK_R: rButtons[0][3].getRadioButton().doClick(); break;
//		case KeyEvent.VK_Q: rButtons[1][0].getRadioButton().doClick(); break;
//		case KeyEvent.VK_S: rButtons[1][1].getRadioButton().doClick(); break;
//		case KeyEvent.VK_D: rButtons[1][2].getRadioButton().doClick(); break;
//		case KeyEvent.VK_F: rButtons[1][3].getRadioButton().doClick(); break; 
//		}
//		
//		
//		
//	}
//	
//
//
//	private void setPreviousAlignment() {
//
//		if(expeTreeUnknown.getRowCount() > 1)
//			setPreviousAlignment(expeTreeUnknown);
//
//		else if(expeTreeRelevant.getRowCount() > 1)
//			setPreviousAlignment(expeTreeRelevant);
//		
//		else if(expeTreeIrrelevant.getRowCount() > 1)
//			setPreviousAlignment(expeTreeIrrelevant);
//	}
//	
//	protected void setNextAlignment() {
//		if(expeTreeUnknown.getRowCount() > 1)
//			setNextAlignment(expeTreeUnknown);
//		
//		else if(expeTreeRelevant.getRowCount() > 1)
//			setNextAlignment(expeTreeRelevant);
//			
//		else if(expeTreeIrrelevant.getRowCount() > 1)
//			setNextAlignment(expeTreeIrrelevant);
//		
//	}
//	
//	private void setNextAlignment(ExpeTree tree) {
//
//		int[] selectedRows = tree.getSelectionRows();
//		int id;
//		
//		if(selectedRows != null && selectedRows.length > 0)
//			id = selectedRows[0];
//		else
//			id = tree.lastSelectedIndex;
//		
//		if(id != -1 && id + 1 < tree.getRowCount()){
//
//			if(tree.getPathForRow(id + 1).getPathCount() != 2)
//				displayClusterSelection(tree, id+1);
//			else if(id + 2 < tree.getRowCount())
//				displayClusterSelection(tree, id+2);
//			else
//				displayClusterSelection(tree, 1);
//		}
//		else
//			displayClusterSelection(tree, 1);
//
//	}
//
//	private void setPreviousAlignment(ExpeTree tree) {
//
//		int[] selectedRows = tree.getSelectionRows();
//		int id;
//		
//		if(selectedRows != null && selectedRows.length > 0)
//			id = selectedRows[0];
//		else
//			id = tree.lastSelectedIndex;
//		
//		if(id != -1 && id - 1 > 0){
//
//			if(tree.getPathForRow(id - 1).getPathCount() != 2)
//				displayClusterSelection(tree, id - 1);
//			else if(id - 2 > 0)
//				displayClusterSelection(tree, id - 2);
//			else
//				displayClusterSelection(tree, tree.getRowCount() - 1);
//		}
//		else
//			displayClusterSelection(tree, tree.getRowCount() - 1);	}
//

}
