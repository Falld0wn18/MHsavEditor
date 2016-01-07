/**
 * 
 */
package com.zsword.modules.event;

import java.util.EventObject;

/**
 * @Description:
 * @Name CellChangeEvent
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月14日 下午4:54:29
 * @Version 1.0
 */
@SuppressWarnings("serial")
public class CellEditingEvent extends EventObject {

	private int row;
	private int column;
	private Object oldValue;
	private Object newValue;

	/**
	 * @comment
	 * @param source
	 */
	public CellEditingEvent(Object source, int row, int column, Object oldValue, Object newValue) {
		super(source);
		this.row = row;
		this.column = column;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @return the oldValue
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * @return the newValue
	 */
	public Object getNewValue() {
		return newValue;
	}
}
