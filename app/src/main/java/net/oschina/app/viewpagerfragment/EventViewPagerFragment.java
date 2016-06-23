package net.oschina.app.viewpagerfragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.EventList;
import net.oschina.app.fragment.EventFragment;
import net.oschina.app.ui.SimpleBackActivity;

/**
 * 活动viewpager页面
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年12月24日 下午4:46:04
 */
public class EventViewPagerFragment extends BaseViewPagerFragment {

    private int position = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        position = bundle.getInt(SimpleBackActivity.BUNDLE_KEY_ARGS, 0);
    }

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(R.array.events);
        if (position == 0) {
            adapter.addTab(title[0], "new_event", EventFragment.class, getBundle(EventList.EVENT_LIST_TYPE_NEW_EVENT));
            adapter.addTab(title[1], "my_event", EventFragment.class, getBundle(EventList.EVENT_LIST_TYPE_MY_EVENT));
            mTabStrip.setVisibility(View.VISIBLE);
        } else {
            adapter.addTab(title[1], "my_event", EventFragment.class, getBundle(EventList.EVENT_LIST_TYPE_MY_EVENT));
            mTabStrip.setVisibility(View.GONE);
        }
        mViewPager.setCurrentItem(position, true);
    }

    private Bundle getBundle(int event_type) {

        Bundle bundle = new Bundle();
        bundle.putInt(EventFragment.BUNDLE_KEY_EVENT_TYPE, event_type);
        return bundle;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }
}
