package net.oschina.app.bean.blog;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qiujuer
 */
public class BlogDetail extends Blog {
    private boolean favorite;
    private long authorId;
    private String authorPortrait;
    private int authorRelation;
    private String category;
    @SerializedName("abstract")
    private String abstractStr;
    private List<About> abouts;
    private List<Comment> comments;

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPortrait() {
        return authorPortrait;
    }

    public void setAuthorPortrait(String authorPortrait) {
        this.authorPortrait = authorPortrait;
    }

    public int getAuthorRelation() {
        return authorRelation;
    }

    public void setAuthorRelation(int authorRelation) {
        this.authorRelation = authorRelation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<About> getAbouts() {
        return abouts;
    }

    public void setAbouts(List<About> abouts) {
        this.abouts = abouts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getAbstract() {
        return abstractStr;
    }

    public void setAbstract(String abstractStr) {
        this.abstractStr = abstractStr;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public static class About implements Serializable {
        public long id;
        public String title;
        public int commentCount;
        public int viewCount;
    }

    public static class Refer implements Serializable {
        public String author;
        public String content;
        @SerializedName(value = "refer", alternate = {"reply"})
        public Refer refer;
    }

    public static class Comment extends Refer implements Serializable {
        public long id;
        public long authorId;
        public String authorPortrait;

        public String pubDate;
        public int appClient;
    }

}

