package net.oschina.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.ui.dialog.ImageMenuDialog;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.TouchImageView;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.http.HttpConfig;

/**
 * 图片预览界面
 */
public class OSCPhotosActivity extends BaseActivity {

    public static final String BUNDLE_KEY_IMAGES = "bundle_key_images";
    private TouchImageView mTouchImageView;
    private ProgressBar mProgressBar;
    private ImageView mOption;
    private String mImageUrl;

    public static void showImagePrivew(Context context,
                                       String imageUrl) {
        Intent intent = new Intent(context, OSCPhotosActivity.class);
        intent.putExtra(BUNDLE_KEY_IMAGES, imageUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_browse);
        mImageUrl = getIntent().getStringExtra(BUNDLE_KEY_IMAGES);

        mTouchImageView = (TouchImageView) findViewById(R.id.photoview);

        mTouchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);

        mOption = (ImageView) findViewById(R.id.iv_more);
        mOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionMenu();
            }
        });

        loadImage(mTouchImageView, mImageUrl);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void showOptionMenu() {
        final ImageMenuDialog dialog = new ImageMenuDialog(this);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setOnMenuClickListener(new ImageMenuDialog.OnMenuClickListener() {
            @Override
            public void onClick(TextView menuItem) {
                if (menuItem.getId() == R.id.menu1) {
                    saveImg();
                } else if (menuItem.getId() == R.id.menu2) {
                    sendTweet();
                } else if (menuItem.getId() == R.id.menu3) {
                    copyUrl();
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 复制链接
     */
    private void copyUrl() {
        TDevice.copyTextToBoard(mImageUrl);
        AppContext.showToastShort("已复制到剪贴板");
    }

    /**
     * 发送到动弹
     */
    private void sendTweet() {
        Bundle bundle = new Bundle();
        bundle.putString(TweetPubActivity.REPOST_IMAGE_KEY, mImageUrl);
        UIHelper.showTweetActivity(this, TweetPubActivity.ACTION_TYPE_REPOST, bundle);
        finish();
    }

    /**
     * 保存图片
     */
    private void saveImg() {
        final String filePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH
                + getFileName(mImageUrl);
        Core.getKJBitmap().saveImage(this, mImageUrl, filePath);
        AppContext.showToastShort(getString(R.string.tip_save_image_suc,
                filePath));
    }

    private String getFileName(String imgUrl) {
        int index = imgUrl.lastIndexOf('/') + 1;
        if (index == -1) {
            return System.currentTimeMillis() + ".jpeg";
        }
        return imgUrl.substring(index);
    }

    /**
     * Load the item's thumbnail image into our {@link ImageView}.
     */
    private void loadImage(final ImageView mHeaderImageView, String imageUrl) {
        HttpConfig.sCookie = ApiHttpClient.getCookie(AppContext.getInstance());
        new Core.Builder()
                .view(mHeaderImageView)
                .url(imageUrl)
                .errorBitmapRes(R.drawable.load_img_error)
                .bitmapCallBack(new BitmapCallBack() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        super.onSuccess(bitmap);
                        mProgressBar.setVisibility(View.GONE);
                        mTouchImageView.setVisibility(View.VISIBLE);
                        mOption.setVisibility(View.VISIBLE);
                    }
                }).doTask();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }

}
