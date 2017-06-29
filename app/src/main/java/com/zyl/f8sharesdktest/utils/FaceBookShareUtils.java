package com.zyl.f8sharesdktest.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.List;

/**
 * facebook分享工具类
 * Created by zyl on 2017/6/28.
 */

public class FaceBookShareUtils {

    private final String TAG = this.getClass().getSimpleName();

    private Activity mActivity;
    private ShareDialog shareDialog;
    private CallbackManager callBackManager;
    public static final int SHARE_REQUEST_CODE = 0x001;

    public FaceBookShareUtils(Activity activity, CallbackManager callBackManager, FacebookCallback facebookCallback) {
        this.mActivity = activity;
        this.callBackManager = callBackManager;
        shareDialog = new ShareDialog(mActivity);
        //注册分享状态监听回调接口
        shareDialog.registerCallback(callBackManager, facebookCallback, FaceBookShareUtils.SHARE_REQUEST_CODE);
    }

    /**
     * 分享连接
     */
    public void shareLink(String contentUrl) {

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(contentUrl))
                    .build();
            shareDialog.show(linkContent);
        }
    }


    /**
     * 分享图片(Bitmap)
     *
     * @param bitmap 图片
     */
    public void sharePicture(Bitmap bitmap) {
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            shareDialog.show(sharePhotoContent);
        }
    }

    /**
     * 分享图片(Bitmap)
     *
     * @param bitmapList Bitmap的列表
     */
    public void sharePicture(List<Bitmap> bitmapList) {
        if (!ShareDialog.canShow(SharePhotoContent.class)) {
            Log.w(TAG, "ShareDialog can not show SharePhotoContent");
            return;
        }
        if (bitmapList == null || bitmapList.size() < 0) {
            Log.w(TAG, "要分享的图片列表为空!");
            return;
        }
        Log.d(TAG, "开始装入ShareMediaContent");
        ShareMediaContent.Builder shareContentBuilder = new ShareMediaContent.Builder();
        for (int i = 0; i < bitmapList.size(); i++) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmapList.get(i))
                    .build();
            shareContentBuilder.addMedium(sharePhoto);
        }
        ShareContent shareContent = shareContentBuilder.build();
        shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC);
    }
}
