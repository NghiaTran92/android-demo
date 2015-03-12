/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viki.drm.client.utils;

import java.util.Properties;

/**
 *
 * @author THANGNT
 */
@SuppressWarnings("serial")
public class EncryptProps extends Properties {

    public int getIntVal(String key, int defaultVal) {
        int result;
        try {
            result = Integer.valueOf(this.getProperty(key));
        } catch (Exception e) {
            result = defaultVal;
        }
        return result;
    }

    public boolean getBoolVal(String key, boolean defaultVal) {
        boolean result;
        try {
            result = Boolean.valueOf(this.getProperty(key));
        } catch (Exception e) {
            result = defaultVal;
        }
        return result;
    }

    public String getStringVal(String key, String defaultVal) {
        String result = null;
        try {
            result = this.getProperty(key);
        } catch (Exception e) {
        }
        if (result == null) {
            result = defaultVal;
        }
        return result;
    }
}
