package com.zsword.test.mh4geditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class JTableListSelectionListener {

	public static void main(String[] args) {
		JFrame f = new JFrame("JTableListSelectionListener");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JTable table;
		String[] columnTitles = { "A", "B", "C", "D" };
		Object[][] rowData = { { "11", "12", "13", "14" }, { "21", "22", "23", "24" }, { "31", "32", "33", "34" },
				{ "41", "42", "43", "44" } };
		table = new JTable(rowData, columnTitles);

		table.setCellSelectionEnabled(true);// 设置此表是否允许同时存在行选择和列选择。
		// 返回用来维持行选择状态的 ListSelectionModel。
		// 此接口表示任何组件的当前选择状态，该组件显示一个具有稳定索引的值列表。
		ListSelectionModel cellSelectionModel = table.getSelectionModel();
		// 单选,只能选择一个单元格
		cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {// 单元格值变动事件
				String selectedData = null;
				int[] selectedRow = table.getSelectedRows();// 被选择的行
				int[] selectedColumns = table.getSelectedColumns();// 被选择的列

				for (int i = 0; i < selectedRow.length; i++) {// 循环取出
					for (int j = 0; j < selectedColumns.length; j++) {
						selectedData = (String) table.getValueAt(selectedRow[i], selectedColumns[j]);
					}
				}
				System.out.println("选择的: " + selectedData);
			}
		});

		f.add(new JScrollPane(table));
		f.setSize(300, 200);
		f.setVisible(true);
	}

}