package net.oschina.app.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Favorite;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserFavoriteAdapter extends ListBaseAdapter<Favorite> {

    static class ViewHolder {

        @Bind(R.id.tv_favorite_title)
        TextView title;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_favorite, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Favorite favorite = (Favorite) mDatas.get(position);

        vh.title.setText(favorite.getTitle());
        return convertView;
    }

}
