package com.ezbook.lamsiuwai.ezbook;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by lamsiuwai on 24/9/2017.
 */
public class BookListingRecycleViewAdapter extends RecyclerView.Adapter<BookListingRecycleViewAdapter.ViewHolder> {
    private Context context;
    private List<BookObject> bookList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    // data is passed into the constructor

    public BookListingRecycleViewAdapter(Context context, List<BookObject> bookList) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.bookList = bookList;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.book_listing_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        List<AddPostImage> bookImages = bookList.get(position).getImages();
        BookListingSwipeAdapter viewPagerAdapter = new BookListingSwipeAdapter(context,bookImages);

        holder.bookListingUserName.setText(bookList.get(position).getBookOwner());
        holder.bookListingBookTitle.setText(bookList.get(position).getBookName());
        holder.bookListingTimetext.setText(bookList.get(position).getCreateTime());
        holder.bookListingPricetext.setText("HKD $ "+ String.valueOf(bookList.get(position).getPrice()));
        holder.bookListingDescText.setText(bookList.get(position).getBookDescrpition());
        holder.likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("likeIcon","Cliked") ;
            }
        });
        holder.msgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("likeIcon","Cliked") ;
            }
        });
        holder.bookImages.setAdapter(viewPagerAdapter);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return bookList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView bookListingUserName;
        private TextView bookListingBookTitle;
        private TextView bookListingTimetext;
        private TextView bookListingPricetext;
        private TextView bookListingDescText;
        private ImageView likeIcon ;
        private ImageView msgIcon;
        private ViewPager bookImages;
        public ViewHolder(View itemView) {
            super(itemView);
            bookListingUserName = itemView.findViewById(R.id.bookListingUserName);
            bookListingBookTitle = itemView.findViewById(R.id.bookListingBookTitle);
            bookListingTimetext = itemView.findViewById(R.id.bookListingTimetext);
            bookListingPricetext = itemView.findViewById(R.id.bookListingPricetext);
            bookListingDescText = itemView.findViewById(R.id.bookListingDescText);
            likeIcon = itemView.findViewById(R.id.bookListingLikeIcon);
            msgIcon = itemView.findViewById(R.id.bookListingMsgIcon);
            bookImages =itemView.findViewById(R.id.ViewPagesImages);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

