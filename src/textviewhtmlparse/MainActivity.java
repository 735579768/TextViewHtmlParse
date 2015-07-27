package textviewhtmlparse;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.textviewhtmlparse.R;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	 

	
	private TextView tv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tv = (TextView) findViewById(R.id.tv);
        // 生成一个支持HTML格式的文本
        String str = getUrlPage("http://www.0yuanwang.com");
        new ParseTextViewHtml(this)
        .setUrlPrefix("http://www.0yuanwang.com")		
        .setTextViewHtml(tv,str);
    }
	protected String getUrlPage(String url){
	    String uriAPI = url;
	 
	    HttpGet httpRequest = new HttpGet(uriAPI);
	    try {
	 
	        HttpResponse httpResponse = new DefaultHttpClient()
	                .execute(httpRequest);
	 
	        if (httpResponse.getStatusLine().getStatusCode() == 200) {
	 
	            String strResult = EntityUtils.toString(httpResponse
	                    .getEntity());
	            //替换掉空行
	           // strResult = eregi_replace("(\r\n|\r|\n|\n\r)", "",strResult);
	            return strResult;
	        } else {
	            return "Error Response: "+ httpResponse.getStatusLine().toString();
	        }
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	        return e.getMessage().toString();          
	    } catch (IOException e) {
	        e.printStackTrace();
	        return e.getMessage().toString();      
	    } catch (Exception e) {
	        e.printStackTrace();
	        return e.getMessage().toString();
	    }
	     
	}
}
