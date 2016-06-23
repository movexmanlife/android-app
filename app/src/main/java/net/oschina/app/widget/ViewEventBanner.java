package net.oschina.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import net.oschina.app.R;
import net.oschina.app.bean.Banner;
import net.oschina.app.util.UIHelper;
import net.qiujuer.genius.blur.StackBlur;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public class ViewEventBanner extends RelativeLayout implements View.OnClickListener {
    private Banner banner;
    private ImageView iv_event_banner_img, iv_event_banner_bg;
    private TextView tv_event_banner_title, tv_event_banner_body;

    public ViewEventBanner(Context context) {
        super(context, null);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_event_banner, this, true);
        iv_event_banner_img = (ImageView) findViewById(R.id.iv_event_banner_img);
        iv_event_banner_bg = (ImageView) findViewById(R.id.iv_event_banner_bg);
        tv_event_banner_title = (TextView) findViewById(R.id.tv_event_banner_title);
        tv_event_banner_body = (TextView) findViewById(R.id.tv_event_banner_body);
        setOnClickListener(this);
    }

    public void initData(RequestManager manager, Banner banner) {
        this.banner = banner;
        tv_event_banner_title.setText(banner.getName());
        tv_event_banner_body.setText(banner.getDetail());
        manager.load(banner.getImg()).into(iv_event_banner_img);
        manager.load(banner.getImg()).centerCrop()
                .transform(new BitmapTransformation(getContext()) {
                    @Override
                    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
                        toTransform = StackBlur.blur(toTransform, 25, true);
                        return toTransform;
                    }

                    @Override
                    public String getId() {
                        return "blur";
                    }
                })
                .into(iv_event_banner_bg);
    }

    @Override
    public void onClick(View v) {
        UIHelper.showBannerDetail(getContext(), banner);
    }
}
