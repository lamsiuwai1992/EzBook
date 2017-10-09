package com.ezbook.lamsiuwai.ezbook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lamsiuwai on 18/9/2017.
 */

public class BookListFragment extends Fragment {
    private String category;
    private String bookType;
    private FirebaseApp app;
    private FirebaseDatabase database;
    private List<BookObject> bookList;
    private List<UserObject> creatorList;
    private BookListingRecycleViewAdapter bookListingAdapter;
    private RecyclerView bookListingView;
    private LinearLayoutManager mLayoutManager;
    private List<LikeBookObject> likeBookObjectList;

    public static BookListFragment newInstance(String category , String bookType) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("bookType",bookType);

        BookListFragment fragment = new BookListFragment();
        fragment.setArguments(bundle);

        return fragment;
    }


    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            category = bundle.getString("category");
            bookType = bundle.getString("bookType");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        readBundle(getArguments());
        final View view = inflater.inflate(R.layout.fragment_booklisting, container, false);
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        bookList = new ArrayList<>();
        creatorList = new ArrayList<>();
        likeBookObjectList =new ArrayList<>();
        Log.d("Categories",category);
        Log.d("BookType",bookType);
        DatabaseReference bookUpload = database.getReference("BookUpload");
        Query query = bookUpload.orderByChild("category").equalTo(category);
        DatabaseReference userLikeBookRef =database.getReference("LikeBook").child(MainActivity.currenUserId);
        userLikeBookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot likeBookOjectSnapshot:dataSnapshot.getChildren()){
                    LikeBookObject likeBookObject = likeBookOjectSnapshot.getValue(LikeBookObject.class);
                    likeBookObjectList.add(likeBookObject);
                    Log.d("id",likeBookObject.getBookId());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookList.clear();
                creatorList.clear();
                for (DataSnapshot bookObjectSnapshot : dataSnapshot.getChildren()) {
                    BookObject bookObject = bookObjectSnapshot.getValue(BookObject.class);
                    if((bookObject.getBookType().equals(bookType)&&(bookObject.getState().equals("Available")))){
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
                                    mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                                    mLayoutManager.setStackFromEnd(true);
                                    mLayoutManager.setReverseLayout(true);
                                    bookListingView = view.findViewById(R.id.rvBookListing);
                                    bookListingView.setLayoutManager(mLayoutManager);
                                    while (bookList.size()!=creatorList.size()){
                                        // avoid crashing the program
                                        Log.d("book size", String.valueOf(bookList.size()));
                                        Log.d("creator size", String.valueOf(creatorList.size()));
                                        return;
                                    }
                                    bookListingAdapter = new BookListingRecycleViewAdapter(getActivity(), bookList,creatorList,likeBookObjectList);
                                    bookListingView.setAdapter(bookListingAdapter);


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {


                                }

                            });
                        bookList.add(bookObject);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }
}
