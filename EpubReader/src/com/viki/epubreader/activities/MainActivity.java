//package com.viki.epubreader.activities;
//
//
//import java.util.Calendar;
//import java.util.List;
//
//import android.content.res.Resources;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v4.view.MotionEventCompat;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnTouchListener;
//import android.webkit.JavascriptInterface;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.widget.Toast;
//
//import com.testfunction.TestZip;
//import com.viki.core.parser.TableOfContent.Chapter;
//import com.viki.drm.client.utils.StringUtil;
//import com.viki.epubreader.R;
//import com.viki.epubreader.ReaderBaseActivity;
//import com.viki.epubreader.objects.BookState;
//import com.viki.epubreader.objects.EpubBook;
//import com.viki.epubreader.objects.EpubBook.BOOK_TYPE;
//import com.viki.epubreader.util.Constant;
//
//public class MainActivity extends ReaderBaseActivity {
//
//	private WebView mBrowser;
//	private DisplayMetrics mDisplayMetrics;
//	private List<Chapter> mListChapters;
//	private String TAG="MainActivity";
//	
//	
//	private int currentPage=0; 
//	private int delta=0;
//	private float posXFirst=0;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		mBook=getBookRead();
//		
//		if(mBook!=null){
//			updateBookNeedReed();
//		}
//	}
//
//	@Override
//	protected EpubBook getBookRead() {
//		// TODO Auto-generated method stub
//		EpubBook book=new EpubBook();
//		book.title="test book";
//		book.path=TestZip.path;
//		book.id=123;
//		book.bookType=BOOK_TYPE.VEF;
//		
//		mState=new BookState();
//		mState._id=123;
//		
//		return book;
//	}
//	
//	@Override
//	protected void setupViews() {
//		// TODO Auto-generated method stub
//		Resources res=getResources();
//		mDisplayMetrics=res.getDisplayMetrics();
//		mBrowser=(WebView)findViewById(R.id.wvBrower);
//		WebSettings webSetting=mBrowser.getSettings();
//		webSetting.setJavaScriptEnabled(true);
//		webSetting.setDefaultTextEncodingName("utf-8");
//		mBrowser.clearCache(true);
//		mBrowser.addJavascriptInterface(new AndroidBridge(), "android");
//		mBrowser.setWebChromeClient(new WebChromeClient());
//		mBrowser.setTag(null);
//		if (mListChapters == null || mListChapters.size() == 0) {
//			mListChapters = mToc.getChapterList();
//		}
//		
//		openChapter(mState.getCurChapter()+2);
//		
//	}
//	private void openChapter(int chapNo) {
//		showProgressDialogPrepareBook();
//
//		String html = this.getContentChapter(chapNo);
//		if (TextUtils.isEmpty(html) || html.equalsIgnoreCase("null")) {
//			progressDialog.dismiss();
//			finish();
//		}
//
//		mState.setCurChapter(chapNo);
//		String dayNight = "";
//		//displayIconDayNight();
//		
//		int height = Math.round(mDisplayMetrics.heightPixels
//				/ mDisplayMetrics.density);
//		int width = Math.round(mDisplayMetrics.widthPixels
//				/ mDisplayMetrics.density);
//
//		String margin = String.valueOf(getResources().getInteger(
//				R.integer.book_default_margin));
//		int pageGap = Integer.valueOf(margin) * 2;
//		int colmnWidth = width - pageGap;
//		height = height -  Math.round(pageGap*Math.round(mDisplayMetrics.density));
//		String html_head = Constant.HTML_HEAD.replace("?px",
//				this.getCurrentFontSize()).replace("?font", "times new roman")
//				+ dayNight + Constant.HTML_CLOSE_HEAD;
//		html_head = html_head.replace("[column-width]",
//				String.valueOf(colmnWidth));
//		html_head = html_head
//				.replace("[column-height]", String.valueOf(height));
//		html_head = html_head.replace("[column-gap]", String.valueOf(pageGap));
//		html_head = html_head.replace("[column-padding]", margin);
//		
//		
//		mBrowser.setBackgroundColor(Color.WHITE);
//		mBrowser.clearCache(true);
//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append(html_head);
//		stringBuilder.append(html);
//		stringBuilder.append(Constant.HTML_END);
//		mBrowser.setVisibility(View.VISIBLE);
//		mBrowser.setOnTouchListener(new OnTouchListener() {
//			
//			 private static final int MAX_CLICK_DURATION = 200;
//			 private static final int MAX_LONG_CLICK_DURATION=2000;
//			 private static final int epsilonClick=4;
//			 private long startClickTime;
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				float posX=event.getX();
//				float posY=event.getY();
//				
//				String position="--X="+posX+"--Y="+posY;
//				 int action = MotionEventCompat.getActionMasked(event);
//			        
//				    switch(action) {
//				        case (MotionEvent.ACTION_DOWN) :
//				            Log.d(TAG,"Action was DOWN"+position);
//				        	startClickTime=Calendar.getInstance().getTimeInMillis();
//				        	posXFirst=posX;
//				            return true;
//				        case (MotionEvent.ACTION_MOVE) :
//				           // Log.d(TAG,"Action was MOVE"+position);
//				        	delta=(int)(posXFirst-posX);
//				        	mBrowser.loadUrl("javascript:scrollNear("+delta+")");
//				            return true;
//				        case (MotionEvent.ACTION_UP) :
//				            Log.d(TAG,"Action was UP"+position);
//				        	long clickDuration=Calendar.getInstance().getTimeInMillis()-startClickTime;
//				        	if(clickDuration<MAX_CLICK_DURATION && Math.abs(posXFirst-posX)<=epsilonClick){
//				        		NextOrPrevPage(posX);
//				        	}
////				        	else if(clickDuration>=MAX_LONG_CLICK_DURATION && Math.abs(posXFirst-posX)<=epsilonClick){
////				        		//mBrowser.setOnTouchListener(null);
////				        		//v.setSelected(true);
////				        		mBrowser.startActionMode(null);
////				        		
////				        	}
//				        	else{
//					        	mBrowser.loadUrl("javascript:activeSetCurrPos()");
//					        	mBrowser.loadUrl("javascript:reloadExactlyPage()");
//				        	}
//				            return true;
//				        case (MotionEvent.ACTION_CANCEL) :
//				            Log.d(TAG,"Action was CANCEL"+position);
//				            return true;
//				        case (MotionEvent.ACTION_OUTSIDE) :
//				            Log.d(TAG,"Movement occurred outside bounds " +
//				                    "of current screen element"+position);
//				            return true;      
//				        default : 
//				            return false;
//				    }      
//			}
//		});
//		mBrowser.loadDataWithBaseURL("file://" + mEngine.getBaseUrl() + "/",
//				stringBuilder.toString(), "text/html", "utf-8", null);
//	}
//	
//	
//	/**
//	 *  chia deu 4 phan bang nhau. Neu o phan thu 1 va phan thu 4 thi chuye?n page con lai danh cho menu(phan thu 2 va thu 3)
//	 * */ 
//	protected void NextOrPrevPage(float posX){
//		int widthClick=mDisplayMetrics.widthPixels/4;
//		
//		// next and prev page click
//		if((posX>=0&&posX<=widthClick) ||(posX>=mDisplayMetrics.widthPixels-widthClick&& posX<=mDisplayMetrics.widthPixels)){
//			// prev click
//			if(posX>=0&&posX<=widthClick){
//				if(currentPage>=0){
//					mBrowser.loadUrl(String.format("javascript:setPageNumber(%d)", currentPage-1));
//				}
//			}
//			// next click
//			else if(posX>=mDisplayMetrics.widthPixels-widthClick&& posX<=mDisplayMetrics.widthPixels){
//				if(currentPage<mState.totalPagesOfChapter){
//					mBrowser.loadUrl(String.format("javascript:setPageNumber(%d)", currentPage+1));
//				}
//			}
//		}
//		// menu click
//		else{
//			
//		}
//	}
//	
//	
//	
//	public String getContentChapter(int chapNo) {
//		
//			String decryptContent = null;
//			Chapter chapter = mListChapters.get(chapNo - 1);
//			if (chapter != null) {
//				String chapterPath = chapter.getUrl();
//				chapterPath = mEngine.getBasePath() + "/" + chapterPath;
//				String html;
//				if (mBook.isEncrypt) {
//					html = mEngine.decryptContentChapter(chapterPath);
//				} else {
//					html = mEngine.getContentChapterWithoutDecrypt(chapterPath);
//				}
//
//				decryptContent = StringUtil.extractBodyHTML(html);
//				String style = StringUtil.extractStyleSheetForReader(html);
//				decryptContent = style + decryptContent;
//
//				if (decryptContent != null
//						&& !decryptContent.equalsIgnoreCase("null")
//						&& mState.getCurChapter() == chapNo) {
//				//	mCacheContent.put(chapNo, decryptContent);
//				} else {
//					//mCacheContent.clear();
//				}
//			}
//			return decryptContent;
//		
//	}
//	
//	public String getCurrentFontSize() {
//		String fontSize=""; /*= pref.getString(
//				SettingActivity.KEY_FONT_SIZE_PREFERENCE, "medium");*/
//		int nFontSize = 20;
//
//		if (fontSize.equalsIgnoreCase("4")) {
//			nFontSize = 16;
//		} else if (fontSize.equalsIgnoreCase("1")) {
//			nFontSize = 20;
//		} else if (fontSize.equalsIgnoreCase("3")) {
//			nFontSize = 24;
//		} else if (fontSize.equalsIgnoreCase("2")) {
//			nFontSize = 28;
//		}
//		return String.valueOf(nFontSize) + "px";
//	}
//
//
//
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	public class AndroidBridge{
//		private String TAG="AndroidBridge";
//		@JavascriptInterface
//		public void logWebview(String message){
//			Log.e(TAG, message);
//		}
//		
//		@JavascriptInterface
//		public void setPageCount(int pageCount) {
//			Log.v("AndroidBridge setPageCount", "do nothing ");
//		}
//		
//		@JavascriptInterface
//		public void onPagedChanged(int currentPage){
//			MainActivity.this.currentPage=currentPage;
//		}
//		
//		@JavascriptInterface
//		public void onPaged(final int pageCount) {
//			Log.v(TAG, "onPaged:"+pageCount);
//			
//			if (mToc == null || mToc.getTotalSize() == 0)
//				return;
//			
//			//mSeekBar.setMax(mState.totalPages);
//			mState.totalPagesOfChapter = pageCount;
//			if (mBrowser.getTag() != null) {
//				mState.curPage = (int) Math.floor(pageCount
//						* mState.getCurPercentage() / 100);
//				mBrowser.setTag(null);
//			}
//			runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
////					moveToPageNoAnimation();
////					updatePageInfo();
//					progressDialog.dismiss();
//					mBrowser.setVisibility(View.VISIBLE);
//				}
//			});
//		}
//		
//	}
//	
//}
