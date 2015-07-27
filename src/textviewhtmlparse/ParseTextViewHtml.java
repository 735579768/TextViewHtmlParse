package textviewhtmlparse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.XMLReader;

import com.textviewhtmlparse.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ParseTextViewHtml implements ImageGetter {
	public String URL_PREFIX="";
	private Context context;
	private boolean autoload=true;
	public HashMap<String,URLDrawable> mydrawablelist;
	private TextView tv;
	
	public ParseTextViewHtml(Context context) {
		this.context = context;
		this.mydrawablelist=new HashMap<String,URLDrawable>();
	}
	public  ParseTextViewHtml setAutoLoad(boolean b){
		autoload=b;
		return this;
	}
	public  ParseTextViewHtml setUrlPrefix(String str){
		URL_PREFIX=str;
		return this;
	}
	public void setTextViewHtml(TextView mTextview,String strHtml){
		this.tv=mTextview;
		//strHtml=regTag(strHtml);
		mTextview.setMovementMethod(LinkMovementMethod.getInstance());
	    Spanned spanned = Html.fromHtml(strHtml,this, new MyTagHandler(context,mTextview,this));
	    mTextview.setText(spanned);
	}
	private String regTag(String str){
		str=str.replaceAll(" ","");
		return str;
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
		try{
		if(screenWidth!=0){
			drawable.setBounds(0, 0, screenWidth, drawable.getIntrinsicHeight()*screenWidth/drawable.getIntrinsicWidth());
			setBounds(0, 0, screenWidth, drawable.getIntrinsicHeight()*screenWidth/drawable.getIntrinsicWidth());
		}else{
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		}
		}catch(Exception e){
			e.getMessage();
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
class MyTagHandler implements TagHandler {

	private Context context;
	private TextView mTextView;
	private ParseTextViewHtml parseObj;
	private int screenWidth;
	public MyTagHandler(Context context,TextView tv, ParseTextViewHtml myimg) {
	   WindowManager wm = (WindowManager)context.getApplicationContext()
	              .getSystemService(Context.WINDOW_SERVICE);
	   screenWidth = wm.getDefaultDisplay().getWidth(); 
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
			String url = parseObj.URL_PREFIX+params[1];
			
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


