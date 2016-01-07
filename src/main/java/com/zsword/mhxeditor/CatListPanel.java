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
 * @Description: Cat list panel
 * @Name CatListPanel
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月11日 下午1:52:41
 * @Version 1.0
 */
@SuppressWarnings("serial")
class CatListPanel extends PanelBase implements ActionListener {

	private static final Map<String, JSONObject> DATA_COLUMNS = Collections
			.unmodifiableMap(new LinkedHashMap<String, JSONObject>() {
				{
					put("offset", JSONObject.parseObject("{label:'位置', renderer:'hex', hidden:true}"));
					put("name", JSONObject.parseObject("{label:'名字', type:'string', length:32, editable:true}"));
					put("exp", JSONObject.parseObject("{label:'经验', length:4, editable:true}"));
					put("lv", JSONObject.parseObject("{label:'等级', length:1, editable:true}"));
					put("habit", JSONObject.parseObject("{label:'习惯', length:1, editable:true}"));
					put("ntimacy", JSONObject.parseObject("{label:'亲密', length:1, editable:true}"));
					put("prefer", JSONObject.parseObject("{label:'倾向', length:1, editable:true}"));
					put("skill01", JSONObject.parseObject("{label:'装载技能1', length:1, editable:true}"));
					put("skill02", JSONObject.parseObject("{label:'装载技能2', length:1, editable:true}"));
					put("skill03", JSONObject.parseObject("{label:'装载技能3', length:1, editable:true}"));
					put("skill04", JSONObject.parseObject("{label:'装载技能4', length:1, editable:true}"));
					put("skill05", JSONObject.parseObject("{label:'装载技能5', length:1, editable:true}"));
					put("skill06", JSONObject.parseObject("{label:'装载技能6', length:1, editable:true}"));
					put("skill07", JSONObject.parseObject("{label:'装载技能7', length:1, editable:true}"));
					put("skill08", JSONObject.parseObject("{label:'装载技能8', length:1, editable:true}"));
					put("psvskill01", JSONObject.parseObject("{label:'装载被技1', length:1, editable:true}"));
					put("psvskill02", JSONObject.parseObject("{label:'装载被技2', length:1, editable:true}"));
					put("psvskill03", JSONObject.parseObject("{label:'装载被技3', length:1, editable:true}"));
					put("psvskill04", JSONObject.parseObject("{label:'装载被技4', length:1, editable:true}"));
					put("psvskill05", JSONObject.parseObject("{label:'装载被技5', length:1, editable:true}"));
					put("psvskill06", JSONObject.parseObject("{label:'装载被技6', length:1, editable:true}"));
					put("psvskill07", JSONObject.parseObject("{label:'装载被技7', length:1, editable:true}"));
					put("psvskill08", JSONObject.parseObject("{label:'装载被技8', length:1, editable:true}"));
					put("skill1", JSONObject.parseObject("{label:'技能1', length:1, editable:true}"));
					put("skill2", JSONObject.parseObject("{label:'技能2', length:1, editable:true}"));
					put("skill3", JSONObject.parseObject("{label:'技能3', length:1, editable:true}"));
					put("skill4", JSONObject.parseObject("{label:'技能4', length:1, editable:true}"));
					put("skill5", JSONObject.parseObject("{label:'技能5', length:1, editable:true}"));
					put("skill6", JSONObject.parseObject("{label:'技能6', length:1, editable:true}"));
					put("skill7", JSONObject.parseObject("{label:'技能7', length:1, editable:true}"));
					put("skill8", JSONObject.parseObject("{label:'技能8', length:1, editable:true}"));
					put("skill9", JSONObject.parseObject("{label:'技能9', length:1, editable:true}"));
					put("skill10", JSONObject.parseObject("{label:'技能10', length:1, editable:true}"));
					put("skill11", JSONObject.parseObject("{label:'技能11', length:1, editable:true}"));
					put("skill12", JSONObject.parseObject("{label:'技能12', length:1, editable:true}"));
					put("skill13", JSONObject.parseObject("{label:'技能13', length:1, editable:true}"));
					put("skill14", JSONObject.parseObject("{label:'技能14', length:1, editable:true}"));
					put("skill15", JSONObject.parseObject("{label:'技能15', length:1, editable:true}"));
					put("skill16", JSONObject.parseObject("{label:'技能16', length:1, editable:true}"));
					put("psvskill1", JSONObject.parseObject("{label:'被技1', length:1, editable:true}"));
					put("psvskill2", JSONObject.parseObject("{label:'被技2', length:1, editable:true}"));
					put("psvskill3", JSONObject.parseObject("{label:'被技3', length:1, editable:true}"));
					put("psvskill4", JSONObject.parseObject("{label:'被技4', length:1, editable:true}"));
					put("psvskill5", JSONObject.parseObject("{label:'被技5', length:1, editable:true}"));
					put("psvskill6", JSONObject.parseObject("{label:'被技6', length:1, editable:true}"));
					put("psvskill7", JSONObject.parseObject("{label:'被技7', length:1, editable:true}"));
					put("psvskill8", JSONObject.parseObject("{label:'被技8', length:1, editable:true}"));
					put("psvskill9", JSONObject.parseObject("{label:'被技9', length:1, editable:true}"));
					put("psvskill10", JSONObject.parseObject("{label:'被技10', length:1, editable:true}"));
					put("psvskill11", JSONObject.parseObject("{label:'被技11', length:1, editable:true}"));
					put("psvskill12", JSONObject.parseObject("{label:'被技12', length:1, editable:true}"));
					put("psvskill12", JSONObject.parseObject("{label:'被技12', length:1, editable:true}"));
					put("psvskill12", JSONObject.parseObject("{label:'被技12', length:1, editable:true}"));
				}
			});
	private JSONObject settings = null;
	private JSONObject lang = null;
	private MHXSavDataFile savFile;
	private JTable dataTable = null;
	private Map<String, Map<String, Object>> dataStruct = null;
	private JLabel statusText = null;
	private JFrame owner = null;

	public CatListPanel(JSONObject settings, JSONObject lang) {
		this(null, settings, lang);
	}

	public CatListPanel(JFrame owner, JSONObject settings, JSONObject lang) {
		super();
		this.owner = owner;
		this.settings = settings;
		this.lang = lang;
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
		tableDef.putAll(DATA_COLUMNS);
		JTable table = this.createTable(tableDef);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		this.dataStruct = tableDef;
		this.dataTable = table;
	}

	public void loadDataFile(MHXSavDataFile savFile) throws IOException {
		this.savFile = savFile;
		List<Map<String, Object>> dataList = savFile.loadCatList(this.dataStruct);
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
			Map<String, Object> def = null;
			Integer len = 0;
			int pos = 0;
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
				String encoding = (String) def.get("encoding");
				savFile.writeStringData(String.valueOf(val), len, encoding);
				return;
			}
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
		String dataKey = "Cat-";
		int col = dataTable.getSelectedColumn();
		String key = dataTable.getColumnName(col);
		if (key.matches("skill\\d*|psvskill\\d*")) {
			key = key.replaceAll("\\d*", "");
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
