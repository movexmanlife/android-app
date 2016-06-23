package net.oschina.app.adapter.general;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.event.Event;
import net.oschina.app.fragment.general.EventFragment;
import net.oschina.app.util.StringUtils;

/**
 * Created by huanghaibin
 * on 16-5-25.
 */
public class EventAdapter extends BaseListAdapter<Event> {
    public EventAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Event item, int position) {
        vh.setText(R.id.tv_event_title, item.getTitle());
        vh.setImageForNet(R.id.iv_event, item.getImg());
        vh.setText(R.id.tv_event_pub_date, StringUtils.getDateString(item.getStartDate()));
        vh.setText(R.id.tv_event_member, item.getApplyCount() + "人参与");
        vh.setTextColor(R.id.tv_event_title,
                AppContext.isOnReadedPostList(EventFragment.HISTORY_EVENT, item.getId() + "") ?
                        (mCallback.getContext().getResources().getColor(R.color.count_text_color_light)) : (mCallback.getContext().getResources().getColor(R.color.day_textColor)));
        switch (item.getStatus()) {
            case Event.STATUS_END:
                vh.setText(R.id.tv_event_state, R.string.event_status_end, R.drawable.bg_event_end, 0x1a000000);
                vh.setTextColor(R.id.tv_event_title, mCallback.getContext().getResources().getColor(R.color.light_gray));
                break;
            case Event.STATUS_ING:
                vh.setText(R.id.tv_event_state, R.string.event_status_ing, R.drawable.bg_event_ing, 0xFF24cf5f);
                break;
            case Event.STATUS_SING_UP:
                vh.setText(R.id.tv_event_state, R.string.event_status_sing_up, R.drawable.bg_event_end, 0x1a000000);
                vh.setTextColor(R.id.tv_event_title, mCallback.getContext().getResources().getColor(R.color.light_gray));
                break;
        }
        int typeStr = R.string.oscsite;
        switch (item.getType()) {
            case Event.EVENT_TYPE_OSC:
                typeStr = R.string.event_type_osc;
                break;
            case Event.EVENT_TYPE_TEC:
                typeStr = R.string.event_type_tec;
                break;
            case Event.EVENT_TYPE_OTHER:
                typeStr = R.string.event_type_other;
                break;
            case Event.EVENT_TYPE_OUTSIDE:
                typeStr = R.string.event_type_outside;
                break;
        }
        vh.setText(R.id.tv_event_type, typeStr);
    }

    @Override
    protected int getLayoutId(int position, Event item) {
        return R.layout.item_list_event;
    }
}
