package net.oschina.app.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.bean.Tweet;
import net.oschina.app.emoji.EmojiKeyboardFragment;
import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.emoji.OnEmojiClickListener;
import net.oschina.app.service.ServerTaskUtils;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.SimpleTextWatcher;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.bitmap.BitmapCreate;
import org.kymjs.kjframe.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author kymjs (http://www.kymjs.com/) on 1/12/16.
 */
public class TweetPubActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final int MAX_TEXT_LENGTH = 160;
    private static final int SELECT_FRIENDS_REEQUEST_CODE = 100;
    private static final String TEXT_SOFTWARE = "#请输入软件名#";

    public static final String ACTION_TYPE = "action_type";
    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    public static final int ACTION_TYPE_RECORD = 2; // 录音
    public static final int ACTION_TYPE_TOPIC = 3; // 话题
    public static final int ACTION_TYPE_REPOST = 4; // 转发

    public static final String REPOST_IMAGE_KEY = "repost_image";
    public static final String REPOST_TEXT_KEY = "tweet_topic";

    @Bind(R.id.ib_emoji_keyboard)
    ImageButton mIbEmoji;
    @Bind(R.id.ib_picture)
    ImageButton mIbPicture;
    @Bind(R.id.ib_mention)
    ImageButton mIbMention;
    @Bind(R.id.ib_trend_software)
    ImageButton mIbTrendSoftware;
    @Bind(R.id.tv_clear)
    TextView mTvClear;
    @Bind(R.id.rl_img)
    View mLyImage;
    @Bind(R.id.iv_clear_img)
    View mIvDel;
    @Bind(R.id.iv_img)
    ImageView mIvImage;
    @Bind(R.id.et_content)
    EditText mEtInput;

    private MenuItem mSendMenu;
    private Tweet tweet = new Tweet();

    private final EmojiKeyboardFragment keyboardFragment = new EmojiKeyboardFragment();

    @Override
    protected int getLayoutId() {
        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getWindow().setSoftInputMode(mode);
        return R.layout.fragment_tweet_pub;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    public void initView() {
        respondExternal(getIntent());
        mIbEmoji.setOnClickListener(this);
        mIbPicture.setOnClickListener(this);
        mIbMention.setOnClickListener(this);
        mIbTrendSoftware.setOnClickListener(this);
        mTvClear.setOnClickListener(this);
        mTvClear.setText(String.valueOf(MAX_TEXT_LENGTH));
        mIvDel.setOnClickListener(this);

        mEtInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if ("@".equals(s.toString())) {
                    toSelectFriends();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mTvClear.setText((MAX_TEXT_LENGTH - s.length()) + "");
                updateMenuState(mSendMenu);
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.emoji_keyboard_fragment, keyboardFragment)
                .commit();
        keyboardFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
            @Override
            public void onEmojiClick(Emojicon v) {
                InputHelper.input2OSC(mEtInput, v);
            }

            @Override
            public void onDeleteButtonClick(View v) {
                InputHelper.backspace(mEtInput);
            }
        });
    }

    @Override
    public void initData() {
        //处理APP内部跳转进入的事件
        int action = getIntent().getIntExtra(ACTION_TYPE, -1);
        selectActive(action);

        if (TextUtils.isEmpty(tweet.getBody())) {
            tweet.setBody(AppContext.getTweetDraft());
            AppContext.setTweetDraft(null);
            mEtInput.setSelection(mEtInput.getText().toString().length());
        }

        mEtInput.setText(tweet.getBody());
        String imgPath = tweet.getImageFilePath();
        if (!TextUtils.isEmpty(imgPath)) {
            if (imgPath.startsWith("http")) {
                setImageFromUrl(imgPath);
            } else {
                setImageFromPath(imgPath);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_picture:
                handleSelectPicture();
                break;
            case R.id.ib_mention:
                toSelectFriends();
                break;
            case R.id.ib_trend_software:
                insertTrendSoftware();
                break;
            case R.id.tv_clear:
                mEtInput.setText(null);
                break;
            case R.id.iv_clear_img:
                mIvImage.setImageBitmap(null);
                mLyImage.setVisibility(View.GONE);
                tweet.setImageFilePath("");
                break;
            case R.id.ib_emoji_keyboard:
                if (!keyboardFragment.isShow()) {// emoji隐藏中
                    keyboardFragment.showEmojiKeyBoard();
                    keyboardFragment.hideSoftKeyboard();
                } else {
                    keyboardFragment.hideEmojiKeyBoard();
                    keyboardFragment.showSoftKeyboard(mEtInput);
                }
                break;
        }
    }

    private void handleSelectPicture() {
        DialogHelp.getSelectDialog(this, getResources().getStringArray(R.array.choose_picture), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectActive(i);
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
        final String tweet = mEtInput.getText().toString();
        if (!TextUtils.isEmpty(tweet)) {
            DialogHelp.getConfirmDialog(this, "是否保存为草稿?", new DialogInterface
                    .OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AppContext.setTweetDraft(tweet);
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 处理外部分享进入的事件
     */
    private void respondExternal(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                tweet.setBody(sharedText);
            } else if (type.startsWith("image/")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                String path = getAbsoluteImagePath(imageUri);
                tweet.setImageFilePath(path);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                ArrayList<Uri> imageUris = intent
                        .getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                String path = getAbsoluteImagePath(imageUris.get(0));
                tweet.setImageFilePath(path);
            }
        }
    }

    /**
     * 跳转选择好友
     */
    private void toSelectFriends() {
        //如果没登录，则先去登录界面
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            return;
        }
        Intent intent = new Intent(this, SelectFriendsActivity.class);
        startActivityForResult(intent, SELECT_FRIENDS_REEQUEST_CODE);
    }

    private void insertTrendSoftware() {
        // 在光标所在处插入“#软件名#”
        int curTextLength = mEtInput.getText().length();
        if (curTextLength >= MAX_TEXT_LENGTH)
            return;
        String software = TEXT_SOFTWARE;
        int start, end;
        if ((MAX_TEXT_LENGTH - curTextLength) >= software.length()) {
            start = mEtInput.getSelectionStart() + 1;
            end = start + software.length() - 2;
        } else {
            int num = MAX_TEXT_LENGTH - curTextLength;
            if (num < software.length()) {
                software = software.substring(0, num);
            }
            start = mEtInput.getSelectionStart() + 1;
            end = start + software.length() - 1;
        }
        if (start > MAX_TEXT_LENGTH || end > MAX_TEXT_LENGTH) {
            start = MAX_TEXT_LENGTH;
            end = MAX_TEXT_LENGTH;
        }
        mEtInput.getText().insert(mEtInput.getSelectionStart(), software);
        mEtInput.setSelection(start, end);// 设置选中文字
    }

    /**
     * 根据不同的type,选择图库中的图片
     */
    private void selectActive(int option) {
        Bundle bundle;
        switch (option) {
            case ACTION_TYPE_ALBUM:
                showToAlbum();
                break;
            case ACTION_TYPE_PHOTO:
                showToCamera();
                break;
            case ACTION_TYPE_TOPIC://同样处理
            case ACTION_TYPE_REPOST:
                bundle = getIntent().getExtras();
                if (bundle != null) {
                    String sharedText = bundle.getString(REPOST_TEXT_KEY);
                    String sharedImage = bundle.getString(REPOST_IMAGE_KEY);
                    tweet.setBody(sharedText);
                    tweet.setImageFilePath(sharedImage);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == SELECT_FRIENDS_REEQUEST_CODE) {
            //选中好友的名字
            String names[] = data.getStringArrayExtra("names");
            if (names != null && names.length > 0) {
                //拼成字符串
                String text = "";
                for (String n : names) {
                    text += "@" + n + " ";
                }
                //插入到文本中
                mEtInput.getText().insert(mEtInput.getSelectionStart(), text);
            }
            return;
        }
        if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
            if (data == null)
                return;
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                String path = ImageUtils.getImagePath(selectedImageUri, this);
                setImageFromPath(path);
            }
        } else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
            setImageFromPath(tweet.getImageFilePath());
        }
    }

    private void setContentText(String topic) {
        setContentText(topic, topic.length());
    }

    private void setContentText(String topic, int selectIndex) {
        if (mEtInput != null) {
            mEtInput.setText(topic);
            mEtInput.setSelection(selectIndex);
        }
    }

    /**
     * 根据url上传动弹图片
     */
    private void setImageFromUrl(final String url) {
        if (TextUtils.isEmpty(url)) return;
        mIvDel.setVisibility(View.GONE);
        mLyImage.setVisibility(View.VISIBLE);
        new Core.Builder().view(mIvImage)
                .loadBitmapRes(R.drawable.load_img_loading)
                .url(url).bitmapCallBack(new BitmapCallBack() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                setImageFromBitmap(bitmap);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mIvDel.setVisibility(View.VISIBLE);
            }
        }).doTask();
    }

    /**
     * 根据文件路径上传动弹图片
     *
     * @param path 图片在本地的路径
     */
    private void setImageFromPath(final String path) {
        if (TextUtils.isEmpty(path)) return;
        try {
            Bitmap bitmap = BitmapCreate.bitmapFromStream(
                    new FileInputStream(path), 512, 512);

            setImageFromBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据bitmap上传动弹图片
     *
     * @param bitmap bitmap
     */
    private void setImageFromBitmap(final Bitmap bitmap) {
        if (bitmap == null) return;
        String temp = FileUtils.getSDCardPath() + "/OSChina/tempfile.png";
        FileUtils.bitmapToFile(bitmap, temp);
        tweet.setImageFilePath(temp);

        // 压缩小图片用于界面显示
        Bitmap minBitmap = ImageUtils.zoomBitmap(bitmap, 100, 100);
        // 销毁之前的图片
        bitmap.recycle();

        mIvImage.setImageBitmap(minBitmap);
        mLyImage.setVisibility(View.VISIBLE);
    }

    private final int RC_CAMERA_PERM = 123;
    private final int RC_ALBUM_PERM = 124;

    /**
     * 进入相机
     */
    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void showToCamera() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            toCamera();
        } else {
            EasyPermissions.requestPermissions(this, "", RC_CAMERA_PERM, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void toCamera() {
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/oschina/Camera/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            AppContext.showToastShort("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = timeStamp + ".jpg";// 照片命名
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);

        tweet.setImageFilePath(savePath + fileName); // 该照片的绝对路径

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    @AfterPermissionGranted(RC_ALBUM_PERM)
    private void showToAlbum() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            toAlbum();
        } else {
            EasyPermissions.requestPermissions(this, "", RC_ALBUM_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * 进入图库
     * requestCode = REQUEST_CODE_GETIMAGE_BYSDCARD;
     */

    private void toAlbum() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pub_topic_menu, menu);
        mSendMenu = menu.findItem(R.id.public_menu_send);
        updateMenuState(mSendMenu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.public_menu_send:
                handleSubmit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 去发送动弹
     */
    private void handleSubmit() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            return;
        }
        String content = mEtInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            mEtInput.requestFocus();
            AppContext.showToastShort(R.string.tip_content_empty);
            return;
        }
        if (content.length() > MAX_TEXT_LENGTH) {
            AppContext.showToastShort(R.string.tip_content_too_long);
            return;
        }
        if (tweet == null) tweet = new Tweet();

        tweet.setAuthorid(AppContext.getInstance().getLoginUid());
        tweet.setBody(content);
        ServerTaskUtils.pubTweet(this, tweet);
        finish();
    }

    /**
     * 更新菜单图标
     */
    private void updateMenuState(MenuItem menu) {
        if (menu == null || mEtInput == null) {
            return;
        }
        if (mEtInput.getText().length() == 0) {
            menu.setEnabled(false);
            menu.setIcon(R.drawable.actionbar_unsend_icon);
        } else {
            menu.setEnabled(true);
            menu.setIcon(R.drawable.actionbar_send_icon);
        }
    }

    private String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } else {
            // 如果游标为空说明获取的已经是绝对路径了
            return uri.getPath();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        int tipres = R.string.pub_tweet_required_album_tip;
        if (perms.get(0).equals(Manifest.permission.CAMERA)) {
            tipres = R.string.pub_tweet_required_camera_tip;
        } else if (perms.get(0).equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            tipres = R.string.pub_tweet_required_album_tip;
        }
        String tip = getString(tipres);
        // 权限被拒绝了
        DialogHelp.getConfirmDialog(this,
                "权限申请",
                tip,
                "去设置",
                "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    }
                },
                null).show();

    }
}
