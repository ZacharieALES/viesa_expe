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
import java.awt.Font;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import clustering.Cluster;
import clustering.ClusterSet;
import main.MainTutorial;
import model.Alignment;
import model.Pattern.PatternStatus;



@SuppressWarnings("serial")
public class ExpeTree extends JTree{

	private JScrollPane jsp_parent;
	static DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root");
	private DefaultTreeModel clusterTM;
	
	private String sOneLocutor = "1 locutor";
	private String sTwoLocutors = "2 locutors";
	
	public int lastSelectedIndex = -1;

	public PatternStatus statuts;

	private boolean is1LocutorExpanded = true;
	private boolean is2LocutorsExpanded = true;
	
	public ExpeTree(PatternStatus statuts){
		super(top);

		if(MainTutorial.IS_TUTO)
			sOneLocutor = "Obtained alignments";
		
		if(statuts == PatternStatus.RELEVANT_AND_OPTIMAL)
			this.statuts = PatternStatus.RELEVANT_AND_NOT_OPTIMAL;
		else
			this.statuts = statuts;

		this.setToggleClickCount(0);
		jsp_parent = new JScrollPane(this);
		jsp_parent.getVerticalScrollBar().setUnitIncrement(10);

		/* Used to avoid displaying blue background when selecting a line */
		this.setOpaque(true);
		this.setBackground(Color.white);
		UIDefaults paneDefaults = new UIDefaults();
		paneDefaults.put("Tree.selectionBackground",null);

		//	    this.putClientProperty("Nimbus.Overrides",paneDefaults);
		//	    this.putClientProperty("Nimbus.Overrides.InheritDefaults",false);


		this.expandPath(this.getPathForRow(0));
		this.setCellRenderer(new ClusterTreeCellRenderer());
		this.setRootVisible(false);
		/* End: Used to avoid displaying blue background when selecting a line */

		clusterTM = new DefaultTreeModel(top);
		this.setModel(clusterTM);
		
		this.addTreeExpansionListener(new TreeExpansionListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				if(sOneLocutor.equals(event.getPath().getLastPathComponent().toString()))
					is1LocutorExpanded = true;
				else if(sTwoLocutors.equals(event.getPath().getLastPathComponent().toString())){
					is2LocutorsExpanded = true;
				}
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				if(sOneLocutor.equals(event.getPath().getLastPathComponent().toString()))
					is1LocutorExpanded = false;
				else if(sTwoLocutors.equals(event.getPath().getLastPathComponent().toString())){
					is2LocutorsExpanded = false;
				}
			}
		});

	}

	public void reinitialize(){
		this.removeAll();
		this.setModel(null);

		top = new DefaultMutableTreeNode("Root");
		clusterTM = new DefaultTreeModel(top);
		this.setModel(clusterTM);

		//		this.updateUI();
	}

	public void setClusterSet(ClusterSet oneLocutor, ClusterSet twoLocutors){

		this.reinitialize();

		int id = 1;

		if(oneLocutor != null && oneLocutor.clusters.size() > 0){

			DefaultMutableTreeNode oneLocutorNode = new DefaultMutableTreeNode(sOneLocutor);

			for(int i = 0 ; i < oneLocutor.size() ; i++){

				Cluster cluster = oneLocutor.get(i);

				/* If the cluster corresponds to the tree type */
				if(cluster.getAlignment().status == statuts
						|| statuts == PatternStatus.RELEVANT_AND_NOT_OPTIMAL
						&& cluster.getAlignment().status == PatternStatus.RELEVANT_AND_OPTIMAL
						){

					cluster.setName("" + cluster.getAlignment().getId());
					DefaultMutableTreeNode clusterNode = new DefaultMutableTreeNode(cluster);
					oneLocutorNode.add(clusterNode);

					id++;
				}

			}

			if(id > 1){
				top.add(oneLocutorNode);
				
				if(is1LocutorExpanded)
					this.expandPath(new TreePath(oneLocutorNode.getPath()));
				else
					this.collapsePath(new TreePath(oneLocutorNode.getPath()));
				
					
			}
		}

		if(twoLocutors != null && twoLocutors.clusters.size() > 0){

			int previousId = id;
			
			DefaultMutableTreeNode twoLocutorsNode = new DefaultMutableTreeNode(sTwoLocutors);

			for(int i = 0 ; i < twoLocutors.size() ; i++){

				Cluster cluster = twoLocutors.get(i);

				/* If the cluster corresponds to the tree type */
				if(cluster.getAlignment().status == statuts
						|| statuts == PatternStatus.RELEVANT_AND_NOT_OPTIMAL
						&& cluster.getAlignment().status == PatternStatus.RELEVANT_AND_OPTIMAL
						){

					cluster.setName(cluster.getAlignment().getId() + "");
					DefaultMutableTreeNode clusterNode = new DefaultMutableTreeNode(cluster);
					twoLocutorsNode.add(clusterNode);

					id++;
				}

			}
			
			if(id > previousId){
			
				top.add(twoLocutorsNode);

				if(is2LocutorsExpanded)
					this.expandPath(new TreePath(twoLocutorsNode.getPath()));
				else
					this.collapsePath(new TreePath(twoLocutorsNode.getPath()));
			}
				
		}
		
		if(top.getChildCount() == 0)
			top.add(new DefaultMutableTreeNode("Pas d'alignement de ce type"));

//		expandAll(true);
		this.updateUI();
	}

	public JScrollPane getJSP(){
		return jsp_parent;
	}

	// If expand is true, expands all nodes in the tree.
	// Otherwise, collapses all nodes in the tree.
	public void expandAll(boolean expand) {
		TreeNode root = (TreeNode)this.getModel().getRoot();

		// Traverse tree from root
		expandAll(new TreePath(root), expand);
	}

	private void expandAll(TreePath parent, boolean expand) {

		// Traverse children
		TreeNode node = (TreeNode)parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (@SuppressWarnings("rawtypes")
			Enumeration e=node.children(); e.hasMoreElements(); ) {
				TreeNode n = (TreeNode)e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			this.expandPath(parent);
		} else {
			this.collapsePath(parent);
		}
	}

	public class ClusterTreeCellRenderer extends DefaultTreeCellRenderer{

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {



			if(value instanceof DefaultMutableTreeNode){

				DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

				if(node.getUserObject() instanceof Cluster){

					Cluster c = (Cluster)(node.getUserObject());
					String s = c.getClusterName();

					if(s == null)
						s = "Alignment";

					JLabel l = (JLabel)super.getTreeCellRendererComponent(tree, s, selected, expanded, leaf, row,
							hasFocus);
					l.setOpaque(false);
					

					PatternStatus st1 = c.getAlignment().status;
					
					String path = "img/trash.png";
					URL imageURL = ClassLoader.getSystemResource(path);
					
					switch(st1){
					case IRRELEVANT: imageURL = ClassLoader.getSystemResource("img/trash.png"); break;
//						switch(st2){
//						case IRRELEVANT: imageURL = ClassLoader.getSystemResource("img/trash_trash.png"); break;
//						case RELEVANT_AND_NOT_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/trash_star2.png");break;
//						case RELEVANT_AND_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/trash_star.png");break;
//						case UNKNOWN: imageURL = ClassLoader.getSystemResource("img/trash_u.png");break;
//						}
//						break;
					case UNKNOWN:imageURL = ClassLoader.getSystemResource("img/unknown.png");break;
//						switch(st2){
//						case IRRELEVANT: imageURL = ClassLoader.getSystemResource("img/u_trash.png"); break;
//						case RELEVANT_AND_NOT_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/u_star2.png");break;
//						case RELEVANT_AND_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/u_star.png");break;
//						case UNKNOWN: imageURL = ClassLoader.getSystemResource("img/u_u.png");break;
//						}
//						break;
					case RELEVANT_AND_NOT_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/star2.png");break;
//						switch(st2){
//						case IRRELEVANT: imageURL = ClassLoader.getSystemResource("img/star2_trash.png"); break;
//						case RELEVANT_AND_NOT_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/star2_star2.png");break;
//						case RELEVANT_AND_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/star2_star.png");break;
//						case UNKNOWN: imageURL = ClassLoader.getSystemResource("img/star2_u.png");break;
//						}
//						break;
					case RELEVANT_AND_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/star.png");break;
//						switch(st2){
//						case IRRELEVANT: imageURL = ClassLoader.getSystemResource("img/star_trash.png"); break;
//						case RELEVANT_AND_NOT_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/star_star2.png");break;
//						case RELEVANT_AND_OPTIMAL: imageURL = ClassLoader.getSystemResource("img/star_star.png");break;
//						case UNKNOWN: imageURL = ClassLoader.getSystemResource("img/star_u.png");break;
//						}
//						break;
					}
					
					l.setIcon(new ImageIcon(imageURL));

					//					   if(c.hasBeenSaved){
					//						   l.setFont(l.getFont().deriveFont(l.getFont().getStyle() | Font.ITALIC));
					//
					////							l.setForeground(new Color(255, 255, 255));
					////							l.setOpaque(true);
					//					   }
				}
				else{
					JLabel l = (JLabel)super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
							hasFocus);
					l.setOpaque(false);
					l.setFont(l.getFont().deriveFont(l.getFont().getStyle() | ~Font.ITALIC));
				}

			}
			else{
				JLabel l = (JLabel)super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
						hasFocus);
				l.setOpaque(false);
				l.setFont(l.getFont().deriveFont(l.getFont().getStyle() | ~Font.ITALIC));
			}

			return this;
		}
	}
	
	@Override
	public void setSelectionRow(int row){
		super.setSelectionRow(row);
		lastSelectedIndex = row;
	}

	public void selectNode(Alignment alignment) {
		
		DefaultTreeModel model = (DefaultTreeModel)getModel();
	    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

	    DefaultMutableTreeNode nodeFound = findNode(root, alignment);
	    
	    if(nodeFound != null)
	    	this.setSelectionPath(new TreePath(nodeFound.getPath()));
	    
		
	}
	
	private DefaultMutableTreeNode findNode(DefaultMutableTreeNode parent, Alignment alignment){
		
	    int childCount = parent.getChildCount();
	    DefaultMutableTreeNode result = null;
	    int i = 0;

	    while(result == null && i < childCount) {

	        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parent.getChildAt(i);
	        
	        if (childNode.getChildCount() > 0) {
	            result = findNode(childNode, alignment);
	        } else if(childNode.getUserObject() != null && childNode.getUserObject() instanceof Cluster){
	            Cluster c = (Cluster)childNode.getUserObject();
	        	Alignment a = c.getAlignment();
	            
	            if(a != null && a.equals(alignment)){
	            	result = childNode;
	            }
	        }
	        
	        i++;

	    }
	    
	    return result;
	}

}
