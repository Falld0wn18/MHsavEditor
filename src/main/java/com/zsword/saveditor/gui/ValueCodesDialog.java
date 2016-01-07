/**
 * 
 */
package com.zsword.saveditor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.alibaba.fastjson.JSONObject;
import com.zsword.modules.swing.DialogBase;
import com.zsword.modules.swing.table.ConfigurableTable;

/**
 * @Description:
 * @Name ValueCodesDialog
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月20日 下午3:25:42
 * @Version 1.0
 */
public class ValueCodesDialog extends DialogBase {

	private static final Map<String, Map<String, Object>> TABLE_COLUMNS = Collections
			.unmodifiableMap(new LinkedHashMap<String, Map<String, Object>>() {
				{
					put("code", JSONObject.parseObject("{label:'代码', width:20}"));
					put("label", JSONObject.parseObject("{label:'名称', width:100}"));
					put("comment", JSONObject.parseObject("{label:'说明'}"));
				}
			});

	private JTable dataTable = null;

	public ValueCodesDialog() {
		super();
		this.setTitle("代码参考");
		this.setPreferredSize(new Dimension(400, 300));
		this.setLayout(new BorderLayout(5, 5));
		this.initItems();
		this.setAlwaysOnTop(true);
	}

	protected void initItems() {
		JTable table = new ConfigurableTable(TABLE_COLUMNS);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		this.dataTable = table;

		this.pack();
	}

	public void loadCodes(JSONObject codes) {
		DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
		model.setRowCount(codes.size());
		int row = 0;
		for (String key : codes.keySet()) {
			Map<String, Object> data = codes.getJSONObject(key);
			dataTable.setValueAt(row + 1, row, 0);
			for (int n = 1; n < dataTable.getColumnCount(); n++) {
				String colId = dataTable.getColumnName(n);
				if ("code".equals(colId)) {
					dataTable.setValueAt(key, row, n);
					continue;
				}
				dataTable.setValueAt(data.get(colId), row, n);
			}
			row++;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}
