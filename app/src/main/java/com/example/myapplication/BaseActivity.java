package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class BaseActivity extends Activity {

	// 弹出等待提示框
	ProgressDialog waitDialog=null;
	AsyncTask waitTask=null;
	public TextView txtTip=null;
	public Dialog showWaitTranslate(String content, final AsyncTask task)
	{

		if(waitDialog!=null && waitDialog.isShowing())
		{
			try {
				if(waitTask!=null && !waitTask.isCancelled())
				{
					waitTask.cancel(false);
				}
			} catch (Exception e) {
			}
			waitDialog.setMessage(content);
		}else
		{
			waitTask=task;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				waitDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
			else
				waitDialog = new ProgressDialog(this);
			waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			waitDialog.setCancelable(true);
			waitDialog.setMessage(content);
			waitDialog.setCanceledOnTouchOutside(false);
			waitDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					System.out.println("已取消加载数据");
					if(task!=null)
						task.cancel(true);
				}
			});
			try {
				if(waitDialog.isShowing()){
					waitDialog.dismiss();
				}
				waitDialog.show();
			} catch (Exception e) {
			}
		}
		return waitDialog;
	}
	// 隐藏等待提示框
	public void hideWait() {
		waitHandler.sendEmptyMessage(0);
	}
	Handler waitHandler=new Handler()
	{
		public void handleMessage(Message msg) {
			try {
				if(waitDialog!=null && waitDialog.isShowing())
				{
					waitDialog.hide();
					waitDialog=null;
					if(waitTask!=null && !waitTask.isCancelled())
					{
						waitTask.cancel(false);
					}
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		};
	};
	Toast toast=null;
	public void showTip(String text)
	{
		if(StringUtil.stringNotNull(text) && text.contains("br"))
			text=text.replaceAll("<br>", "\r\n").replaceAll("<br/>", "\r\n").replaceAll("<br />", "\r\n");
		int time=1000;
		if(text.length()>=30)
			time=4000;
		else if(text.length()>=20)
			time=3000;
		else if(text.length()>=13)
			time=2500;
		else if(text.length()>=10)
			time=1500;
		if(toast==null)
			toast =Toast.makeText(this, text, time);
		else
			toast.setText(text);
		toast.show();
	}

}
