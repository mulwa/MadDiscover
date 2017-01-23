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

import com.example.cj_sever.merchandisers.Models.Event;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FilteredEvents extends AppCompatActivity {
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private static  final String EVENTS = "Events";
    private FirebaseDatabase database;
    private Query mDatabaseReference;
    private String categoryFilter;
    private TextView mtitle;
    private TextView m_null_results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event__list);
        findview();
        categoryFilter = getIntent().getStringExtra(Constants.filter_category);
        if(getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.event_list));
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(categoryFilter);
        stringBuilder.append(" ");
        stringBuilder.append(getString(R.string.event_list));
        mtitle.setText(stringBuilder);
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference(EVENTS).getRef().orderByChild(Constants.EVENT_CATEGORY).equalTo(categoryFilter);
        fetchEvents();
    }
    private void findview(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.eventRecyler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mtitle = (TextView) findViewById(R.id.filter_title);
        m_null_results = (TextView) findViewById(R.id.tv_null_display);
        m_null_results.setVisibility(View.GONE);

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
        if(!isNetworkAvailable()){
            Snackbar snackbar = Snackbar.make(coordinatorLayout,"No internet connection ", Snackbar.LENGTH_INDEFINITE)
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

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void fetchEvents(){
        FirebaseRecyclerAdapter<Event,viewHolder> firebaseadapter = new FirebaseRecyclerAdapter<Event, viewHolder>(
                Event.class,
                R.layout.event_list_custom,
                viewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final viewHolder viewHolder, final Event event, int i) {
                progressBar.setVisibility(View.GONE);


                final String image = event.getImage();

                viewHolder.mTitle.setText(event.getTitle());
                viewHolder.mDescrition.setText(event.getDescription());
                viewHolder.mDate.setText(event.getDateFrom());
                viewHolder.mVenue.setText("Venue: " +event.getVenue());
                viewHolder.mTime.setText(event.getStartTime());


                Picasso.with(getApplicationContext())
                        .load(event.getImage())
                        .resize(100,0)
                        .error(R.drawable.image_placeholder)
                        .into(viewHolder.mImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                if(viewHolder.progressBar != null){
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError() {
                                viewHolder.progressBar.setVisibility(View.GONE);

                            }
                        });

                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),EventPop.class);
                        intent.putExtra(Constants.EVENTNAME,event.getTitle());
                        intent.putExtra(Constants.EVENTIMAGE,image);
                        intent.putExtra(Constants.EVENTDESCRIPTION,event.getDescription());
                        intent.putExtra(Constants.EVENT_DAY_FROM,event.getDateFrom());
                        intent.putExtra(Constants.EVENT_DAY_TO,event.getDateTo());
                        intent.putExtra(Constants.EVENT_START_TIME, event.getStartTime());
                        intent.putExtra(Constants.EVENT_END_TIME,event.getEndTime());
                        intent.putExtra(Constants.EVENTVENUE,event.getVenue());
                        intent.putExtra(Constants.EVENTORGANIZER,event.getOrganiser());
                        intent.putExtra(Constants.EVENTOWNER,event.getOwner());
                        intent.putExtra(Constants.OWNER_MOBILE,event.getOwnerMobile());
                        intent.putExtra(Constants.EVENT_CATEGORY,event.getCategory());
                        intent.putExtra(Constants.EVENT_KEY,event.getEventKey());
                        startActivity(intent);
                        finish();

                    }
                });

            }

        };

        recyclerView.setAdapter(firebaseadapter);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()){
                    progressBar.setVisibility(View.GONE);
                    mtitle.setVisibility(View.GONE);
                    m_null_results.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public  static class viewHolder extends RecyclerView.ViewHolder{
        private ImageView mImage;
        private TextView mTitle,mDescrition, mDate,mTime, mVenue;
        private ProgressBar progressBar;
        private LinearLayout layout;

        public viewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mImage = (ImageView) itemView.findViewById(R.id.eventImage);
            layout = (LinearLayout) itemView.findViewById(R.id.listLayout);
            mVenue = (TextView) itemView.findViewById(R.id.tvvenue);
            mDate = (TextView) itemView.findViewById(R.id.tvdate);
            mTime = (TextView) itemView.findViewById(R.id.tvtime);
            mDescrition  =  (TextView) itemView.findViewById(R.id.tventDescrition);
            progressBar = (ProgressBar) itemView.findViewById(R.id.imageprogressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
