package com.zyl.f8sharesdktest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.zyl.f8sharesdktest.constants.TwitterConstants;
import com.zyl.f8sharesdktest.utils.FaceBookShareUtils;

import io.fabric.sdk.android.Fabric;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private CallbackManager callbackManager;

    private Button shareBtn;
    private Button sharePicUrlBtn;
    private Button sharePicBitmapBtn;
    Bitmap bitmap;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        shareBtn = (Button) findViewById(R.id.share_url_btn);
        sharePicUrlBtn = (Button) findViewById(R.id.share_pic_url_btn);
        sharePicBitmapBtn = (Button) findViewById(R.id.share_pic_bitmap_btn);
        shareBtn.setOnClickListener(this);
        sharePicUrlBtn.setOnClickListener(this);
        sharePicBitmapBtn.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        TwitterAuthConfig config = new TwitterAuthConfig(TwitterConstants.TWITTER_KEY, TwitterConstants.TWITTER_SECRET);
        Fabric.with(this, new Twitter(config));
        try {
//            url = new URL("http","www.baidu.com","index.html");
            url = new URL("http://baidu.com/index.html");
            Log.d(TAG, url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private FacebookCallback facebookCallback = new FacebookCallback() {
        @Override
        public void onSuccess(Object o) {
            Log.d(TAG, "share success : " + o);
            recycleBitmap();
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "share cancel");
            recycleBitmap();
        }

        @Override
        public void onError(FacebookException error) {
            Log.d(TAG, "share error : " + error.toString());
            recycleBitmap();
        }
    };

    public void onClick(View view) {
        Log.d(TAG, "click on =====");
        switch (view.getId()) {
            case R.id.share_url_btn:
                Log.d(TAG, "share_url_btn being clicked !!");
                new FaceBookShareUtils(this, callbackManager, facebookCallback).shareLink("http://www.baidu.com");
                break;
            case R.id.share_pic_url_btn:
                Log.d(TAG, "share_pic_url_btn being clicked !!");
                //new FaceBookShareUtils(this, callbackManager, facebookCallback).sharePicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                TweetComposer.Builder builder = new TweetComposer.Builder(this)
                        .text("分享到推特")
                        .image(Uri.parse("/storage/emulated/0/tencent/MicroMsg/WeiXin/microMsg.1478832617264.jpg"))
                        .url(url);
                builder.show();

                break;
            case R.id.share_pic_bitmap_btn:
                Log.d(TAG, "share_pic_bitmap_btn being clicked !!");
                pickupPhotos();
                break;
            default:
                break;
        }
    }

    private void pickupPhotos() {
        PhotoPicker.builder()
                .setPhotoCount(6)
                .setPreviewEnabled(false)
                .setShowCamera(true)
                .start(this);
    }

    private ArrayList<Bitmap> decodeBitmapListFromPath(List<String> photoPaths) {
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        if (photoPaths != null && photoPaths.size() > 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            try {
                for (String photoPath : photoPaths) {
                    bitmap = BitmapFactory.decodeFile(photoPath, options);
                    bitmaps.add(bitmap);
                }
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "Bitmap out of memory");
            }


            Log.d(TAG, "bitmaps = " + bitmaps);
        }
        return bitmaps;
    }

    private void recycleBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photoPaths = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Log.d(TAG, "photos = " + photoPaths);
                if (photoPaths != null) {
                    ArrayList<Bitmap> bitmaps = decodeBitmapListFromPath(photoPaths);
                    if (bitmaps != null && bitmaps.size() > 0) {
                        Log.d(TAG, "开始分享选中的图片");
                        new FaceBookShareUtils(this, callbackManager, facebookCallback).sharePicture(bitmaps);
                    } else {
                        Log.d(TAG, "没有选中任何图片");
                        Toast.makeText(this, "至少要选择一张要分享的图片", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w(TAG, "photoPathcs == null");
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == FaceBookShareUtils.SHARE_REQUEST_CODE) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleBitmap();
    }
}
