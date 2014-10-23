package com.bei.wx;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bei.pclient.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXUtil {

	// APP_ID 替换为你的应用从官方网站申请到的合法appId
	private static final String APP_ID = "wxcde5d7d6d8cb41b2";
	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI wxAPI;
	
	public WXUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 注册到微信
	 * @param context
	 */
	public void regToWx(Context context) {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		wxAPI = WXAPIFactory.createWXAPI(context, APP_ID, true);
		// 将应用的appid注册到微信
		wxAPI.registerApp(APP_ID);
	}
	
	/**
	 * 从微信注销
	 */
	public void unregFromWx() {
		wxAPI.unregisterApp();
	}
	
	/**
	 * 判断是否安装微信
	 * @return
	 */
	public boolean isInstalled() {
		return wxAPI.isWXAppInstalled();
	}
	
	/**
	 * 分享web页面到微信
	 * @param urlStr
	 * @param webTitle
	 * @param resources
	 * @param where
	 */
	public void sendWebPage(String urlStr, String webTitle, Resources resources, int where) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = urlStr;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "贝竹一证通无线接入端";
		msg.description = webTitle + "\n［贝竹一证通，旅游很轻松!］";
		Bitmap thumb = BitmapFactory.decodeResource(resources, R.drawable.webicon);
		msg.thumbData = Util.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = where;
		wxAPI.sendReq(req);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
}
