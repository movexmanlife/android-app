package net.oschina.app.bean.blog;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by fei on 2016/5/24.
 * desc:   blog bean
 */
public class Blog implements Serializable {

    public static final int VIEW_TYPE_DATA = 0;
    public static final int VIEW_TYPE_TITLE_HEAT = 1;
    public static final int VIEW_TYPE_TITLE_NORMAL = 2;

    private long id;
    private String title;
    private String body;
    private String author;
    private String pubDate;
    private int commentCount;
    private int viewCount;
    private String href;
    private boolean recommend;  //是否推荐
    private boolean original;  //是否原创
    private int type;   //博客类型

    @Expose
    private int viewType = VIEW_TYPE_DATA; //  界面显示类型 0:常规, 1: 热门 2:最近


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }
}

