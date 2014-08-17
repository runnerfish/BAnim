package com.ruijie.mydemo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class CertifyActivity extends BaseActivity {
	
	public static final String URL = "http://tieba.baidu.com/";
	public static final int TYPE_WIFI = 1;
	public static final int TYPE_MOBILE = 0;
	public static final int TYPE_NOT_CONNETCED = -1;
	
	private TextView textView; 
	private WebView webView;
	private String currentUrl;
	
	@Override
	public void setView() {
		setContentView(R.layout.activity_wifi_certify);
	}

	@SuppressLint("JavascriptInterface") @Override
	public void initView() {
		textView = (TextView) findViewById(R.id.tv_welcome);
		webView = (WebView) findViewById(R.id.wv_certify);
		webView.getSettings().setJavaScriptEnabled(true);  
		webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");  
		webView.setWebViewClient(new MyWebViewClient());  
		
		if (checkInternetConnection() == TYPE_NOT_CONNETCED) {
			webView.loadUrl("http://www.baidu.com");  
		}else if (checkInternetConnection() == TYPE_WIFI) {
			//此阶段只是判断已连接上Wifi
			//手机若连接上Wifi，无论是否开启GPRS，都强制走Wifi
			//添加Http请求至百度
			textView.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
		}else{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("提示").setMessage("亲，您连接的是移动网络");
			dialog.setPositiveButton("OK", null).show();
		}
	}

	@Override
	public void setListener() {
		
	}
	
	final class InJavaScriptLocalObj {  
        public void showSource(String html) {  
            System.out.println("====>html="+html);  
        }  
    }  
	
	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
			return;
		}
		super.onBackPressed();
	}
	
	public int checkInternetConnection(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			switch (networkInfo.getType()) {
			case TYPE_WIFI:
				return TYPE_WIFI;
			case TYPE_MOBILE:
				return TYPE_MOBILE;
			}
		}
		return TYPE_NOT_CONNETCED;
	}
	
	public class MyWebViewClient extends WebViewClient{
		@Override  
		public void onPageStarted(WebView view, String url, Bitmap favicon) {  
			currentUrl = url;
			Log.d("StartedTAG", url);
			super.onPageStarted(view, url, favicon);  
		}  
		
		@Override  
		public boolean shouldOverrideUrlLoading(WebView view, String url) {  
			view.loadUrl(url);  
			return true;  
		}  
		
		@Override  
		public void onPageFinished(WebView view, String url) {  
			Log.d("FinishedTAG", url);
			currentUrl = url;
			int urlLength = currentUrl.length();
			if (urlLength >= URL.length()) {
				if (currentUrl.substring(0, URL.length()).equals(URL)) {
					AlertDialog.Builder dialog= new AlertDialog.Builder(CertifyActivity.this);
					dialog.setTitle("提示").setMessage("网络认证成功，欢迎进入捷伴行世界");
					dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							webView.setVisibility(View.GONE);
							textView.setVisibility(View.VISIBLE);
						}
					});
					dialog.show();
				}
			}
//        view.loadUrl("javascript:window.local_obj.showSource('<head>'+"  
//                + "document.getElementsByTagName('title')[0].innerHTML+'</head>');");  
			
		}  
		
		@Override  
		public void onReceivedError(WebView view, int errorCode,  
				String description, String failingUrl) {  
			super.onReceivedError(view, errorCode, description, failingUrl);  
		}  
	}

}
