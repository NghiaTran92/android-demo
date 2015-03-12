/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viki.drm.client.utils;
/**
 *
 * @author THANGNT
 */
public class HexUtils {

    public static char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static char[] byteToHex(byte[] byteBuf) throws Exception {
        if (byteBuf.length > Integer.MAX_VALUE) {
            throw new Exception("byte is too large");
        }
        char[] result = new char[2 * byteBuf.length];
        for (int i = 0; i < byteBuf.length; i++) {
            byte b = byteBuf[i];
            result[2 * i] = HEX_CHAR[((b >> 4) & 0x0F)]; //& 11110000
            result[2 * i + 1] = HEX_CHAR[(b & 0x0F)];
        }
        return result;
    }

    public static byte[] hexToByte(char[] charBuf) throws Exception {
        if (charBuf.length % 2 == 1) {
            throw new Exception("Leng of the char array is not illegal");
        }
        byte[] result = new byte[charBuf.length / 2];
        for (int i = 0; i < charBuf.length; i += 2) {
            byte big = getByteVal(charBuf[i]);
            byte end = getByteVal(charBuf[i + 1]);
            result[i / 2] = (byte) (((big << 4) & end) & 0xFF);
        }
        return result;
    }

    public static byte[] hexToByte(String charSeq) throws Exception {
        if (charSeq.length() % 2 == 1) {
            throw new Exception("Leng of the char array is not illegal");
        }
        byte[] result = new byte[charSeq.length() / 2];
        for (int i = 0; i < charSeq.length(); i += 2) {
            byte big = getByteVal(charSeq.charAt(i));
            byte end = getByteVal(charSeq.charAt(i + 1));
            result[i / 2] = (byte) ((big << 4) | end);
        }
        return result;
    }

    private static byte getByteVal(char c) throws Exception {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'A':
                return 10;
            case 'B':
                return 11;
            case 'C':
                return 12;
            case 'D':
                return 13;
            case 'E':
                return 14;
            case 'F':
                return 15;
            default:
                throw new Exception("char is not a Hex char.");
        }
    }

    public static void main(String[] args) {
    }
}
