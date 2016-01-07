/**
 * 
 */
package com.zsword.mh4geditor;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.alibaba.fastjson.JSONObject;
import com.zsword.mh4geditor.data.MH4GSavDataFile;
import com.zsword.modules.event.CellEditingEvent;
import com.zsword.modules.event.CellEditingListener;
import com.zsword.modules.swing.PanelBase;
import com.zsword.modules.swing.table.ConfigurableTable;

/**
 * @Description:
 * @Name PersonInfoPanel
 * @Company ZSword (C) Cpyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月22日 下午5:46:10
 * @Version 1.0
 */
class PersonInfoPanel extends PanelBase {

	@SuppressWarnings("serial")
	private static final Map<String, Map<String, Object>> INFO_COLUMNS = Collections
			.unmodifiableMap(new LinkedHashMap<String, Map<String, Object>>() {
				{
					put("label", JSONObject.parseObject("{label:'属性'}"));
					put("value", JSONObject.parseObject("{label:'值', editable:true}"));
				}
			});
	@SuppressWarnings("serial")
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

	private MH4GSavDataFile savFile;
	private JTable dataTable = null;

	/**
	 * @comment
	 */
	public PersonInfoPanel() {
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

	public void loadDataFile(MH4GSavDataFile savFile) throws IOException {
		this.savFile = savFile;
		Map<String, Map<String, Object>> struct = INFO_ATTRS;
		Map<String, Object> info = savFile.loadCharacter(struct);
		int row = 0;
		((DefaultTableModel) dataTable.getModel()).setRowCount(info.size());
		for (String key : struct.keySet()) {
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
			int col = this.dataTable.getSelectedColumn();
			String key = (String) dataTable.getValueAt(row, 1);
			int addr = savFile.getDataOffset();
			if (addr == -1) {
				throw new IllegalArgumentException("地址无效-" + addr);
			}
			Integer len = 0;
			int pos = 0;
			Map<String, Map<String, Object>> dataStruct = INFO_ATTRS;
			Map<String, Object> def = null;
			for (String k : dataStruct.keySet()) {
				def = dataStruct.get(k);
				len = (Integer) def.get("length");
				if (len == null) {
					continue;
				}
				if (k.equals(key)) {
					break;
				}
				pos = pos + len;
			}
			savFile.seek(addr + pos);
			String type = (String) def.get("type");
			if ("string".equals(type)) {
				String str = (String) val;
				savFile.writeStringData(str);
				int r = len - str.length() * 2 - 1;
				if (r > 0) {
					byte[] emptyData = new byte[r];
					savFile.write(emptyData);
				}
				return;
			} else {
				if (String.class.isAssignableFrom(val.getClass())) {
					if ("bytes".equals(type)) {
						val = Integer.parseInt((String) val, 16);
					} else {
						val = Integer.parseInt((String) val);
					}
				}
			}
			savFile.writeInt((Integer) val, len);
		} catch (Exception e) {
			handleError(e, "提交数据修改出错-");
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
	}
}
