package com.ezbook.lamsiuwai.ezbook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lamsiuwai on 18/9/2017.
 */

public class LikePostFragment extends Fragment {
    private FirebaseApp app;
    private FirebaseDatabase database;
    private  List<BookObject> bookList ;
    private  List<UserObject> creatorList  ;
    private BookListingRecycleViewAdapter bookListingAdapter;
    private RecyclerView bookListingView;
    private LinearLayoutManager mLayoutManager;
    private List<LikeBookObject> likeBookObjectList ;

    public static LikePostFragment newInstance() {
        LikePostFragment fragment = new LikePostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_booklisting, container, false);
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        bookList = new ArrayList<>();
        creatorList = new ArrayList<>();
        likeBookObjectList =new ArrayList<>();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarBooklisting);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);
        final DatabaseReference bookUpload = database.getReference("BookUpload");
        DatabaseReference userLikeBookRef =database.getReference("LikeBook").child(MainActivity.currenUserId);
        userLikeBookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot likeBookOjectSnapshot:dataSnapshot.getChildren()){
                    LikeBookObject likeBookObject = likeBookOjectSnapshot.getValue(LikeBookObject.class);
                    likeBookObjectList.add(likeBookObject);
                    String bookId = likeBookObject.getBookId();
                    Log.d("id",likeBookObject.getBookId());

                    bookUpload.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                BookObject bookObject = dataSnapshot.getValue(BookObject.class);
                                if(bookObject.getState().equals("Available")){
                                    final String creatorId = bookObject.getBookOwner();
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot userObj:dataSnapshot.getChildren()){
                                                if(userObj.getKey().equals(creatorId)){
                                                    Log.d("Creator:",creatorId);
                                                    UserObject userObject = userObj.getValue(UserObject.class);
                                                    creatorList.add(userObject);

                                                }
                                            }
                                            while (bookList.size()!=creatorList.size()){
                                                // spin waiting to wait all the book and list .
                                                Log.d("book size", String.valueOf(bookList.size()));
                                                Log.d("creator size", String.valueOf(creatorList.size()));
                                                return;
                                            }

                                            //set the recycler view
                                            mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                                            mLayoutManager.setStackFromEnd(true);
                                            mLayoutManager.setReverseLayout(true);
                                            bookListingView = view.findViewById(R.id.rvBookListing);
                                            bookListingView.setLayoutManager(mLayoutManager);
                                            bookListingAdapter = new BookListingRecycleViewAdapter(getActivity(), bookList,creatorList,likeBookObjectList);
                                            bookListingView.setAdapter(bookListingAdapter);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }

                                    });
                                    Log.d("BookList",bookObject.getBookId());
                                    bookList.add(bookObject);
                                }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }
}
