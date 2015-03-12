package com.viki.epubreader.util;

import com.viki.epubreader.EpubEngine;
import com.viki.epubreader.objects.EbookException;
import com.viki.epubreader.objects.EpubBook;
import com.viki.epubreader.objects.EpubBook.BOOK_TYPE;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


public class EpubReaderUtil {
	public static EpubBook parseEpub(BOOK_TYPE bookType, String vefFilePatj) {
		EpubBook book=null;
		try {
			EpubEngine bookEngine = new EpubEngine(bookType, vefFilePatj,
					null, true);
			book=bookEngine.parseBookInfo();
		} catch (EbookException e) {
			e.printStackTrace();
		}
		return book;
	}
	
	
	public static String getIMEI(Context context) {
		return getDeviceID(context);
	}
	
	private static String getDeviceID(Context act) {
		TelephonyManager tm = (TelephonyManager) act
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		if (TextUtils.isEmpty(deviceId)) {
			deviceId = tm.getSimSerialNumber();
			if (TextUtils.isEmpty(deviceId)) {
				deviceId = android.provider.Settings.Secure.getString(
						act.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}
		}
		return deviceId;
	}

}
