package com.TextViewHtmlParse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MyTagHandler implements TagHandler {

	private Context context;
	private TextView mTextView;
	private ParseTextViewHtml parseObj;
	
	public MyTagHandler(Context context,TextView tv, ParseTextViewHtml myimg) {
		this.context = context;
		this.mTextView=tv;
		this.parseObj=myimg;
	}
	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		// TODO Auto-generated method stub
		// 处理标签<img>
		if (tag.toLowerCase().equals("img")) {
			// 获取长度
			int len = output.length();
			// 获取图片地址
			ImageSpan[] images = output.getSpans(len-1, len, ImageSpan.class);
			String imgURL = images[0].getSource();
			
			// 使图片可点击并监听点击事件
			output.setSpan(new ImageClick(context, imgURL), len-1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	private class ImageClick extends ClickableSpan {

		private String url;
		private Context context;
		
		public ImageClick(Context context, String url) {
			this.context = context;
			this.url = url;
		}
		
		@Override
		public void onClick(View widget) {
			// TODO Auto-generated method stub
			// 将图片URL转化为本地路径，可以将图片处理类里的图片处理过程写为一个方法，方便调用
			Toast.makeText(context, "当前图片的url为："+url,3000).show();
			String imageName = Common.md5(url);
			String sdcardPath = Environment.getExternalStorageDirectory().toString(); // 获取SDCARD的路径
			//获取图片后缀名
			String[] ss = url.split("\\.");
			String ext = ss[ss.length - 1];
			
			// 最终图片保持的地址
			String savePath = sdcardPath + "/" + context.getPackageName() + "/" + imageName + "." + ext;
			new ImageAsync((URLDrawable)parseObj.mydrawablelist.get(imageName)).execute(savePath, url);
		}
	
	}
	private class ImageAsync extends AsyncTask<String, Integer, Drawable> {

		private URLDrawable drawable;
		
		public ImageAsync(URLDrawable drawable) {
			this.drawable = drawable;
		}
		
		@Override
		protected Drawable doInBackground(String... params) {
			// TODO Auto-generated method stub
			String savePath = params[0];
			String url = params[1];
			
			InputStream in = null;
			try {
				// 获取网络图片
				HttpGet http = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = (HttpResponse) client.execute(http);
				BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(response.getEntity());
				in = bufferedHttpEntity.getContent();
				
			} catch (Exception e) {
				try {
					if (in != null)
						in.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}

			if (in == null) return drawable;
			
			try {
				File file = new File(savePath);
				String basePath = file.getParent();
				File basePathFile = new File(basePath);
				if (!basePathFile.exists()) {
					basePathFile.mkdirs();
				}
				file.createNewFile();
				FileOutputStream fileout = new FileOutputStream(file);
				byte[] buffer = new byte[4*1024];
				while (in.read(buffer) != -1) {
					fileout.write(buffer);
				}
				fileout.flush();
				
				Drawable mDrawable = Drawable.createFromPath(savePath);
				//this.drawable=(URLDrawable) mDrawable;
				return mDrawable;
			} catch (Exception e) {
				// TODO: handle exception
			}
			return drawable;
		}

		@Override
		protected void onPostExecute(Drawable result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				drawable.setDrawable(result);
				mTextView.setText(mTextView.getText()); // 通过这里的重新设置 TextView 的文字来更新UI
			}
		}
	}
		

}
