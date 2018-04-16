package com.stathis.constantinos.felippex;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.TodayPackagesAdapter;
import Models.FPackage;
import Models.Transactor;

public class TodayReceivedActivity extends AppCompatActivity {

    private final String APP_TAG = "FelippEx";

    private DatabaseReference mDatabaseReference;
    private String transporterUID;
    private String exampleCall;
    private String querySynthKey;

    private List<FPackage> mFPackageList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TodayPackagesAdapter mTodayPackagesAdapter;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_received);
        mProgressBar = (ProgressBar) findViewById(R.id.mtProgressBar);

        mRecyclerView = (RecyclerView) findViewById(R.id.today_packages_recycler_view);
        mTodayPackagesAdapter = new TodayPackagesAdapter(mFPackageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mTodayPackagesAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        transporterUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        populateList();
    }

    private void populateList() {

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
                    for (DataSnapshot fpackageSnapshot : dataSnapshot.getChildren()) {
                        FPackage fPackage = parseDataSnapshotToPackage(fpackageSnapshot);
                        mFPackageList.add(fPackage);
                    }

                    mTodayPackagesAdapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.INVISIBLE);
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

    @NonNull
    private FPackage parseDataSnapshotToPackage(DataSnapshot fpackageSnapshot) {
        FPackage fPackage = new FPackage();
        Transactor sender = new Transactor();
        Transactor receiver = new Transactor();
        try {
            sender.setFullName(fpackageSnapshot.child("sender/fullName").getValue().toString());
            receiver.setFullName(fpackageSnapshot.child("receiver/fullName").getValue().toString());
        } catch (NullPointerException e) {
            Log.e(APP_TAG, "Something went wrong on getting names from snapshot");
        }
        fPackage.settSender(sender);
        fPackage.settReceiver(receiver);
        fPackage.setTransactionId(fpackageSnapshot.getKey());

        return fPackage;
    }
}
