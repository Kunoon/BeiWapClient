package com.bei.wapclient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.bei.pclient.R;
import com.bei.pclient.webview.JSInterface;
import com.bei.pull.lib.PullToRefreshWebView;
import com.bei.wx.WXUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewConfiguration;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class BeiClientActivity extends ActionBarActivity {
	private ActionBar actionBar;
	private MenuItem mItemHome, mItemShare, mItemCopyURL, mItemShowBrowser, mItemTelServer, 
					 mItemShareWechatFriends, mItemShareWechatMoments, mItemShareQQFriends, 
					 mItemShareQQzone, mItemGoBack;
	private static final String URLB = "http://192.168.1.122:8888/wap";
	private PullToRefreshWebView mPullRefreshWebView;
	private WebView mWebView;
	private WXUtil wxUtil;
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_actionbarbctivity);

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.ticon);

		setOverflowShowingAlways();
		
		MobclickAgent.setDebugMode( true );
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		wxUtil = new WXUtil();
		wxUtil.regToWx(getApplicationContext());
		
		mPullRefreshWebView = (PullToRefreshWebView) findViewById(R.id.webview);
		mWebView = mPullRefreshWebView.getRefreshableView();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("AAA START");
		MobclickAgent.onResume(this);
		
		// 修改ua使得web端正确判断
		String ua = mWebView.getSettings().getUserAgentString();
		mWebView.getSettings().setUserAgentString(ua+"; BeiZ_Wap/ECCF3EFE2A3E11DAED68A4F63B8E2D14");
        mWebView.setWebViewClient(webViewClient);
        mWebView.setDownloadListener(new MyWebViewDownLoadListener()); 
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JSInterface(handler), "beizhu");
        mWebView.loadUrl(URLB);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("AAA END");
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		wxUtil.unregFromWx();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Object mHelperUtils;
                    Toast.makeText(this, R.string.exit_two_click, Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
            } else {
                    finish();
            }
            return true;
    }
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admain, menu);
		return true;
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub	
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) { }
			}
		}
		return super.onMenuOpened(featureId, menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		mItemHome = menu.findItem(R.id.action_home);
		mItemCopyURL = menu.findItem(R.id.action_copy_url);
		mItemShowBrowser = menu.findItem(R.id.action_ie);
		mItemTelServer = menu.findItem(R.id.action_tel);
		mItemShareWechatFriends = menu.findItem(R.id.action_share_wechat_friends);
		mItemShareWechatMoments = menu.findItem(R.id.action_share_wechat_moments);
		mItemShareQQFriends = menu.findItem(R.id.action_share_qq_friends);
		mItemShareQQzone = menu.findItem(R.id.action_share_qq_qzone);
		
//		menu.findItem(R.id.action_home).setVisible(!isShare);
//		menu.findItem(R.id.action_share).setVisible(!isShare);
//		menu.findItem(R.id.action_copy_url).setVisible(!isShare);
//		menu.findItem(R.id.action_ie).setVisible(!isShare);
//		menu.findItem(R.id.action_tel).setVisible(!isShare);
//		menu.findItem(R.id.action_share_wechat_friends).setVisible(isShare);
//		menu.findItem(R.id.action_share_wechat_moments).setVisible(isShare);
//		menu.findItem(R.id.action_share_qq_friends).setVisible(isShare);
//		menu.findItem(R.id.action_share_qq_qzone).setVisible(isShare);
//		menu.findItem(R.id.action_goback).setVisible(isShare);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		String str = null;
		try {
			str =  mWebView.getUrl().toString();
		} catch (Exception e) {
			
		}
		switch (item.getItemId()) {
		case R.id.action_home:
			mWebView.clearView();
			mWebView.loadUrl(URLB);
			setTitle(R.string.home);
			return true;
//		case R.id.action_share:
//			openOptionsMenu();
//			return true;
		case R.id.action_copy_url:
			Toast.makeText(getApplicationContext(), R.string.copy_url_success, Toast.LENGTH_SHORT).show();
			ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			if(str != null && str.length() != 0)
				cmb.setText(str);
			else
				Toast.makeText(getApplicationContext(), R.string.get_url_fail, Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_ie:
			Intent intt = new Intent();        
			intt.setAction("android.intent.action.VIEW");
			if(str != null && str.length() != 0) {
		        Uri content_url = Uri.parse(str);
		        intt.setData(content_url);  
		        startActivity(intt);
			} else {
				Toast.makeText(getApplicationContext(), R.string.get_url_fail, Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_tel:
			Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+getString(R.string.calling)));
			startActivity(intent);
			return true;
		case R.id.action_share_wechat_friends:
			if(wxUtil.isInstalled()) {
				if(str != null && str.length() != 0)
					wxUtil.sendWebPage(str, mWebView.getTitle(), getResources(), SendMessageToWX.Req.WXSceneSession);
				else
					Toast.makeText(getApplicationContext(), R.string.get_url_fail, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), R.string.uninstall_wechat, Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_share_wechat_moments:
			if(wxUtil.isInstalled()) {
				if(str != null && str.length() != 0)
					wxUtil.sendWebPage(str, mWebView.getTitle(), getResources(), SendMessageToWX.Req.WXSceneTimeline);
				else
					Toast.makeText(getApplicationContext(), R.string.get_url_fail, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), R.string.uninstall_wechat, Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_share_qq_friends:
			mWebView.goBack();
			return true;
		case R.id.action_share_qq_qzone:
			mWebView.goBack();
			return true;
//		case R.id.action_goback:
//			return true;
		case android.R.id.home:
			mWebView.goBack();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private WebViewClient webViewClient = new WebViewClient(){   
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			if(url.equals(URLB) || url.equals(URLB + "/scenic/wap_index.html")
					|| url.equals(URLB + "/scenic/wap_index"))
				actionBar.hide();
			else
				actionBar.show();
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
		}
	};
	
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if(msg.obj.toString().equals("首页"))
					actionBar.hide();
				else
					actionBar.show();
				setTitle(msg.obj.toString());
				break;
			default:
				break;
			}
		}
	};
	
	private class MyWebViewDownLoadListener implements DownloadListener{
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {        	
        	Log.i("tag", "url="+url);        	
        	Log.i("tag", "userAgent="+userAgent);
        	Log.i("tag", "contentDisposition="+contentDisposition);        	
        	Log.i("tag", "mimetype="+mimetype);
        	Log.i("tag", "contentLength="+contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);        	 
        }
    }

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
