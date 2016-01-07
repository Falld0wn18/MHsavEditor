/**
 * 
 */
package com.zsword.mhxeditor;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.alibaba.fastjson.JSONObject;
import com.zsword.mhxeditor.data.MHXSavDataFile;
import com.zsword.modules.event.CellEditingEvent;
import com.zsword.modules.event.CellEditingListener;
import com.zsword.modules.swing.PanelBase;
import com.zsword.modules.swing.table.ConfigurableTable;

/**
 * @Description: Character info panel
 * @Name CharacterPanel
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年11月30日 下午4:29:24
 * @Version 1.0
 */
@SuppressWarnings("serial")
class CharacterPanel extends PanelBase {

	private static final Map<String, Map<String, Object>> INFO_COLUMNS = Collections
			.unmodifiableMap(new LinkedHashMap<String, Map<String, Object>>() {
				{
					put("label", JSONObject.parseObject("{label:'属性'}"));
					put("value", JSONObject.parseObject("{label:'值', editable:true}"));
				}
			});
	private static final Map<String, Map<String, Object>> INFO_ATTRS = Collections
			.unmodifiableMap(new LinkedHashMap<String, Map<String, Object>>() {
				{
					put("名字", JSONObject.parseObject("{length:24, type:'string'}"));
					put("性别", JSONObject.parseObject("{length:1}"));
					put("髪型", JSONObject.parseObject("{length:1}"));
					put("内衣样式", JSONObject.parseObject("{length:1}"));
					put("声音", JSONObject.parseObject("{length:1}"));
					put("瞳色", JSONObject.parseObject("{length:1}"));
					put("メイク样式", JSONObject.parseObject("{length:1}"));
					put("メイク色", JSONObject.parseObject("{length:3, type:'bytes'}"));
					put("髪色", JSONObject.parseObject("{length:3, type:'bytes'}"));
					put("内衣色", JSONObject.parseObject("{length:3, type:'bytes'}"));
					put("肤色", JSONObject.parseObject("{length:3, type:'bytes'}"));
					put("unknow1", JSONObject.parseObject("{length:2, type:'bytes'}"));
					put("HR", JSONObject.parseObject("{length:4}"));
					put("HRP", JSONObject.parseObject("{length:4}"));
					put("金钱", JSONObject.parseObject("{length:4}"));
					put("游戏时间(sec)", JSONObject.parseObject("{length:4}"));
					put("继承时间(sec)", JSONObject.parseObject("{length:4}"));
				}
			});

	private MHXSavDataFile savFile;
	private JTable dataTable = null;

	public CharacterPanel() {
		super();
		this.initItems();
	}

	protected void initItems() {
		JTable table = this.createTable(INFO_COLUMNS);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
		this.dataTable = table;
	}

	private JTable createTable(Map<String, Map<String, Object>> config) {
		JTable table = new ConfigurableTable(config);
		table.setRowHeight(25);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(true);
		((ConfigurableTable) table).addCellEditingListener(new CellEditingListener() {
			@Override
			public void valueChanged(CellEditingEvent evt) {
				Object val = evt.getNewValue();
				commitEditData(val);
			}
		});
		return table;
	}

	public void loadDataFile(MHXSavDataFile savFile) throws IOException {
		this.savFile = savFile;
		Map<String, Object> info = savFile.loadCharacter();
		int row = 0;
		((DefaultTableModel) dataTable.getModel()).setRowCount(info.size());
		if (info.isEmpty()) {
			return;
		}
		for (String key : info.keySet()) {
			if (key.startsWith("unknow")) {
				continue;
			}
			dataTable.setValueAt(row + 1, row, 0);
			dataTable.setValueAt(key, row, 1);
			dataTable.setValueAt(info.get(key), row++, 2);
		}
	}

	private void commitEditData(Object val) {
		try {
			int row = this.dataTable.getSelectedRow();
			String key = (String) dataTable.getValueAt(row, 1);
			savFile.putCharacterInfo(key, val);
		} catch (Exception e) {
			handleError(e, "提交数据修改出错-");
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
	}
}
