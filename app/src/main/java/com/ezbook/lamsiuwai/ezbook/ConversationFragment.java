package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 18/9/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConversationFragment extends Fragment {
    private FirebaseAuth auth;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference currentContactListRef;
    DatabaseReference userListRef;
    public FirebaseRecyclerAdapter<ShowChatListObject, ShowChatListViewHolder> mFirebaseAdapter;
    ProgressBar progressBar;
    LinearLayoutManager mLinearLayoutManager;

    public static ConversationFragment newInstance() {
        ConversationFragment fragment = new ConversationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        auth = FirebaseAuth.getInstance();


        firebaseDatabase = FirebaseDatabase.getInstance();

        //myRef = firebaseDatabase.getReference("ContactList").child("76yEQem2XeeE147VAsINa7hgvnI3");

        currentContactListRef = firebaseDatabase.getReference("ContactList").child(MainActivity.currenUserId);
        userListRef = firebaseDatabase.getReference("Users");
        progressBar = view.findViewById(R.id.show_chat_progressBar);

        //Recycler View
        recyclerView = view.findViewById(R.id.show_chat_recyclerView);

        mLinearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(mLinearLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(ProgressBar.VISIBLE);


        mFirebaseAdapter = new FirebaseRecyclerAdapter<ShowChatListObject, ShowChatListViewHolder>(ShowChatListObject.class, R.layout.show_chat_contact_item, ShowChatListViewHolder.class, currentContactListRef) {

            public void populateViewHolder(final ShowChatListViewHolder viewHolder, final ShowChatListObject model, final int position) {

                progressBar.setVisibility(ProgressBar.INVISIBLE);
                String userKey = getRef(position).getKey();
                Log.d ("contactList" ,this.getRef(position).getKey());
                userListRef.child(userKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserObject userObj = dataSnapshot.getValue(UserObject.class);
                        if(!userObj.getName().equals("Null"))
                        viewHolder.PersonName(userObj.getName());
                        viewHolder.PersonIcon(userObj.getProfileIcon());
                        viewHolder.personLastMsg(model.getLastMessage());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        DatabaseReference  ref = mFirebaseAdapter.getRef(position);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String retrieveId = dataSnapshot.getKey();
                                Log.d("onClickKey",retrieveId);
                                userListRef.child(retrieveId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            UserObject userObj = dataSnapshot.getValue(UserObject.class);
                                            String retrieveName = userObj.getName();
                                            String retrieveUserIconUrl = userObj.getProfileIcon();
                                            Intent intent = new Intent(getContext(), ChatConversationActivity.class);
                                            intent.putExtra("imageId", retrieveUserIconUrl);
                                            intent.putExtra("name", retrieveName);
                                            intent.putExtra("retrieveId",retrieveId);

                                            startActivity(intent);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        recyclerView.setAdapter(mFirebaseAdapter);


    }


    //View Holder For Recycler View
    public static class ShowChatListViewHolder extends RecyclerView.ViewHolder {
        private final TextView personName, personLastMsg;
        private final ImageView personIcon;
        private final LinearLayout layout;
        final LinearLayout.LayoutParams params;

        public ShowChatListViewHolder(final View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.chatPersonName);
            personLastMsg =itemView.findViewById(R.id.chatPersonLastMsg);
            personIcon = itemView.findViewById(R.id.chatPersonIcon);
            layout = itemView.findViewById(R.id.show_chat_single_item_layout);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        private void PersonName(String title) {
            personName.setText(title);
        }

        private void Layout_hide() {
            params.height = 0;
            layout.setLayoutParams(params);

        }


        private void personLastMsg(String title) {
            personLastMsg.setText(title);
        }


        private void PersonIcon(String url) {

            if (!url.equals("Null")) {
                Glide.with(itemView.getContext())
                        .load(url)
                        .crossFade()
                        .thumbnail(0.5f)
                        .placeholder(R.mipmap.loading)
                        .bitmapTransform(new CircleTransform(itemView.getContext()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(personIcon);
            }

        }


    }
}