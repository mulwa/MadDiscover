package com.example.cj_sever.merchandisers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj_sever.merchandisers.Adapter.eventRecylerAdapter;
import com.example.cj_sever.merchandisers.Models.Event;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Event_List extends AppCompatActivity {
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private static  final String EVENTS = "Events";
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private List<Event> listEvents;
    private eventRecylerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event__list);

        database = FirebaseDatabase.getInstance();
        mDatabaseReference =  database.getReference(EVENTS).getRef();

        findview();
        Toast.makeText(getApplicationContext(),"on create called",Toast.LENGTH_SHORT).show();




    }
    private void findview(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.event_list));
        }
        recyclerView = (RecyclerView) findViewById(R.id.eventRecyler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        listEvents = new ArrayList<>();
        adapter = new eventRecylerAdapter(listEvents,getApplicationContext(),mDatabaseReference);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getAllData();



        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }
    private void getAllData(){
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> newEvent = (Map<String, Object>) dataSnapshot.getValue();
                if(newEvent != null){
//                    listEvents.add(new Event(newEvent.get(Constants.firebase_reference_Event_image).toString(),
//                            newEvent.get(Constants.firebase_reference_Event_title).toString(),
//                            newEvent.get(Constants.firebase_reference_Event_description).toString(),
//                            newEvent.get(Constants.firebase_reference_Event_venue).toString(),
//                            newEvent.get(Constants.firebase_reference_Event_dateFrom).toString(),
//                            newEvent.get(Constants.firebase_reference_Event_dateTo).toString(),
//                            newEvent.get(Constants.firebase_reference_Event_startTime).toString(),
//                            newEvent.get(Constants.firebase_reference_Event_endTime).toString(),
//                            newEvent.get(Constants.firebase_reference_Event_organiser).toString(),
//                            newEvent.get(Constants.firebase_reference_Event_owner).toString()
//                            ));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

                    adapter = new eventRecylerAdapter(listEvents,getApplicationContext(),mDatabaseReference);
                    adapter.notifyDataSetChanged();


                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(),"on start called",Toast.LENGTH_SHORT).show();
        if(!isNetworkAvailable()){
            progressBar.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar.make(coordinatorLayout,getResources().getString(R.string.non_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.accent));
            snackbar.show();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),"on Resume called",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(),"on stop called",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"on destroy called",Toast.LENGTH_SHORT).show();

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
