package com.ezbook.lamsiuwai.ezbook;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {

    private  final String Storage_Path = "BookImages/";
    private  final String Database_BookImages_Path = "BookImagesUpload";
    private  final String Database_BookObejct_Path = "BookUpload";
    private int currentPosition = 0 ;
    private ImageView Image0;
    private ImageView Image1;
    private ImageView Image2;
    private ImageView Image3;
    private FirebaseApp app;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    Uri FilePathUri;
    DatabaseReference databaseImagesReference;
    DatabaseReference bookObjectReference;
    StorageReference storageReference;
    ProgressDialog progressDialog ;
    private Button UploadButton;
    int Image_Request_Code = 7;
    private List<AddPostImage> imgList;
    private RecyclerView mRecyclerView;
    private AddPostSelectionAdapter mRecyclerViewAdpater ;
    private List<AddPostSelectMenu> menuList;
    private boolean isSetCategory =false;
    private Button sumitBtn ;
    private Button cancelBtn;
    private EditText bookName;
    private EditText price;
    private EditText bookDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        app = FirebaseApp.getInstance();
        auth = FirebaseAuth.getInstance(app);
        storage = FirebaseStorage.getInstance(app);
        databaseImagesReference = FirebaseDatabase.getInstance().getReference(Database_BookImages_Path);
        bookObjectReference = FirebaseDatabase.getInstance().getReference(Database_BookObejct_Path);

        storageReference = FirebaseStorage.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        imgList = new ArrayList<>();
        progressDialog = new ProgressDialog(AddPostActivity.this);
        UploadButton = (Button)findViewById(R.id.buttonUploadImage);
        Image0 =(ImageView)findViewById(R.id.imageUpload0);
        Image1 = (ImageView)findViewById(R.id.imageUpload1);
        Image2 = (ImageView)findViewById(R.id.imageUpload2);
        Image3 = (ImageView)findViewById(R.id.imageUpload3);
        mRecyclerView = (RecyclerView) findViewById(R.id.addPostMenuRecycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        menuList =new ArrayList<>() ;
        menuList.add(new AddPostSelectMenu("Category",""));
        menuList.add(new AddPostSelectMenu("BookType",""));
        mRecyclerViewAdpater = new AddPostSelectionAdapter(this, menuList);
        mRecyclerView.setAdapter(mRecyclerViewAdpater);
        sumitBtn = (Button)findViewById(R.id.addSubmitBth);
        cancelBtn = (Button)findViewById(R.id.addCancelBth);
        bookName = (EditText) findViewById(R.id.addPostBookNameField);
        price = (EditText) findViewById(R.id.addPostPriceField);
        bookDesc = (EditText) findViewById(R.id.addPostDescField);

        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPosition <=3){
                // Creating intent.
                FilePathUri = null;
                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);}
                else
                    Toast.makeText(AddPostActivity.this,"You cannot upload more than 4 images!",Toast.LENGTH_LONG).show();

            }
        });
        mRecyclerViewAdpater.setClickListener(new AddPostSelectionAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String value ;
                int selectionPosition = position ;
                switch (selectionPosition){
                    case 0 : value ="default" ; menuList.get(1).setSelection(""); mRecyclerViewAdpater.notifyItemChanged(1) ;break;
                    case 1 : value = mRecyclerViewAdpater.getItem(0); break;
                    default: value = "error";

                }
                if(!isSetCategory &&selectionPosition==1){
                    Toast.makeText(AddPostActivity.this,"Please choose category first!",Toast.LENGTH_LONG).show();
                }else{
                Intent i = new Intent(AddPostActivity.this, SelectionListActivity.class);
                i.putExtra("selectionValue", value);
                i.putExtra("position",String.valueOf(selectionPosition));
                startActivityForResult(i,100);}



            }
        });

        sumitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFiledNonEmpty()){
                    String bkName = bookName.getText().toString().trim();
                    String category = menuList.get(0).getSelection();
                    String bookType = menuList.get(1).getSelection();
                    int bkprice = Integer.parseInt(price.getText().toString().trim());
                    String bkDesc =bookDesc.getText().toString().trim();
                    String user = auth.getCurrentUser().getUid();
                    BookObject newBookObject = new BookObject(bkName,imgList,user,bkprice,category,bookType,bkDesc);
                    createNewBook(newBookObject);
                }else {
                    Toast.makeText(AddPostActivity.this, "Please Input Details and Upload at Least One Picture", Toast.LENGTH_LONG).show();
                }


            }



        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the current activity
                finish();
            }
        });



    }

    private void createNewBook(BookObject bookObject) {
        try{
            View view = this.getCurrentFocus();
            if(view!=null){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(AddPostActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);}
            String bookId = bookObjectReference.push().getKey();
            bookObjectReference.child(bookId).setValue(bookObject).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            Toast.makeText(AddPostActivity.this,"Book create successfully!",Toast.LENGTH_LONG).show();
            finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPostActivity.this,"Error!Please try again",Toast.LENGTH_LONG).show();
            }
        });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean checkFiledNonEmpty() {
        if((imgList.size() > 0 )&& (bookName.getText().toString().trim().length() > 0)&&(menuList.get(0).getSelection().length()>0)&&(menuList.get(1).getSelection().length()>0)&&(price.getText().toString().trim().length() > 0)&&(bookDesc.getText().toString().trim().length() > 0)){
        return true ;
        }
        return false;
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null){
            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                switch (currentPosition){
                    case 0:
                            Image0.setImageBitmap(bitmap) ;
                            break;
                    case 1:
                            Image1.setImageBitmap(bitmap);
                            break;
                    case 2:
                            Image2.setImageBitmap(bitmap);
                            break;
                    case 3:
                            Image3.setImageBitmap(bitmap);
                            break;
                }

                UploadImageFileToFirebaseStorage();


            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
        else if (requestCode == 100) {
            if(resultCode == RESULT_OK) {
                String updatePostion = data.getStringExtra("ListPosition");
                String updateValue = data.getStringExtra("userSelection");
                Log.d("ListPosition",updatePostion);
                Log.d("userSelection",updateValue);

                if(updatePostion.equals("0")){
                    menuList.get(0).setSelection(updateValue);
                    mRecyclerViewAdpater.notifyItemChanged(0);
                    isSetCategory =true ;
                }else if(updatePostion.equals("1")){
                    menuList.get(1).setSelection(updateValue);
                    mRecyclerViewAdpater.notifyItemChanged(1);
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

                            // Getting image name from EditText and store into string variable.
                            //String TempImageName = ImageName.getText().toString().trim();

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();
                            currentPosition++;
                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();


                            @SuppressWarnings("VisibleForTests")
                            AddPostImage imageUploadInfo = new AddPostImage(taskSnapshot.getDownloadUrl().toString());
                            imgList.add(imageUploadInfo);

                            // Getting image upload ID.
                            String ImageUploadId = databaseImagesReference.push().getKey();

                            // Adding image upload id s child element into databaseImagesReference.
                            databaseImagesReference.child(ImageUploadId).setValue(imageUploadInfo);
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();
                            switch (currentPosition){
                                case 0:
                                    Image0.setImageBitmap(null) ;
                                    break;
                                case 1:
                                    Image1.setImageBitmap(null);
                                    break;
                                case 2:
                                    Image2.setImageBitmap(null);
                                    break;
                                case 3:
                                    Image3.setImageBitmap(null);
                                    break;
                            }
                            // Showing exception error message.
                            Toast.makeText(AddPostActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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
        else {

            Toast.makeText(AddPostActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }


}

