/**
 * 
 */
package com.zsword.modules.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @Description:
 * @Name StringDataUtils
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月16日 下午9:25:05
 * @Version 1.0
 */
public abstract class StringDataUtils {

	public static Integer parseInt(String val, Integer defaultVal) {
		return NumberUtils.toInt(val, defaultVal);
	}

	public static Integer parseInt(String val, int radix, Integer defaultVal) {
		if (StringUtils.isEmpty(val)) {
			return defaultVal;
		}
		return Integer.parseInt(val, radix);
	}
}
