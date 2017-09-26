package com.ezbook.lamsiuwai.ezbook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    SendMessage SM;

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

        // Get the Firebase app and all primitives we'll use
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        auth = FirebaseAuth.getInstance(app);
        storage = FirebaseStorage.getInstance(app);
        nssImgList = new ArrayList<>();
        secondaryImgList =new ArrayList<>();
        primaryImgList =new ArrayList<>();



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
                nssRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                nssAdpater = new CategoryListingRecyclerViewAdapter(getContext(), nssImgList);
                nssRecyclerView.setAdapter(nssAdpater);
                nssAdpater.setClickListener(new CategoryListingRecyclerViewAdapter.ItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        SM.sendData("nss", nssAdpater.getItem(position));
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
                secondaryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                secondaryAdapter = new CategoryListingRecyclerViewAdapter(getContext(), secondaryImgList);
                secondaryRecyclerView.setAdapter(secondaryAdapter);
                secondaryAdapter.setClickListener(new CategoryListingRecyclerViewAdapter.ItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        SM.sendData("secondary", secondaryAdapter.getItem(position));
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
                primaryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                primaryAdapter = new CategoryListingRecyclerViewAdapter(getContext(), primaryImgList);
                primaryRecyclerView.setAdapter(primaryAdapter);
                primaryAdapter.setClickListener(new CategoryListingRecyclerViewAdapter.ItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        SM.sendData("primary", primaryAdapter.getItem(position));
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }
}