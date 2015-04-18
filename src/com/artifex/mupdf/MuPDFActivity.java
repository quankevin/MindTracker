package com.artifex.mupdf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ppkj.mindrays.R;

/**
 * @author kiritor </br> 重构的进度对话框
 */
class ProgressDialogX extends ProgressDialog {
	public ProgressDialogX(Context context) {
		super(context);
	}

	/** 用于控制对话框的显示与关闭 */
	private boolean mCancelled = false;

	public boolean isCancelled() {
		return mCancelled;
	}

	@Override
	public void cancel() {
		mCancelled = true;
		super.cancel();
	}
}

/**
 * @author Kiritor</br> PDF阅读的主界面,一切操作都是基于这个界面的
 */
public class MuPDFActivity extends Activity {
	/** 文本的三种状态 */
	private enum LinkState {
		DEFAULT, HIGHLIGHT, INHIBIT
	};

	private final int TAP_PAGE_MARGIN = 5;
	/** 延迟搜索 */
	private static final int SEARCH_PROGRESS_DELAY = 200;
	private MuPDFCore core;
	/** 文件名 */
	private String mFileName;
	/** 阅读区,用于显示pdf文档的内容 */
	private ReaderView mDocView;
	/** 按钮视图 */
	private View mButtonsView;
	private boolean mButtonsVisible;
	private EditText mPasswordView;
	private TextView mFilenameView;
	private SeekBar mPageSlider;
	private TextView mPageNumberView;
	private ImageButton mOutlineButton;

	// private SearchTaskResult mSearchTaskResult;
	private AlertDialog.Builder mAlertBuilder;
	private LinkState mLinkState = LinkState.DEFAULT;
	private final Handler mHandler = new Handler();

	private MuPDFCore openFile(String path) {
		int lastSlashPos = path.lastIndexOf('/');
		mFileName = new String(lastSlashPos == -1 ? path
				: path.substring(lastSlashPos + 1));
		System.out.println("Trying to open " + path);
		try {
			core = new MuPDFCore(path);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		return core;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAlertBuilder = new AlertDialog.Builder(this);

		if (core == null) {
			core = (MuPDFCore) getLastNonConfigurationInstance();

			if (savedInstanceState != null
					&& savedInstanceState.containsKey("FileName")) {
				mFileName = savedInstanceState.getString("FileName");
			}
		}
		if (core == null) {
			Intent intent = getIntent();
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				Uri uri = intent.getData();
				if (uri.toString().startsWith("content://media/external/file")) {
					/*
					 * Handle view requests from the Transformer Prime's file
					 * manager Hopefully other file managers will use this same
					 * scheme, if not using explicit paths.
					 */
					Cursor cursor = getContentResolver().query(uri,
							new String[] { "_data" }, null, null, null);
					if (cursor.moveToFirst()) {
						uri = Uri.parse(cursor.getString(0));
					}
				}
				core = openFile(Uri.decode(uri.getEncodedPath()));
			}
			if (core != null && core.needsPassword()) {
				requestPassword(savedInstanceState);
				return;
			}
		}
		if (core == null) {
			AlertDialog alert = mAlertBuilder.create();
			alert.setTitle(R.string.open_failed);
			alert.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			alert.show();
			return;
		}

		createUI(savedInstanceState);
	}

	public void requestPassword(final Bundle savedInstanceState) {
		mPasswordView = new EditText(this);
		mPasswordView.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mPasswordView
				.setTransformationMethod(new PasswordTransformationMethod());

		AlertDialog alert = mAlertBuilder.create();
		alert.setTitle(R.string.enter_password);
		alert.setView(mPasswordView);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (core.authenticatePassword(mPasswordView.getText()
								.toString())) {
							createUI(savedInstanceState);
						} else {
							requestPassword(savedInstanceState);
						}
					}
				});
		alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alert.show();
	}

	public void createUI(Bundle savedInstanceState) {

		if (core == null)
			return;
		// Now create the UI.
		// First create the document view making use of the ReaderView's
		// internal
		// gesture recognition
		mDocView = new ReaderView(this) {
			private boolean showButtonsDisabled;

			public boolean onSingleTapUp(MotionEvent e) {
				if (e.getX() < super.getWidth() / TAP_PAGE_MARGIN) {
					super.moveToPrevious();
				} else if (e.getX() > super.getWidth() * (TAP_PAGE_MARGIN - 1)
						/ TAP_PAGE_MARGIN) {
					super.moveToNext();
				} else if (!showButtonsDisabled) {
					int linkPage = -1;
					if (mLinkState != LinkState.INHIBIT) {
						MuPDFPageView pageView = (MuPDFPageView) mDocView
								.getDisplayedView();
						if (pageView != null) {
							// XXX linkPage = pageView.hitLinkPage(e.getX(),
							// e.getY());
						}
					}

					if (linkPage != -1) {
						mDocView.setDisplayedViewIndex(linkPage);
					} else {
						if (!mButtonsVisible) {
							showButtons();
						} else {
							hideButtons();
						}
					}
				}
				return super.onSingleTapUp(e);
			}

			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				if (!showButtonsDisabled)
					hideButtons();

				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			public boolean onTouchEvent(MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					showButtonsDisabled = false;

				return super.onTouchEvent(event);
			}

			protected void onSettle(View v) {
				// When the layout has settled ask the page to render
				// in HQ
				((PageView) v).addHq();
			}

			protected void onUnsettle(View v) {
				// When something changes making the previous settled view
				// no longer appropriate, tell the page to remove HQ
				((PageView) v).removeHq();
			}
		};

		mDocView.setAdapter(new MuPDFPageAdapter(this, core));
		// Make the buttons overlay, and store all its
		// controls in variables
		makeButtonsView();

		// Set the file-name text
		mFilenameView.setText(mFileName);

		// Activate the seekbar
		mPageSlider
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) {
						mDocView.setDisplayedViewIndex(seekBar.getProgress());
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						updatePageNumView(progress);
					}
				});

		if (core.hasOutline()) {
			mOutlineButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					OutlineItem outline[] = core.getOutline();
					if (outline != null) {
						OutlineActivityData.get().items = outline;
						Intent intent = new Intent(MuPDFActivity.this,
								OutlineActivity.class);
						startActivityForResult(intent, 0);
					}
				}
			});
		} else {
			mOutlineButton.setVisibility(View.GONE);
		}

		// Reenstate last state if it was recorded
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		mDocView.setDisplayedViewIndex(prefs.getInt("page" + mFileName, 0));

		if (savedInstanceState == null
				|| !savedInstanceState.getBoolean("ButtonsHidden", false))
			showButtons();

		// Stick the document view and the buttons overlay into a parent view
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(mDocView);
		layout.addView(mButtonsView);
		layout.setBackgroundResource(R.drawable.tiled_background);
		// layout.setBackgroundResource(R.color.canvas);
		setContentView(layout);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode >= 0)
			mDocView.setDisplayedViewIndex(resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	public Object onRetainNonConfigurationInstance() {
		MuPDFCore mycore = core;
		core = null;
		return mycore;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mFileName != null && mDocView != null) {
			outState.putString("FileName", mFileName);

			// Store current page in the prefs against the file name,
			// so that we can pick it up each time the file is loaded
			// Other info is needed only for screen-orientation change,
			// so it can go in the bundle
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page" + mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}

		if (!mButtonsVisible)
			outState.putBoolean("ButtonsHidden", true);

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mFileName != null && mDocView != null) {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page" + mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}
	}

	public void onDestroy() {
		if (core != null)
			core.onDestroy();
		core = null;
		super.onDestroy();
	}

	void showButtons() {
		if (core == null)
			return;
		if (!mButtonsVisible) {
			mButtonsVisible = true;
			// Update page number text and slider
			int index = mDocView.getDisplayedViewIndex();
			updatePageNumView(index);
			mPageSlider.setMax(core.countPages() - 1);
			mPageSlider.setProgress(index);

		}
	}

	void hideButtons() {
		if (mButtonsVisible) {
			mButtonsVisible = false;

		}
	}

	void updatePageNumView(int index) {
		if (core == null)
			return;
		mPageNumberView.setText(String.format("%d/%d", index + 1,
				core.countPages()));
	}

	void makeButtonsView() {
		mButtonsView = getLayoutInflater().inflate(R.layout.pdf_buttons, null);
		mFilenameView = (TextView) mButtonsView.findViewById(R.id.docNameText);
		mPageSlider = (SeekBar) mButtonsView.findViewById(R.id.pageSlider);
		mPageNumberView = (TextView) mButtonsView.findViewById(R.id.pageNumber);
		mOutlineButton = (ImageButton) mButtonsView
				.findViewById(R.id.outlineButton);
		mPageNumberView.setVisibility(View.INVISIBLE);
		mPageSlider.setVisibility(View.INVISIBLE);
	}

	public static Bitmap backBitmap = null;

}
