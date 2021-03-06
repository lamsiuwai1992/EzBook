package com.ezbook.lamsiuwai.ezbook;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lamsiuwai on 13/9/2017.
 */

public class CategoryListingFragment extends Fragment {

    private FirebaseApp app;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private CategoryListingRecyclerViewAdapter secondaryAdapter;
    private CategoryListingRecyclerViewAdapter primaryAdapter;
    private CategoryListingRecyclerViewAdapter nssAdpater;
    private List<SubjectImage> nssImgList;
    private List<SubjectImage> secondaryImgList;
    private List<SubjectImage> primaryImgList;
    private RecyclerView nssRecyclerView;
    private RecyclerView secondaryRecyclerView;
    private RecyclerView primaryRecyclerView;
    private MaterialSearchView searchView;
    SendMessage SM;
    SendQueryResult QuerryResult;
    public static CategoryListingFragment newInstance() {
        CategoryListingFragment fragment = new CategoryListingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_categorylisting, container, false);
        setHasOptionsMenu(true);
        // Get the Firebase app and all primitives we'll use
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        auth = FirebaseAuth.getInstance(app);
        storage = FirebaseStorage.getInstance(app);
        nssImgList = new ArrayList<>();
        secondaryImgList =new ArrayList<>();
        primaryImgList =new ArrayList<>();


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarCategoryListing);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
       ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("EzBook");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        searchView = (MaterialSearchView)view.findViewById(R.id.search_view);
        searchView.setHint("Enter the name of the books");
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Submit","yes");
                if(query!=null && !query.isEmpty())
                {
                    Log.d("Result",query.toLowerCase());
                    QuerryResult.sendResult(query.toLowerCase());
                    return true ;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        DatabaseReference nssCategoryRef = database.getReference("category").child("nss");
        DatabaseReference secondaryCategoryRef = database.getReference("category").child("secondary");
        DatabaseReference primaryCategoryRef = database.getReference("category").child("primary");

        nssCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nssImgList.clear();
                for (DataSnapshot item_snapshot : dataSnapshot.getChildren()) {
                    SubjectImage img = item_snapshot.getValue(SubjectImage.class);
                    nssImgList.add(img);

                }
                nssRecyclerView = view.findViewById(R.id.rvNssSubjects);
                nssRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                nssAdpater = new CategoryListingRecyclerViewAdapter(getActivity(), nssImgList);
                nssRecyclerView.setAdapter(nssAdpater);
                nssAdpater.setClickListener(new CategoryListingRecyclerViewAdapter.ItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        SM.sendData("HKDSE", nssAdpater.getSubjectName(position));
                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        secondaryCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                secondaryImgList.clear();
                for (DataSnapshot item_snapshot : dataSnapshot.getChildren()) {
                    SubjectImage img = item_snapshot.getValue(SubjectImage.class);
                    secondaryImgList.add(img);

                }
                secondaryRecyclerView = view.findViewById(R.id.rvSecondarySubjects);
                secondaryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                secondaryAdapter = new CategoryListingRecyclerViewAdapter(getActivity(), secondaryImgList);
                secondaryRecyclerView.setAdapter(secondaryAdapter);
                secondaryAdapter.setClickListener(new CategoryListingRecyclerViewAdapter.ItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        SM.sendData("Secondary", secondaryAdapter.getSubjectName(position));
                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        primaryCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                primaryImgList.clear();
                for (DataSnapshot item_snapshot : dataSnapshot.getChildren()) {
                    SubjectImage img = item_snapshot.getValue(SubjectImage.class);
                    primaryImgList.add(img);

                }
                primaryRecyclerView = view.findViewById(R.id.rvPrimarySubjects);
                primaryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                primaryAdapter = new CategoryListingRecyclerViewAdapter(getActivity(), primaryImgList);
                primaryRecyclerView.setAdapter(primaryAdapter);
                primaryAdapter.setClickListener(new CategoryListingRecyclerViewAdapter.ItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        SM.sendData("Primary", primaryAdapter.getSubjectName(position));
                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }

    interface SendMessage {
        void sendData(String category, String bookType);
    }

    interface SendQueryResult{
        void sendResult(String bookName);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            SM = (SendMessage) getActivity();
            QuerryResult = (SendQueryResult) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
    }
}