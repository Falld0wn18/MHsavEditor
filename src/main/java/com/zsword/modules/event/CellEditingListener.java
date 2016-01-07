/**
 * 
 */
package com.zsword.modules.event;

import java.util.EventListener;

/**
 * @Description:
 * @Name CellEditingListener
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月14日 下午4:57:36
 * @Version 1.0
 */
public interface CellEditingListener extends EventListener {

	public void valueChanged(CellEditingEvent event);
}
