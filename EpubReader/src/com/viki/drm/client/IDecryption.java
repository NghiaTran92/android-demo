/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viki.drm.client;

import java.io.InputStream;

/**
 *
 * @author THANGNT
 */
public interface IDecryption {

    public static final int HEX_STRING = 0;
    public static final int BASE64_STRING = 1;
    public static final int MB = 1048576;

    public void setType(int type);

    public byte[] decrypt(InputStream inStream) throws Exception;
}
