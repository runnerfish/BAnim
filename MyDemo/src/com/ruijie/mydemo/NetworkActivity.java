package com.ruijie.mydemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NetworkActivity extends BaseActivity implements OnClickListener {

	public static final int SHOW_RESPONSE = 0;
	public static final String PATH = "http://www.baidu.com/";
	
	private Button button;
	private TextView textView;
	private Handler handler;
	
	@Override
	public void setView() {
		setContentView(R.layout.act_network);
	}

	@Override
	public void initView() {
		button = (Button) findViewById(R.id.btn_request);
		textView = (TextView) findViewById(R.id.tv_content);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SHOW_RESPONSE:
					textView.setText(msg.obj.toString());
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	@Override
	public void setListener() {
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_request:
			httpClient();
			break;

		default:
			break;
		}
	}
	
	private void httpUrlConn(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					URL url = new URL(PATH);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
					conn.setRequestMethod("GET");
					InputStream in = conn.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder result = new StringBuilder();
					while (reader.readLine() != null) {
						result.append(reader.readLine());
					}
					
					Message msg = new Message();
					msg.what = SHOW_RESPONSE;
					msg.obj = result.toString();
					handler.sendMessage(msg);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void httpClient(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(PATH);
				try {
					HttpResponse response = httpClient.execute(httpGet);
					if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
						HttpEntity entity = response.getEntity();
						String result = EntityUtils.toString(entity, "utf-8");
						Message msg = new Message();
						msg.what = SHOW_RESPONSE;
						msg.obj = result;
						handler.sendMessage(msg);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
