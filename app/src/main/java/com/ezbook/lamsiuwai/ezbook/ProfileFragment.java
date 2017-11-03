package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 18/9/2017.
 */

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.R.attr.data;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;

    FirebaseUser user;
    DatabaseReference databaseReference;
    private Button btnLogout;
    String uid;
    TextView username;
    AlertDialog changename;
    EditText editText;
    ImageView usericon;
    UserObject userObj;
    Uri FilePathUri;

    ProgressDialog progressDialog ;
    StorageReference storageReference;

    private  final String Storage_Path = "ProfileIcon/";
    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_INTENT = 5;
    public static final int READ_EXTERNAL_STORAGE = 0, MULTIPLE_PERMISSIONS = 10;

    private String pictureImagePath = "";
    final CharSequence[] options = {"Camera", "Gallery"};
    String[] permissions= new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,};


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

        username = view.findViewById(R.id.userName);
        changename = new AlertDialog.Builder(getActivity()).create();
        editText = new EditText(getActivity());


        usericon = view.findViewById(R.id.userIcon);
        btnLogout = view.findViewById(R.id.btn_logout);
        recyclerView = (RecyclerView) view.findViewById(R.id.show_book_recyclerView);

        progressDialog = new ProgressDialog(getActivity());
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(MainActivity.currenUserId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userObj = dataSnapshot.getValue(UserObject.class);
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

        username.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                changename.setTitle("Change Your Username");
                changename.setView(editText,50,0,50,0);
                editText.setText(username.getText());
                editText.setSelection(username.getText().length());

                changename.show();
            }
        });

        changename.setButton(DialogInterface.BUTTON_POSITIVE,"SAVE", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i ){
                databaseReference.child("name").setValue(editText.getText().toString());
                username.setText(userObj.getName());
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePathUri = null;

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select the Source");
                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Camera"))
                        {
                            if (checkPermissions())
                            {
                                callCamera();
                            }
                        }
                        if(options[item].equals("Gallery"))
                        {
                            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                            }
                            else
                            {
                                callgalary();
                            }
                        }
                    }
                });
                builder.show();
            }
        });
        return view;
    }

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callgalary();
                return;

            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    callCamera();
                }
            }
        }
    }

    private void callgalary(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    private void callCamera() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;

        File file = new File(pictureImagePath);

        Uri outputFileUri = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getApplicationContext().getApplicationContext().getPackageName() + ".provider", file);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        cameraIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(cameraIntent, CAMERA_INTENT);
    }


    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT&& resultCode == RESULT_OK && data != null && data.getData() != null){
            FilePathUri = data.getData();

            try {
                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), FilePathUri);
                usericon.setImageBitmap(bitmap);

                UploadImageFileToFirebaseStorage();
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }else if(requestCode == CAMERA_INTENT && resultCode == RESULT_OK){

            File imgFile = new  File(pictureImagePath);

            if(imgFile.exists()) {
                FilePathUri =Uri.fromFile(imgFile);

                try {
                    // Getting selected image into Bitmap.
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), FilePathUri);
                    usericon.setImageBitmap(bitmap);

                    UploadImageFileToFirebaseStorage();
                }
                catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    private void UploadImageFileToFirebaseStorage() {
        if (FilePathUri != null) {
            // Setting progressDialog Title.
            progressDialog.setTitle("Upload images to the server");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getActivity().getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            Uri downloadurl = taskSnapshot.getDownloadUrl();
//                            databaseReference.child("profileIcon").setValue(downloadurl.toString());
                        }
                    })

                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception error message.
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        }
    }
}
