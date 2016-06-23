package net.oschina.app.adapter.general;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import net.oschina.app.R;

/**
 * Created by fei on 2016/5/24.
 * desc:
 */
public class QuesActionAdapter extends BaseAdapter {

    private String[] data;
    private Context context;
    private int[] positions;

    public QuesActionAdapter(Context context, int[] positions) {
        this.context = context;
        this.data = context.getResources().getStringArray(R.array.ques_item);
        this.positions = positions;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_item_question_grid, parent, false);
            viewHolder.tv_action = (Button) convertView.findViewById(R.id.bt_ques_grid_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (positions[position] == 1) {
            viewHolder.tv_action.setTextColor(context.getResources().getColor(R.color.ques_bt_text_color_light));
        } else {
            viewHolder.tv_action.setTextColor(context.getResources().getColor(R.color.ques_bt_text_color_dark));
        }
        viewHolder.tv_action.setText(data[position]);
        return convertView;
    }

    public static class ViewHolder {
        Button tv_action;
    }
}
