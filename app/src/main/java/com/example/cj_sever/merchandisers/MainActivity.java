package com.example.cj_sever.merchandisers;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj_sever.merchandisers.Models.Advert;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView myRecyclerview;

    private static  final String POSTS = "Posts";

    private ProgressBar bar;
    private GeneralFunctions generalFunctions;
    private FirebaseDatabase database ;
    private DatabaseReference mDatabaseReference ;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance();
        mDatabaseReference= database.getReference();



        Log.d(TAG," url:"+mDatabaseReference.child(POSTS).getRef());



        initializeUI();
        setSupportActionBar(toolbar);
        if(getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_adverts));
        }
        fetchAdverts();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Add_advert.class);
                startActivity(intent);
            }
        });

    }
    private void fetchAdverts(){
        FirebaseRecyclerAdapter<Advert,viewHolder> adapter = new FirebaseRecyclerAdapter<Advert, viewHolder>(
                Advert.class,R.layout.adverts_list_custom_layout,viewHolder.class,mDatabaseReference.child(POSTS).getRef()
        ) {
            @Override
            protected void populateViewHolder(final viewHolder viewHolder, Advert model, int position) {
                bar.setVisibility(View.GONE);
                viewHolder.mytitle.setText(model.getTitle());
                viewHolder.mydescription.setText(model.getDescription());
                viewHolder.mymobile.setText(model.getMobile());
                viewHolder.myamount.setText(" Ksh. " + model.getAmount());
                Log.d(TAG,""+model.getAmount());

                Picasso.with(MainActivity.this)
                        .load(model.getImage()).resize(80,0)
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .into(viewHolder.myimage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                if (viewHolder.mprogressbar != null){
                                    viewHolder.mprogressbar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError() {
                                viewHolder.mprogressbar.setVisibility(View.GONE);

                            }
                        });



            }
        };

        myRecyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        myRecyclerview.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isNetworkAvailable()){
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

    private void initializeUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        myRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        myRecyclerview.setHasFixedSize(true);
        myRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        private ImageView myimage;
        private TextView mytitle,mydescription,myamount,mymobile;
        private ProgressBar mprogressbar;

        public viewHolder(View itemView) {
            super(itemView);

            myimage = (ImageView) itemView.findViewById(R.id.imageThumbnail);
            mytitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mydescription = (TextView) itemView.findViewById(R.id.tvdescription);
            myamount = (TextView) itemView.findViewById(R.id.tvamount);
            mymobile = (TextView) itemView.findViewById(R.id.tvmobile);
            mprogressbar = (ProgressBar) itemView.findViewById(R.id.imageprogressBar);
            mprogressbar.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the TopEvents/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
