package net.oschina.app.adapter.general;

import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.blog.Blog;
import net.oschina.app.fragment.general.BlogFragment;
import net.oschina.app.fragment.general.UserBlogFragment;
import net.oschina.app.util.StringUtils;

import java.util.List;


/**
 * Created by fei on 2016/5/24.
 * desc:
 */
public class BlogAdapter extends BaseListAdapter<Blog> {

    private boolean isUserBlog;

    public BlogAdapter(Callback callback) {
        super(callback);
    }

    public void setUserBlog(boolean userBlog) {
        isUserBlog = userBlog;
    }

    @Override
    protected void convert(ViewHolder vh, Blog item, int position) {

        switch (getItemViewType(position)) {

            case Blog.VIEW_TYPE_TITLE_HEAT:
                vh.setText(R.id.tv_blog_item_banner, R.string.blog_list_title_heat);
                break;
            case Blog.VIEW_TYPE_TITLE_NORMAL:
                vh.setText(R.id.tv_blog_item_banner, R.string.blog_list_title_normal);
                break;
            default:
                TextView title = vh.getView(R.id.tv_item_blog_title);
                TextView content = vh.getView(R.id.tv_item_blog_body);
                TextView history = vh.getView(R.id.tv_item_blog_history);
                TextView see = vh.getView(R.id.tv_info_view);
                TextView answer = vh.getView(R.id.tv_info_comment);

                if (item.isRecommend()) {
                    vh.setImage(R.id.iv_item_blog_tag_recommend, R.drawable.ic_label_recommend);
                    vh.setVisibility(R.id.iv_item_blog_tag_recommend);
                } else {
                    vh.setGone(R.id.iv_item_blog_tag_recommend);
                }

                if (item.isOriginal()) {
                    vh.setImage(R.id.iv_item_blog_tag, R.drawable.ic_label_originate);
                    vh.setVisibility(R.id.iv_item_blog_tag);
                } else {
                    vh.setGone(R.id.iv_item_blog_tag);
                }

                String title1 = item.getTitle();
                if (title1 != null) {
                    title.setText(title1.trim());
                }

                String cacheName = BlogFragment.HISTORY_BLOG;

                if (isUserBlog) {
                    cacheName = UserBlogFragment.HISTORY_BLOG;
                }

                if (AppContext.isOnReadedPostList(cacheName, item.getId() + "")) {
                    title.setTextColor(mCallback.getContext().getResources().getColor(R.color.count_text_color_light));
                    content.setTextColor(mCallback.getContext().getResources().getColor(R.color.count_text_color_light));
                } else {
                    title.setTextColor(mCallback.getContext().getResources().getColor(R.color.blog_title_text_color_light));
                    content.setTextColor(mCallback.getContext().getResources().getColor(R.color.ques_bt_text_color_dark));
                }

                String body = item.getBody();
                if (body != null) {
                    content.setText(body.trim());
                }

                String author = item.getAuthor();
                if (author != null) {
                    author = author.trim();
                    history.setText((author.length() > 9 ? author.substring(0, 9) : author) + "  " + StringUtils.friendly_time(item.getPubDate().trim()));
                }

                see.setText(item.getViewCount() + "");
                answer.setText(item.getCommentCount() + "");

                break;
        }
    }

    @Override
    protected int getLayoutId(int position, Blog item) {
        return item.getViewType() == Blog.VIEW_TYPE_DATA ? R.layout.fragment_item_blog : R.layout.fragment_item_blog_line;
    }

    @Override
    public int getItemViewType(int position) {
        List<Blog> datas = getDatas();
        return datas.get(position).getViewType();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }
}
