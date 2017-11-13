package com.ezbook.lamsiuwai.ezbook;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/**
 * Created by hohilary on 6/11/2017.
 */

public class MyBookListingRecycleViewAdapter extends RecyclerView.Adapter<MyBookListingRecycleViewAdapter.ViewHolder> {

    View view1;
    MyBookListingRecycleViewAdapter.ViewHolder viewHolder1;

    private Context context;
    private List<BookObject> bookList;
    DatabaseReference databaseReference_book;

    public MyBookListingRecycleViewAdapter(Context context, List<BookObject> bookList){
        this.context = context;
        this.bookList = bookList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView BookTitle;
        public TextView BookTime;
        public TextView BookPrice;
        public TextView BookDescription;
        public Switch BookStatus;
        public ImageView BookImage;
        public CardView RecycleBookList;

        public ViewHolder(View v){
            super(v);
            BookTitle = (TextView)v.findViewById(R.id.bookTitle);
            BookTime = (TextView)v.findViewById(R.id.bookTime);
            BookPrice = (TextView)v.findViewById(R.id.bookPrice);
            BookDescription = (TextView)v.findViewById(R.id.bookDesc);
            BookStatus = (Switch) v.findViewById(R.id.bookStatus);
            RecycleBookList = (CardView)v.findViewById(R.id.booklistCard);
        }
    }

    @Override
    public MyBookListingRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items,parent,false);
        viewHolder1 = new MyBookListingRecycleViewAdapter.ViewHolder(view1);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final MyBookListingRecycleViewAdapter.ViewHolder holder, final int position){
        List<AddPostImage> bookImages = bookList.get(position).getImages();
        if (!bookList.get(position).getState().equals("Available")){
            holder.RecycleBookList.setBackgroundColor(Color.parseColor("#E6E6E6"));
            holder.BookStatus.setChecked(true);
        }

        holder.BookTitle.setText(bookList.get(position).getBookName());
        holder.BookTime.setText(bookList.get(position).getCreateTime());
        holder.BookDescription.setText(bookList.get(position).getBookDescrpition());
        holder.BookPrice.setText("$"+ String.valueOf(bookList.get(position).getPrice()));

        holder.BookStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                databaseReference_book = FirebaseDatabase.getInstance().getReference("BookUpload").child(bookList.get(position).getBookId());
                if (isChecked){
                    holder.RecycleBookList.setBackgroundColor(Color.parseColor("#E6E6E6"));
                    bookList.get(position).setState("Sold");
                    databaseReference_book.child("state").setValue("Sold");
                }
                else {
                    holder.RecycleBookList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    databaseReference_book.child("state").setValue("Available");
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return bookList.size();
    }
}
