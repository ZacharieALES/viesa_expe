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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import exception.InvalidArgumentsToCreateAnAAColumnFormat;
import experesults.CancelColumnsChangesAction;
import experesults.ChangeAnnotationColumnAction;
import experesults.ValidateColumnsChangesAction;
import model.AAColumnFormat;
import model.AAColumnFormat.ColumnType;
import model.AnnotationColumn;
import model.CommentColumn;
import model.Corpus;
import model.PositionedColumn;
import net.miginfocom.swing.MigLayout;

public class CSVColumnsSelector extends JDialog implements AWTEventListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<ColumnType> al_selection = new ArrayList<ColumnType>();
	private final CSVTable csvTable;
	private boolean isShiftPressed = false;
	private int lastSelectedColumn = -1;
	private final List<String[]> csvFile;
	public AAColumnFormat aacf = null;
	public static Color black = new Color(0, 0, 0);

	public CSVColumnsSelector(JFrame parent, final List<String[]> csvFile) {

		super(parent, "Column selector", true);

		this.csvFile = csvFile;

		/* Set the size of the JDialog */
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		int width = (int) screenSize.getWidth();

		this.setSize(Math.min(width, 80 * Corpus.getCorpus().initialAnnotationColumns.size()), 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		Toolkit tk = Toolkit.getDefaultToolkit();
		tk.addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);

		/* Main panel of the JDialog */
		JPanel jp = new JPanel();
		jp.setLayout(new MigLayout("fill"));

		csvTable = new CSVTable(csvFile);
		if (Corpus.getCorpus().isColumnFormatDefined())
			setPreviousColumnsSelection();

		JTableHeader header = csvTable.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(csvTable));

		MouseAdapter ma = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				int idCol = csvTable.columnAtPoint(e.getPoint());
				ColumnType selectionValue = al_selection.get(idCol);

				// if(e.getClickCount() == 1)
				{

					int minId = idCol;
					int maxId = idCol;

					/*
					 * If shift is pressed and there was a previously selected
					 * column
					 */
					if (lastSelectedColumn != -1 && isShiftPressed) {

						/*
						 * Set the bounds on the index of the columns that need
						 * to be modified
						 */
						minId = Math.min(lastSelectedColumn, idCol);
						maxId = Math.max(lastSelectedColumn, idCol);

						/*
						 * Set the value to which the columns will be set (i.e.,
						 * the value of the previously selected column)
						 */
						selectionValue = al_selection.get(lastSelectedColumn);

					}

					/*
					 * If right click is pressed and only one column must be
					 * modified
					 */
					else if (e.getButton() == MouseEvent.BUTTON3)
						selectionValue = selectionValue.getPrevious();

					/*
					 * If left click is pressed and only one column must be
					 * modified
					 */
					else if (e.getButton() == MouseEvent.BUTTON1)
						selectionValue = selectionValue.getNext();

					/*
					 * Set all the columns between the last selected column and
					 * column idCol
					 */
					for (int i = minId; i <= maxId; i++) {
						al_selection.set(i, selectionValue);
						csvTable.getColumnModel().getColumn(i)
						.setHeaderValue(selectionValue.getName());

						int id = Corpus.getCorpus().initialCommentColumns.indexOf(i);

						if(id == -1)
							id = Corpus.getCorpus().initialAnnotationColumns.indexOf(i);

						if(Corpus.getCorpus().results != null)
							Corpus.getCorpus().results.addAction(new ChangeAnnotationColumnAction(id, selectionValue));
					}

					lastSelectedColumn = idCol;
					csvTable.getTableHeader().repaint();
					csvTable.repaint();
				}

			}

		};


		csvTable.addMouseListener(ma);
		csvTable.getTableHeader().addMouseListener(ma);


		csvTable.setShowGrid(false);
		csvTable.setIntercellSpacing(new Dimension(0,0));

		JButton jb_ok = new JButton("OK");
		JButton jb_cancel = new JButton("Cancel");
		JHelpButton jhb_help = new JHelpButton(
				parent,
				"<html>This window is used to modify the columns type of the csv files.<br>"
						+ "To change the type of a column, click on it.<br><br>"
						+ "The possible types of a column are:<br>"
						//						+ "- unused: the column is not displayed and not used to extract the patterns;<br>"
						+ "- comment: the column is displayed but not used to extract the patterns;<br>"
						+ "- annotation: the column is used to extract the patterns."
						+ "<br><br>Tips:<br>- You can use left and right clicks;<br>- You can use shift to modify a range of columns with two clicks (one click without shift on the starting column followed by one click with shift on the ending column).</html>");

		jb_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CSVColumnsSelector.this.confirm();
				if(Corpus.getCorpus().results != null)
					Corpus.getCorpus().results.addAction(new ValidateColumnsChangesAction());
			}
		});

		jb_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				if(Corpus.getCorpus().results != null)
					Corpus.getCorpus().results.addAction(new CancelColumnsChangesAction());
			}
		});

		JPanel jp_button = new JPanel(new MigLayout("", "push[][]", ""));
		jp_button.add(jb_ok);
		jp_button.add(jb_cancel);
		jp_button.add(jhb_help);

		jp.add(csvTable.getJSP(), "grow, wrap");
		jp.add(jp_button);

		this.setContentPane(jp);

	}

	public void setPreviousColumnsSelection() {

		int visibleColumnsNb = 0;

		/* For each column in the initial annotations columns */
		for (int i = 0; i < Corpus.getCorpus().initialAnnotationColumns.size() ; ++i) {

			int csvColId = Corpus.getCorpus().initialAnnotationColumns.get(i);

			ColumnType type = null;
			int id = 0;

			while(type == null && id < Corpus.getCorpus().getTotalNumberOfColumns()){

				if(Corpus.getCorpus().getPositionedColumn(id).position == csvColId)
					type = Corpus.getCorpus().getPositionedColumn(id).column.getType();
				else
					id++;

			}

			if(type == null)
				type = ColumnType.COMMENT;

			al_selection.set(visibleColumnsNb, type);
			csvTable.getColumnModel().getColumn(visibleColumnsNb)
			.setHeaderValue(type.getName());

			visibleColumnsNb++;

		}

		csvTable.getTableHeader().repaint();
		csvTable.repaint();

	}

	public AAColumnFormat showThis() {
		this.setVisible(true);
		return aacf;
	}

	public class CSVTable extends JTable {

		private static final long serialVersionUID = 2L;

		private JScrollPane jsp_parent;

		public CSVTable(List<String[]> csvFile) {

			super();

			jsp_parent = new JScrollPane(this,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jsp_parent.getVerticalScrollBar().setUnitIncrement(10);
			jsp_parent
			.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			this.setModel(new CSVTableModel(csvFile));

			CSVTableRenderer rend = new CSVTableRenderer();

			int gap = 11;
			int width = this.getJSP().getWidth()
					- (gap * (this.getColumnCount() + 1));
			int size = Math.max(width / this.getColumnCount(), 4 * gap);

			for (int i = 0; i < this.getColumnCount(); i++) {

				al_selection.add(ColumnType.COMMENT);
				TableColumn column = this.getColumnModel().getColumn(i);
				column.setMinWidth(size);
				column.setCellRenderer(rend);

			}

		}

		public JScrollPane getJSP() {
			return jsp_parent;
		}

	}

	private class CSVTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 3L;
		private List<String[]> rowdata;
		private String[] columnNames;

		public CSVTableModel(List<String[]> rowdata) {

			List<String[]> partialRowData = new ArrayList<>();

			/* For each row */
			for(String[] as: rowdata){

				String[] ats = new String[Corpus.getCorpus().initialAnnotationColumns.size()];

				int id = 0;

				/* For each original column */
				for(int i = 0 ; i < as.length ; ++i){

					if(Corpus.getCorpus().initialAnnotationColumns.indexOf(i) != -1){

						ats[id] = as[i];
						id++;
					}

				}

				partialRowData.add(ats);

			}

			this.rowdata = partialRowData;
			columnNames = new String[Corpus.getCorpus().initialAnnotationColumns.size()];

			/* Initially, all the columns are set to COMMENT */
			for (int i = 0; i < getColumnCount(); i++) {
				al_selection.add(ColumnType.COMMENT);
				columnNames[i] = "Unused";
			}

		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public Object getValueAt(int row, int col) {
			return rowdata.get(row)[col];
		}

		public String getColumnName(int c) {
			return columnNames[c];
		}

		public int getRowCount() {
			return rowdata.size();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

	}

	private class CSVTableRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 4L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);

			ColumnType i = al_selection.get(column);

			if (i != ColumnType.COMMENT)
				this.setBackground(i.getColor());

			else if (row % 2 == 0)
				this.setBackground(new Color(232, 231, 248));
			else
				this.setBackground(new Color(255, 255, 255));

			this.setForeground(black);

			String valueDisplayed;

			/*
			 * Bug from java, getTableCellRendererComponent is sometime called
			 * with value == null
			 * http://stackoverflow.com/questions/3054775/jtable
			 * -strange-behavior
			 * -from-getaccessiblechild-method-resulting-in-null-point
			 * https://josm.openstreetmap.de/ticket/6301
			 */
			try {
				valueDisplayed = value.toString();
				this.setHorizontalAlignment(JLabel.LEFT);

				setText(valueDisplayed);
			} catch (NullPointerException e) {
			}

			return this;

		}
	}

	@Override
	public void eventDispatched(AWTEvent event) {

		if (event instanceof KeyEvent) {
			KeyEvent keyEvent = (KeyEvent) event;

			if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {

				if (keyEvent.getID() == KeyEvent.KEY_PRESSED)
					isShiftPressed = true;
				else if (keyEvent.getID() == KeyEvent.KEY_RELEASED)
					isShiftPressed = false;
			} else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
				if (keyEvent.getID() == KeyEvent.KEY_PRESSED)
					confirm();
		}
	}

	@SuppressWarnings("incomplete-switch")
	public void confirm() {
		// public AAColumnFormat(ArrayList<PositionedColumn> columns, double
		// maxSim, PositiveScoreTable pst) throws
		// InvalidArgumentsToCreateAnAAColumnFormat{

		int id = 0;
		boolean foundAnnotationColumn = false;
		int i = 0;

		while(i < csvFile.get(0).length && !foundAnnotationColumn){

			if(Corpus.getCorpus().initialAnnotationColumns.indexOf(i) != -1){
				ColumnType c = al_selection.get(id);

				switch (c) {
				case ANNOTATION:
					foundAnnotationColumn = true;
					break;
					// Add a case for numerical columns
				}

				id++;
			}

			i++;
		}

		if(foundAnnotationColumn){
			ArrayList<PositionedColumn> al_pc = new ArrayList<PositionedColumn>();

			id = 0;

			for (i = 0; i < csvFile.get(0).length; i++) {

				if(Corpus.getCorpus().initialAnnotationColumns.indexOf(i) != -1){
					ColumnType c = al_selection.get(id);

					switch (c) {
					case COMMENT:
						al_pc.add(new PositionedColumn(new CommentColumn(), i));
						break;
					case ANNOTATION:
						al_pc.add(new PositionedColumn(new AnnotationColumn(), i));
						break;
					}

					id++;
				}
				else if(Corpus.getCorpus().initialCommentColumns.indexOf(i) != -1){
					al_pc.add(new PositionedColumn(new CommentColumn(), i));
				}

			}

			try {
				aacf = new AAColumnFormat(al_pc);
			} catch (InvalidArgumentsToCreateAnAAColumnFormat e) {
				e.printStackTrace();
			}

			setVisible(false);
		}
		
		/* If no annotation column is found */
		else{
			JOptionPane.showMessageDialog(this, "At least one annotation column must be selected");
		}

	}

	/**
	 * Class used to center the column headers
	 * 
	 * @author zach
	 * 
	 */
	private static class HeaderRenderer implements TableCellRenderer {

		DefaultTableCellRenderer renderer;

		public HeaderRenderer(JTable table) {
			renderer = (DefaultTableCellRenderer) table.getTableHeader()
					.getDefaultRenderer();
			renderer.setHorizontalAlignment(JLabel.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {
			return renderer.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);
		}
	}

}
