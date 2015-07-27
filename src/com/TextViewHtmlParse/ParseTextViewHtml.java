package com.TextViewHtmlParse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;

import me.zhangqian.test.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.view.WindowManager;
import android.widget.TextView;

public class ParseTextViewHtml implements ImageGetter {
	
	private Context context;
	public HashMap<String,URLDrawable> mydrawablelist;
	
	public ParseTextViewHtml(Context context) {
		this.context = context;
		
		this.mydrawablelist=new HashMap<String,URLDrawable>();
	}
	public void setTextViewHtml(TextView mTextview,String strHtml){
		  mTextview.setMovementMethod(LinkMovementMethod.getInstance());
	      Spanned spanned = Html.fromHtml(strHtml,this, new MyTagHandler(context,mTextview,this));
	      mTextview.setText(spanned);
	}	
	@Override
	public Drawable getDrawable(String source) {
		// TODO Auto-generated method stub
	      WindowManager wm = (WindowManager)context.getApplicationContext()
	              .getSystemService(Context.WINDOW_SERVICE);
	   int screenWidth = wm.getDefaultDisplay().getWidth(); 
		//将source进行MD5加密并保存至本地
		String imageName = Common.md5(source);
		String sdcardPath = Environment.getExternalStorageDirectory().toString(); // 获取SDCARD的路径
		//获取图片后缀名
		String[] ss = source.split("\\.");
		String ext = ss[ss.length - 1];
		
		// 最终图片保持的地址
		String savePath = sdcardPath + "/" + context.getPackageName() + "/" + imageName + "." + ext;
		
		File file = new File(savePath);
		if (file.exists()) {
			// 如果文件已经存在，直接返回
			Drawable drawable = Drawable.createFromPath(savePath);
			//drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			mydrawablelist.put(imageName, new URLDrawable(drawable,screenWidth));
			return drawable;
		}

		// 不存在文件时返回默认图片，并异步加载网络图片
		Resources res = context.getResources();
		URLDrawable drawable = new URLDrawable(res.getDrawable(R.drawable.defualt_image),screenWidth);
		mydrawablelist.put(imageName, drawable);
		return drawable;
		
	}

}

class URLDrawable extends BitmapDrawable {
	
	private Drawable drawable;
	private int screenWidth=0;
	public URLDrawable(Drawable defaultDraw,int screenWidth) {
		this.screenWidth=screenWidth;
		setDrawable(defaultDraw);
	}
	public URLDrawable(Drawable defaultDraw) {
		setDrawable(defaultDraw);
	}
	public void setDrawable(Drawable nDrawable) {
		drawable = nDrawable;
		if(screenWidth!=0){
			drawable.setBounds(0, 0, screenWidth, drawable.getIntrinsicHeight()*screenWidth/drawable.getIntrinsicWidth());
			setBounds(0, 0, screenWidth, drawable.getIntrinsicHeight()*screenWidth/drawable.getIntrinsicWidth());
		}else{
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		}
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		drawable.draw(canvas);
	}
	
}

class Common {
	
	public static String md5(String sourceStr) {
		
		byte[] source = sourceStr.getBytes();

		String s = null;
		
		char hexDigits[] = {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
		};
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();
			char[] str = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return s;
	}

}


