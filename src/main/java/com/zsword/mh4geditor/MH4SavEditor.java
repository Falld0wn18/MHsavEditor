package com.zsword.mh4geditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zsword.mh4geditor.SavDataEditPanel.SavDataType;
import com.zsword.mh4geditor.data.MH4GSavDataFile;
import com.zsword.modules.fileio.utils.ConfigIOUtils;
import com.zsword.modules.swing.FrameBase;
import com.zsword.modules.swing.utils.ComponentUtils;
import com.zsword.saveditor.MHsavEditSupport;
import com.zsword.saveditor.gui.ValueCodesDialog;

/**
 * @Description:
 * @Name ClientGUI
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月10日 下午2:50:42
 * @Version 1.0
 */
@SuppressWarnings("serial")
public class MH4SavEditor extends FrameBase implements MHsavEditSupport {

	private enum GUIAction {
		OpenSav, SaveSav, Exit, SaveSettings, UpdateRomDB, ConnectToServ, DisconnectToServ, EditMemData
	}

	private static final String KEY_ATTR = "key";
	private static final String VALUE_ATTR = "value";
	private static final String LABEL_ATTR = "label";
	private static final String COMMENT_ATTR = "comment";
	private static final String APP_NAME = "MH4G Sav Editor";
	private static final String _DEVPATH = "file://D:/EclipseProjs/zsword/javaee/desktopApps/gameTools/MHsavEditor/SavEditor";
	private static final String LANG_PATH = "/lang_zh_CN.json";
	private static final String SETTINGS_PATH = "/settings.json";
	private static final String EQUIPT_LIST_PATH = "/EquiptList.json";
	private static final String EQUIPT_TYPE_PATH = "/EquiptType.json";
	private static final String EQUIPT_NATURE_PATH = "/EquiptNature.json";
	private static final String EQUIPT_DECORATION_PATH = "/EquiptDecoration.json";
	private static final String ITEM_LIST_PATH = "/ItemList.json";
	private static final String STONE_SKILL_PATH = "/StoneSkill.json";
	private static final String INSECT_TYPE_PATH = "/InsectType.json";

	private static final boolean _EnableMemEdit = true;
	private JSONObject appLang = null;
	private JSONObject appSettings = null;
	private JSONObject equiptListCodes = null;
	private JSONObject equiptTypeCodes = null;
	private JSONObject equiptNatureCodes = null;
	private JSONObject equiptDecorationCodes = null;
	private JSONObject itemListCodes = null;
	private JSONObject stoneSkillCodes = null;
	private JSONObject insectTypeCodes = null;
	private MH4GSavDataFile savDataFile = null;
	private JTabbedPane mainTabPane;
	private PersonInfoPanel personInfoPanel;
	private ValueCodesDialog valueCodesDlg = null;
	private SavDataEditPanel personEquiptPanel = null;
	private SavDataEditPanel personItemPanel = null;
	private SavDataEditPanel equiptBoxPanel = null;
	private SavDataEditPanel itemBoxPanel = null;
	private Map<String, JComponent> settingsFieldMap = new HashMap<String, JComponent>();
	private Map<String, JButton> actButtonMap = new HashMap<String, JButton>();
	private JLabel statusLabel = null;
	private final String settingsPath;
	private final String languagePath;

	private static String loadDataFilePath(String path) {
		if (1 > _State) {
			path = _DEVPATH + path;
		}
		return path;
	}

	public MH4SavEditor() {
		super();
		try {
			LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel(lookAndFeels[1].getClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String languagePath = loadDataFilePath(LANG_PATH);
		String settingsPath = loadDataFilePath(SETTINGS_PATH);
		this.languagePath = languagePath;
		this.settingsPath = settingsPath;
		this.loadLanguage();
		String cfgPath = null;
		try {
			cfgPath = settingsPath;
			this.appSettings = ConfigIOUtils.loadConfig(cfgPath);
			cfgPath = loadDataFilePath("/data/MH4G" + EQUIPT_LIST_PATH);
			this.equiptListCodes = ConfigIOUtils.loadConfig(cfgPath, true);
			cfgPath = loadDataFilePath("/data/MH4G" + EQUIPT_TYPE_PATH);
			this.equiptTypeCodes = ConfigIOUtils.loadConfig(cfgPath, true);
			cfgPath = loadDataFilePath("/data/MH4G" + EQUIPT_NATURE_PATH);
			this.equiptNatureCodes = ConfigIOUtils.loadConfig(cfgPath, true);
			cfgPath = loadDataFilePath("/data/MH4G" + EQUIPT_DECORATION_PATH);
			this.equiptDecorationCodes = ConfigIOUtils.loadConfig(cfgPath, true);
			cfgPath = loadDataFilePath("/data/MH4G" + ITEM_LIST_PATH);
			this.itemListCodes = ConfigIOUtils.loadConfig(cfgPath, true);
			cfgPath = loadDataFilePath("/data/MH4G" + STONE_SKILL_PATH);
			this.stoneSkillCodes = ConfigIOUtils.loadConfig(cfgPath, true);
			cfgPath = loadDataFilePath("/data/MH4G" + INSECT_TYPE_PATH);
			this.insectTypeCodes = ConfigIOUtils.loadConfig(cfgPath, true);
		} catch (Exception e) {
			handleError(e, String.format("加载配置文件[%s]出错-", cfgPath));
		}
		this.setTitle(APP_NAME + " ver " + APP_VERSION);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				MH4SavEditor.this.onDestroy();
			}
		});
		this.initItems();
		if (-1 == _State) {
			this.loadSavFile(new File("D:/Games/3DS/MHX/mhefqs/examples/n3ds/user1"));
		}
	}

	private void loadLanguage() {
		String filePath = this.languagePath;
		try {
			this.appLang = ConfigIOUtils.loadConfig(filePath);
		} catch (IOException e) {
			handleError(e, String.format("加载配置文件[%s]出错", filePath));
		}
	}

	protected void initItems() {
		Dimension size = new Dimension(1200, 600);
		this.setPreferredSize(size);
		this.setJMenuBar(this.initMenubar());

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		GUIAction[] actions = { GUIAction.OpenSav, GUIAction.Exit };
		for (GUIAction act : actions) {
			String actName = act.name();
			JSONObject lang = appLang.getJSONObject(actName);
			JButton btn = new JButton(lang.getString(LABEL_ATTR));
			btn.setActionCommand(actName);
			btn.setToolTipText(lang.getString(COMMENT_ATTR));
			btn.addActionListener(this);
			toolbar.add(btn);
			actButtonMap.put(actName, btn);
		}
		add(toolbar, BorderLayout.NORTH);

		JTabbedPane tabPane = new JTabbedPane();
		PersonInfoPanel infoPanel = new PersonInfoPanel();
		tabPane.add("角色信息", infoPanel);
		this.personInfoPanel = infoPanel;
		SavDataEditPanel dataEditPanel = initSavEditPanel(SavDataType.PersonEquipt);
		tabPane.add("随身上装备", dataEditPanel);
		this.personEquiptPanel = dataEditPanel;
		dataEditPanel = initSavEditPanel(SavDataType.PersonItem);
		tabPane.add("随身道具", dataEditPanel);
		this.personItemPanel = dataEditPanel;
		dataEditPanel = initSavEditPanel(SavDataType.EquiptBox);
		tabPane.add("装备箱", dataEditPanel);
		this.equiptBoxPanel = dataEditPanel;
		dataEditPanel = initSavEditPanel(SavDataType.ItemBox);
		tabPane.add("道具箱", dataEditPanel);
		this.itemBoxPanel = dataEditPanel;
		tabPane.setVisible(false);
		add(tabPane);
		this.mainTabPane = tabPane;

		JToolBar statusBar = new JToolBar();
		statusBar.setFloatable(false);
		JLabel statusLabel = new JLabel("未载入");
		statusBar.add(statusLabel);
		add(statusBar, BorderLayout.SOUTH);
		this.statusLabel = statusLabel;
		this.pack();
		this.center();
	}

	private JMenuBar initMenubar() {
		String labelFmt = "%s(%s)";
		JMenuBar menubar = new JMenuBar();
		String key = "File";
		Map<String, Object> config = appLang.getJSONObject(key);
		char keychar = key.charAt(0);
		JMenu menu = new JMenu(String.format(labelFmt, config.get(LABEL_ATTR), keychar));
		menu.setMnemonic(Character.toLowerCase(keychar));
		menu.setToolTipText((String) config.get(COMMENT_ATTR));
		JMenuItem item = null;
		GUIAction[] actions = { GUIAction.OpenSav, GUIAction.Exit };
		for (GUIAction act : actions) {
			String actName = act.name();
			config = appLang.getJSONObject(actName);
			keychar = actName.charAt(0);
			item = new JMenuItem(String.format(labelFmt, config.get(LABEL_ATTR), keychar));
			item.setMnemonic(Character.toLowerCase(keychar));
			item.setActionCommand(actName);
			item.setToolTipText((String) config.get(COMMENT_ATTR));
			item.addActionListener(this);
			menu.add(item);
		}
		menubar.add(menu);
		return menubar;
	}

	private SavDataEditPanel initSavEditPanel(SavDataType type) {
		SavDataEditPanel panel = new SavDataEditPanel(appSettings, appLang, type);
		final SavDataEditPanel tablePanel = panel;
		panel.addTableSelectionListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (MouseEvent.BUTTON1 == e.getButton()) {
					String codeKey = tablePanel.getSelectedCellValueCodeKey();
					showValueCodesPanel(codeKey);
				}
			}
		});
		return panel;
	}

	private String valueCodeType = null;

	private void showValueCodesPanel(String type) {
		if (type.equals(this.valueCodeType)) {
			return;
		}
		JSONObject codes = null;
		if ("equiptId".equals(type)) {
			codes = this.equiptListCodes;
		} else if ("Equipt-type".equals(type)) {
			codes = this.equiptTypeCodes;
		} else if ("Equipt-natureType".equals(type)) {
			codes = this.equiptNatureCodes;
		} else if ("Equipt-stoneSkill".equals(type)) {
			codes = this.stoneSkillCodes;
		} else if ("Equipt-decoration".equals(type)) {
			codes = this.equiptDecorationCodes;
		} else if ("Equipt-insectType".equals(type)) {
			codes = this.insectTypeCodes;
		} else if ("item".equals(type)) {
			codes = this.itemListCodes;
		}
		this.valueCodeType = type;
		if (codes == null) {
			if (this.valueCodesDlg != null) {
				valueCodesDlg.setVisible(false);
			}
			return;
		}
		if (this.valueCodesDlg == null) {
			this.valueCodesDlg = new ValueCodesDialog();
		}
		valueCodesDlg.loadCodes(codes);
		valueCodesDlg.setVisible(true);
	}

	protected void openSav() {
		String workDir = appSettings.getString("savFileDir");
		File savFile = this.showOpenFileDialog(workDir);
		if (savFile == null) {
			return;
		}
		workDir = savFile.getParentFile().getPath();
		appSettings.put("savFileDir", workDir);
		loadSavFile(savFile);
		try {
		} catch (Exception e) {
			handleError(e, "加载存档文件出错-");
		}
	}

	protected void loadSavFile(File savFile) {
		try {
			if (this.savDataFile != null) {
				this.savDataFile.close();
			}
			MH4GSavDataFile dataFile = new MH4GSavDataFile(savFile);
			if (this.personInfoPanel != null) {
				personInfoPanel.loadDataFile(dataFile);
			}
			if (this.personEquiptPanel != null) {
				personEquiptPanel.loadDataFile(dataFile);
			}
			if (this.personItemPanel != null) {
				personItemPanel.loadDataFile(dataFile);
			}
			if (this.equiptBoxPanel != null) {
				equiptBoxPanel.loadDataFile(dataFile);
			}
			if (this.itemBoxPanel != null) {
				itemBoxPanel.loadDataFile(dataFile);
			}
			this.savDataFile = dataFile;
			this.mainTabPane.setSelectedIndex(0);
			this.mainTabPane.setVisible(true);
			setStatusText("存档文件:" + savFile.getPath());
		} catch (Exception e) {
			handleError(e, "读取存档文件出错-");
		}
	}

	protected void saveSavAs() {
		String workDir = appSettings.getString("savFileDir");
		File saveFile = this.showSaveFileDialog(workDir);
		if (saveFile == null) {
			return;
		}
		workDir = saveFile.getParentFile().getPath();
		appSettings.put("savFileDir", workDir);
		try {
			savDataFile.writeToFile(saveFile, 0, savDataFile.length());
			showMessage("成功保存存档文件为" + saveFile.getPath());
		} catch (Exception e) {
			handleError(e, "保存配置文件出错-");
		}
	}

	protected Object getSettingValue(String fieldName) {
		JComponent field = settingsFieldMap.get(fieldName);
		Object value = ComponentUtils.getValue(field);
		return value;
	}

	protected void setStatusText(String msg) {
		statusLabel.setText(msg);
	}

	protected void exit() {
		this.onDestroy();
		this.dispose();
		System.exit(0);
	}

	protected void onDestroy() {
		try {
			if (savDataFile != null) {
				savDataFile.close();
			}
			ConfigIOUtils.saveConfig(appSettings, this.settingsPath);
		} catch (IOException e) {
			handleError(e, "保存配置文件出错-");
		}
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

	protected static void textToJson() {
		File file = new File(_DEVPATH.substring(7) + "/data" + ITEM_LIST_PATH);
		String line = null;
		try {
			JSONObject json = new JSONObject(true);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				line = line.toUpperCase().trim().replaceAll("\\s+", ":");
				String[] strs = line.split(":");
				JSONObject data = new JSONObject();
				if (strs.length < 2) {
					System.out.println(line);
				}
				if (strs.length > 3) {
					data.put("label", strs[1]);
					data.put("comment", strs[3]);
					json.put(strs[2], data);
				}
			}
			reader.close();
			System.out.println(JSONObject.toJSONString(json, true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void csvToJson() {
		File file = new File(_DEVPATH.substring(7) + "/data" + EQUIPT_LIST_PATH);
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			int row = 0;
			List<String> typeList = null;
			List<String> typeCodes = null;
			Map<String, List<Map<String, Object>>> dataMap = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				row++;
				String[] strs = line.split(",");
				if (row == 1) {
					typeList = new ArrayList<String>();
					dataMap = new LinkedHashMap<String, List<Map<String, Object>>>();
					for (int i = 1; i < strs.length; i++) {
						String str = strs[i].trim();
						typeList.add(str);
						dataMap.put(str, new ArrayList<Map<String, Object>>());
					}
					continue;
				} else if (row == 2) {
					typeCodes = new ArrayList<String>();
					for (int i = 1; i < strs.length; i++) {
						String str = strs[i].trim();
						typeCodes.add(str);
					}
					continue;
				}
				String idCode = strs[0].trim();
				for (int i = 1; i < strs.length; i++) {
					Map<String, Object> data = new JSONObject();
					data.put("id", "(" + typeCodes.get(i - 1) + ")" + idCode);
					data.put("label", strs[i].trim());
					String type = typeList.get(i - 1);
					data.put("type", type);
					List<Map<String, Object>> dataList = dataMap.get(type);
					dataList.add(data);
				}
			}
			reader.close();
			JSONObject result = new JSONObject(true);
			for (int i = 0; i < typeList.size(); i++) {
				String type = typeList.get(i);
				List<Map<String, Object>> dataList = dataMap.get(type);
				for (int n = 0; n < dataList.size(); n++) {
					Map<String, Object> data = dataList.get(n);
					String id = (String) data.get("id");
					if (result.containsKey(id)) {
						System.out.println(id);
					}
					data.remove("id");
					result.put(id, data);
				}
			}
			JSONObject.writeJSONStringTo(result, new FileWriter("D:/Equipt.json"), SerializerFeature.PrettyFormat);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
