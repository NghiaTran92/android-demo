package com.testfunction;

import java.io.File;
import java.io.IOException;

import com.viki.epubreader.util.ZipUtil;

import android.os.Environment;

public class TestZip {

	public static String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestEpub/Nhung_nguoi_dan_ong_co_van_de.epub";
	public static String unzipPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestEpub/Nhung_nguoi_dan_ong_co_van_de";
	
	public void run(){
		try {
			ZipUtil.unzipAll(new File(path), new File(unzipPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
