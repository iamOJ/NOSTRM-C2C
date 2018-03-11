package com.choprarohan.litrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Rohan Chopra on 1/7/18.
 */

public class TransactionActivity extends AppCompatActivity {

    TransactionAdapter adapter;
    ArrayList<Transaction> items = new ArrayList<>();
    BottomNavigationView navigation;
    RecyclerView recyclerView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    intent.setClass(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_txn:

                    return true;
                case R.id.navigation_profile:
                    intent.setClass(getBaseContext(), ProfileActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_ledger:
                    intent.setClass(getBaseContext(), LedgerActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_txn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigation.setSelectedItemId(R.id.navigation_txn);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




        recyclerView = (RecyclerView) findViewById(R.id.txnList);



        DatabaseReference dbTxn = FirebaseDatabase.getInstance().getReference("transactions");


        Query query = dbTxn.orderByChild("date");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();

                for(DataSnapshot txnSnapshot: dataSnapshot.getChildren()){
                    Transaction txn = txnSnapshot.getValue(Transaction.class);



                    items.add(txn);
                }


                // set up the RecyclerView
                recyclerView.setLayoutManager(new LinearLayoutManager(TransactionActivity.this));
                adapter = new TransactionAdapter(items);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
