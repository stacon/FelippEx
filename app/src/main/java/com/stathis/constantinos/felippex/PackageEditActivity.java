package com.stathis.constantinos.felippex;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    // Package associated variables
    private FPackage deliveryPackage;

    // Firebase related functions
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    // Transporter associated Firebase user
    private FirebaseUser transporter;

    // Error status and boolean for record procedure
    Boolean error = false;
    String errorStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_edit);

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

        // Firebase related assignments
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = storage.getReferenceFromUrl("gs://felippex-f5ace.appspot.com/");

        // Transporter user assignment
        transporter = FirebaseAuth.getInstance().getCurrentUser();

        Log.d(APP_TAG, "PackageEdit activity started");

    }

    public void recordDelivery(View V) {
        Log.d(APP_TAG, "Attempting to assign transactors");
        assignTransactors();
        if (error) {Log.e(APP_TAG, errorStatus); error = false; return;}
        Log.d(APP_TAG, "Transactors assignment succeeded");
        checkIfImageHasBeenSelected();
        if (error) {Log.e(APP_TAG, errorStatus); error = false; return;}
        Log.d(APP_TAG, "Attempting to assign delivery information to FPackage Object");
        attemptToStoreDelivery();
        leaveActivity();
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

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

    private void assignTransactors() {
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

    private void checkIfImageHasBeenSelected() {
        if (packageImageView.getDrawable() == null) {
            error = true;
            errorStatus = "Please set an image package";
            Toast.makeText(this, "Please take a photo of the package", Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptToStoreDelivery() {
        createHashedImageName();
        StorageReference imagesRef = mStorageRef.child("packageImages");
        StorageReference spaceRef = mStorageRef.child("packageImages/" + imageNameHashed);

        packageImageView.setDrawingCacheEnabled(true);
        packageImageView.buildDrawingCache();
        Bitmap bitmap = packageImageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = spaceRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                CodeHelper.showErrorDialog(PackageEditActivity.this, "Image upload failed");
                error = true;
                errorStatus = "Image Upload Failed. Please try again later";
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
            }
        });


    }

    private void createHashedImageName() {
        String lTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageNameHashed =
                Math.abs((sender.getFullName().hashCode() * 37) +
                        (receiver.getFullName().hashCode() * 18) +
                        (lTimestamp.hashCode() * 15)) + ".jpg";
        Log.d(APP_TAG, "Hashed image name created: " + imageNameHashed);
    }

    private Bitmap prepareImageForPreview(Bitmap image) {
        Log.d(APP_TAG, "Preparing image for preview");
        Matrix matrix = new Matrix();
        matrix.preRotate(90);
        return Bitmap.createBitmap(image, 0,0,image.getWidth(),image.getHeight(), matrix, true);
    }

    private void leaveActivity() {
        Toast.makeText(this, "Delivery has been recorded successfully", Toast.LENGTH_SHORT).show();
        Log.d(APP_TAG, "Leaving PackageEditActivity");
        finish();
    }
}
