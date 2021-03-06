package com.ezbook.lamsiuwai.ezbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        final ImageView img =(ImageView)v.findViewById(R.id.bookListingImage);
        Glide.with(context).load(bookImage.get(position).getImageURL()).crossFade().thumbnail(0.5f).placeholder(R.mipmap.loading).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
        container.addView(v);

        return v;
    }


    @Override
    public void destroyItem(View container, int position, Object object) {
        container.refreshDrawableState();
    }
}
