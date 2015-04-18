package com.ppkj.mindrays.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ppkj.mindrays.R;

public class ActionSheet {

    public interface OnActionSheetSelected {
	void onClickSheet(int whichButton);
    }

    private ActionSheet() {
    }

    public static Dialog showSheet(Context context,
	    final OnActionSheetSelected actionSheetSelected,
	    OnCancelListener cancelListener) {
	final Dialog dlg = new Dialog(context, R.style.ActionSheet);
	LayoutInflater inflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	LinearLayout layout = (LinearLayout) inflater.inflate(
		R.layout.actionsheet, null);
	final int cFullFillWidth = 10000;
	layout.setMinimumWidth(cFullFillWidth);

	GridView mContent = (GridView) layout.findViewById(R.id.content);
	TextView mCancel = (TextView) layout.findViewById(R.id.cancel);
	mContent.setAdapter(new SheetAdapter(context));
	mContent.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view,
		    int position, long id) {
		actionSheetSelected.onClickSheet(position);
		dlg.dismiss();
	    }
	});

	mCancel.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		actionSheetSelected.onClickSheet(-1);
		dlg.dismiss();
	    }
	});

	Window w = dlg.getWindow();
	WindowManager.LayoutParams lp = w.getAttributes();
	lp.x = 0;
	final int cMakeBottom = -1000;
	lp.y = cMakeBottom;
	lp.gravity = Gravity.BOTTOM;
	dlg.onWindowAttributesChanged(lp);
	// dlg.setCanceledOnTouchOutside(false);
	if (cancelListener != null)
	    dlg.setOnCancelListener(cancelListener);

	dlg.setContentView(layout);
	dlg.show();

	return dlg;
    }

    private static class SheetAdapter extends BaseAdapter {
	private int[] logos = new int[] { R.drawable.logo_sinaweibo,
		R.drawable.logo_wechat, R.drawable.logo_wechatmoments,
		R.drawable.logo_qzone, R.drawable.logo_qq };
	private String[] names = new String[] { "新浪微博", "微信", "朋友圈", "QQ空间",
		"QQ" };
	private Context c;

	public SheetAdapter(Context c) {
	    this.c = c;

	}

	@Override
	public int getCount() {
	    // TODO Auto-generated method stub
	    return names.length;
	}

	@Override
	public Object getItem(int position) {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public long getItemId(int position) {
	    // TODO Auto-generated method stub
	    return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    Holder holder;
	    if (convertView == null) {
		convertView = LayoutInflater.from(c).inflate(
			R.layout.item_share, null);
		holder = new Holder();
		holder.logo = (ImageView) convertView
			.findViewById(R.id.share_image);
		holder.name = (TextView) convertView
			.findViewById(R.id.share_name);
		convertView.setTag(holder);
	    } else {
		holder = (Holder) convertView.getTag();
	    }
	    holder.logo.setImageResource(logos[position]);
	    holder.name.setText(names[position]);
	    return convertView;
	}

	private class Holder {

	    private ImageView logo;
	    private TextView name;

	}
    }
}
