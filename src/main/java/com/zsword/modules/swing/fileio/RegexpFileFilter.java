/**
 * 
 */
package com.zsword.modules.swing.fileio;

import java.io.File;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileFilter;

/**
 * @Description:
 * @Name RegexpFileFilter
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年11月30日 下午8:45:28
 * @Version 1.0
 */
public class RegexpFileFilter extends FileFilter {

	private Pattern regexp;
	private String description;

	public RegexpFileFilter(String regexp, String description) {
		this.regexp = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		this.description = description;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String fname = f.getName();
		return regexp.matcher(fname).matches();
	}

	@Override
	public String getDescription() {
		return this.description;
	}

}
