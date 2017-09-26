package com.ezbook.lamsiuwai.ezbook;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by lamsiuwai on 24/9/2017.
 */

public class BookListingSwipeAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<AddPostImage> bookImage;
    public BookListingSwipeAdapter(Context context ,List<AddPostImage> bookImage){
        this.context = context;
        this.bookImage = bookImage;
    }

    @Override
    public int getCount() {
        return bookImage.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view ==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.booklisting_swipe,container,false);
        ImageView img =(ImageView)v.findViewById(R.id.bookListingImage);
        Glide.with(context).load(bookImage.get(position).getImageURL()).into(img);
        container.addView(v);
        return v;
    }


    @Override
    public void destroyItem(View container, int position, Object object) {
        container.refreshDrawableState();
    }
}
