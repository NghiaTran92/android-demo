package com.viki.epubreader.objects;

import java.util.ArrayList;

public class EpubBook {
	public ArrayList<Manifest> manifestList = new ArrayList<Manifest>();
	public ArrayList<String> spineList = new ArrayList<String>();
	public String coverPath = null;
	public String tocPath = null;
	public BOOK_TYPE bookType = BOOK_TYPE.VEF;
	public String path = null;

	// For book encrypt
	public boolean isEncrypt = false;
	public String serverKey = "";
	public byte[] rightObject;
	public int totalPage;
	public String title;
	public String author;
	public long id;

	public static enum BOOK_TYPE {
		VEF, HTML, TEXT
	}

	public static class Manifest {
		public String _id;
		public String href;
		public String mediaType;

		public Manifest() {
		}

		public Manifest(String id, String href, String mediaType) {
			this._id = id;
			this.href = href;
			this.mediaType = mediaType;
		}
	}
}
