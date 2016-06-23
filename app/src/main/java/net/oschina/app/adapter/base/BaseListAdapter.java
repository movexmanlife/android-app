package net.oschina.app.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.RequestManager;

import net.oschina.app.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 通用的ViewHolder
 * <p/>
 * Created by 火蚁 on 15/4/8.
 */
@SuppressWarnings("unused")
public abstract class BaseListAdapter<T> extends BaseAdapter implements ViewHolder.Callback {
    protected LayoutInflater mInflater;
    private List<T> mDatas;
    protected Callback mCallback;

    public BaseListAdapter(Callback callback) {
        this.mCallback = callback;
        this.mInflater = LayoutInflater.from(callback.getContext());
        this.mDatas = new ArrayList<T>();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (position >= 0 && position < mDatas.size())
            return mDatas.get(position);
        return null;
    }




    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T time = getItem(position);
        int layoutId = getLayoutId(position, time);
        final ViewHolder vh = ViewHolder.getViewHolder(this, convertView, parent, layoutId, position);
        convert(vh, time, position);
        return vh.getConvertView();
    }

    public List<T> getDatas() {
        return this.mDatas;
    }

    protected abstract void convert(ViewHolder vh, T item, int position);

    protected abstract int getLayoutId(int position, T item);

    public void updateItem(int location, T item) {
        if (mDatas.isEmpty()) return;
        mDatas.set(location, item);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        checkListNull();
        mDatas.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int location, T item) {
        checkListNull();
        mDatas.add(location, item);
        notifyDataSetChanged();
    }

    public void addItem(List<T> items) {
        checkListNull();
        mDatas.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(int position, List<T> items) {
        checkListNull();
        mDatas.addAll(position, items);
        notifyDataSetChanged();
    }

    public void removeItem(int location) {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.remove(location);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void checkListNull() {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
    }

    public int getCurrentPage() {
        return getCount() % 20;
    }

    @Override
    public RequestManager getImgLoader() {
        return mCallback.getImgLoader();
    }

    @Override
    public LayoutInflater getInflate() {
        return mInflater;
    }

    public interface Callback {
        RequestManager getImgLoader();

        Context getContext();

        Date getSystemTime();
    }
}
