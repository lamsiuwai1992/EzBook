package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 18/9/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    DatabaseReference myRef;
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

        myRef = firebaseDatabase.getReference("Users");

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


        mFirebaseAdapter = new FirebaseRecyclerAdapter<ShowChatListObject, ShowChatListViewHolder>(ShowChatListObject.class, R.layout.show_chat_contact_item, ShowChatListViewHolder.class, myRef) {

            public void populateViewHolder(final ShowChatListViewHolder viewHolder, ShowChatListObject model, final int position) {

                progressBar.setVisibility(ProgressBar.INVISIBLE);

                if (!model.getName().equals("Null")) {
                    viewHolder.PersonName(model.getName());
                    viewHolder.PersonIcon(model.getProfileIcon());

                    String email = auth.getCurrentUser().getEmail();

                    //hide the contact of himself
                    if (model.getEmail().equals(email)) {
                        viewHolder.Layout_hide();


                    } else
                        viewHolder.PersonEmail(model.getEmail());
                }


                //OnClick Item
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {

                        DatabaseReference ref = mFirebaseAdapter.getRef(position);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String retrieveName = dataSnapshot.child("Name").getValue(String.class);
                                String retrieveEmail = dataSnapshot.child("Email").getValue(String.class);
                                String retrieveUrl = dataSnapshot.child("ProfileIcon").getValue(String.class);


                                Intent intent = new Intent(getContext(), ChatConversationActivity.class);
                                intent.putExtra("image_id", retrieveUrl);
                                intent.putExtra("email", retrieveEmail);
                                intent.putExtra("name", retrieveName);

                                startActivity(intent);
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
        private final TextView personName, personEmail;
        private final ImageView personIcon;
        private final LinearLayout layout;
        final LinearLayout.LayoutParams params;

        public ShowChatListViewHolder(final View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.chatPersonName);
            personEmail =itemView.findViewById(R.id.chatPersonEmail);
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


        private void PersonEmail(String title) {
            personEmail.setText(title);
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