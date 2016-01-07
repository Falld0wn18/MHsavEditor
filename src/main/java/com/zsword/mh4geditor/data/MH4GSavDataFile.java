/**
 * 
 */
package com.zsword.mh4geditor.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zsword.mh4geditor.data.SavedataCipher.GameVersion;
import com.zsword.modules.io.ByteDataFile;

/**
 * @Description: Monster Hunter 4G save data file
 * @Name SavDataFile
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月19日 下午7:21:23
 * @Version 1.0
 */
public class MH4GSavDataFile extends ByteDataFile {

	private static final String DATATEXT_ENCODING = "Unicode";
	private File srcFile = null;
	private int dataOffset = 0;

	/**
	 * @comment
	 * @param file
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public MH4GSavDataFile(File file, String mode) throws IOException {
		super(decryptFile(file), mode);
		this.srcFile = file;
		this.dataOffset = file.length() % 16 == 0 ? 8 : 0;
	}

	public MH4GSavDataFile(File file) throws IOException {
		this(file, "rw");
	}

	/**
	 * @comment Load Character info
	 * @param struct
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> loadCharacter(Map<String, Map<String, Object>> struct) throws IOException {
		Map<String, Object> info = new HashMap<String, Object>();
		long pos = this.dataOffset;
		this.seek(pos);
		for (String key : struct.keySet()) {
			Map<String, Object> def = struct.get(key);
			Integer len = (Integer) def.get("length");
			if (len == null) {
				continue;
			}
			if (key.startsWith("unknow")) {
				this.skipBytes(len);
				continue;
			}
			String type = (String) def.get("type");
			if ("string".equals(type)) {
				String str = this.readStringData(len);
				info.put(key, str);
			} else if ("bytes".equals(type)) {
				int val = this.readBytesAsInt(len);
				String str = String.format("%0" + len + "X", val);
				info.put(key, str);
			} else {
				Object val = this.readBytesAsInt(len);
				info.put(key, val);
			}
		}
		return info;
	}

	/**
	 * @comment Load carry equipments
	 * @param dataStruct
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> loadCarryEquipment(Map<String, Map<String, Object>> dataStruct)
			throws IOException {
		long pos = 0x40 + this.dataOffset;
		this.seek(pos);
		return this.loadEquipmentList(7, dataStruct);
	}

	/**
	 * @comment Load carry items
	 * @param dataStruct
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> loadCarryItem(Map<String, Map<String, Object>> dataStruct) throws IOException {
		long pos = 0xCB14 + this.dataOffset;
		this.seek(pos);
		return this.loadItemList(24, dataStruct);
	}

	/**
	 * @comment Load equipment box
	 * @param dataStruct
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> loadEquipmentBox(Map<String, Map<String, Object>> dataStruct) throws IOException {
		long pos = 0x173E + this.dataOffset;
		this.seek(pos);
		return this.loadEquipmentList(480, dataStruct);
	}

	/**
	 * @comment Load item box
	 * @param dataStruct
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> loadItemBox(Map<String, Map<String, Object>> dataStruct) throws IOException {
		long pos = 0x15E + this.dataOffset;
		this.seek(pos);
		return this.loadItemList(1400, dataStruct);
	}

	/**
	 * @comment Load item list
	 * @param count
	 * @param struct
	 * @return
	 * @throws IOException
	 */
	private List<Map<String, Object>> loadItemList(int count, Map<String, Map<String, Object>> struct)
			throws IOException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int pos = (int) this.getFilePointer();
		int size = 4;
		for (int i = 0; i < count; i++) {
			int offset = pos + i * size;
			int num = 0;
			Map<String, Object> data = new HashMap<String, Object>();
			for (String key : struct.keySet()) {
				Map<String, Object> def = struct.get(key);
				if ("offset".equals(key)) {
					data.put("offset", offset);
					continue;
				}
				Integer len = (Integer) def.get("length");
				if (len == null) {
					continue;
				}
				num += len;
				if (key.startsWith("unknow")) {
					this.skipBytes(len);
					continue;
				}
				Object val = this.readBytesAsInt(len);
				if (len != null) {
					val = String.format("%0" + (len * 2) + 'X', val);
				}
				data.put(key, val);
			}
			if (num < size) {
				this.skipBytes(size - num);
			}
			list.add(data);
		}
		return list;
	}

	/**
	 * @comment Load equipment list
	 * @param count
	 * @param struct
	 * @return
	 * @throws IOException
	 */
	private List<Map<String, Object>> loadEquipmentList(int count, Map<String, Map<String, Object>> struct)
			throws IOException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int pos = (int) this.getFilePointer();
		int size = 28;
		for (int i = 0; i < count; i++) {
			int offset = pos + i * size;
			int num = 0;
			Map<String, Object> data = new HashMap<String, Object>();
			for (String key : struct.keySet()) {
				Map<String, Object> def = struct.get(key);
				if ("offset".equals(key)) {
					data.put("offset", offset);
					continue;
				}
				Integer len = (Integer) def.get("length");
				if (len == null) {
					continue;
				}
				num += len;
				if (key.startsWith("unknow")) {
					this.skipBytes(len);
					continue;
				}
				Object val = this.readBytesAsInt(len);
				if (len != null) {
					val = String.format("%0" + (len * 2) + 'X', val);
				}
				data.put(key, val);
			}
			if (num < size) {
				this.skipBytes(size - num);
			}
			list.add(data);
		}
		return list;
	}

	/**
	 * @comment Read string data
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public String readStringData(int len) throws IOException {
		return this.readBytesAsString(len, DATATEXT_ENCODING, true);
	}

	/**
	 * @comment Write string data
	 * @param str
	 * @throws IOException
	 */
	public void writeStringData(String str) throws IOException {
		this.writeString(str, DATATEXT_ENCODING, true);
	}

	private static SavedataCipher cipher = new SavedataCipher(GameVersion.MH4G_JP);

	private static File decryptFile(File file) throws IOException {
		if (file.length() % 16 == 0) {
			File outFile = new File(file.getParentFile(), file.getName() + ".temp");
			file = cipher.decryptFile(file, outFile);
		}
		return file;
	}

	private static void encryptFile(File file) throws IOException {
		if (file.length() % 16 == 0) {
			File workFile = new File(file.getParentFile(), file.getName() + ".temp");
			cipher.encryptFile(workFile, file);
			if (workFile.exists()) {
				// workFile.delete();
			}
		}
	}

	/**
	 * @return the dataOffset
	 */
	public int getDataOffset() {
		return dataOffset;
	}

	@Override
	public void close() throws IOException {
		super.close();
		encryptFile(srcFile);
	}
}
