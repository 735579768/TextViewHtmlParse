package com.main;

import java.io.File;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());
	}

	public static void initImageLoader(Context context) {
    	File cacheDir = StorageUtils.getCacheDirectory(context);
    	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
    	        .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
    	        .diskCacheExtraOptions(480, 800, null)
    	        .taskExecutor(null)
    	        .taskExecutorForCachedImages(null)
    	        .threadPoolSize(5) // default
    	        .threadPriority(Thread.NORM_PRIORITY - 2) // default
    	        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
    	        .denyCacheImageMultipleSizesInMemory()
    	        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
    	        .memoryCacheSize(2 * 1024 * 1024)
    	        .memoryCacheSizePercentage(13) // default
    	        .diskCache(new UnlimitedDiskCache(cacheDir)) // default
    	        .diskCacheSize(50 * 1024 * 1024)
    	        .diskCacheFileCount(100)
    	        .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
    	        .imageDownloader(new BaseImageDownloader(context)) // default
    	        .imageDecoder(new BaseImageDecoder(false)) // default
    	        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
    	        .writeDebugLogs()
    	        .build();
    	ImageLoader.getInstance().init(config);//全局初始化此配置  
	}
}
