/**
 * 
 */
package com.zsword.modules.swing.utils;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * @Description:
 * @Name ComponentUtils
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月11日 上午10:40:21
 * @Version 1.0
 */
public abstract class ComponentUtils {

	public static Object getValue(JComponent com) {
		if (com == null) {
			return null;
		}
		Class<?> comType = com.getClass();
		Object val = null;
		if (JTextComponent.class.isAssignableFrom(comType)) {
			val = ((JTextComponent) com).getText();
		} else {
			throw new RuntimeException("Unsupported getValue component-" + comType);
		}
		return val;
	}
}
