package com.example.cj_sever.merchandisers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cj_sever.merchandisers.Models.Event;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class TopEvents extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageView imageThumbnail;
//    private CircleImageView circleThumbmail;
    private TextView tvUsername, tvUserEmail;
    //    viaraibles for firebase authentication
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;
    private Query mDatabaseReference;
    private DatabaseReference advertDatabaseRefences;

    //    variables for logged in user
    public String userName;
    private String userEmail;
    private Uri userPhoto;

    private ListView m_top_event_list;
//    private ListView m_top_advert_list;
    private TextView mtitle;
    private ProgressBar m_event_progressBar;
//    private ProgressBar m_advert_progressBar;
    private TextView m_connection_error;
    private Button b_try_Again;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference(Constants.event_node).getRef().orderByChild(Constants.firebase_reference_Event_dateFrom).limitToFirst(3);

        findViews();
        checkAuthentication();
        fetchTopEvents();



    }
    private void findViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
//        View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_home,null);
        View headerView = navigationView.getHeaderView(0);
        imageThumbnail = (ImageView) headerView.findViewById(R.id.accountThumbnail);
//        circleThumbmail = (CircleImageView) headerView.findViewById(R.id.accountThumbnail);
        tvUsername = (TextView) headerView.findViewById(R.id.accountUsername);
        tvUserEmail  = (TextView) headerView.findViewById(R.id.accountEmail);

//        m_top_advert_list = (ListView) findViewById(R.id.topAdvert);
        m_top_event_list = (ListView) findViewById(R.id.topEvents);
        mtitle = (TextView) findViewById(R.id.title1);
        m_event_progressBar = (ProgressBar) findViewById(R.id.event_progressBar);
//        m_advert_progressBar = (ProgressBar) findViewById(R.id.advert_progressBar);
        mtitle.setText("Top Events ");
        m_connection_error = (TextView) findViewById(R.id.connection_error);
        b_try_Again = (Button) findViewById(R.id.btn_tryAgain);
        m_connection_error.setVisibility(View.GONE);
        b_try_Again.setVisibility(View.GONE);

        b_try_Again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_event_progressBar.setVisibility(View.VISIBLE);
                m_connection_error.setVisibility(View.INVISIBLE);
                fetchTopEvents();
            }
        });
    }
    private void fetchTopEvents(){
        if(isNetworkAvailable()){
            FirebaseListAdapter<Event> eventFirebaseListAdapter = new FirebaseListAdapter<Event>(this,Event.class,R.layout.event_list_custom,mDatabaseReference) {
                @Override
                protected void populateView(final View view, final Event model, int position) {
                    m_event_progressBar.setVisibility(View.GONE);
                    ((TextView) view.findViewById(R.id.tvTitle)).setText(model.getTitle());
                    ((TextView) view.findViewById(R.id.tvvenue)).setText(model.getVenue());
                    ((TextView) view.findViewById(R.id.tventDescrition)).setText(model.getDescription());
                    ((TextView) view.findViewById(R.id.tvdate)).setText(model.getDateFrom());
                    ((TextView) view.findViewById(R.id.tvtime)).setText(model.getStartTime());
                    ImageView mImage = (ImageView) view.findViewById(R.id.eventImage);
                    LinearLayout mlayout = (LinearLayout) view.findViewById(R.id.listLayout);
                    final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.imageprogressBar);
                    progressBar.setVisibility(View.VISIBLE);

                    Picasso.with(getApplicationContext())
                            .load(model.getImage())
                            .resize(100,0)
                            .error(R.drawable.image_placeholder)
                            .into(mImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if(progressBar != null){
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onError() {
                                    progressBar.setVisibility(View.GONE);

                                }
                            });
                    mlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(),EventPop.class);
                            intent.putExtra(Constants.EVENTNAME,model.getTitle());
                            intent.putExtra(Constants.EVENTIMAGE,model.getImage());
                            intent.putExtra(Constants.EVENTDESCRIPTION,model.getDescription());
                            intent.putExtra(Constants.EVENT_DAY_FROM,model.getDateFrom());
                            intent.putExtra(Constants.EVENT_DAY_TO,model.getDateTo());
                            intent.putExtra(Constants.EVENT_START_TIME, model.getStartTime());
                            intent.putExtra(Constants.EVENT_END_TIME,model.getEndTime());
                            intent.putExtra(Constants.EVENTVENUE,model.getVenue());
                            intent.putExtra(Constants.EVENTORGANIZER,model.getOrganiser());
                            intent.putExtra(Constants.EVENTOWNER,model.getOwner());
                            intent.putExtra(Constants.OWNER_MOBILE,model.getOwnerMobile());
                            intent.putExtra(Constants.EVENT_CATEGORY,model.getCategory());
                            intent.putExtra(Constants.EVENT_KEY,model.getEventKey());
                            startActivity(intent);

                        }
                    });

                }
            };
            m_top_event_list.setAdapter(eventFirebaseListAdapter);
            eventFirebaseListAdapter.notifyDataSetChanged();

        }else{
            m_connection_error.setVisibility(View.VISIBLE);
            b_try_Again.setVisibility(View.VISIBLE);
            m_event_progressBar.setVisibility(View.INVISIBLE);
            mtitle.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(authListener !=null){
            auth.removeAuthStateListener(authListener);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void checkAuthentication(){
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    userName = user.getDisplayName();
                    userEmail = user.getEmail();
                    userPhoto = user.getPhotoUrl();
                    updateUi(userName,userEmail,userPhoto);

                }
            }
        };
    }
    private void updateUi(String username, String email, Uri photoUri){
        tvUsername.setText(username);
        tvUserEmail.setText(email);
        Picasso.with(getApplicationContext())
                .load(photoUri)
                .into(imageThumbnail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the TopEvents/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out) {
            auth.signOut();
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }
        if(id==R.id.filter){
            popFilter();

        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
          if (id == R.id.addEvent) {
            addEvent();

        } else if (id == R.id.login) {
            log_in();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void addEvent(){
        startActivity(new Intent(TopEvents.this,Add_event.class));
        finish();
    }
    private void log_in(){
        startActivity(new Intent(TopEvents.this,Login.class));
        finish();
    }
    private void popFilter(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TopEvents.this);
        final String[] category = getResources().getStringArray(R.array.eventCategory);
        alertDialogBuilder.setTitle(getString(R.string.eventDialogFilterTitle));
        alertDialogBuilder.setItems(R.array.eventCategory, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedCategory = category[i];
                if(selectedCategory != null){
                    Intent filterIntent = new Intent(TopEvents.this,FilteredEvents.class);
                    filterIntent.putExtra(Constants.filter_category,selectedCategory);
                    startActivity(filterIntent);

                }

            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//               what to execute  when user selects cancel button

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}