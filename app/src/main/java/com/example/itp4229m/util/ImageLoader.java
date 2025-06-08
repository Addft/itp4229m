package com.example.itp4229m.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.itp4229m.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private static LruCache<String, Bitmap> memoryCache;
    private static final int MAX_SIZE = 4 * 1024 * 1024; // 4MB

    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        if (url == null || url.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_placeholder);
            return;
        }

        Bitmap cachedBitmap = getBitmapFromMemCache(url);
        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
            return;
        }

        new ImageDownloadTask(imageView, context).execute(url);
    }

    private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            memoryCache.put(key, bitmap);
        }
    }

    private static Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    private static class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<Context> contextReference;
        private String url;

        ImageDownloadTask(ImageView imageView, Context context) {
            imageViewReference = new WeakReference<>(imageView);
            contextReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageResource(R.drawable.ic_placeholder);
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            try {
                return downloadBitmap(url);
            } catch (Exception e) {
                Log.e(TAG, "Error downloading image: " + url, e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                addBitmapToMemoryCache(url, bitmap);

                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            } else {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageResource(R.drawable.ic_error);
                }
            }
        }

        private Bitmap downloadBitmap(String urlString) throws IOException {
            HttpURLConnection connection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }
}