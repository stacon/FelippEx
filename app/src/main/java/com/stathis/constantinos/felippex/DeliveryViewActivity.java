package com.stathis.constantinos.felippex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import Models.FPackage;

public class DeliveryViewActivity extends AppCompatActivity {

    private final String APP_TAG = "FelippEx";
    private String transactionID;
    private String viewTypeRequested;
    private FPackage deliveryPackage;

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

        // Get intent information
        intentReceived = getIntent();
        transactionID = (String) intentReceived.getStringExtra("transactionID");
        viewTypeRequested = (String) intentReceived.getStringExtra("requestedView");

        assignViewVars();

        // Initialize Firebase DB and storage
        mDatabase = FirebaseDatabase.getInstance().getReference().child("deliveries");
        mStorage = FirebaseStorage.getInstance();

        if (viewTypeRequested.equals("viewPackage")) {
            disableMarkAsDeliveredButton();
            initEditButton();
            initDeleteButton();
        }

        if (viewTypeRequested.equals("viewDelivery")) {
            disableEditButton();
            disableDeleteButton();
            initMarkAsDeliveredButton();
        }
    }

    // 1. ViewTypeRequested = ViewPackage related function ===================== //
    private void initDeleteButton() {
        deleteDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeliveryViewActivity.this ,"Deleting delivery record...", Toast.LENGTH_SHORT).show();
                disableUI();
                attemptToDeleteRecordFromFirebase();
            }
        });
    }

    private void initEditButton() {
        editDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryViewActivity.this, PackageEditActivity.class);
                intent.putExtra("editMode", true);
                intent.putExtra("transactionId", transactionID);
                startActivity(intent);
            }
        });
    }

    private void disableMarkAsDeliveredButton() {
        markAsDeliveredButton.setEnabled(false);
        markAsDeliveredButton.setBackgroundColor(Color.parseColor("#dbdbdb"));
    }

    private void attemptToDeleteRecordFromFirebase() {
        mDatabase.child(transactionID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(DeliveryViewActivity.this, "Deletion was successful", Toast.LENGTH_LONG).show();
                attemptToDeletePhotoFromFirebase();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DeliveryViewActivity.this, "Deletion FAILED", Toast.LENGTH_LONG).show();
                enableUI();
            }
        });
    }

    private void attemptToDeletePhotoFromFirebase() {
        mStorage.getReferenceFromUrl(photoUrl).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(APP_TAG, "Photo Deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(APP_TAG, "Photo Deletion FAILED");
            }
        });
    }

    // 2. ViewTypeRequested = ViewDelivery related function ==================== //

    private void disableDeleteButton() {
        deleteDeliveryButton.setEnabled(false);
        deleteDeliveryButton.setBackgroundColor(Color.parseColor("#dbdbdb"));
    }

    private void disableEditButton() {
        editDeliveryButton.setEnabled(false);
        editDeliveryButton.setBackgroundColor(Color.parseColor("#dbdbdb"));
    }

    private void initMarkAsDeliveredButton(){
        markAsDeliveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptToMarkDeliveryAsDelivered();
            }
        });
    }

    private void attemptToMarkDeliveryAsDelivered() {
        acProgressBar.setVisibility(View.VISIBLE);
        disableUI();
        deliveryPackage.setDelivered(true);
        deliveryPackage.setAsDeliveredSyntheticKey();

        mDatabase.child(transactionID).setValue(deliveryPackage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(DeliveryViewActivity.this, "Package marked as DELIVERED", Toast.LENGTH_SHORT).show();
                leaveActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DeliveryViewActivity.this, "FAILURE: Package failed to be marked as delivered", Toast.LENGTH_SHORT).show();
                acProgressBar.setVisibility(View.INVISIBLE);
                enableUI();
            }
        });
    }

    // 3. Shared functions between modes ====================================== //

    // Links view with Java code
    private void assignViewVars() {
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

        markAsDeliveredButton = (Button) findViewById(R.id.mark_as_delivered_button);
        editDeliveryButton = (Button) findViewById(R.id.edit_delivery_button);
        deleteDeliveryButton = (Button) findViewById(R.id.delete_delivery_button);
    }

    // Query DB for given transactionId through intent
    private void queryDeliveryAndApplyToView() {
        Query deliveryQuery = mDatabase.orderByKey().equalTo(transactionID);
        deliveryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(APP_TAG, "Query was successful for id: " + transactionID);
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    titleIdLabelTextView.setText(ds.getKey());

                    deliveryPackage = ds.getValue(FPackage.class);

                    fillViewElements(deliveryPackage);
                    photoUrl = ds.child("imageRefUri").getValue().toString();
                    getPackagePhotoAndSetToImageView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(APP_TAG, databaseError.getMessage());
            }
        });
    }

    // Fill elements in view
    private void fillViewElements(final FPackage packR) {
        senderFullNameTextView.setText(packR.getSender().getFullName());
        senderPhoneNumberTextView.setText(packR.getSender().getPhoneNumber());
        senderAddressTextView.setText(packR.getSender().getAddress());

        receiverFullNameTextView.setText(packR.getReceiver().getFullName());
        receiverPhoneNumberTextView.setText(packR.getReceiver().getPhoneNumber());
        receiverAddressTextView.setText(packR.getReceiver().getAddress());

        receiverAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.co.in/maps?q=\"" + packR.getReceiver().getAddress() + "\"";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        senderAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.co.in/maps?q=\"" + packR.getSender().getAddress() + "\"";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    // Query image from Firebase storage and apply to imageView
    private void getPackagePhotoAndSetToImageView() {
        StorageReference ref = mStorage.getReferenceFromUrl(photoUrl);
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
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

    private void disableUI() {
        mScrollView.setVisibility(View.INVISIBLE);
        acProgressBar.setVisibility(View.VISIBLE);
    }

    private void enableUI() {
        acProgressBar.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.VISIBLE);
    }

    private void leaveActivity() {
        Log.d(APP_TAG, "Leaving Delivery view activity");
        finish();
    }

}
