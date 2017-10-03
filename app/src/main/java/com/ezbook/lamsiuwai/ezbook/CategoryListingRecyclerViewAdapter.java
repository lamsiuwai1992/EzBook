package com.ezbook.lamsiuwai.ezbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by lamsiuwai on 15/9/2017.
 */

public class CategoryListingRecyclerViewAdapter extends RecyclerView.Adapter<CategoryListingRecyclerViewAdapter.ViewHolder> {
        private Context context;
        private List<SubjectImage> listImage;
        private LayoutInflater mInflater;
        private ItemClickListener mClickListener;


        // data is passed into the constructor

        public CategoryListingRecyclerViewAdapter(Context context, List<SubjectImage> objects) {
            this.context=context;
            this.mInflater = LayoutInflater.from(context);
            listImage = objects;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.categorylisting_recyclerview_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        // binds the data to the view and textview in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Glide.with(context).load(listImage.get(position).getSubjectIcon()).into(holder.imageView);
            holder.myTextView.setText(listImage.get(position).getSubjectName());
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return listImage.size();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imageView;
            public TextView myTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView =itemView.findViewById(R.id.imageSubjectView);
                myTextView = itemView.findViewById(R.id.tvSubjectName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        public String getSubjectName(int position) {
            return listImage.get(position).getSubjectName();
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

