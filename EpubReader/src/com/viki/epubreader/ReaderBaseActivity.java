package com.viki.epubreader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.viki.core.parser.TableOfContent;
import com.viki.epubreader.objects.BookState;
import com.viki.epubreader.objects.EpubBook;



public abstract class ReaderBaseActivity extends ActionBarActivity {
	protected EpubEngine mEngine;
	protected EpubBook mBook = null;
	protected TableOfContent mToc = null;
	protected BookState mState = null;
	protected PrepareBookTask prepareBookTask;
	protected PowerManager.WakeLock mWakeLock;
	protected ProgressDialog progressDialog;
	private ArrayList<AsyncTask<?, ?, ?>> listTaskRunning;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		listTaskRunning = new ArrayList<AsyncTask<?, ?, ?>>();
	}

	protected void keepScreenOn() {
		if (Build.VERSION.SDK_INT < 17) {
			final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
					"My Tag");
			mWakeLock.acquire();
		} else {
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	protected void releaseKeepScreenOn() {
		if (Build.VERSION.SDK_INT < 17) {
			mWakeLock.release();
		} else {
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		keepScreenOn();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveStatetoDb();
		releaseKeepScreenOn();
	}

	protected void saveStatetoDb() {
		// TODO save last state of book to db
	}

	protected void updateBookNeedReed() {
		prepareBookForReader();
	}

	@SuppressLint("NewApi")
	protected void prepareBookForReader() {
		if (prepareBookTask != null
				&& prepareBookTask.getStatus() == Status.RUNNING) {
			prepareBookTask.cancel(true);
		}
		prepareBookTask = new PrepareBookTask(this);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			prepareBookTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			prepareBookTask.execute();
		}
	}

	protected void doBack() {
		finish();
	}

	protected void prepareCacheIfNeed() {

	}

	protected abstract void setupViews();

	protected void prepareOtherInfo() {

	}

	protected abstract EpubBook getBookRead();

	protected final class PrepareBookTask extends AsyncTask<Void, Void, Void> {
		protected WeakReference<ReaderBaseActivity> readRfc;
		
		protected boolean isCancel = false;

		public PrepareBookTask(ReaderBaseActivity read) {
			readRfc = new WeakReference<ReaderBaseActivity>(read);
		}

		@Override
		protected Void doInBackground(Void... params) {
			ReaderBaseActivity read = readRfc.get();

			// Prepare environment
			try {
				read.mEngine = new EpubEngine(read.mBook.bookType,
						read.mBook.path, null, false);
				AssetManager asm = read.getAssets();
				//InputStream is = asm.open("drm.properties");
				//read.mEngine.setInputStreamDRMProperties(is);
//				if (read.mBook.isEncrypt) {
//					read.mEngine.setRightObject(read.mBook.rightObject);
//					read.mEngine.setIMEI(EpubReaderUtil.getIMEI(read));
//				}

				// Get table of content
				// Parse table of content from file
				read.mEngine.parseBookInfo();
				read.mToc = read.mEngine.parseTableOfContent();
				if (read.mToc == null || read.mToc.getTotalSize() == 0) {
					read.doBack();
				}

				// Check baseUrl
				String firstPath = read.mToc.getChapter(0).getUrl();
				if (firstPath.indexOf("/") > 0) {
					String baseUrl = read.mEngine.getBaseUrl();
					baseUrl = baseUrl.substring(baseUrl.length() - 1)
							.equalsIgnoreCase("/") ? baseUrl : baseUrl + "/";
					String pathOfChapter = firstPath.substring(0,
							firstPath.lastIndexOf("/"));
					pathOfChapter = pathOfChapter.substring(0, 1)
							.equalsIgnoreCase("/") ? pathOfChapter.substring(1)
							: pathOfChapter;
					read.mEngine.setBaseUrl(baseUrl + pathOfChapter);
				}
			} catch (Exception e) {
				e.printStackTrace();
				read.doBack();
			}

			read.prepareCacheIfNeed();
			read.prepareOtherInfo();

			// Update view count & visited time
			read.mBook.totalPage = read.getTotalBookPage();
			if (isCancelled()) {
				isCancel = true;
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			ReaderBaseActivity read = readRfc.get();
			read.showProgressDialogPrepareBook();
		}

		@Override
		protected void onPostExecute(Void result) {
			ReaderBaseActivity read = readRfc.get();
			if (read == null || read.isFinishing() || isCancel) {
				read.progressDialog.dismiss();
				return;
			}
			read.progressDialog.dismiss();
			mState.totalChapter = mToc.getTotalSize();
			read.setupViews();
		}
	}

	protected int getTotalBookPage() {
		if (mToc != null && mToc.getChapter(mToc.getTotalSize() - 1) != null) {
			return mToc.getChapter(mToc.getTotalSize() - 1).getEndPage();
		}
		return 0;
	}

	protected void showProgressDialogPrepareBook() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}

		progressDialog
				.setMessage(getString(R.string.prepare_book_data));
		progressDialog.setCancelable(true);
		progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						progressDialog.dismiss();
						finish();
						return true;
					}
				}
				return false;
			}
		});
		progressDialog.show();
	}

	@Override
	protected void onDestroy() {
		if (prepareBookTask != null) {
			prepareBookTask.cancel(true);
		}
		super.onDestroy();
	}

	/**
	 * Add Asynctask run, so can auto cancel when destroy
	 * 
	 * @param task
	 */
	protected void addTaskRuning(AsyncTask<?, ?, ?> task) {
		synchronized (listTaskRunning) {
			listTaskRunning.add(task);
		}
	}

}
