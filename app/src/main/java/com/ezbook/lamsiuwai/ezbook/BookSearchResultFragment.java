package com.ezbook.lamsiuwai.ezbook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
 * Created by lamsiuwai on 12/10/2017.
 */

public class BookSearchResultFragment extends Fragment {
    private String bookName;
    private FirebaseApp app;
    private FirebaseDatabase database;
    private List<BookObject> bookList;
    private List<UserObject> creatorList;
    private BookListingRecycleViewAdapter bookListingAdapter;
    private RecyclerView bookListingView;
    private LinearLayoutManager mLayoutManager;
    private List<LikeBookObject> likeBookObjectList;
    private TextView noBooksTag ;
    private ProgressBar progressBar;
    public static BookSearchResultFragment newInstance(String bookName) {
        Bundle bundle = new Bundle();
        bundle.putString("bookName", bookName);

        BookSearchResultFragment fragment = new BookSearchResultFragment();
        fragment.setArguments(bundle);

        return fragment;
    }


    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            bookName = bundle.getString("bookName");
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
        noBooksTag =view.findViewById(R.id.booklisting_no_books);
        progressBar = view.findViewById(R.id.booklisting_progressBar);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarBooklisting);
        toolbar.setTitle(bookName);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        Log.d("bookName",bookName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        DatabaseReference bookUpload = database.getReference("BookUpload");
        Query query = bookUpload.orderByChild("searchBookName").startAt(null,bookName).endAt(bookName+"\uf8ff");
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
                progressBar.setVisibility(View.GONE);
                bookList.clear();
                creatorList.clear();
                for (DataSnapshot bookObjectSnapshot : dataSnapshot.getChildren()) {
                    BookObject bookObject = bookObjectSnapshot.getValue(BookObject.class);
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
                                if((bookList.size() > 0)&&(creatorList.size()> 0)){
                                    noBooksTag.setVisibility(View.INVISIBLE);
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
