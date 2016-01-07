/**
 * 
 */
package com.zsword.modules.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.zsword.modules.event.CellEditingEvent;
import com.zsword.modules.event.CellEditingListener;
import com.zsword.modules.swing.ComponentCellEditor;

/**
 * @Description:
 * @Name ConfigurableTable
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月14日 下午3:19:14
 * @Version 1.0
 */
public class ConfigurableTable extends JTable {
	private EventListenerList cellEditingListeners = new EventListenerList();
	private Map<String, Map<String, Object>> config;
	private List<Integer> editableColumns;

	public ConfigurableTable(Map<String, Map<String, Object>> config) {
		super(new Vector<Object>(), buildColumnNames(config.keySet()));
		this.config = config;
		this.initItems();
	}

	private static Vector<String> buildColumnNames(Collection<String> list) {
		Vector<String> vec = new Vector<String>();
		if (!list.contains("no")) {
			vec.add("no");
		}
		vec.addAll(list);
		return vec;
	}

	protected void initItems() {
		this.editableColumns = new ArrayList<Integer>();
		TableColumnModel colModel = this.getColumnModel();
		for (int i = 0; i < this.getColumnCount(); i++) {
			TableColumn column = colModel.getColumn(i);
			String key = this.getColumnName(i);
			Map<String, Object> obj = config.get(key);
			if (obj == null) {
				if ("no".equals(key)) {
					column.setHeaderValue("序号");
					column.setPreferredWidth(20);
				}
				continue;
			}
			Integer len = (Integer) obj.get("length");
			column.setIdentifier(key);
			String name = (String) obj.get("label");
			name = name == null ? key : name;
			column.setHeaderValue(name);
			Integer width = (Integer) obj.get("width");
			if (width != null) {
				column.setPreferredWidth(width);
			}
			String hidden = String.valueOf(obj.get("hidden"));
			if (Boolean.TRUE.toString().equals(hidden)) {
				column.setMinWidth(0);
				column.setMaxWidth(0);
				column.setResizable(false);
			}
			String editable = String.valueOf(obj.get("editable"));
			if (Boolean.TRUE.toString().equalsIgnoreCase(editable)) {
				editableColumns.add(i);
			}
			String type = (String) obj.get("type");
			if (type != null) {
				if ("int".equalsIgnoreCase(type)) {
					Integer maxVal = (Integer) obj.get("max");
					maxVal = maxVal == null ? Integer.MAX_VALUE : maxVal;
					Integer minVal = (Integer) obj.get("min");
					minVal = minVal == null ? Integer.MIN_VALUE : minVal;
					SpinnerModel spinnerModel = new SpinnerNumberModel(minVal > 0 ? minVal : 0, minVal.intValue(),
							maxVal.intValue(), 1);
					JSpinner spinner = new JSpinner(spinnerModel);
					column.setCellEditor(new ComponentCellEditor(spinner));
				}
			}
			String renderer = (String) obj.get("renderer");
			if (renderer != null) {
				if ("hex".equalsIgnoreCase(renderer)) {
					String format = (String) obj.get("format");
					if (format == null) {
						if (len != null) {
							format = "%0" + (len * 2) + 'X';
						}
					}
					column.setCellRenderer(new HexCellRender(format));
				}
			}
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return editableColumns.contains(column);
	}

	@Override
	public void editingStopped(ChangeEvent evt) {
		int row = editingRow;
		int col = editingColumn;
		Object oldValue = getValueAt(row, col);
		super.editingStopped(evt);
		Object newValue = getValueAt(row, col);
		if (!String.valueOf(newValue).equals(String.valueOf(oldValue))) {
			CellEditingListener[] listeners = this.cellEditingListeners.getListeners(CellEditingListener.class);
			if (listeners.length > 0) {
				CellEditingEvent newEvt = new CellEditingEvent(evt.getSource(), row, col, oldValue, newValue);
				for (CellEditingListener l : listeners) {
					l.valueChanged(newEvt);
				}
			}
		}
	}

	public void addCellEditingListener(CellEditingListener l) {
		this.cellEditingListeners.add(CellEditingListener.class, l);
	}

	private class HexCellRender extends DefaultTableCellRenderer {
		private String format;

		public HexCellRender(String format) {
			super();
			this.format = format == null ? "%#X" : format;
		}

		@Override
		protected void setValue(Object value) {
			if (value instanceof Number) {
				value = String.format(format, value);
			}
			super.setValue(value);
		}
	}
}
