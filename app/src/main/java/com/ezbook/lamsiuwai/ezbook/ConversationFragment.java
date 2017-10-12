package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 18/9/2017.
 */

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference currentContactListRef;
    private DatabaseReference currentChatListRef;
    private DatabaseReference userListRef;
    public FirebaseRecyclerAdapter<ShowChatListObject, ShowChatListViewHolder> mFirebaseAdapter;
    private ProgressBar progressBar;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView contactlist_no_contact;
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

        currentContactListRef = firebaseDatabase.getReference("ContactList").child(MainActivity.currenUserId);
        currentChatListRef = firebaseDatabase.getReference("Chat").child(MainActivity.currenUserId);
        userListRef = firebaseDatabase.getReference("Users");
        progressBar = view.findViewById(R.id.show_chat_progressBar);
        contactlist_no_contact = view.findViewById(R.id.contactlist_no_contact);
        //Recycler View
        recyclerView = view.findViewById(R.id.show_chat_recyclerView);

        mLinearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(mLinearLayoutManager);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Paint p = new Paint();
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        /* swipe right
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_delete_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);*/
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_delete_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //Toast.makeText(getContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                //Toast.makeText(getContext(), "on Swiped "+position, Toast.LENGTH_SHORT).show();
                String key = mFirebaseAdapter.getRef(position).getKey();
                Log.d("Key ",key);
                currentContactListRef.child(key).setValue(null);
                currentChatListRef.child(key).removeValue();
                //currentContactListRef.getRef(position).getKey();
                //mFirebaseAdapter.notifyDataSetChanged();

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        currentContactListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    progressBar.setVisibility(View.INVISIBLE);
                    contactlist_no_contact.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ShowChatListObject, ShowChatListViewHolder>(ShowChatListObject.class, R.layout.show_chat_contact_item, ShowChatListViewHolder.class, currentContactListRef) {

            public void populateViewHolder(final ShowChatListViewHolder viewHolder, final ShowChatListObject model, final int position) {

                progressBar.setVisibility(ProgressBar.INVISIBLE);
                contactlist_no_contact.setVisibility(View.INVISIBLE);
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
        private final ImageView personIcon , cameraIcon;
        private final LinearLayout layout;
        final LinearLayout.LayoutParams params;

        public ShowChatListViewHolder(final View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.chatPersonName);
            personLastMsg =itemView.findViewById(R.id.chatPersonLastMsg);
            personIcon = itemView.findViewById(R.id.chatPersonIcon);
            cameraIcon = itemView.findViewById(R.id.chatPersonLastMsgIcon);
            layout = itemView.findViewById(R.id.show_chat_single_item_layout);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        private void PersonName(String title) {
            personName.setText(title);
        }


        private void personLastMsg(String title) {
            if(title.equals("https")){
                cameraIcon.setVisibility(View.VISIBLE);
                personLastMsg.setVisibility(View.INVISIBLE);
            }
            else
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