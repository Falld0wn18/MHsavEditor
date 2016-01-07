/**
 * 
 */
package com.zsword.test;

import java.io.UnsupportedEncodingException;

/**
 * @Description:
 * @Name DataCalcTest
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年11月30日 上午11:45:15
 * @Version 1.0
 */
public class DataCalcTest {

	public static void main(String[] args) {
		try {
			byte[] data = "JemiChow".getBytes("Unicode");
			for (int i = 0; i < data.length; i++) {
				System.out.printf("%d, ", data[i]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
