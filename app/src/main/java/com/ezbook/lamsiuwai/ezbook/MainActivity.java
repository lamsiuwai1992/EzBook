package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 18/9/2017.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements CategoryListingFragment.SendMessage{
    public static int DeviceWidth;
    public static String currenUserId;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            currenUserId = auth.getCurrentUser().getUid();

        }else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }


        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        DeviceWidth = metrics.widthPixels;

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                selectedFragment = CategoryListingFragment.newInstance();
                                break;
                            case R.id.navigation_add:
                                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                                //selectedFragment = AddPostFragment.newInstance();
                                startActivity(intent);
                                break;
                            case R.id.navigation_msg:
                                selectedFragment = ConversationFragment.newInstance();
                                break;
                            case R.id.navigation_like:
                                selectedFragment = LikePostFragment.newInstance();
                                break;
                            case R.id.navigation_profile:
                                selectedFragment = ProfileFragment.newInstance();
                                break;
                        }
                        if(selectedFragment!=null){
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();}
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, CategoryListingFragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }


    @Override
    public void sendData(String category, String bookType) {
        Log.d("category",category);
        Log.d("bookType",bookType);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, BookListFragment.newInstance(category,bookType));
        transaction.commit();

    }
}
