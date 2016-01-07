/**
 * 
 */
package com.zsword.mh4geditor.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.zsword.modules.crypto.provider.Blowfish;
import com.zsword.modules.utils.BitsUtil;

/**
 * @Description:
 * @Name SavedataCipher
 * @Company ZSword (C) Copyright
 * @Author JemiZhuu(周士淳)
 * @Date 2015年9月19日 上午10:26:45
 * @Version 1.0
 */
class SavedataCipher {

	public static void main(String[] args) {
		try {
			SavedataCipher cipher = new SavedataCipher(GameVersion.MH4G_JP);
			File savedataFile = new File("D:/Games/3DS/MHX/mhefqs/examples/n3ds/user3");
			// savedataFile = new
			// File("D:/Games/3DS/MHX/mhefqs/examples/n3ds/user1.temp");
			File decryptFile = new File(savedataFile.getParentFile(), savedataFile.getName() + "_dec");
			File encryptFile = new File(decryptFile.getParentFile(), savedataFile.getName() + "_enc");
			if (savedataFile.getName().endsWith(".temp")) {
				cipher.encryptFile(savedataFile, encryptFile);
				return;
			}
			decryptFile = cipher.decryptFile(savedataFile, decryptFile);
			System.out.println("Decrypt savedata: " + savedataFile.getName() + " to " + decryptFile.getName());
			encryptFile = cipher.encryptFile(decryptFile, encryptFile);
			System.out.println("Encrypt savedata: " + decryptFile.getName() + " to " + encryptFile.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final String BLOWFISH_ALGORITHM = "Blowfish";
	private static final String BLOWFISH_KEY = "blowfish key iorajegqmrna4itjeangmb agmwgtobjteowhv9mope";// "blowfish
																											// key
																											// iorajegqmrna4itjeangmb
																											// agmwgtobjteowhv9mope";

	enum GameVersion {
		MH3G_JP(0), MH3G_NA(1), MH3G_EU(2), MH4_JP(3), MH4_NA(4), MH4_EU(5), MH4G_JP(6), MH4G_NA(7), MH4G_EU(
				8), MH4G_KR(9);

		private int code;

		private GameVersion(int code) {
			this.code = code;
		}

		/**
		 * @return the code
		 */
		public int getCode() {
			return code;
		}

	}

	private Blowfish cipher;

	public SavedataCipher(GameVersion version) {
		switch (version) {
		case MH4G_EU:
		case MH4G_NA:
		case MH4G_JP:
			this.cipher = new Blowfish();
			String keyStr = BLOWFISH_KEY;
			cipher.init(true, keyStr.getBytes());
			break;
		default:
			throw new IllegalArgumentException("Ivalid game selected.");
		}
	}

	protected byte[] xor(byte[] buff, int pos, int key) {
		for (int i = pos; i < buff.length; i++) {
			if (key == 0) {
				key = 1;
			}
			key = key * 0xb0 % 0xff53;
			int val = bytesToInt(new byte[] { buff[i], buff[i + 1] }, false);
			val ^= key;
			val = Integer.reverseBytes(val);
			buff[i] = (byte) (val >> 24);
			buff[i + 1] = (byte) (val >> 16);
			i++;
		}
		return buff;
	}

	public byte[] encrypt(byte[] buff) throws IOException {
		ByteArrayOutputStream output = null;
		try {
			long seedValue = makeLong(buff[3], buff[2], buff[1], buff[0]);
			int seed = (int) (seedValue >> 16);
			int csum = sum(buff, 8);
			byte[] data = BitsUtil.intToBytes(csum);
			for (int i = 0; i < 4; i++) {
				buff[4 + i] = data[i];
			}
			buff = xor(buff, 4, seed);
			data = BitsUtil.intToBytes((seed << 16) + 0x10);
			output = new ByteArrayOutputStream();
			output.write(data);
			output.write(buff, 4, buff.length - 4);
			buff = output.toByteArray();
			buff = byteswap(buff);
			int dataLen = buff.length;
			buff = cipher.encrypt(buff);
			if (buff.length > dataLen) {
				buff = Arrays.copyOfRange(buff, buff.length - dataLen, buff.length);
			}
			buff = byteswap(buff);
			return buff;
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public File encryptFile(File savedata_file, File outFile) throws IOException {
		byte[] savedata = readFileData(savedata_file);
		OutputStream output = null;
		try {
			savedata = encrypt(savedata);
			output = new FileOutputStream(outFile);
			output.write(savedata);
			output.flush();
			return outFile;
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	protected byte[] decrypt(byte[] buff) {
		buff = byteswap(buff);
		buff = cipher.decryptBytes(buff, buff.length);
		buff = byteswap(buff);
		int seed = (int) ((bytesToInt(buff, 0, false) & 0xFFFFFFFFL) >> 16);
		buff = xor(buff, 4, seed);
		int csum = bytesToInt(buff, 4, false);
		if (csum != (sum(buff, 8) & 0xFFFFFFFFL)) {
			throw new IllegalArgumentException("Invalid checksum in header.");
		}
		return buff;
	}

	public File decryptFile(File savedataFile, File outFile) throws IOException {
		byte[] savedata = readFileData(savedataFile);
		savedata = decrypt(savedata);
		OutputStream output = null;
		try {
			output = new FileOutputStream(outFile);
			output.write(savedata);
			output.flush();
			return outFile;
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	private byte[] byteswap(byte[] buff) {
		int len = buff.length;
		int idx = 0;
		while (idx < len) {
			byte b0 = buff[idx];
			byte b1 = buff[idx + 1];
			byte b2 = buff[idx + 2];
			byte b3 = buff[idx + 3];
			buff[idx] = b3;
			buff[idx + 1] = b2;
			buff[idx + 2] = b1;
			buff[idx + 3] = b0;
			idx += 4;
		}
		return buff;
	}

	private int bytesToInt(byte[] src, boolean bigEndin) {
		return bytesToInt(src, 0, bigEndin);
	}

	private int bytesToInt(byte[] src, int start, boolean bigEndin) {
		byte[] temp = { 0, 0, 0, 0 };
		int idx = 0;
		for (int i = start; i < src.length; i++) {
			if (idx > 3)
				break;
			temp[idx++] = src[i];
		}
		if (!bigEndin) {
			return makeInt(temp[3], temp[2], temp[1], temp[0]);
		} else {
			return makeInt(temp[0], temp[1], temp[2], temp[3]);
		}
	}

	private int sum(byte[] buff, int start) {
		int result = 0;
		for (int i = start; i < buff.length; i++) {
			result = result + (int) (0xff & buff[i]);
		}
		return result;
	}

	private int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (int) (((b3 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff) << 0));
	}

	private long makeLong(byte b3, byte b2, byte b1, byte b0) {
		return (((b3 & 0xffL) << 24) | ((b2 & 0xffL) << 16) | ((b1 & 0xffL) << 8) | ((b0 & 0xffL) << 0));
	}

	private byte[] readFileData(File file) throws IOException {
		ByteArrayOutputStream output = null;
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			output = new ByteArrayOutputStream();
			byte[] buff = new byte[1024 * 500];
			int count = -1;
			while ((count = input.read(buff)) > 0) {
				output.write(buff, 0, count);
			}
			output.flush();
			return output.toByteArray();
		} finally {
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.close();
			}
		}
	}

	private File writeFileData(File file, byte[] data) throws IOException {
		OutputStream output = null;
		try {
			output = new FileOutputStream(file);
			output.write(data);
			output.flush();
			return file;
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	/*
	 * class DLCCipher: def __init__(self, game): if game == MH4G_NA or game ==
	 * MH4G_EU: self._cipher = Blowfish.new(b'AgK2DYheaCjyHGPB') elif game ==
	 * MH4G_JP: self._cipher = Blowfish.new(b'AgK2DYheaCjyHGP8') elif game ==
	 * MH4G_KR: self._cipher = Blowfish.new(b'AgK2DYheaOjyHGP8') else: raise
	 * ValueError('Ivalid game selected.')
	 * 
	 * def encrypt(self, buff): buff += hashlib.sha1(buff).digest() size =
	 * len(buff) if len(buff) % 8 != 0: buff += b'\x00' * (8 - len(buff) % 8)
	 * buff = array.array('I', buff) buff.byteswap() buff = array.array('I',
	 * self._cipher.encrypt(buff.tostring())) buff.append(size) buff.byteswap()
	 * return buff.tostring()
	 */
	/*
	 * public File decrypt(self, buff): buff = array.array('I', buff)
	 * buff.byteswap() size = buff.pop() if size > len(buff) * 4:
	 * 
	 * raise ValueError('Invalid file size in footer.') buff = array.array('I',
	 * self._cipher.decrypt(buff.tostring())) buff.byteswap() buff =
	 * buff.tostring()[:size] md = buff[-20:] buff = buff[:-20] if md !=
	 * hashlib.sha1(buff).digest():
	 * 
	 * raise ValueError('Invalid SHA1 hash in footer.') return buff
	 * 
	 * /* def encrypt_file(self, dlc_file, out_file): dlc = open(dlc_file,
	 * 'rb').read() dlc = self.encrypt(dlc) open(out_file, 'wb').write(dlc)
	 */
	/*
	 * public File decryptFile( dlc_file, out_file): dlc = open(dlc_file,
	 * 'rb').read() dlc = self.decrypt(dlc) open(out_file, 'wb').write(dlc)
	 */
}
