package com.stathis.constantinos.felippex;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Models.FPackage;
import Models.Transactor;

public class PackageEditActivity extends AppCompatActivity {

    // Final variables
    private final String APP_TAG="FelippEx";
    static final int REQUEST_TAKE_PHOTO = 1;

    // Form related variables
    private TextView mTitleTextView;

    private ScrollView mScrollView;
    private EditText mSendersName;
    private EditText mSendersPhone;
    private EditText mSendersAddress;
    private Transactor sender;

    private EditText mReceiversName;
    private EditText mReceiversPhone;
    private EditText mReceiversAddress;
    private Transactor receiver;

    // Photo related variables
    private ImageView packageImageView;
    private String mCurrentPhotoPath;
    private String imageNameHashed;
    private Bitmap imageOutput;
    private Uri downloadUrl;

    // Delivery Button
    private Button mPackageReceivedButton;

    // Package associated variables
    private FPackage deliveryPackage;

    // Firebase related functions
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    // Transporter associated Firebase user
    private FirebaseUser transporter;

    // EditMode switch in Boolean
    private Boolean editMode = false;
    private String transcationIdForEdit;
    private String photoUrlForEdit;
    private FPackage packageForEdit;

    // Error status and boolean for record procedure
    private Boolean error = false;
    private String errorStatus;

    //UI Variable
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_edit);
        init();
    }

    private void init() {
        mTitleTextView = (TextView) findViewById(R.id.retrieval_form_textView);
        mScrollView = (ScrollView) findViewById(R.id.main_scrollView);
        // Senders information var assignment
        mSendersName = (EditText) findViewById(R.id.sender_fullName_input);
        mSendersPhone = (EditText) findViewById(R.id.sender_phone_input);
        mSendersAddress = (EditText) findViewById(R.id.sender_address_input);

        // Receivers information var assignment
        mReceiversName = (EditText) findViewById(R.id.receiver_fullName_input);
        mReceiversPhone = (EditText) findViewById(R.id.receiver_phone_input);
        mReceiversAddress = (EditText) findViewById(R.id.receiver_address_input);

        // Image information var assignment
        packageImageView = (ImageView) findViewById(R.id.package_image_placeholder);

        // Package received button
        mPackageReceivedButton = findViewById(R.id.package_received_button);

        // Firebase related assignments
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("deliveries");
        mStorageRef = mStorage.getReferenceFromUrl("gs://felippex-f5ace.appspot.com/");

        // Transporter user assignment
        transporter = FirebaseAuth.getInstance().getCurrentUser();

        // Progress bar hook and dimming
        mProgressBar = (ProgressBar) findViewById(R.id.deliveryProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        if (getIntent().hasExtra("editMode")) {
            editMode = getIntent().getExtras().getBoolean("editMode");
            Log.d(APP_TAG, "The package view activity is now on EDIT MODE");
            transcationIdForEdit = getIntent().getStringExtra("transactionId");
        }

        if (editMode) {
            queryTransactionAndFillFields();
            mTitleTextView.setText(transcationIdForEdit);
            mPackageReceivedButton.setText(R.string.save_changes_button);
            mPackageReceivedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveChangesOnDelivery();
                }
            });
        } else {
            mPackageReceivedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordDelivery();
                }
            });
        }

        Log.d(APP_TAG, "PackageEdit activity started");
    }

    // 1. New Package Functions

    // Fired upon clicking receive package
    public void recordDelivery() {
        Log.d(APP_TAG, "Attempting to assign transactors");
        assignTransactorsAttemp();
        if (error) {Log.e(APP_TAG, errorStatus); error = false; return;}
        Log.d(APP_TAG, "Transactors assignment succeeded");
        checkIfImageHasBeenSetOnImageView();
        if (error) {Log.e(APP_TAG, errorStatus); error = false; return;}
        Log.d(APP_TAG, "Attempting to assign delivery information to FPackage Object");
        disableUI();
        mProgressBar.setVisibility(View.VISIBLE);
        attemptToStoreDelivery();
        if (error) {Log.e(APP_TAG, errorStatus); error = false; return;}
    }

    // Does the whole procedure and communication with Firebase
    private void attemptToStoreDelivery() {
        Toast.makeText(this, "Please wait while saving the delivery information...", Toast.LENGTH_LONG).show();
        createHashedImageName();
        StorageReference spaceRef = mStorageRef.child("packageImages/" + imageNameHashed);
        packageImageView.setDrawingCacheEnabled(true);
        packageImageView.buildDrawingCache();
        Bitmap bitmap = packageImageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = spaceRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                error = true;
                errorStatus = "Image Upload Failed. Please try again later";
                mProgressBar.setVisibility(View.INVISIBLE);
                enableUI();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getDownloadUrl();
                deliveryPackage = new FPackage(sender,receiver,transporter.getUid(), downloadUrl.toString());
                if (error) {Log.e(APP_TAG, errorStatus);error = false; return;}
                Log.d(APP_TAG, "Image upload and Uri retrieval succeeded");
                Log.d(APP_TAG, "Attempting to store delivery info to database");
                mDatabase.child("deliveries").push().setValue(deliveryPackage);
                mProgressBar.setVisibility(View.INVISIBLE);
                enableUI();
                leaveActivity();
            }
        });

    }

    // Fires once Photo package is clicked
    public void onRequestPhoto(View v) {
        Log.d(APP_TAG, "Photo the package was clicked");
        dispatchTakePictureIntent();
    }

    // Following Snippets Source: https://developer.android.com/training/camera/photobasics.html
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d(APP_TAG, "Attempt to create file slot for photo taking succeeded");
            } catch (IOException ex) {
                Log.e(APP_TAG,"Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d(APP_TAG, "Requesting camera for photo taking");
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Creates a temp image file for the imageView
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Handling usage of camera resource result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    // Sets Picture at ImageView
    private void setPic() {
        Log.d(APP_TAG, "Attempting to set picture at ImageView placeholder");

        // Get the dimensions of the View
        int targetW = packageImageView.getWidth();
        int targetH = packageImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        imageOutput = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        packageImageView.setImageBitmap(prepareImageForPreview(imageOutput));
        Log.d(APP_TAG,"Image set on ImageView");
    }

    // Attempting to assign sender and receiver as Transactor Objects. Sets error if fail
    private void assignTransactorsAttemp() {
        Log.d(APP_TAG, "ssignTransactors() fired");
        String senderNameInput = mSendersName.getText().toString();
        String senderPhoneInput = mSendersPhone.getText().toString();
        String senderAddressInput = mSendersAddress.getText().toString();
        String receiverNameInput = mReceiversName.getText().toString();
        String receiverPhoneInput = mReceiversPhone.getText().toString();
        String receiverAddressInput = mReceiversAddress.getText().toString();

        Log.d(APP_TAG, "Checking inputs from view");
        if (senderNameInput.isEmpty() ||
                senderPhoneInput.isEmpty() ||
                senderAddressInput.isEmpty() ||
                receiverNameInput.isEmpty() ||
                receiverPhoneInput.isEmpty() ||
                receiverAddressInput.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields regarding sender and receiver valid information", Toast.LENGTH_SHORT).show();
            error = true;
            errorStatus = "Transactor Inputs appear to be invalid";
            return;
        }

        Log.d(APP_TAG, "Setting sender transactor");
        sender = new Transactor(
                senderNameInput,
                senderPhoneInput,
                senderAddressInput
        );
        Log.d(APP_TAG, "Sender transactor SET");

        Log.d(APP_TAG, "Setting receiver transactor");
        receiver = new Transactor(
                receiverNameInput,
                receiverPhoneInput,
                receiverAddressInput
        );
        Log.d(APP_TAG, "Receiver transactor SET");
    }

    // Checks if image is set on imageview or it saves an error
    private void checkIfImageHasBeenSetOnImageView() {
        if (packageImageView.getDrawable() == null) {
            error = true;
            errorStatus = "Please set an image package";
            Toast.makeText(this, "Please take a photo of the package", Toast.LENGTH_SHORT).show();
        }
    }

    // Creates a unique hashed name for storing an image
    private void createHashedImageName() {
        String lTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageNameHashed =
                Math.abs((sender.getFullName().hashCode() * 37) +
                        (receiver.getFullName().hashCode() * 18) +
                        (lTimestamp.hashCode() * 15)) + ".jpg";
        Log.d(APP_TAG, "Hashed image name created: " + imageNameHashed);
    }

    // 2. Editing Functions
    private void queryTransactionAndFillFields() {
        Query deliveryQuery = mDatabase.orderByKey().equalTo(this.transcationIdForEdit);
        deliveryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(APP_TAG, "Query for EDITING was successful for id: " + transcationIdForEdit);
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    packageForEdit = ds.getValue(FPackage.class);
                    setEditFields(packageForEdit);

                        photoUrlForEdit = packageForEdit.getImageRefUri();
                        getPackagePhotoAndSetToImageView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(APP_TAG, databaseError.getMessage());
            }
        });
    }

    private void setEditFields(FPackage packageSnapshot) {
        mSendersName.setText(packageSnapshot.getSender().getFullName());
        mSendersPhone.setText(packageSnapshot.getSender().getPhoneNumber());
        mSendersAddress.setText(packageSnapshot.getSender().getAddress());
        mReceiversName.setText(packageSnapshot.getReceiver().getFullName());
        mReceiversPhone.setText(packageSnapshot.getReceiver().getPhoneNumber());
        mReceiversAddress.setText(packageSnapshot.getReceiver().getAddress());
    }

    private void getPackagePhotoAndSetToImageView() {
        Log.d(APP_TAG, "getPackagePhoto() fired!");
        StorageReference ref = mStorage.getReferenceFromUrl(photoUrlForEdit);
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(APP_TAG, "Adding photo at imageView");
                    Bitmap packagePhotoBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    packagePhotoBitmap = prepareImageForPreview(packagePhotoBitmap);
                    packageImageView.setImageBitmap(packagePhotoBitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(APP_TAG, e.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveChangesOnDelivery() {
        // TODO: do stuff
    }
    // 3. Common Functions

    // Finishes current activity along with some others tasks
    private void leaveActivity() {
        Toast.makeText(this, "Delivery has been recorded successfully", Toast.LENGTH_SHORT).show();
        Log.d(APP_TAG, "Leaving PackageEditActivity");
        finish();
    }

    // Preparing and rotating Image for the ImageView and mStorage
    private Bitmap prepareImageForPreview(Bitmap image) {
        Log.d(APP_TAG, "Preparing image for preview");
        Matrix matrix = new Matrix();
        matrix.preRotate(90);
        return Bitmap.createBitmap(image, 0,0,image.getWidth(),image.getHeight(), matrix, true);
    }

    // Disabling UI view elements (e.g. for loading)
    private void disableUI(){
        mScrollView.setVisibility(View.INVISIBLE);
        mPackageReceivedButton.setEnabled(false);
    }

    // Enabling UI view elements
    private void enableUI(){
        mScrollView.setVisibility(View.VISIBLE);
        mPackageReceivedButton.setEnabled(true);
    }
}
