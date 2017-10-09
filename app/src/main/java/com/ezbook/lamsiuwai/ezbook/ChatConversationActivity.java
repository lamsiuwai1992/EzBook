package com.ezbook.lamsiuwai.ezbook;
/**
 * Created by lamsiuwai on 28/9/2017.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.security.AccessController.getContext;


public class ChatConversationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference senderRef,receiverRef,senderLastMsg , receiverLastMsg;
    private FirebaseRecyclerAdapter<ShowChatCoversationObject, ChatConversationViewHolder> mFirebaseAdapter;
    public LinearLayoutManager mLinearLayoutManager;
    static String SenderName;
    private FirebaseAuth auth;
    private String userId;

    ImageView attachmentIcon,sendIcon,no_data_available_image,receiverIcon;
    EditText messageArea;
    TextView no_chat,receiverName;

    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    ProgressBar progressBar;
    public static final int READ_EXTERNAL_STORAGE = 0, MULTIPLE_PERMISSIONS = 10;
    Uri mImageUri = Uri.EMPTY;

    private String pictureImagePath = "";
    final CharSequence[] options = {"Camera", "Gallery"};
    String[] permissions= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_chat_conversation);

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        String receiverId =getIntent().getStringExtra("retrieveId");
        firebaseDatabase = FirebaseDatabase.getInstance();
        senderRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(userId).child(receiverId);
        senderRef.keepSynced(true);
        senderLastMsg = FirebaseDatabase.getInstance().getReference().child("ContactList").child(userId).child(receiverId).child("lastMessage");



        receiverRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(receiverId).child(userId);
        receiverLastMsg = FirebaseDatabase.getInstance().getReference().child("ContactList").child(receiverId).child(userId).child("lastMessage");
        receiverRef.keepSynced(true);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_chat_appBarLayout);
        //setSupportActionBar(toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.navigation);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        receiverIcon = (ImageView)findViewById(R.id.userIcon);
        receiverName = (TextView) findViewById(R.id.userName);
        receiverName.setText(getIntent().getStringExtra("name"));
        String receiverIconUrl  = getIntent().getStringExtra("imageId");
        Glide.with(getApplicationContext())
                .load(receiverIconUrl)
                .crossFade()
                .thumbnail(0.5f)
                .placeholder(R.mipmap.loading)
                .bitmapTransform(new CircleTransform(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(receiverIcon);
        SenderName = getIntent().getStringExtra("name");
        recyclerView = (RecyclerView)findViewById(R.id.fragmentChatRecyclerView);
        attachmentIcon = (ImageView)findViewById(R.id.attachButton);
        sendIcon = (ImageView)findViewById(R.id.sendButton);
        no_data_available_image = (ImageView)findViewById(R.id.no_data_available_image);
        messageArea = (EditText)findViewById(R.id.messageArea);
        mProgressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar)findViewById(R.id.progressBar3);
        no_chat = (TextView)findViewById(R.id.noChatText);
        mLinearLayoutManager = new LinearLayoutManager(ChatConversationActivity.this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mLinearLayoutManager.setStackFromEnd(true);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString().trim();

                if(!messageText.equals("")){
                    ArrayMap<String, String> map = new ArrayMap<>();
                    map.put("message", messageText);
                    map.put("sender", userId);
                    senderRef.push().setValue(map);
                    receiverRef.push().setValue(map);
                    senderLastMsg.setValue(messageText);
                    receiverLastMsg.setValue(messageText);
                    messageArea.setText("");
                    recyclerView.postDelayed(new Runnable() {
                        @Override public void run()
                        {
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);

                        }
                    }, 500);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Input Something!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        attachmentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatConversationActivity.this);
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
                            if (ContextCompat.checkSelfPermission(ChatConversationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat.requestPermissions(ChatConversationActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
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
    }
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
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

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ShowChatCoversationObject, ChatConversationViewHolder>(ShowChatCoversationObject.class, R.layout.show_chat_converation_item, ChatConversationViewHolder.class, senderRef) {


            public void populateViewHolder(final ChatConversationViewHolder viewHolder, ShowChatCoversationObject model, final int position) {

                viewHolder.getSender(model.getSender());
                viewHolder.getMessage(model.getMessage());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {

                        final DatabaseReference ref = mFirebaseAdapter.getRef(position);
                        ref.keepSynced(true);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String imageUrl = dataSnapshot.child("message").getValue(String.class);
                                if(imageUrl.startsWith("https"))
                                {
                                    //Toast.makeText(ChatConversationActivity.this, "URL : " + retrieve_image_url, Toast.LENGTH_SHORT).show();
                                    Intent intent = (new Intent(ChatConversationActivity.this,EnlargeImageView.class));
                                    intent.putExtra("url",imageUrl);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };
        Log.d("LOGGED", "Set Layout : " );
        recyclerView.setAdapter(mFirebaseAdapter);





        senderRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren())
                {
                    //Log.d("LOGGED", "Data SnapShot : " +dataSnapshot.toString());
                    progressBar.setVisibility(ProgressBar.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    no_data_available_image.setVisibility(View.GONE);
                    no_chat.setVisibility(View.GONE);
                    recyclerView.postDelayed(new Runnable() {
                        @Override public void run()
                        {
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                        }
                    }, 500);
                    recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v,
                                                   int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (bottom < oldBottom) {
                                recyclerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                    }
                                }, 100);
                            }
                        }
                    });
                }
                else {
                    //Log.d("LOGGED", "NO Data SnapShot : " +dataSnapshot.toString());
                    progressBar.setVisibility(ProgressBar.GONE);
                    recyclerView.setVisibility(View.GONE);
                    no_data_available_image.setVisibility(View.VISIBLE);
                    no_chat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                this.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //View Holder For Recycler View
    public static class ChatConversationViewHolder extends RecyclerView.ViewHolder {
        private FirebaseAuth auth;
        private final TextView message, sender;
        private String senderId ;
        private final ImageView chat_image_incoming,chat_image_outgoing;
        View mView;
        final LinearLayout.LayoutParams params,text_params;
        LinearLayout layout;

        public ChatConversationViewHolder(final View itemView) {
            super(itemView);
            //Log.d("LOGGED", "ON Chat_Conversation_ViewHolder : " );
            mView = itemView;
            message = mView.findViewById(R.id.fetch_chat_messgae);
            sender =  mView.findViewById(R.id.fetch_chat_sender);
            chat_image_incoming = mView.findViewById(R.id.chat_uploaded_image_incoming);
            chat_image_outgoing = mView.findViewById(R.id.chat_uploaded_image_outgoing);
            auth = FirebaseAuth.getInstance();
            senderId = auth.getCurrentUser().getUid();

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            text_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout = mView.findViewById(R.id.chat_linear_layout);
        }

        private void getSender(String title) {


            if(title.equals(senderId))
            {   //messgae send from user
                params.setMargins((MainActivity.DeviceWidth/3),5,10,10);
                text_params.setMargins(15,10,0,5);
                sender.setLayoutParams(text_params);
                mView.setLayoutParams(params);
                mView.setBackgroundResource(R.drawable.shape_outcoming_message);
                sender.setText("YOU");
                chat_image_outgoing.setVisibility(View.VISIBLE);
                chat_image_incoming.setVisibility(View.GONE);

            }
            else
            {   //message send from others
                params.setMargins(10,0,(MainActivity.DeviceWidth/3),10);
                sender.setGravity(Gravity.START);
                text_params.setMargins(60,10,0,5);
                sender.setLayoutParams(text_params);
                mView.setLayoutParams(params);
                mView.setBackgroundResource(R.drawable.shape_incoming_message);
                sender.setText(SenderName);
                chat_image_outgoing.setVisibility(View.GONE);
                chat_image_incoming.setVisibility(View.VISIBLE);
            }
        }

        private void getMessage(String title) {

            if(!title.startsWith("https"))
            {

                if(!sender.getText().equals(SenderName))
                {
                    text_params.setMargins(15,10,22,15);
                }
                else
                {
                    text_params.setMargins(65,10,22,15);
                }

                message.setLayoutParams(text_params);
                message.setText(title);
                message.setTextColor(Color.parseColor("#FFFFFF"));
                message.setVisibility(View.VISIBLE);
                chat_image_incoming.setVisibility(View.GONE);
                chat_image_outgoing.setVisibility(View.GONE);
            }
            else
            {
                if (chat_image_outgoing.getVisibility()==View.VISIBLE && chat_image_incoming.getVisibility()==View.GONE)
                {
                    chat_image_outgoing.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    Glide.with(itemView.getContext())
                            .load(title)
                            .crossFade()
                            .fitCenter()
                            .placeholder(R.mipmap.loading)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(chat_image_outgoing);
                }
                else
                {
                    chat_image_incoming.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    Glide.with(itemView.getContext())
                            .load(title)
                            .crossFade()
                            .fitCenter()
                            .placeholder(R.mipmap.loading)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(chat_image_incoming);
                }
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("LOGGED", "InSIDE onActivityResult : ");
        Log.d("LOGGED", " requestCode : " + requestCode+" resultCode : " + resultCode+" DATA "+data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ChatImages").child(mImageUri.getLastPathSegment());
            Log.d("LOGGED", "ImageURI : " +mImageUri);


            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();

                    ArrayMap<String, String> map = new ArrayMap<>();
                    map.put("message", downloadUri.toString());
                    map.put("sender", userId);
                    senderRef.push().setValue(map);
                    receiverRef.push().setValue(map);
                    senderLastMsg.setValue("https");
                    receiverLastMsg.setValue("https");
                    mProgressDialog.dismiss();
                }
            });
        }

        else if (requestCode == 5 && resultCode == RESULT_OK ) {

            File imgFile = new  File(pictureImagePath);
            if(imgFile.exists()) {
                Log.d("LOGGED", "imgFile : " + imgFile);

                Uri fileUri =Uri.fromFile(imgFile);
                Log.d("LOGGED", "fileUri : " + fileUri);

                StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ChatImages").child(fileUri.getLastPathSegment());

                mProgressDialog.setMessage("Uploading...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

                filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                        ArrayMap<String, String> map = new ArrayMap<>();
                        map.put("message", downloadUri.toString());
                        map.put("sender", userId);
                        senderRef.push().setValue(map);
                        receiverRef.push().setValue(map);
                        senderLastMsg.setValue("https");
                        receiverLastMsg.setValue("https");
                        mProgressDialog.dismiss();
                    }
                });
            }
        }

        else if (requestCode == 5)
        {
            Toast.makeText(this, "resultCode : "+ resultCode, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Glide.clear(imageView);
        Glide.get(getApplicationContext()).clearMemory();
    }

    private void callCamera() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        Log.d("LOGGED", "imageFileName :  "+ imageFileName);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        Log.d("LOGGED", "PicturePath :  "+ pictureImagePath);

        File file = new File(pictureImagePath);

        Uri outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getApplicationContext().getPackageName() + ".provider", file);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        cameraIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        Log.d("LOGGED", "pictureImagePath :  "+ pictureImagePath);
        Log.d("LOGGED", "outputFileUri :  "+ outputFileUri);

        startActivityForResult(cameraIntent, 5);
    }


    private void callgalary() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
