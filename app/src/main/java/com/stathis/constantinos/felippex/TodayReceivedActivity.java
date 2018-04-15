package com.stathis.constantinos.felippex;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TodayReceivedActivity extends AppCompatActivity {

    private final String APP_TAG = "FelippEx";

    private DatabaseReference mDatabaseReference;
    private String transporterUID;
    private String exampleCall;
    private String querySynthKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_received);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        transporterUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        querySynthKey = transporterUID + "-" + CodeHelper.getDateNowToString();


        Query query = mDatabaseReference.child("deliveries").
                orderByChild("syntheticReceiptValue").
                equalTo(querySynthKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(APP_TAG, "Query was successful");
                if (dataSnapshot.exists()) {
                    Log.d(APP_TAG, "Previewing data...");
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Log.d(APP_TAG, issue.toString());
                    }
                } else {
                    Log.d(APP_TAG, "No data found with the synthetic key of " + querySynthKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(APP_TAG, databaseError.getMessage());
            }
        });
    }
}
