package com.ezbook.lamsiuwai.ezbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;
/**
 * Created by lamsiuwai on 18/9/2017.
 */

public class AddPostSelectionAdapter extends RecyclerView.Adapter<AddPostSelectionAdapter.ViewHolder> {
    private Context context;
    private List<AddPostSelectMenu> listMenu;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;



    // data is passed into the constructor

    public AddPostSelectionAdapter(Context context, List<AddPostSelectMenu> objects) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.listMenu =objects;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.add_post_item_menu, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.menu.setText(listMenu.get(position).getMenu());
        holder.selection.setText(listMenu.get(position).getSelection());

    }



    // total number of rows
    @Override
    public int getItemCount() {
        return listMenu.size();
    }

    public String getItem(int position) {
        return listMenu.get(position).getSelection();

    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView menu;
        TextView selection;

        public ViewHolder(View itemView) {
            super(itemView);
            menu =itemView.findViewById(R.id.tvMenuName);
            selection =itemView.findViewById(R.id.tvMenuSelect);
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

