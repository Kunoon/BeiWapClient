package com.bei.pclient.webview;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class JSInterface {
	
	private Handler handler;
	
	public JSInterface(Handler handler) {
		// TODO Auto-generated constructor stub
		this.handler = handler;
	}
	
	/**
	 * 设置请求来源
	 * @return
	 */
	@JavascriptInterface
    public String onSetClient() {
        Log.i("$$$$$$$ onSetClient ===> ", "android");
        return "android";
    }
	
//	/**
//	 * 获取页面header
//	 * @param title
//	 * @param eventId umeng事件ID ：hotels 酒店、scenics 景区
//	 * @param key 参数名
//	 * @param value 参数值
//	 */
//	@JavascriptInterface
//    public void onGetHeader(String title, String eventId, String key, String value) {
//        Log.i("$$$$$$$ onGetHeader ===> ", title);
//        HashMap<String,String> map = new HashMap<String,String>();
//        map.put("type", key);
//        map.put("value", value);
//        MobclickAgent.onEvent(mContext, eventId, map);
//        handler.obtainMessage(0, title).sendToTarget();
//    }
	@JavascriptInterface
	public void onGetHeader(String title) {
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$==> " + title);
		handler.obtainMessage(0, title).sendToTarget();
	}
	
    @JavascriptInterface
    public String onIDButtonClick(String text) {
        final String str = text;
        Log.i("$$$$$$$ onIDButtonClick ===> ", str);
        return "This text is returned from Java layer.  js text = " + text;
    }
    
    @JavascriptInterface
    public void onQRButtonClick(String url, int width, int height) {
        final String str = "onImageClick: text = " + url + "  width = " + width + "  height = " + height;
        Log.i("$$$$$$$ onImageClick ===> ", str);
    }
    
    @JavascriptInterface
    public void onGoButtonClick() {
        Log.i("$$$$$$$ onImageClick ===> ", "正在跳转到其他页面！！！");
    }
}
