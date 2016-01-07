/**
 * 
 */
package com.zsword.modules.utils.data;

import com.zsword.modules.utils.BitsUtil;

/**
 * @Description: Bit Data Calculator
 * @Name BitDataCalc
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年12月1日 上午10:17:01
 * @Version 1.0
 */
public abstract class BitDataCalc {

	private static final boolean DEBUG = false;

	public static int readBitsBytes(byte[] byteData, int length, int offset) {
		int val = BitsUtil.getInt(byteData, false);
		String str = Integer.toBinaryString(val);
		str = String.format("%" + (byteData.length * 8) + "s", str).replace(" ", "0");
		int start = str.length() - length - offset;
		int end = str.length() - offset;
		str = str.substring(start, end);
		return Integer.parseInt(str, 2);
	}

	public static void writeBitsBytes(byte[] byteData, int bitsVal, int length, int offset) {
		int idx = 0;
		String str = String.format("%18s", Integer.toBinaryString(bitsVal)).replace(" ", "0");
		int start = length - 8 + offset;
		int end = length;
		String valStr = str.substring(start, end);
		int data = byteData[idx] & 0x7F;
		String dataStr = String.format("%8s", Integer.toBinaryString(data)).replace(" ", "0");
		dataStr = valStr + dataStr.substring(8 - offset, 8);
		byteData[idx++] = (byte) Integer.parseInt(dataStr, 2);
		// System.out.printf("%X,", byteData[idx - 1]);

		offset = 8;
		end = start;
		start = start - offset;
		valStr = str.substring(start, end);
		byteData[idx++] = (byte) Integer.parseInt(valStr, 2);
		// System.out.printf("%X,", byteData[idx - 1]);

		offset = start;
		end = start;
		start = 0;
		valStr = str.substring(start, end);
		data = byteData[idx] & 0xFF;
		dataStr = String.format("%8s", Integer.toBinaryString(data)).replace(" ", "0");
		dataStr = dataStr.substring(0, 8 - offset) + valStr;
		byteData[idx++] = (byte) Integer.parseInt(dataStr, 2);
		// System.out.printf("%X\n", byteData[idx - 1]);
		/// System.out.println(idx + ", " + start + " - " + valStr + " , " +
		// str);
	}
}
