package com.testfunction;

import com.viki.epubreader.objects.EpubBook;
import com.viki.epubreader.objects.EpubBook.BOOK_TYPE;
import com.viki.epubreader.util.EpubReaderUtil;

public class TestEpubEngine {

	public void run(){
		EpubBook book=EpubReaderUtil.parseEpub(BOOK_TYPE.VEF, TestZip.path);
		
	}
}
