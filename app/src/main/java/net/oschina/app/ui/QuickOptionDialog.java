package net.oschina.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import net.oschina.app.R;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.team.fragment.NoteEditFragment;
import net.oschina.app.util.UIHelper;

public class QuickOptionDialog extends Dialog implements
        android.view.View.OnClickListener {

    public interface OnQuickOptionformClick {
        void onQuickOptionClick(int id);
    }

    private OnQuickOptionformClick mListener;

    private QuickOptionDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    private QuickOptionDialog(Context context, int defStyle) {
        super(context, defStyle);
        View contentView = View.inflate(context, R.layout.dialog_quick_option, null);
        contentView.findViewById(R.id.ly_quick_option_text).setOnClickListener(
                this);
        contentView.findViewById(R.id.ly_quick_option_album)
                .setOnClickListener(this);
        contentView.findViewById(R.id.ly_quick_option_photo)
                .setOnClickListener(this);
        contentView.findViewById(R.id.ly_quick_option_voice)
                .setOnClickListener(this);
        contentView.findViewById(R.id.ly_quick_option_scan).setOnClickListener(
                this);
        contentView.findViewById(R.id.ly_quick_option_note).setOnClickListener(
                this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                QuickOptionDialog.this.dismiss();
                return true;
            }
        });
        super.setContentView(contentView);

    }

    public QuickOptionDialog(Context context) {
        this(context, R.style.quick_option_dialog);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    public void setOnQuickOptionformClickListener(OnQuickOptionformClick lis) {
        mListener = lis;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.ly_quick_option_text:
                onClickTweetPub(R.id.ly_quick_option_text);
                break;
            case R.id.ly_quick_option_album:
                onClickTweetPub(R.id.ly_quick_option_album);
                break;
            case R.id.ly_quick_option_photo:
                onClickTweetPub(R.id.ly_quick_option_photo);
                break;
            case R.id.ly_quick_option_voice:
                UIHelper.showSimpleBack(getContext(), SimpleBackPage.RECORD);
                break;
            case R.id.ly_quick_option_scan:
                UIHelper.showScanActivity(getContext());
                break;
            case R.id.ly_quick_option_note:
                onClickNote();
                break;
            default:
                break;
        }
        if (mListener != null) {
            mListener.onQuickOptionClick(id);
        }
        dismiss();
    }


    private void onClickTweetPub(int id) {
        int type = -1;
        switch (id) {
            case R.id.ly_quick_option_album:
                type = TweetPubActivity.ACTION_TYPE_ALBUM;
                break;
            case R.id.ly_quick_option_photo:
                type = TweetPubActivity.ACTION_TYPE_PHOTO;
                break;
            default:
                break;
        }
        UIHelper.showTweetActivity(getContext(), type, null);
    }

    private void onClickNote() {
        Bundle bundle = new Bundle();
        bundle.putInt(NoteEditFragment.NOTE_FROMWHERE_KEY,
                NoteEditFragment.QUICK_DIALOG);
        UIHelper.showSimpleBack(getContext(), SimpleBackPage.NOTE_EDIT, bundle);
    }
}