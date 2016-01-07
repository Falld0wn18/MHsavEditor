/**
 * 
 */
package com.zsword.mhxeditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.zsword.mhxeditor.data.MHXSavDataFile;
import com.zsword.modules.event.CellEditingEvent;
import com.zsword.modules.event.CellEditingListener;
import com.zsword.modules.swing.PanelBase;
import com.zsword.modules.swing.table.ConfigurableTable;

/**
 * @Description: Item box panel
 * @Name ItemBoxPanel
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月11日 下午1:52:41
 * @Version 1.0
 */
@SuppressWarnings("serial")
class ItemBoxPanel extends PanelBase implements ActionListener {

	public enum BoxType {
		CarryItem, ItemBox
	}

	@SuppressWarnings("serial")
	private static final Map<String, JSONObject> ITEM_COLUMNS = Collections
			.unmodifiableMap(new LinkedHashMap<String, JSONObject>() {
				{
					put("no", JSONObject.parseObject("{label:'序号'}"));
					put("item", JSONObject
							.parseObject("{label:'道具', length:2, editable:true, type:'int', max:1950, min:1}"));
					put("count",
							JSONObject.parseObject("{label:'数量', length:2, editable:true, type:'int', max:99, min:0}"));
				}
			});
	private JSONObject settings = null;
	private JSONObject lang = null;
	private BoxType listType = null;
	private MHXSavDataFile savFile;
	private JTable dataTable = null;
	private Map<String, Map<String, Object>> dataStruct = null;
	private JLabel statusText = null;
	private JFrame owner = null;

	public ItemBoxPanel(JSONObject settings, JSONObject lang, BoxType listType) {
		this(null, settings, lang, listType);
	}

	public ItemBoxPanel(JFrame owner, JSONObject settings, JSONObject lang, BoxType listType) {
		super();
		this.owner = owner;
		this.settings = settings;
		this.lang = lang;
		this.listType = listType;
		this.setLayout(new BorderLayout(5, 5));
		initItems();
	}

	protected void initItems() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setBorderPainted(false);
		JLabel label = new JLabel("状态:无");
		toolbar.add(label);
		this.statusText = label;
		add(toolbar, BorderLayout.NORTH);

		Map<String, Map<String, Object>> tableDef = new LinkedHashMap<String, Map<String, Object>>();
		switch (listType) {
		case CarryItem:
		case ItemBox:
			tableDef.putAll(ITEM_COLUMNS);
			break;
		default:
		}
		JTable table = this.createTable(tableDef);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		this.dataStruct = tableDef;
		this.dataTable = table;
	}

	public void loadDataFile(MHXSavDataFile savFile) throws IOException {
		this.savFile = savFile;
		List<Map<String, Object>> dataList = null;
		switch (listType) {
		case CarryItem:
			dataList = savFile.loadCarryItem(this.dataStruct);
			break;
		case ItemBox:
			dataList = savFile.loadItemBox(this.dataStruct);
			break;
		default:
		}
		DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
		model.setRowCount(dataList.size());
		for (int i = 0; i < dataList.size(); i++) {
			Map<String, Object> data = dataList.get(i);
			dataTable.setValueAt(i + 1, i, 0);
			for (int n = 1; n < dataTable.getColumnCount(); n++) {
				String name = dataTable.getColumnName(n);
				Object val = data.get(name);
				dataTable.setValueAt(val, i, n);
			}
		}
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

	public void addTableSelectionListener(MouseListener l) {
		this.dataTable.addMouseListener(l);
	}

	private void commitEditData(Object val) {
		try {
			int row = this.dataTable.getSelectedRow();
			int idx = row;
			int itemId = -1;
			int itemCount = -1;
			for (int i = 0; i < dataTable.getColumnCount(); i++) {
				String colName = dataTable.getColumnName(i);
				if ("item".equals(colName)) {
					itemId = Integer.parseInt(String.valueOf(dataTable.getValueAt(row, i)));
					continue;
				} else if ("count".equals(colName)) {
					itemCount = (Integer) dataTable.getValueAt(row, i);
					continue;
				}
			}
			if (itemId < 0 || itemCount < 0) {
				throw new IllegalArgumentException("数据无效-" + itemId + ", " + itemCount);
			}
			switch (listType) {
			case ItemBox:
				savFile.writeBoxItem(idx, itemId, itemCount);
				break;
			case CarryItem:
				savFile.writeCarryItem(idx, itemId, itemCount);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			handleError(e, "提交数据修改出错-");
		}
	}

	public TableColumn getSelectedColumn() {
		int col = this.dataTable.getSelectedColumn();
		return this.dataTable.getColumnModel().getColumn(col);
	}

	public String getSelectedCellValueCodeKey() {
		String dataKey = null;
		switch (listType) {
		case CarryItem:
		case ItemBox:
			dataKey = "";
			break;
		default:
			break;
		}
		int col = dataTable.getSelectedColumn();
		String key = dataTable.getColumnName(col);
		if ("equiptId".equals(key)) {
			for (int i = 0; i < dataTable.getColumnCount(); i++) {
				String colName = dataTable.getColumnName(i);
				if ("type".equals(colName)) {
					int row = dataTable.getSelectedRow();
					Object colVal = dataTable.getValueAt(row, i);
					if (colVal.toString().matches("07|08|09|0A|0B|0C|0D|0E|0F|10|11|12|13|14")) {
						dataKey = "";
					}
					break;
				}
			}
		} else if (key.matches("sharpness|grind")) {
			for (int i = 0; i < dataTable.getColumnCount(); i++) {
				String colName = dataTable.getColumnName(i);
				if ("type".equals(colName)) {
					int row = dataTable.getSelectedRow();
					Object colVal = dataTable.getValueAt(row, i);
					if ("06".equals(colVal)) {
						key = "stoneSkill";
					}
					break;
				}
			}
		} else if ("insectType".equals(key)) {
			key = null;
			for (int i = 0; i < dataTable.getColumnCount(); i++) {
				String colName = dataTable.getColumnName(i);
				if ("type".equals(colName)) {
					int row = dataTable.getSelectedRow();
					Object colVal = dataTable.getValueAt(row, i);
					if ("13".equals(colVal)) {
						key = "insectType";
					}
					break;
				}
			}
		} else if (key.startsWith("decoration")) {
			key = "decoration";
		}
		return dataKey + key;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		String actName = StringUtils.uncapitalize(cmd);
		try {
			Method method = this.getClass().getDeclaredMethod(actName);
			method.invoke(this);
		} catch (Exception e) {
			handleError(e, "执行命令出错-" + cmd + ": ");
		}
	}
}
