/**
 * 
 */
package com.zsword.mhxeditor.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.zsword.modules.io.ByteDataFile;
import com.zsword.modules.utils.data.BitDataCalc;

/**
 * @Description: Monster Hunter X save data file
 * @Name MHXSavDataFile
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年11月30日 下午3:59:49
 * @Version 1.0
 */
public class MHXSavDataFile extends ByteDataFile {

	private static final String DATATEXT_ENCODING = "UTF-8";
	private Map<String, Object> headerData = null;
	private List<Map<String, Object>> characterOffsetList = null;
	private Map<String, Map<String, Object>> character = null;

	/**
	 * @comment Constructor
	 * @param file
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public MHXSavDataFile(File file, String mode) throws FileNotFoundException {
		super(file, mode);
	}

	public MHXSavDataFile(File file) throws FileNotFoundException {
		this(file, "rw");
	}

	/**
	 * @comment Load header data
	 * @param struct
	 * @throws IOException
	 */
	public void loadHeader(JSONObject struct) throws IOException {
		Map<String, Object> header = new HashMap<String, Object>();
		this.seek(0);
		Iterator<String> it = struct.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Map<String, Object> map = struct.getJSONObject(key);
			String info = (String) map.get("info");
			long offset = Long.parseLong(key, 16);
			int size = (Integer) map.get("size");
			this.seek(offset);
			header.put(info, this.readBytesAsInt(size));
		}
		this.headerData = header;
	}

	/**
	 * @comment Load character offset data
	 * @param idx
	 * @param struct
	 * @return
	 * @throws IOException
	 */
	public Map<String, Map<String, Object>> loadDataOffset(int idx, JSONObject struct) throws IOException {
		int baseAddr = (Integer) headerData.get("Offset to character" + idx);
		this.seek(baseAddr);
		Map<String, Map<String, Object>> infoData = new LinkedHashMap<String, Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("info", "Data offset");
		data.put("offset", baseAddr);
		infoData.put("offset", data);
		Iterator<String> it = struct.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Map<String, Object> map = struct.getJSONObject(key);
			String info = (String) map.get("info");
			if (info.toLowerCase().startsWith("unknow")) {
				continue;
			}
			long offset = Long.parseLong(key, 16);
			this.seek(offset);
			data = new HashMap<String, Object>();
			data.putAll(map);
			data.put("offset", baseAddr + offset);
			infoData.put(info, data);
		}
		this.character = infoData;
		return character;
	}

	/**
	 * @comment Load character info
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> loadCharacter() throws IOException {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Iterator<String> it = character.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if ("offset".equals(key)) {
				continue;
			}
			Map<String, Object> map = character.get(key);
			long offset = (Long) map.get("offset");
			int size = (Integer) map.get("size");
			if (size > 32) {
				continue;
			}
			this.seek(offset);
			Object val = null;
			if (size <= 4) {
				val = this.readBytesAsInt(size);
			} else if (size <= 8) {
				val = this.readBytesAsLong(size);
			} else {
				String encoding = (String) map.get("encoding");
				val = this.readStringData(size, encoding);
			}
			data.put(key, val);
		}
		return data;
	}

	public void putCharacterInfo(String attrName, Object value) throws IOException {
		Map<String, Object> map = character.get(attrName);
		long offset = (Long) map.get("offset");
		this.seek(offset);
		int size = (Integer) map.get("size");
		String type = (String) map.get("type");
		if ("string".equals(type)) {
			String str = (String) value;
			String encoding = (String) map.get("encoding");
			this.writeStringData(str, size, encoding);
			return;
		} else {
			if (String.class.isAssignableFrom(value.getClass())) {
				if ("bytes".equals(type)) {
					value = Integer.parseInt((String) value, 16);
				} else {
					value = Integer.parseInt((String) value);
				}
			}
		}
		this.writeInt((Integer) value, size);
	}

	/**
	 * @comment Load equipment box
	 * @param struct
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> loadEquipmentBox(Map<String, Map<String, Object>> struct) throws IOException {
		long offset = (Long) character.get("Equipment box").get("offset");
		int total = 1400;
		this.seek(offset);
		return this.loadEquipmentList(struct, total);
	}

	/**
	 * @comment Load equipment list
	 * @param count
	 * @param struct
	 * @return
	 * @throws IOException
	 */
	private List<Map<String, Object>> loadEquipmentList(Map<String, Map<String, Object>> struct, int count)
			throws IOException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int pos = (int) this.getFilePointer();
		int size = 36;
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
	 * @comment Load item box
	 * @param struct
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> loadItemBox(Map<String, Map<String, Object>> struct) throws IOException {
		long offset = (Long) character.get("Item box").get("offset");
		int total = 1400;
		this.seek(offset);
		return this.loadItemList(struct, total);
	}

	/**
	 * @comment Load carry items
	 * @param struct
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> loadCarryItem(Map<String, Map<String, Object>> struct) throws IOException {
		long offset = (Long) character.get("Item slots").get("offset");
		int total = 24;
		this.seek(offset);
		return this.loadItemList(struct, total);
	}

	private List<Map<String, Object>> loadItemList(Map<String, Map<String, Object>> struct, int total)
			throws IOException {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		byte[] data = new byte[total * 18 / 8];
		this.read(data);

		int byteIdx = 0;
		int bitOffset = 0;
		int val = 0;
		int itemId = 0;
		int itemCount = 0;
		for (int i = 0; i < total; i++) {
			byteIdx = i * 18 / 8;
			bitOffset = i * 18 % 8;
			byte[] buff = new byte[3];
			for (int n = 0; n < buff.length; n++) {
				buff[n] = data[byteIdx++];
			}
			val = BitDataCalc.readBitsBytes(buff, 18, bitOffset);
			String str = String.format("%18s", Integer.toBinaryString(val)).replace(" ", "0");
			itemId = Integer.parseInt(str.substring(7, 18), 2);
			itemCount = Integer.parseInt(str.substring(0, 7), 2);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("no", i + 1);
			map.put("item", itemId);
			map.put("count", itemCount);
			dataList.add(map);
		}
		return dataList;
	}

	/**
	 * @comment Write item data
	 * @param idx
	 * @param itemId
	 * @param count
	 * @throws IOException
	 */
	public void writeBoxItem(int idx, int itemId, int count) throws IOException {
		Map<String, Object> infoData = character.get("Item box");
		long offset = (Long) infoData.get("offset");
		this.writeItemData(offset, idx, itemId, count);
	}

	public void writeCarryItem(int idx, int itemId, int count) throws IOException {
		Map<String, Object> infoData = character.get("Item slots");
		long offset = (Long) infoData.get("offset");
		this.writeItemData(offset, idx, itemId, count);
	}

	private void writeItemData(long offset, int idx, int itemId, int count) throws IOException {
		int byteIdx = idx * 18 / 8;
		int bitOffset = idx * 18 % 8;
		offset = offset + byteIdx;
		this.seek(offset);
		byte[] data = new byte[3];
		this.read(data);
		int val = (count << 11) + itemId;
		BitDataCalc.writeBitsBytes(data, val, 18, bitOffset);
		this.seek(offset);
		this.write(data);
	}

	public List<Map<String, Object>> loadCatList(Map<String, Map<String, Object>> struct) throws IOException {
		Map<String, Object> infoData = character.get("Cat list");
		long dataOffset = (Long) infoData.get("offset");
		int count = 50;
		int size = 0x13F;
		this.seek(dataOffset);
		int pos = (int) this.getFilePointer();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
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
				Object val = null;
				String type = (String) def.get("type");
				if ("string".equals(type)) {
					String encoding = (String) def.get("encoding");
					val = this.readStringData(len, encoding);
				} else {
					val = this.readBytesAsInt(len);
					if (len != null) {
						val = String.format("%0" + (len * 2) + 'X', val);
					}
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
	public String readStringData(int len, String encoding) throws IOException {
		encoding = encoding == null ? DATATEXT_ENCODING : encoding;
		return this.readBytesAsString(len, DATATEXT_ENCODING, false);
	}

	/**
	 * @comment Write string data
	 * @param str
	 * @throws IOException
	 */
	public void writeStringData(String str, int len, String encoding) throws IOException {
		encoding = encoding == null ? DATATEXT_ENCODING : encoding;
		int count = this.writeString(str, encoding, false);
		int r = len - count - 1;
		if (r > 0) {
			byte[] emptyData = new byte[r];
			this.write(emptyData);
		}
	}
}