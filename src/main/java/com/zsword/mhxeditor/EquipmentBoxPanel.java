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
 * @Description: Equipment box panel
 * @Name EquipmentBoxPanel
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月11日 下午1:52:41
 * @Version 1.0
 */
@SuppressWarnings("serial")
class EquipmentBoxPanel extends PanelBase implements ActionListener {

	public enum BoxType {
		CarryEquipt, EquiptBox
	}

	@SuppressWarnings("serial")
	private static final Map<String, JSONObject> ITEM_COLUMNS = Collections
			.unmodifiableMap(new LinkedHashMap<String, JSONObject>() {
				{
					put("offset", JSONObject.parseObject("{label:'位置', renderer:'hex'}"));
					put("item", JSONObject.parseObject("{label:'道具', length:2, editable:true, renderer:'hex'}"));
					put("num", JSONObject.parseObject("{label:'数量', length:2, editable:true}"));
				}
			});
	@SuppressWarnings("serial")
	private static final Map<String, JSONObject> EQUIPT_COLUMNS = Collections
			.unmodifiableMap(new LinkedHashMap<String, JSONObject>() {
				{
					put("offset", JSONObject.parseObject("{label:'位置', renderer:'hex', hidden:true}"));
					put("type", JSONObject.parseObject("{label:'种类', length:1, editable:true}"));
					put("equiptId", JSONObject.parseObject("{label:'装备ID', length:2, editable:true}"));
					put("level", JSONObject.parseObject("{label:'等级/孔数', length:1, editable:true}"));
					put("natureVal", JSONObject.parseObject("{label:'属性值', length:1, editable:true}"));
					put("natureType", JSONObject.parseObject("{label:'属性类型', length:1, editable:true}"));
					put("decoration1", JSONObject.parseObject("{label:'装饰珠1', length:2, editable:true}"));
					put("decoration2", JSONObject.parseObject("{label:'装饰珠2', length:2, editable:true}"));
					put("decoration3", JSONObject.parseObject("{label:'装饰珠3', length:2, editable:true}"));
					put("sharpness", JSONObject.parseObject("{label:'锋利/技1', length:1, editable:true}"));
					put("ability", JSONObject.parseObject("{label:'附加能力', length:1, editable:true}"));
					put("abilityVal", JSONObject.parseObject("{label:'技1值', length:1, editable:true}"));
					put("insectType", JSONObject.parseObject("{label:'虫种类', length:1, editable:true}"));
					put("grind", JSONObject.parseObject("{label:'研磨/技2', length:1, editable:true}"));
					put("rarity", JSONObject.parseObject("{label:'稀有度', length:1, editable:true}"));
					put("etc", JSONObject.parseObject("{label:'其它/技2值', length:1, editable:true}"));
					put("strengthen", JSONObject.parseObject("{label:'极限强化/技2值', length:1, editable:true}"));
					put("0unknow", JSONObject.parseObject("{label:'未知', length:2}"));
					/*
					 * put("1unknow", JSONObject.parseObject(
					 * "{label:'未知', length:2}")); put("2unknow",
					 * JSONObject.parseObject("{label:'未知', length:2}"));
					 * put("3unknow", JSONObject.parseObject(
					 * "{label:'未知', length:2}"));
					 */
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

	public EquipmentBoxPanel(JSONObject settings, JSONObject lang, BoxType listType) {
		this(null, settings, lang, listType);
	}

	public EquipmentBoxPanel(JFrame owner, JSONObject settings, JSONObject lang, BoxType listType) {
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
		case CarryEquipt:
		case EquiptBox:
			tableDef.putAll(EQUIPT_COLUMNS);
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
		case CarryEquipt:
			dataList = savFile.loadEquipmentBox(this.dataStruct);
			break;
		case EquiptBox:
			dataList = savFile.loadEquipmentBox(this.dataStruct);
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
			int col = this.dataTable.getSelectedColumn();
			String key = dataTable.getColumnName(col);
			int addr = -1;
			for (int i = 0; i < dataTable.getColumnCount(); i++) {
				String colName = dataTable.getColumnName(i);
				if ("offset".equals(colName)) {
					addr = (Integer) dataTable.getValueAt(row, i);
					break;
				}
			}
			if (addr == -1) {
				throw new IllegalArgumentException("地址无效-" + addr);
			}
			Integer len = 0;
			int pos = 0;
			for (String k : dataStruct.keySet()) {
				Map<String, Object> def = dataStruct.get(k);
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
			if (String.class.isAssignableFrom(val.getClass())) {
				val = Integer.parseInt((String) val, 16);
			}
			savFile.writeInt((Integer) val, len);
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
		case CarryEquipt:
		case EquiptBox:
			dataKey = "Equipt-";
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
