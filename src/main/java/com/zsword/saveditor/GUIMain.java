/**
 * 
 */
package com.zsword.saveditor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.zsword.mh4geditor.MH4SavEditor;
import com.zsword.mhxeditor.MHXSavEditor;

/**
 * @Description:
 * @Name GUIMain
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年12月1日 上午11:04:50
 * @Version 1.0
 */
public class GUIMain {

	enum GameVersion {
		MH4G, MHX
	}

	public static void main(String[] args) {
		GameVersion[] versions = GameVersion.values();
		Object result = null;
		// result = GameVersion.MHX;
		if (result == null) {
			if ((result = JOptionPane.showInputDialog(null, "请选择游戏版本", "选择游戏", JOptionPane.QUESTION_MESSAGE, null,
					versions, GameVersion.MHX)) == null) {
				return;
			}
		}
		GameVersion gameType = (GameVersion) result;
		JFrame editor = null;
		switch (gameType) {
		case MH4G:
			editor = new MH4SavEditor();
			break;
		case MHX:
			editor = new MHXSavEditor();
		default:
			break;
		}
		editor.setVisible(true);
	}
}
