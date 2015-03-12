package com.viki.epubreader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import com.viki.core.parser.BookInfoSAXParser;
import com.viki.core.parser.ContainerSAXParser;
import com.viki.core.parser.TableOfContent;
import com.viki.core.parser.TocSAXParser;
import com.viki.drm.client.DecrytionFactory;
import com.viki.drm.client.IDecryption;
import com.viki.drm.client.IRightObject;
import com.viki.epubreader.objects.EbookException;
import com.viki.epubreader.objects.EpubBook;
import com.viki.epubreader.objects.EpubBook.BOOK_TYPE;
import com.viki.epubreader.util.ZipUtil;





public class EpubEngine {
	private BOOK_TYPE mBookType;
	private String mEpubPath;
	private String mUnzipPath;
	private String mContentPath;
	private String mBasePath;
	private String mBaseUrl;
	private String mTocPath;
	private byte[] mRightObject;
	private String imei;
	private InputStream drmProps;

	/**
	 * Constructor
	 * 
	 * @param bookType
	 * @param bookPath
	 * @throws EbookException
	 */
	public EpubEngine(BOOK_TYPE bookType, String bookPath, String contentPath,
			boolean deleteOld) throws EbookException {
		mEpubPath = bookPath;
		mBookType = bookType;
		/*
		 * VEF book
		 */
		if (mBookType == BOOK_TYPE.VEF) {
			// 1. get unzip path
			String vefDir = mEpubPath.substring(0,
					mEpubPath.lastIndexOf("/") + 1);
			String vefName = mEpubPath.substring(
					mEpubPath.lastIndexOf("/") + 1, mEpubPath.lastIndexOf("."));
			mUnzipPath = vefDir + "" + vefName;

			// unzip if not exists
			File unzipDir = new File(mUnzipPath);
			if (!unzipDir.exists() || deleteOld) {
				try {
					// Unzip book files
					File f = new File(bookPath);
					ZipUtil.unzipAll(f, unzipDir);
					// Get content path
					mContentPath = parseContentPath(mUnzipPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 2. get content path if not parse file
			if (mContentPath == null || mContentPath.equalsIgnoreCase("")) {
				if (contentPath != null) {
					mContentPath = contentPath;
				} else {
					mContentPath = parseContentPath(mUnzipPath);
				}
			}

			// 3. get base url
			mBaseUrl = mBasePath = mContentPath.substring(0,
					mContentPath.lastIndexOf("/"));
		}

		/*
		 * text book
		 */
		else if (mBookType == BOOK_TYPE.TEXT) {
			// 1. get unzip path
			mUnzipPath = "";
			// 2. get content path
			mContentPath = contentPath;
			// 3. get base url
			String title = bookPath.substring(bookPath.lastIndexOf("/") + 1,
					bookPath.lastIndexOf("."));
			mBaseUrl = mBasePath = bookPath.substring(0,
					bookPath.lastIndexOf("/") + 1)
					+ "." + title;
		}
		/*
		 * html book
		 */
		else if (mBookType == BOOK_TYPE.HTML) {
			// 1. get unzip path
			mUnzipPath = "";
			// 2. get content path
			mContentPath = contentPath;
			// 3. get base url
			mBaseUrl = mBasePath = bookPath.substring(0,
					bookPath.lastIndexOf("/"));
		} else {
			throw new EbookException(
					"Unsupported format. Only vef can be used.");
		}
	}

	/**
	 * Parses container.xml to get content path
	 * 
	 * @param rootDir
	 */
	public String parseContentPath(String rootDir) {
		String contentPath = null;
		try {
			String containerPath = mUnzipPath + "/META-INF/container.xml";
			ContainerSAXParser csp = new ContainerSAXParser();
			contentPath = rootDir + "/" + csp.getRootFilePath(containerPath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return contentPath;
	}

	/**
	 * Parses content.opf
	 * 
	 * @return
	 * @throws EbookException
	 */
	public EpubBook parseBookInfo() throws EbookException {
		EpubBook bookInfo = new EpubBook();

		BookInfoSAXParser bookInfoParser = new BookInfoSAXParser();
		try {
			bookInfo = bookInfoParser.getBookInfo(mContentPath);
			bookInfo.bookType = BOOK_TYPE.VEF;
			bookInfo.path = mEpubPath;
			mTocPath = mBasePath + "/" + bookInfo.tocPath;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bookInfo;
	}

	/**
	 * Parses toc.ncx
	 * 
	 * @return
	 * @throws EbookException
	 */
	public TableOfContent parseTableOfContent() throws EbookException {
		TableOfContent toc = new TableOfContent();

		TocSAXParser parser = new TocSAXParser(this);
		try {
			toc = parser.getTableOfContents(mTocPath);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return toc;
	}

	/**
	 * Get Base URL
	 * 
	 * @return
	 */
	public String getBaseUrl() {
		return mBaseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		mBaseUrl = baseUrl;
	}

	public String getBasePath() {
		return mBasePath;
	}

	public void setBasePath(String basePath) {
		mBasePath = basePath;
	}

	/**
	 * Get Content Path
	 * 
	 * @return
	 */
	public String getContentPath() {
		return mContentPath;
	}

	/**
	 * Gets ePub Unzip Path
	 * 
	 * @return
	 */
	public String getUnzipPath() {
		return mUnzipPath;
	}

	public void setRightObject(byte[] fileObj) {
		this.mRightObject = fileObj;
	}

	public void setIMEI(String imei) {
		this.imei = imei;
	}

	public void setInputStreamDRMProperties(InputStream drmProperties) {
		this.drmProps = drmProperties;
	}

	/**
	 * Decrypt html content
	 * 
	 * @return
	 */
	public String decryptContentChapter(String chapterPath) {
		try {
			chapterPath = chapterPath.replace("%20", " ");
			InputStream robj = new ByteArrayInputStream(mRightObject);
			IRightObject rightObj = DecrytionFactory.createRightObj(robj,
					this.imei, "ServerKey".getBytes(), drmProps);

			String pathForRightObj = chapterPath
					.substring(mUnzipPath.length() + 1);
			IDecryption decrypter = DecrytionFactory.createDecryption(rightObj,
					pathForRightObj, "", drmProps);
			File file = new File(chapterPath);
			byte[] resultBytes = decrypter.decrypt(new FileInputStream(file));
			String resultStr = new String(resultBytes);
			return resultStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getContentChapterWithoutDecrypt(String chapterPath) {
		try {
			File file = new File(chapterPath);
			String resultStr = IOUtils.toString(new FileInputStream(file),
					Charset.defaultCharset());
			return resultStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
