package net.oschina.app.team.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.team.bean.TeamDiscuss;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;

import butterknife.ButterKnife;
import butterknife.Bind;

/**
 * team 讨论区帖子
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月9日 下午6:22:54
 */
public class TeamDiscussAdapter extends ListBaseAdapter<TeamDiscuss> {

    static class ViewHolder {

        @Bind(R.id.tv_title)
        TextView title;
        @Bind(R.id.tv_description)
        TextView description;
        @Bind(R.id.tv_author)
        TextView author;
        @Bind(R.id.tv_date)
        TextView time;
        @Bind(R.id.tv_count)
        TextView comment_count;
        @Bind(R.id.tv_vote_up)
        TextView vote_up;

        @Bind(R.id.iv_face)
        public AvatarView face;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_team_discuss, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TeamDiscuss item = mDatas.get(position);

        vh.face.setUserInfo(item.getAuthor().getId(), item.getAuthor()
                .getName());
        vh.face.setAvatarUrl(item.getAuthor().getPortrait());
        vh.title.setText(item.getTitle());
        String body = item.getBody().trim();
        vh.description.setVisibility(View.GONE);
        vh.description.setVisibility(View.VISIBLE);
        vh.description.setText(HTMLUtil.replaceTag(body));
        vh.author.setText(item.getAuthor().getName());
        vh.time.setText(StringUtils.friendly_time(item.getCreateTime()));
        vh.vote_up.setText(item.getVoteUp() + "");
        vh.comment_count.setText(item.getAnswerCount() + "");
        return convertView;
    }
}
