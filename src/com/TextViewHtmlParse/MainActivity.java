package com.TextViewHtmlParse;

import me.zhangqian.test.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;

public class MainActivity extends Activity {
	
	private final static String HTML_CONTENT = "<img src='http://img.1985t.com/uploads/attaches/2014/07/19016-WezXvz.jpg' />没有谁的生活始终充满了幸福快乐没有谁的生活始终充满了幸福快乐没有谁的生活始终充满了幸福快乐<img src='http://img.7160.com/uploads/allimg/140603/9-140603105047.jpg' />没有谁的生活始终充满了幸福快乐没有谁的生活始终充满了幸福快乐<img src='http://www.7ymm.com/uploads/allimg/y20110711/740310c19e81c3be6e711430a2783acc.jpg' />没有谁的生活始终充满了幸福快乐没有谁的生活始终充满了幸福快乐<img src='http://www.7ymm.com/uploads/allimg/y20110711/d7a5e207c5e7a81bfb5aad207e5207d2.jpg' />没有谁的生活始终充满了幸福快乐没有谁的生活始终充满了幸福快乐<img src='http://www.7ymm.com/uploads/allimg/y20110711/48b63d2fe2eb3294401fc7d01905dcea.jpg' />没有谁的生活始终充满了幸福快乐没有谁的生活始终充满了幸福快乐";

	
	private TextView tv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tv = (TextView) findViewById(R.id.tv);
        // 生成一个支持HTML格式的文本
        new ParseTextViewHtml(this).setTextViewHtml(tv,HTML_CONTENT);
    }
    
}
