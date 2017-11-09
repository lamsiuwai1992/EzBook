package com.ezbook.lamsiuwai.ezbook;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;


/**
 * Created by hohilary on 6/11/2017.
 */

public class MyBookListingRecycleViewAdapter extends RecyclerView.Adapter<MyBookListingRecycleViewAdapter.ViewHolder> {

    View view1;
    MyBookListingRecycleViewAdapter.ViewHolder viewHolder1;
    TextView Booktitle;


    private Context context;
    private List<BookObject> bookList;

    public MyBookListingRecycleViewAdapter(Context context, List<BookObject> bookList){
        this.context = context;
        this.bookList = bookList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView BookTitle;
        public TextView BookTime;
        public TextView BookPrice;
        public TextView BookDescription;

        public ViewHolder(View v){
            super(v);
            BookTitle = (TextView)v.findViewById(R.id.bookTitle);
            BookTime = (TextView)v.findViewById(R.id.bookTime);
            BookPrice = (TextView)v.findViewById(R.id.bookPrice);
            BookDescription = (TextView)v.findViewById(R.id.bookDesc);
        }
    }

    @Override
    public MyBookListingRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items,parent,false);
        viewHolder1 = new MyBookListingRecycleViewAdapter.ViewHolder(view1);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(MyBookListingRecycleViewAdapter.ViewHolder holder, int position){
        holder.BookTitle.setText(bookList.get(position).getBookName());
        holder.BookTime.setText(bookList.get(position).getCreateTime());
        holder.BookDescription.setText(bookList.get(position).getBookDescrpition());
        holder.BookPrice.setText("$"+ String.valueOf(bookList.get(position).getPrice()));
    }

    @Override
    public int getItemCount(){
        return bookList.size();
    }
}
