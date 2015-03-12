/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viki.drm.client;

import java.io.InputStream;

import com.viki.drm.client.impl.AES;
import com.viki.drm.client.impl.VEFRightObj;



/**
 *
 * @author THANGNT
 */
public class DecrytionFactory {

    public static IDecryption createDecryption(IRightObject rightObj, String path, String type, InputStream drmProperties) throws Exception {
        AES result = new AES(rightObj, path, drmProperties);
        result.setType(IDecryption.HEX_STRING);
        return result;
    }

    public static IRightObject createRightObj(InputStream in, String deviceName, byte[] clientKey) throws Exception {
        return new VEFRightObj(in, deviceName, clientKey);
    }
    
    public static IRightObject createRightObj(InputStream in, String deviceName, byte[] clientKey, InputStream drmProps) throws Exception {
        return new VEFRightObj(in, deviceName, clientKey, drmProps);
    }
    
    public static IRightObject createRightObj(byte[] in, String deviceName, byte[] clientKey) throws Exception {
        return new VEFRightObj(in, deviceName, clientKey);
    }
}
