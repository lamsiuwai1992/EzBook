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
import com.miguelcatalan.materialsearchview.MaterialSearchView;

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
    private List<UserObject> creatorList;
    private List<LikeBookObject> likeBookObjectList;
    private BookListingRecycleViewAdapter bookListingAdapter;
    private RecyclerView bookListingView;
    private LinearLayoutManager mLayoutManager;
    private MaterialSearchView searchView;
    private TextView noBooksTag ;
    private ProgressBar progressBar;

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
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
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarBooklisting);
        toolbar.setTitle(bookType);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        searchView = (MaterialSearchView)view.findViewById(R.id.search_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                        fm.popBackStack();
            }
        });
        Log.d("Categories",category);
        Log.d("BookType",bookType);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                if(bookList.size() >0){
                BookListingRecycleViewAdapter bookListingAdapter = new BookListingRecycleViewAdapter(getActivity(), bookList,creatorList,likeBookObjectList);
                bookListingView.setAdapter(bookListingAdapter);
                Log.d("On Close Book Size", String.valueOf(bookList.size()));
                }
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty() && (bookList.size()>0 )){
                    List<BookObject> tempBookList = new ArrayList<BookObject>();
                    List<UserObject> tempCreatorList = new ArrayList<UserObject>();
                    for(int i = 0 ; i <= (bookList.size()-1);i++){
                        if(bookList.get(i).getSearchBookName().contains(newText.toLowerCase())){
                            tempBookList.add(bookList.get(i));
                            tempCreatorList.add(creatorList.get(i));
                        }
                    }

                    BookListingRecycleViewAdapter bookListingAdapter = new BookListingRecycleViewAdapter(getActivity(), tempBookList,tempCreatorList,likeBookObjectList);
                    bookListingView.setAdapter(bookListingAdapter);
                    return true;
                }
                return false;
            }
        });
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
                progressBar.setVisibility(View.GONE);
                bookList.clear();
                creatorList.clear();
                for (DataSnapshot bookObjectSnapshot : dataSnapshot.getChildren()) {
                    BookObject bookObject = bookObjectSnapshot.getValue(BookObject.class);
                    if((bookObject.getBookType().equals(bookType)&&(bookObject.getState().equals("Available")))){
                        noBooksTag.setVisibility(View.INVISIBLE);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
    }


}
