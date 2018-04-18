package com.stathis.constantinos.felippex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final String APP_TAG="FelippEx";
    private Button packageRetrievalButton;
    private Button packageDeliveranceButton;
    private Button packagesListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageRetrievalButton = (Button) findViewById(R.id.receivePackageButton);
        packageDeliveranceButton = (Button) findViewById(R.id.deliverPackageButton);
        packagesListButton = (Button) findViewById(R.id.viewPackagesButton);


        packageRetrievalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(APP_TAG, "Package retrieval option clicked");
                Intent intent = new Intent(MainActivity.this, PackageEditActivity.class);
                startActivity(intent);
            }
        });

        packageDeliveranceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PackageListActivity.class);
                intent.putExtra("viewMode", "deliveries");
                Log.d(APP_TAG, "Requested package list with MODE: deliveries");
                startActivity(intent);
            }
        });

        packagesListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PackageListActivity.class);
                intent.putExtra("viewMode", "receipts");
                Log.d(APP_TAG, "Requested package list with MODE: receipts");
                startActivity(intent);
            }
        });
    }



}
