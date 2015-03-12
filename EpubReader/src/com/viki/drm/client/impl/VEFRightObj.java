/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viki.drm.client.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Security;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.viki.drm.client.IRightObject;
import com.viki.drm.client.utils.EncryptProps;
import com.viki.drm.client.utils.VegaGenerator;

import android.provider.Settings.Global;


/**
 * 
 * @author THANGNT
 */
public class VEFRightObj implements IRightObject {

	private byte[] ivBytes = "                ".getBytes();
	private HashMap<String, byte[]> keyList;
	private byte[] data;
	private String deviceName;
	private byte[] clientKey;
	private InputStream drmProperties;

	public VEFRightObj(InputStream in, String deviceName, byte[] clientKey)
			throws Exception {
		data = IOUtils.toByteArray(in);
		this.deviceName = deviceName;
		this.clientKey = clientKey;
	}

	public VEFRightObj(InputStream in, String deviceName, byte[] clientKey,
			InputStream drmProp) throws Exception {
		data = IOUtils.toByteArray(in);
		this.deviceName = deviceName;
		this.clientKey = clientKey;
		this.drmProperties = drmProp;
	}

	public VEFRightObj(byte[] in, String deviceName, byte[] clientKey)
			throws Exception {
		data = in;
		this.deviceName = deviceName;
		this.clientKey = clientKey;
	}

	@Override
	public byte[] getKey(String filePath) {
		if (keyList == null) {
			try {
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				keyList = new HashMap<String, byte[]>();
				String cipherMode;
				int keySize;
				EncryptProps props = new EncryptProps();
				if (drmProperties != null)
					props.load(drmProperties);

				cipherMode = props.getStringVal("ciphermode",
						"AES/ECB/PKCS7Padding");
				keySize = props.getIntVal("keysize", 128 / 8);
				String provider = props.getStringVal("provider", "BC");
				boolean usingIV = props.getBoolVal("usingiv", false);
				Cipher cipher;
				if (provider.equals("")) {
					cipher = Cipher.getInstance(cipherMode);
				} else {
					cipher = Cipher.getInstance(cipherMode, provider);
				}
				if (usingIV) {
					int size = cipher.getBlockSize();
					ivBytes = new byte[size];
					for (int i = 0; i < ivBytes.length; i++) {
						ivBytes[i] = 1;
					}
				} else {
					ivBytes = null;
				}
				byte[] raw = VegaGenerator.createRightObjKey(deviceName,
						clientKey, keySize);

				SecretKeySpec skeySpec = new SecretKeySpec(raw,
						cipherMode.split("/")[0]);
				if (ivBytes != null) {
					cipher.init(Cipher.DECRYPT_MODE, skeySpec,
							new IvParameterSpec(ivBytes));
				} else {
					cipher.init(Cipher.DECRYPT_MODE, skeySpec);
				}

				byte[] cipherText = Base64.decodeBase64(data);
				int ctLength = cipherText.length;
				byte[] plainText = new byte[cipher.getOutputSize(ctLength)];
				int ptLength = cipher.update(cipherText, 0, ctLength,
						plainText, 0);
				ptLength += cipher.doFinal(plainText, ptLength);

				Properties rightObj = new Properties();
				rightObj.load(new ByteArrayInputStream(plainText));
				Set<Object> keys = rightObj.keySet();
				for (Object object : keys) {
					String key = (String) object;
					String val = rightObj.getProperty(key);
					keyList.put(key, val.getBytes());
				}

			} catch (Exception ex) {
					ex.printStackTrace();
			}
		}
		byte[] result = Base64.decodeBase64(keyList.get(filePath));
		return result;
	}

	public static void main(String[] args) throws Exception {
	}
}
