package net.oschina.app.ui.blog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.sso.UMSsoHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Report;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.blog.BlogDetail;
import net.oschina.app.contract.BlogDetailContract;
import net.oschina.app.fragment.general.BlogDetailFragment;
import net.oschina.app.ui.ReportDialog;
import net.oschina.app.ui.ShareDialog;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.URLsUtils;
import net.oschina.app.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

public class BlogDetailActivity extends AppCompatActivity implements BlogDetailContract.Operator {
    private long mId;
    private EmptyLayout mEmptyLayout;
    private BlogDetail mBlog;
    private BlogDetailContract.View mView;
    private ShareDialog dialog;

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }

        mId = getIntent().getLongExtra("id", 0);
        if (mId == 0)
            finish();
        else {
            mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
            mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    initData();
                }
            });
            initData();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_blog_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_report) {
            toReport();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBlog() {
        BlogDetailFragment fragment = BlogDetailFragment.instantiate(this, mBlog);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.lay_container, fragment);
        trans.commitAllowingStateLoss();
        mView = fragment;
    }

    private void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
            layout.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        OSChinaApi.getBlogDetail(mId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<BlogDetail>>() {
                    }.getType();

                    ResultBean<BlogDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        handleData(resultBean.getResult());
                        return;
                    }
                    showError(EmptyLayout.NODATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }


    private void handleData(BlogDetail blog) {
        showError(View.INVISIBLE);
        mBlog = blog;
        showBlog();
    }

    private int check() {
        if (mId == 0 || mBlog == null) {
            AppContext.showToast("数据加载中...");
            return 0;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return 0;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            return 0;
        }
        // 返回当前登录用户ID
        return AppContext.getInstance().getLoginUid();
    }


    @Override
    public BlogDetail getBlogDetail() {
        return mBlog;
    }

    @Override
    public void toFavorite() {
        int uid = check();
        if (uid == 0)
            return;

        AsyncHttpResponseHandler mFavoriteHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    Result res = XmlUtils.toBean(net.oschina.app.bean.ResultBean.class,
                            new ByteArrayInputStream(arg2)).getResult();
                    if (res.OK()) {
                        BlogDetailContract.View view = mView;
                        if (view == null)
                            return;

                        mBlog.setFavorite(!mBlog.isFavorite());
                        view.toFavoriteOk(mBlog);
                        if (mBlog.isFavorite())
                            AppContext.showToastShort(R.string.add_favorite_success);
                        else
                            AppContext.showToastShort(R.string.del_favorite_success);
                    } else {
                        if (mBlog.isFavorite())
                            AppContext.showToastShort(R.string.del_favorite_faile);
                        else
                            AppContext.showToastShort(R.string.add_favorite_faile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(arg0, arg1, arg2, e);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                BlogDetail blogDetail = mBlog;
                if (blogDetail == null)
                    return;
                if (blogDetail.isFavorite())
                    AppContext.showToastShort(R.string.del_favorite_faile);
                else
                    AppContext.showToastShort(R.string.add_favorite_faile);
            }

            @Override
            public void onStart() {
                showWaitDialog(R.string.progress_submit);
            }

            @Override
            public void onFinish() {
                hideWaitDialog();
            }
        };

        if (mBlog.isFavorite()) {
            OSChinaApi.delFavorite(uid, mId,
                    FavoriteList.TYPE_BLOG, mFavoriteHandler);
        } else {
            OSChinaApi.addFavorite(uid, mId,
                    FavoriteList.TYPE_BLOG, mFavoriteHandler);
        }
    }

    @Override
    public void toShare() {
        String content;
        String url;
        String title;
        if (mId != 0 && mBlog != null) {
            url = String.format(URLsUtils.URL_MOBILE + "blog/%s", mId);
            if (mBlog.getBody().length() > 55) {
                content = HTMLUtil.delHTMLTag(mBlog.getBody().trim());
                if (content.length() > 55)
                    content = StringUtils.getSubString(0, 55, content);
            } else {
                content = HTMLUtil.delHTMLTag(mBlog.getBody().trim());
            }
            title = mBlog.getTitle();

            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(content) || TextUtils.isEmpty(title)) {
                AppContext.showToast("内容加载失败...");
                return;
            }
        } else {
            AppContext.showToast("内容加载失败...");
            return;
        }

        if(dialog == null){
           dialog = new ShareDialog(this);
        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(R.string.share_to);
        dialog.setShareInfo(title, content, url);
        dialog.show();
    }

    @Override
    public void toFollow() {
        int uid = check();
        if (uid == 0)
            return;

        // 只关注不可取消
        OSChinaApi.updateRelation(uid, mBlog.getAuthorId(), 1,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        try {
                            Result result = XmlUtils.toBean(net.oschina.app.bean.ResultBean.class,
                                    new ByteArrayInputStream(arg2)).getResult();
                            if (result.OK()) {
                                // 更改用户状态
                                BlogDetailContract.View view = mView;
                                if (view != null)
                                    view.toFollowOk(mBlog);
                                return;
                            }
                            AppContext.showToast("关注失败!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(arg0, arg1, arg2, e);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        AppContext.showToast("关注失败!");
                    }

                    @Override
                    public void onFinish() {
                        hideWaitDialog();
                    }

                    @Override
                    public void onStart() {
                        showWaitDialog(R.string.progress_submit);
                    }
                });
    }

    @Override
    public void toSendComment(long id, long authorId, String comment) {
        int uid = check();
        if (uid == 0)
            return;

        if (TextUtils.isEmpty(comment)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    net.oschina.app.bean.ResultBean rsb = XmlUtils.toBean(net.oschina.app.bean.ResultBean.class,
                            new ByteArrayInputStream(arg2));
                    Result res = rsb.getResult();
                    if (res.OK()) {
                        BlogDetailContract.View view = mView;
                        if (view != null)
                            view.toSendCommentOk();
                    } else {
                        AppContext.showToastShort(res.getErrorMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(arg0, arg1, arg2, e);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                AppContext.showToastShort(R.string.comment_publish_faile);
            }

            @Override
            public void onStart() {
                showWaitDialog(R.string.progress_submit);
            }

            @Override
            public void onFinish() {
                hideWaitDialog();
            }
        };

        if (mId != id)
            OSChinaApi.replyBlogComment(mId, uid, comment, id, authorId, handler);
        else
            OSChinaApi.publicBlogComment(mId, uid, comment, handler);
    }

    @Override
    public void toReport() {
        int uid = check();
        if (uid == 0)
            return;


        final ReportDialog dialog = new ReportDialog(this,
                mBlog.getHref(), mId, Report.TYPE_QUESTION);
        dialog.setCancelable(true);
        dialog.setTitle(R.string.report);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setNegativeButton(R.string.cancle, null);
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (TextUtils.isEmpty(arg2)) {
                    AppContext.showToastShort(R.string.tip_report_success);
                } else {
                    AppContext.showToastShort(new String(arg2));
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
                AppContext.showToastShort(R.string.tip_report_faile);
            }

            @Override
            public void onFinish() {
                hideWaitDialog();
            }

            @Override
            public void onStart() {
                showWaitDialog(R.string.progress_submit);
            }
        };
        dialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int which) {
                        Report report = null;
                        if ((report = dialog.getReport()) != null) {
                            OSChinaApi.report(report, handler);
                        }
                        d.dismiss();
                    }
                });
        dialog.show();
    }


    private ProgressDialog mDialog;

    public ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelp.getWaitDialog(this, message);
        }

        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

    public void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = dialog.getController().getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideWaitDialog();
        mEmptyLayout = null;
        mView = null;
        mBlog = null;
        dialog = null;
    }
}
