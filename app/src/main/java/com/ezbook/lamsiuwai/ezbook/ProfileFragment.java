package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 18/9/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    DatabaseReference databaseReference;
    FirebaseUser user;
    String uid;
    List<String> itemlist;
    TextView username;
    ImageView usericon;
    ArrayAdapter<String> adapter;

    private FirebaseAuth auth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference currentContactListRef;
    private DatabaseReference currentChatListRef;
    private DatabaseReference userListRef;


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        itemlist = new ArrayList<>();

        username = view.findViewById(R.id.userName);
        usericon = view.findViewById(R.id.userIcon);
        btnLogout = view.findViewById(R.id.btn_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(MainActivity.currenUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    UserObject userObj = dataSnapshot.getValue(UserObject.class);
                    if(!userObj.getName().equals("Null")){
                        username.setText(userObj.getName());
                        Glide.with(ProfileFragment.this.getContext()).load(userObj.getProfileIcon()).into(usericon);
//                        Glide.with(ProfileFragment.this).load(userObj.getProfileIcon()).bitmapTransform(new CircleTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(usericon);
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }
}
