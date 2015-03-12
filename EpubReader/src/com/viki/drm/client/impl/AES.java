/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viki.drm.client.impl;

import java.io.InputStream;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.viki.drm.client.IDecryption;
import com.viki.drm.client.IRightObject;
import com.viki.drm.client.utils.EncryptProps;

/**
 *
 * @author THANGNT
 */
public class AES implements IDecryption {

    private Cipher cipher;
    private int typeOfInput;

    public AES(IRightObject rightObj, String path, InputStream drmProps) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        EncryptProps props = new EncryptProps();
        if(drmProps != null)
        	props.load(drmProps);
        
        String cipherMode = props.getStringVal("ciphermode", "AES/ECB/PKCS7Padding");
//        int keySize = props.getIntVal("keysize", 128 / 8);
        String provider = props.getStringVal("provider", "BC");
        boolean usingIV = props.getBoolVal("usingiv", false);
        if (provider.equals("")) {
            cipher = Cipher.getInstance(cipherMode);
        } else {
            cipher = Cipher.getInstance(cipherMode, provider);
        }
        byte[] ivBytes;
        if (usingIV) {
            int size = cipher.getBlockSize();
            ivBytes = new byte[size];
            for (int i = 0; i < ivBytes.length; i++) {
                ivBytes[i] = 1;
            }
        } else {
            ivBytes = null;
        }
        byte[] raw = rightObj.getKey(path);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, cipherMode.split("/")[0]);
        if (ivBytes != null) {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(ivBytes));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        }
    }

    @Override
    public byte[] decrypt(InputStream inStream) throws Exception {
        byte[] buf = IOUtils.toByteArray(inStream);
        byte[] cipherText = Base64.decodeBase64(buf);
        int ctLength = cipherText.length;
        byte[] plainText = new byte[cipher.getOutputSize(ctLength)];
        int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
        ptLength += cipher.doFinal(plainText, ptLength);
        return plainText;
    }

    @Override
    public void setType(int type) {
        this.typeOfInput = type;
    }

	public int getType() {
		return typeOfInput;
	}
}
