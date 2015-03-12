package com.viki.drm.client.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class StringUtil {
	public static String null2str(String str) {
		if (str == null) {
			return "";
		} else {
			return str;
		}
	}

	public static String getValidUrl(String url) {
		return url == null ? url : url.replace(" ", "%20");
	}

	public static String md5(String strToEnscrypt) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

		try {
			md.update(strToEnscrypt.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		byte byteData[] = md.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		if (s == null || s.equalsIgnoreCase(""))
			return null;
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		int length = data.length;
		for (int i = 0; i < length; ++i) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (++two_halfs < 1);
		}
		return buf.toString();
	}

	public static String extractBodyHTML(String html) {
		if (html == null)
			return null;

		Pattern p = Pattern.compile("<body[^>]*>(.*?)</body>", Pattern.DOTALL);
		Matcher m = p.matcher(html);
		if (m.find()) {
			String content = m.group(1);
			return content;
		}

		return null;
	}

	public static String extractStyleSheetForReader(String html) {
		String extractStyle = "";
		if (html == null) {
			return extractStyle;
		}

		Pattern p = Pattern.compile("<head>(.*?)</head>", Pattern.DOTALL);
		Matcher m = p.matcher(html);
		if (m.find()) {
			String headTAG = m.group(1);
			p = Pattern.compile("<link.*?/>", Pattern.DOTALL);
			while (m.find()) {
				extractStyle = extractStyle + m.group(0);
			}
			p = Pattern.compile("<style[^>]*>(.*?)</style>", Pattern.DOTALL);
			m = p.matcher(headTAG);
			while (m.find()) {
				extractStyle = extractStyle + m.group(0);
			}
		}
		return extractStyle;
	}

	public static String removeSign(String s) {
		// Remove Combining Diacritical Marks:
		// http://en.wikipedia.org/wiki/List_of_Unicode_characters
		s = s.toLowerCase();
		// a
		s = s.replaceAll("[\u00E0\u00E1\u00E3\u1EA1\u1EA3"
				+ "\u0103\u1EB1\u1EAF\u1EB3\u1EB5\u1EB7"
				+ "\u00E2\u1EA7\u1EA5\u1EA9\u1EAB\u1EAD"
				+ "\u0041\u00C1\u00C0\u1EA2\u00C3\u1EA0"
				+ "\u0102\u1EAE\u1EB0\u1EB2\u1EB4\u1EB6"
				+ "\u00C2\u1EA4\u1EA6\u1EA8\u1EAA\u1EAC]", "\u0061");
		// e
		s = s.replaceAll("[\u00E9\u00E8\u1EBB\u1EBD\u1EB9"
				+ "\u00EA\u1EBF\u1EC1\u1EC3\u1EC5\u1EC7"
				+ "\u00CA\u1EBE\u1EC0\u1EC2\u1EC4\u1EC6"
				+ "\u0045\u00C9\u00C8\u1EBA\u1EB8\u1EBC]", "\u0065");
		// o
		s = s.replaceAll("[\u00F3\u00F2\u00F5\u1ECD\u1ECF"
				+ "\u00F4\u1ED1\u1ED3\u1ED5\u1ED7\u1ED9"
				+ "\u01A1\u1EDB\u1EDD\u1EDF\u1EE1\u1EE3"
				+ "\u01A0\u1EDA\u1EDC\u1EDE\u1EE0\u1EE2"
				+ "\u004F\u00D2\u00D3\u00D5\u1ECC\u1ECE"
				+ "\u00CA\u1EBE\u1EC0\u1EC2\u1EC4\u1EC6]", "\u006F");

		// u
		s = s.replaceAll("[\u00F9\u00FA\u1EE5\u1EE7\u0169"
				+ "\u01B0\u1EE9\u1EEB\u1EED\u1EEF\u1EF1"
				+ "\u01AF\u1EE8\u1EEA\u1EEC\u1EEE\u1EF0"
				+ "\u0055\u00D9\u00DA\u1EE4\u1EE6\u0168]", "\u0075");

		// i
		s = s.replaceAll("[\u00EC\u00ED\u0129\u1EC9\u1ECB"
				+ "\u0049\u00CC\u00CD\u0128\u1EC8\u1ECA]", "\u0069");
		// d
		s = s.replaceAll("[\u00D0\u0111\u0110]", "\u0064");
		// Space
		s = s.replaceAll(" ", "-");

		return s;
	}

	public static String removeSign2(String s) {
		// Remove Combining Diacritical Marks:
		// http://en.wikipedia.org/wiki/List_of_Unicode_characters
		s = s.toLowerCase();
		// a
		s = s.replaceAll("[\u00E0\u00E1\u00E3\u1EA1\u1EA3"
				+ "\u0103\u1EB1\u1EAF\u1EB3\u1EB5\u1EB7"
				+ "\u00E2\u1EA7\u1EA5\u1EA9\u1EAB\u1EAD"
				+ "\u0041\u00C1\u00C0\u1EA2\u00C3\u1EA0"
				+ "\u0102\u1EAE\u1EB0\u1EB2\u1EB4\u1EB6"
				+ "\u00C2\u1EA4\u1EA6\u1EA8\u1EAA\u1EAC]", "\u0061");
		// e
		s = s.replaceAll("[\u00E9\u00E8\u1EBB\u1EBD\u1EB9"
				+ "\u00EA\u1EBF\u1EC1\u1EC3\u1EC5\u1EC7"
				+ "\u00CA\u1EBE\u1EC0\u1EC2\u1EC4\u1EC6"
				+ "\u0045\u00C9\u00C8\u1EBA\u1EB8\u1EBC]", "\u0065");
		// o
		s = s.replaceAll("[\u00F3\u00F2\u00F5\u1ECD\u1ECF"
				+ "\u00F4\u1ED1\u1ED3\u1ED5\u1ED7\u1ED9"
				+ "\u01A1\u1EDB\u1EDD\u1EDF\u1EE1\u1EE3"
				+ "\u01A0\u1EDA\u1EDC\u1EDE\u1EE0\u1EE2"
				+ "\u004F\u00D2\u00D3\u00D5\u1ECC\u1ECE"
				+ "\u00CA\u1EBE\u1EC0\u1EC2\u1EC4\u1EC6]", "\u006F");

		// u
		s = s.replaceAll("[\u00F9\u00FA\u1EE5\u1EE7\u0169"
				+ "\u01B0\u1EE9\u1EEB\u1EED\u1EEF\u1EF1"
				+ "\u01AF\u1EE8\u1EEA\u1EEC\u1EEE\u1EF0"
				+ "\u0055\u00D9\u00DA\u1EE4\u1EE6\u0168]", "\u0075");

		// i
		s = s.replaceAll("[\u00EC\u00ED\u0129\u1EC9\u1ECB"
				+ "\u0049\u00CC\u00CD\u0128\u1EC8\u1ECA]", "\u0069");
		// d
		s = s.replaceAll("[\u00D0\u0111\u0110]", "\u0064");
		// y
		s = s.replaceAll("ï¿½", "y");
		return s;
	}

	public static String rsa(String originalString, String publicKey) {
		if (originalString == null || publicKey == null)
			return originalString;

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(
				Base64Coder.decode(publicKey));
		KeyFactory kf = null;
		PublicKey pubKey = null;
		Cipher pkCipher = null;
		byte[] encryptedInByte = null;
		String encryptedInString = null;

		try {
			kf = KeyFactory.getInstance("RSA");
			pubKey = kf.generatePublic(spec);

			pkCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			pkCipher.init(Cipher.ENCRYPT_MODE, pubKey);

			encryptedInByte = pkCipher.doFinal(originalString.getBytes());
			encryptedInString = new String(Base64Coder.encode(encryptedInByte));
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		return encryptedInString;
	}

	public static String hmacSHA1(String data, String key) {
		String resultStr = "";
		if (key == null) {
			key = "";
		}
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
		Mac mac;
		try {
			mac = Mac.getInstance("HmacSHA1");
			mac.init(keySpec);
			byte[] result = mac.doFinal(data.getBytes());
			resultStr = new String(Base64Coder.encode(result));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return resultStr;
	}

	public static String formatHtmlContent(String html) {
		html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head>"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
				+ "<style type=\"text/css\">*{margin:0;padding:0;font-size:14px !important;color:black; font-family: sans-serif, \"times new roman\" !important}body{margin:0;border:none;line-height:18px; background:white;text-align:justify}p{text-indent:12px;}p,div{padding: 6px 0px}</style>"
				+ "</head></body>" + html + "</body></html>";
		return html;
	}

	public static String encryptAES(Object original, Object key) {
		String encrypted = null;
		int keyLength = 16;
		try {
			String strKey = String.valueOf(key);
			if (strKey.length() > keyLength) {
				strKey = strKey.substring(0, keyLength - 1);
			} else if (strKey.length() < keyLength) {
				for (int i = strKey.length() + 1; i <= keyLength; i++) {
					strKey = strKey + " ";
				}
			}

			SecretKeySpec keySpec = new SecretKeySpec(strKey.getBytes("UTF-8"),
					"AES");
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			aes.init(Cipher.ENCRYPT_MODE, keySpec);

			String strOriginal = String.valueOf(original);
			byte[] ciphertext = aes.doFinal(strOriginal.getBytes("UTF-8"));
			return String.valueOf(Base64Coder.encode(ciphertext));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return encrypted;
	}

	public static String decryptAES(Object encrypted, Object key) {
		String decrypted = null;
		int keyLength = 16;
		try {
			String strKey = String.valueOf(key);
			if (strKey.length() > keyLength) {
				strKey = strKey.substring(0, keyLength - 1);
			} else if (strKey.length() < keyLength) {
				for (int i = strKey.length() + 1; i <= keyLength; i++) {
					strKey = strKey + " ";
				}
			}

			SecretKeySpec keySpec = new SecretKeySpec(strKey.getBytes("UTF-8"),
					"AES");
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			aes.init(Cipher.DECRYPT_MODE, keySpec);

			String strEncrypted = String.valueOf(encrypted);
			byte[] ciphertext = aes.doFinal(Base64Coder.decode(strEncrypted));
			decrypted = new String(ciphertext);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return decrypted;
	}

	public static String getPriceFromNumber(int price) {
		return new DecimalFormat("#,###,###").format(price) + " VNÄ?";
	}

}
