package com.stathis.constantinos.felippex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class DeliveryViewActivity extends AppCompatActivity {

    private final String APP_TAG = "FelippEx";
    private String transactionID;
    private String viewTypeRequested;

    private String photoUrl;
    private Bitmap packagePhotoBitmap;

    private ScrollView mScrollView;
    private ProgressBar acProgressBar;

    private TextView titleIdLabelTextView;

    private TextView receiverFullNameTextView;
    private TextView receiverPhoneNumberTextView;
    private TextView receiverAddressTextView;

    private TextView senderFullNameTextView;
    private TextView senderPhoneNumberTextView;
    private TextView senderAddressTextView;

    private ImageView packagePictureImageView;
    private MapView receiverAddressMapView;

    private Button markAsDeliveredButton;
    private Button editDeliveryButton;
    private Button deleteDeliveryButton;

    private Intent intentReceived;

    // Firebase related functions
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_view);

        init();
        queryDeliveryAndApplyToView();

    }

    private void init() {
        Log.d(APP_TAG, "Delivery View Activity started");

        mScrollView = (ScrollView) findViewById(R.id.ac_main_scroll_view);
        mScrollView.setVisibility(View.INVISIBLE);
        acProgressBar = (ProgressBar) findViewById(R.id.ac_progress_bar);

        titleIdLabelTextView = (TextView) findViewById(R.id.today_received_title_textView);

        receiverFullNameTextView = (TextView) findViewById(R.id.receiver_full_name_textview);
        receiverPhoneNumberTextView = (TextView) findViewById(R.id.receiver_phone_number_textview);
        receiverAddressTextView = (TextView) findViewById(R.id.receiver_address_textview);

        senderFullNameTextView = (TextView) findViewById(R.id.sender_full_name_textview);
        senderPhoneNumberTextView = (TextView) findViewById(R.id.sender_phone_number_textview);
        senderAddressTextView = (TextView) findViewById(R.id.sender_address_textview);

        packagePictureImageView = (ImageView) findViewById(R.id.package_view_imageview);
        receiverAddressMapView = (MapView) findViewById(R.id.receiver_address_mapview);

        markAsDeliveredButton = (Button) findViewById(R.id.mark_as_delivered_button);
        editDeliveryButton = (Button) findViewById(R.id.edit_delivery_button);
        deleteDeliveryButton = (Button) findViewById(R.id.delete_delivery_button);

        intentReceived = getIntent();
        transactionID = (String) intentReceived.getStringExtra("transactionID");
        viewTypeRequested = (String) intentReceived.getStringExtra("requestedView");

        if (viewTypeRequested.equals("viewPackage")) {
            markAsDeliveredButton.setEnabled(false);
            markAsDeliveredButton.setBackgroundColor(Color.parseColor("#dbdbdb"));
            // TODO: set editButton
            deleteDeliveryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });

        }

        if (viewTypeRequested.equals("viewDelivery")) {
            editDeliveryButton.setEnabled(false);
            editDeliveryButton.setBackgroundColor(Color.parseColor("#dbdbdb"));
            deleteDeliveryButton.setEnabled(false);
            deleteDeliveryButton.setBackgroundColor(Color.parseColor("#dbdbdb"));
            // TODO: markAsDeliveredButton
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("deliveries");
        mStorage = FirebaseStorage.getInstance();

    }

    private void queryDeliveryAndApplyToView() {
        Query deliveryQuery = mDatabase.orderByKey().equalTo(transactionID);
        deliveryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(APP_TAG, "Query was successful.");
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    titleIdLabelTextView.setText(ds.getKey());

                    Log.d(APP_TAG, "Sender :" + ds.child("sender/fullName").getValue().toString());

                    senderFullNameTextView.setText(ds.child("sender/fullName").getValue().toString());
                    senderAddressTextView.setText(ds.child("sender/address").getValue().toString());
                    senderPhoneNumberTextView.setText(ds.child("sender/phoneNumber").getValue().toString());

                    receiverFullNameTextView.setText(ds.child("receiver/fullName").getValue().toString());
                    receiverAddressTextView.setText(ds.child("receiver/address").getValue().toString());
                    receiverPhoneNumberTextView.setText(ds.child("receiver/phoneNumber").getValue().toString());
                    photoUrl = ds.child("imageRefUri").getValue().toString();
                    Log.e(APP_TAG, "PhotoUrl :" + photoUrl);
                    getPackagePhotoAndSetToImageView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(APP_TAG, databaseError.getMessage());
            }
        });
    }

    private void getPackagePhotoAndSetToImageView() {
        Log.d(APP_TAG, "getPackagePhoto() fired!");
        StorageReference ref = mStorage.getReferenceFromUrl(photoUrl);
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(APP_TAG, "Adding photo at imageView");
                    packagePhotoBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    packagePictureImageView.setImageBitmap(packagePhotoBitmap);
                    enableUI();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(APP_TAG, e.getMessage());
                    enableUI();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void enableUI() {
        acProgressBar.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.VISIBLE);
    }

}
