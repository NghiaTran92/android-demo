package com.viki.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewParent;



public class WebView extends android.webkit.WebView {

	public WebView(Context context, AttributeSet attrs, int defStyleAttr,
			boolean privateBrowsing) {
		super(context, attrs, defStyleAttr, privateBrowsing);
		// TODO Auto-generated constructor stub
	}

	public WebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public WebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public WebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback;

    @Override
    public ActionMode startActionMode(Callback callback) {
        ViewParent parent = getParent();
        if (parent == null) {
            return null;
        }
        mActionModeCallback = new CustomActionModeCallback();
        return parent.startActionModeForChild(this, mActionModeCallback);
    }
    
    private class CustomActionModeCallback implements ActionMode.Callback {
        
        // Everything up to this point is the same as in the question

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearFocus(); // This is the new code to remove the text highlight
             mActionMode = null;
        }

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub
			return false;
		}
    }

}
