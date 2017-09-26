package com.ezbook.lamsiuwai.ezbook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lamsiuwai on 18/9/2017.
 */

public class BookListFragment extends Fragment {
    private String category;
    private String bookType;
    private FirebaseApp app;
    private FirebaseDatabase database;
    private List<BookObject> bookList;
    private BookListingRecycleViewAdapter bookListingAdapter;
    private RecyclerView bookListingView;

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
        DatabaseReference bookUpload = database.getReference("BookUpload");
        bookUpload.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookList.clear();
                for (DataSnapshot item_snapshot : dataSnapshot.getChildren()) {
                    BookObject bookObject = item_snapshot.getValue(BookObject.class);
                    bookList.add(bookObject);

                }
                bookListingView = view.findViewById(R.id.rvBookListing);
                bookListingView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                bookListingAdapter = new BookListingRecycleViewAdapter(getContext(), bookList);
                bookListingView.setAdapter(bookListingAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
