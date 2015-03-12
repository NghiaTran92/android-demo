package com.viki.drm.client;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.AlgorithmParameters;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.provider.Settings.Global;

public class tripleDES {
	public SecretKey key = null;
	public static AlgorithmParameters desparam = null;
	public static final String Hex = "0123456789ABCDEF";
	public static String mstrDefaultKey = "101010101010101010101010101010101010101010101010";
	public static final byte[] Hex2Byte = { (byte) 0x00, (byte) 0x01,
			(byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
			(byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
			(byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F };

	/*************************************************************
	 * Description: Encrypt bytes use Desede :Provider "SunJCE" Not using
	 * Padding Make Padding fill with 0xff
	 * 
	 * @param bInputData
	 * @return
	 * @throws java.lang.Exception
	 * @cuongnc
	 * @since
	 * @modifier
	 *************************************************************/
	public byte[] encrypt0(byte[] bInputData) throws Exception {
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(fillBlock(bInputData));
	}

	public byte[] encrypt0(String strmsg) throws Exception {
		/********************************************************************
		 * Purpose Fill block Description Before encrypt with NoPadding
		 ********************************************************************/
		byte[] data = fillBlock(strmsg);
		return encrypt0(data);
	}

	public String encrypt(String strmsg) throws Exception {
		/********************************************************************
		 * Purpose Fill block Description Before encrypt with NoPadding
		 ********************************************************************/
		byte[] data = fillBlock(strmsg);
		return getHexDump(encrypt0(data));
	}

	private byte[] fillBlock(String strmsg) {
		byte[] dataTemp = strmsg.getBytes();
		return fillBlock(dataTemp);
	}

	private byte[] fillBlock(byte[] dataTemp) {
		int i = dataTemp.length % 8;
		int length = (i == 0) ? (dataTemp.length) : (dataTemp.length - i + 8);
		byte[] data = new byte[length];
		if (i != 0) {
			for (int j = 0; j < length; j++) {
				if (j < dataTemp.length) {
					data[j] = dataTemp[j];
				} else {
					data[j] = (byte) 0xff;
				}
			}
		} else {
			data = dataTemp;
		}
		return data;
	}

	public byte[] decrypt0(byte[] inpBytes) throws Exception {
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(inpBytes);
	}

	public String decrypt(byte[] inpBytes) throws Exception {
		byte result[] = decrypt0(inpBytes);
		String strHex = getHexDump(result);
		strHex = strHex.toLowerCase();
		strHex = strHex.replaceAll("ff", "");
		return new String(convertHexToByte(strHex));
	}

	public static String getHexDump(byte[] data) {
		String dump = "";
		try {
			int dataLen = data.length;
			for (int i = 0; i < dataLen; i++) {
				dump += Character.forDigit((data[i] >> 4) & 0x0f, 16);
				dump += Character.forDigit(data[i] & 0x0f, 16);
			}
		} catch (Throwable t) {
			dump = "Throwable caught when dumping = " + t;
		}
		return dump;
	}

	public byte[] convertHexToByte(String strHex) throws AppException {
		String strTemp = "";
		strTemp = strHex.toUpperCase();
		if (strTemp.length() % 2 != 0) {
			throw new AppException();
		}
		int length = strTemp.length() / 2;
		byte[] keySec = new byte[length];
		for (int i = 0; i < length; i++) {
			char ch0 = strTemp.charAt(2 * i);
			char ch1 = strTemp.charAt(2 * i + 1);
			int loByte = Hex.indexOf(ch0);
			int hiByte = Hex.indexOf(ch1);
			byte lo = Hex2Byte[loByte];
			byte hi = Hex2Byte[hiByte];
			Byte byelo = new Byte(lo);

			Byte byehi = new Byte(hi);

			int inlo = byelo.intValue();
			int inhi = byehi.intValue();
			int value = inlo * 16 + inhi;
			keySec[i] = (byte) value;

		}
		return keySec;
	}

	public SecretKey createKey(String key) throws AppException {
		SecretKey keyReturn;
		byte[] keyData = convertHexToByte(key);
		keyReturn = new SecretKeySpec(keyData, "DESede");

		return keyReturn;
	}

	/*************************************************************
	 * Description: Create key using in Desede Algirithrm
	 * 
	 * @param
	 * @return
	 * @throws
	 * @cuongnc
	 * @since unknown
	 * @modifier
	 *************************************************************/

	public String createRadomKey() {
		String strKey = "";
		for (int i = 0; i < 48; i++) {
			double dbkey = Math.random() * 1000;
			int intkey = (int) Math.round(dbkey);
			intkey = intkey % 16;
			strKey += Hex.charAt(intkey);
		}
		return strKey;
	}

	public void encryptFile(String strDir, String strFileIn, String strFileOut,
			boolean isEncrypt) {
		String strSeparator = System.getProperty("file.separator");
		String vstrIn = strDir + strSeparator + strFileIn;
		String vstrOut = strDir + strSeparator + strFileOut;
		RandomAccessFile fileIn = null;
		RandomAccessFile fileOut = null;
		try {
			fileIn = new RandomAccessFile(vstrIn, "r");
			fileOut = new RandomAccessFile(vstrOut, "rw");
			long intLength = fileIn.length();
			byte[] b = new byte[(int) intLength];
			fileIn.read(b);
			if (isEncrypt) {
				fileOut.write(encrypt0(b));
			} else {
				fileOut.write(decrypt0(b));
			}
			fileIn.close();
			fileOut.close();
		} catch (Exception ex) {
				ex.printStackTrace();
		} finally {
			try {
				fileIn.close();
				fileOut.close();
			} catch (IOException ex1) {
					ex1.printStackTrace();
			}
		}
	}
}
