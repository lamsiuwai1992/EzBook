package com.ezbook.lamsiuwai.ezbook;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import com.bumptech.glide.Glide;
/**
 * Created by lamsiuwai on 24/9/2017.
 */
public class BookListingRecycleViewAdapter extends RecyclerView.Adapter<BookListingRecycleViewAdapter.ViewHolder> {
    private Context context;
    private List<BookObject> bookList;
    private ItemClickListener mClickListener;
    private List<UserObject> userObjectList;
    private List<LikeBookObject> likeBookObjectList;
    private FirebaseDatabase database;
    private TabLayout myTab ;
    private FirebaseApp app;

    // data is passed into the constructor
    public BookListingRecycleViewAdapter(Context context, List<BookObject> bookList,List<UserObject>userObjectList,List<LikeBookObject>likeBookObjectList) {
        this.context=context;
        this.bookList = bookList;
        this.userObjectList =userObjectList;
        this.likeBookObjectList = likeBookObjectList;
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = mInflater.inflate(R.layout.book_listing_row, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_listing_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        List<AddPostImage> bookImages = bookList.get(position).getImages();
        final DatabaseReference userLikeBookRef =database.getReference("LikeBook").child(MainActivity.currenUserId);
        BookListingSwipeAdapter viewPagerAdapter = new BookListingSwipeAdapter(context,bookImages);
        holder.bookListingUserName.setText(userObjectList.get(position).getName());
        Glide.with(context).load(userObjectList.get(position).getProfileIcon()).crossFade().thumbnail(0.5f).placeholder(R.mipmap.loading).bitmapTransform(new CircleTransform(context)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.bookListingUserIcon);
        holder.bookListingBookTitle.setText(bookList.get(position).getBookName());
        holder.bookListingTimetext.setText(bookList.get(position).getCreateTime());
        holder.bookListingPricetext.setText("HKD $ "+ String.valueOf(bookList.get(position).getPrice()));
        holder.bookListingDescText.setText(bookList.get(position).getBookDescrpition());

        //vender the favorite icon
        for(int i = 0 ; i < likeBookObjectList.size() ; i++){
            LikeBookObject item =likeBookObjectList.get(i);
            if(item.getBookId().equals(bookList.get(position).getBookId())){
                Log.d("Adapter",item.getBookId());
                holder.likeIconEmpty.setVisibility(View.GONE);
                holder.likeIconFill.setVisibility(View.VISIBLE);
            }
        }
        //make the dots invisible
        if(bookImages.size() <= 1){
           myTab.setVisibility(View.GONE);}

        holder.likeIconEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeBookObject newLikeBookObject = new LikeBookObject(bookList.get(position).getBookId());
                likeBookObjectList.add(newLikeBookObject);
                userLikeBookRef.setValue(likeBookObjectList);
                holder.likeIconEmpty.setVisibility(View.GONE);
                holder.likeIconFill.setVisibility(View.VISIBLE);
            }
        });
        holder.likeIconFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            for(int i = 0 ; i < likeBookObjectList.size() ; i++){
                LikeBookObject item =likeBookObjectList.get(i);
                if(item.getBookId().equals(bookList.get(position).getBookId())){
                    likeBookObjectList.remove(i);
                }
            }
            userLikeBookRef.setValue(likeBookObjectList);
            holder.likeIconEmpty.setVisibility(View.VISIBLE);
            holder.likeIconFill.setVisibility(View.GONE);

            }
        });


        //generate the conversation
        holder.msgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("msgIcon","Cliked") ;
                String retrieveName = userObjectList.get(position).getName();
                String retrieveUserIconUrl = userObjectList.get(position).getProfileIcon();
                String retrieveId = bookList.get(position).getBookOwner();
                if(retrieveId.equals(MainActivity.currenUserId)){
                    Toast.makeText(context,"You cannot send message to yourself",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(context, ChatConversationActivity.class);
                    Log.d("retrieveName",retrieveName) ;
                    Log.d("retrieveUserIconUrl",retrieveUserIconUrl) ;
                    Log.d("retrieveId",retrieveId) ;
                    intent.putExtra("imageId", retrieveUserIconUrl);
                    intent.putExtra("name", retrieveName);
                    intent.putExtra("retrieveId",retrieveId);
                    context.startActivity(intent);
                }
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
        private ImageView bookListingUserIcon;
        private TextView bookListingBookTitle;
        private TextView bookListingTimetext;
        private TextView bookListingPricetext;
        private TextView bookListingDescText;
        private ImageView likeIconEmpty ;
        private ImageView likeIconFill;
        private ImageView msgIcon;
        private ViewPager bookImages;

        public ViewHolder(View itemView) {
            super(itemView);
            bookListingUserName = itemView.findViewById(R.id.bookListingUserName);
            bookListingBookTitle = itemView.findViewById(R.id.bookListingBookTitle);
            bookListingTimetext = itemView.findViewById(R.id.bookListingTimetext);
            bookListingPricetext = itemView.findViewById(R.id.bookListingPricetext);
            bookListingDescText = itemView.findViewById(R.id.bookListingDescText);
            bookListingUserIcon = itemView.findViewById(R.id.bookListingUserIcon);
            likeIconEmpty = itemView.findViewById(R.id.bookListingLikeIconEmpty);
            likeIconFill = itemView.findViewById(R.id.bookListingLikeIconFill);
            msgIcon = itemView.findViewById(R.id.bookListingMsgIcon);
            bookImages =itemView.findViewById(R.id.ViewPagesImages);
            myTab = itemView.findViewById(R.id.tabIndicator);
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

