/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viki.drm.client.utils;

/**
 *
 * @author THANGNT
 */
public class VegaGenerator {
public static byte[] createRightObjKey(String deviceName, byte[] serverKey,int keyLength) {
        byte[] raw = new byte[keyLength];
        byte[] tmp = deviceName.getBytes();
        for (int i = 0; i < keyLength; i++) {
            raw[i] = tmp[i % tmp.length];
        }
        return raw;
    }    
}
