/**
 * 
 */
package com.zsword.modules.fileio.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

/**
 * @Description:
 * @Name ConfigLoadUtils
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月11日 上午10:09:52
 * @Version 1.0
 */
public abstract class ConfigIOUtils {

	private static final String DEFAULT_ENCODING = "UTF-8";

	public static JSONObject loadConfig(String filePath) throws IOException {
		return loadConfig(filePath, false);
	}

	public static JSONObject loadPropertiesText(String filePath) throws IOException {
		BufferedReader reader = null;
		JSONObject result = null;
		try {
			InputStream input = null;
			if (filePath.startsWith("file:")) {
				input = new FileInputStream(filePath.substring(5));
			} else {
				input = ConfigIOUtils.class.getResourceAsStream(filePath);
			}
			reader = new BufferedReader(new InputStreamReader(input, DEFAULT_ENCODING));

			result = new JSONObject(true);
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				String[] strs = line.split("\\s*=\\s*");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("label", strs[1]);
				result.put(strs[0], map);
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}

	public static JSONObject loadConfig(String filePath, boolean ordered) throws IOException {
		BufferedReader reader = null;
		JSONObject json = null;
		try {
			InputStream input = null;
			if (filePath.startsWith("file:")) {
				input = new FileInputStream(filePath.substring(5));
			} else {
				input = ConfigIOUtils.class.getResourceAsStream(filePath);
			}
			reader = new BufferedReader(new InputStreamReader(input, DEFAULT_ENCODING));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line.trim());
			}
			List<Feature> features = new ArrayList<Feature>();
			if (ordered) {
				features.add(Feature.OrderedField);
			}
			json = JSONObject.parseObject(buffer.toString(), features.toArray(new Feature[features.size()]));
		} catch (Exception e) {
			throw new IOException("加载配置文件出错" + filePath, e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return json;
	}

	public static JSONArray loadArrayConfig(String filePath) throws IOException {
		BufferedReader reader = null;
		JSONArray json = null;
		try {
			InputStream input = null;
			if (filePath.startsWith("file:")) {
				input = new FileInputStream(filePath.substring(5));
			} else {
				input = ConfigIOUtils.class.getResourceAsStream(filePath);
			}
			reader = new BufferedReader(new InputStreamReader(input, DEFAULT_ENCODING));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line.trim());
			}
			json = JSONArray.parseArray(buffer.toString());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return json;
	}

	public static void saveConfig(Object config, String filePath) throws IOException {
		if (config == null) {
			return;
		}
		BufferedWriter writer = null;
		try {
			if (filePath.startsWith("file:")) {
				filePath = filePath.substring(5);
			} else {
				filePath = ConfigIOUtils.class.getResource(filePath).getFile();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), DEFAULT_ENCODING));
			String jsonStr = JSONObject.toJSONString(config, true);
			writer.write(jsonStr);
			writer.flush();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static File loadRemoteConfigFile(String urlStr, String filePath) throws IOException {
		URLConnection conn = null;
		BufferedOutputStream output = null;
		File config = null;
		try {
			URL url = new URL(urlStr);
			conn = url.openConnection();
			InputStream input = conn.getInputStream();
			if (filePath.startsWith("file:")) {
				filePath = filePath.substring(5);
			} else {
				filePath = ConfigIOUtils.class.getResource(filePath).getFile();
			}
			output = new BufferedOutputStream(new FileOutputStream(filePath));
			byte[] buff = new byte[1024 * 500];
			int c = -1;
			while ((c = input.read(buff)) > 0) {
				output.write(buff, 0, c);
			}
			output.flush();
			config = new File(filePath);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
				if (conn != null) {
					((HttpURLConnection) conn).disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return config;
	}
}
