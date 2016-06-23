package net.oschina.app.contract;

import net.oschina.app.bean.blog.BlogDetail;

/**
 * Created by qiujuer
 * on 16/5/28.
 */

public interface BlogDetailContract {
    interface Operator {
        BlogDetail getBlogDetail();

        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 关注
        void toFollow();

        // 举报
        void toReport();

        // 提交评价
        void toSendComment(long id, long authorId, String comment);
    }

    interface View {
        void toFavoriteOk(BlogDetail blogDetail);

        void toShareOk();

        void toFollowOk(BlogDetail blogDetail);

        void toSendCommentOk();
    }
}
