package com.example.cj_sever.merchandisers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj_sever.merchandisers.Models.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreEventDetails extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView collapsingImage;
    private CoordinatorLayout coordinatorLayout;
    private String imageUrl;
    private ImageButton imageShare,imageCall;
    private TextView mtvdescription, mtvCategory,mtvVenue,mtvDate;
    private AppBarLayout appBarLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetView;
    private EditText m_comment;
    private RecyclerView comment_list;
    private Button send_comment;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference mDatabaseReference;
    private String userName;
    private String photoUrl;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String currentDate;
    private ProgressDialog progressDialog;
    private String TAG = "TEST";
    private String EVENTS ="Events";

    private String eventKey;




   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_event_details);
        imageUrl = getIntent().getStringExtra(Constants.EVENTIMAGE);
        initializeUi();
        initCollapsingToolbar();
        auth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDate = dateFormat.format(calendar.getTime());
        progressDialog = new ProgressDialog(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        comment_list = (RecyclerView) findViewById(R.id.commentList);
//        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        checkAuthentication();

        eventKey = getIntent().getStringExtra(Constants.EVENT_KEY);
        fetchComments();




        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateComment()){
                    saveComment();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authListener != null){
            auth.removeAuthStateListener(authListener);
        }
    }

    private void initializeUi(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingImage = (ImageView) findViewById(R.id.collapsingImage);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setTitle(getIntent().getStringExtra(Constants.EVENTNAME));

        }
        Picasso.with(MoreEventDetails.this)
                .load(imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(collapsingImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable)collapsingImage.getDrawable()).getBitmap();
                        Palette.from(bitmap).maximumColorCount(14).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch vibrant = palette.getDarkVibrantSwatch();
                                if(vibrant != null){
//                                    getSupportActionBar().setBackgroundColor(vibrant.getRgb());
//                                    mtvTime.setTextColor(vibrant.getTitleTextColor());
                                    toolbar.setBackgroundColor(vibrant.getRgb());
                                    appBarLayout.setBackgroundColor(vibrant.getTitleTextColor());
                                }

                            }
                        });

                    }

                    @Override
                    public void onError() {


                    }
                });

        mtvdescription = (TextView) findViewById(R.id.tvdescription);
        mtvCategory = (TextView)findViewById(R.id.tvcategory);
        mtvVenue = (TextView)findViewById(R.id.tvvenue);
        mtvDate = (TextView)findViewById(R.id.tvdate);

        m_comment = (EditText) findViewById(R.id.edtComment);
        send_comment = (Button) findViewById(R.id.btnComment);
        comment_list = (RecyclerView) findViewById(R.id.commentList);
        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        bottomSheetView = findViewById(R.id.comment_bottom_sheet_view);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setPeekHeight(100);

        mtvdescription.setText(getIntent().getStringExtra(Constants.EVENTDESCRIPTION));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.category));
        stringBuilder.append(":");
        stringBuilder.append(getIntent().getStringExtra(Constants.EVENT_CATEGORY));
        mtvCategory.setText(stringBuilder.toString());


        StringBuilder dateString = new StringBuilder();
        dateString.append(getString(R.string.dateFrom));
        dateString.append(":");
        dateString.append(getIntent().getStringExtra(Constants.EVENT_DAY_FROM));
        dateString.append(" ");
        dateString.append(getString(R.string.dateTo));
        dateString.append(":");
        dateString.append(getIntent().getStringExtra(Constants.EVENT_DAY_TO));
        dateString.append(" ");
        dateString.append("Time: ");
        dateString.append(" ");
        dateString.append(getIntent().getStringExtra(Constants.EVENT_START_TIME));
        dateString.append("To:");
        dateString.append(" ");
        dateString.append(getIntent().getStringExtra(Constants.EVENT_START_TIME));
        mtvDate.setText(dateString);

        StringBuilder venueString = new StringBuilder();
        venueString.append("Venue:");
        venueString.append(" ");
        venueString.append(getIntent().getStringExtra(Constants.EVENTVENUE));
        venueString.append("  ");
        venueString.append("Organizers:");
        venueString.append(" ");
        venueString.append(getIntent().getStringExtra(Constants.EVENTORGANIZER));
        mtvVenue.setText(venueString);


        imageShare = (ImageButton)findViewById(R.id.ibshare);
        imageCall = (ImageButton) findViewById(R.id.ibcall);

    }
    private boolean validateComment(){
        if(TextUtils.isEmpty(m_comment.getText().toString().trim())){
            Toast.makeText(getApplicationContext(),"Please Enter Comment",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!isNetworkAvailable()){
            Snackbar snackbar = Snackbar.make(coordinatorLayout,getString(R.string.non_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.accent));
            snackbar.show();
            return false;
        }
        return true;
    }
    private void checkAuthentication(){
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){
                    userName = user.getDisplayName();
                    photoUrl = String.valueOf(user.getPhotoUrl());

                }else {
                    Toast.makeText(getApplicationContext(),"You need to loggin ",Toast.LENGTH_LONG).show();
                }

            }
        };
    }
    private void showDialog(){
        progressDialog.setMessage("Saving Your Event Details");
        progressDialog.show();
    }
    private void stopDialog(){
        progressDialog.dismiss();
    }

    private void saveComment(){
        showDialog();
        Comment comment = new Comment(userName,m_comment.getText().toString().trim(),photoUrl,currentDate);
        mDatabaseReference.child(Constants.comments_node).child(eventKey).push().setValue(comment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                stopDialog();
                if(databaseError ==null){
//                    Toast.makeText(getApplicationContext(),"comment Saved successfully",Toast.LENGTH_SHORT).show();
                    GeneralFunctions.makeSnackbar("comment Saved successfully",coordinatorLayout);
                    m_comment.setText("");
                }else {
                    Toast.makeText(getApplicationContext(),"failled"+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void fetchComments(){
        DatabaseReference commentRef = mDatabaseReference.child(Constants.comments_node).child(eventKey);
        FirebaseRecyclerAdapter<Comment,viewholder> commentAdapter = new FirebaseRecyclerAdapter<Comment, viewholder>(
                Comment.class,
                R.layout.comments_layout,
                viewholder.class,
                commentRef
        ) {
            @Override
            protected void populateViewHolder(viewholder viewHolder, Comment model, int position) {
                viewHolder.m_username.setText(model.getUserName());
                viewHolder.m_comment_date.setText(model.getTime());
                viewHolder.m_comment.setText(model.getComment());

                Picasso.with(getApplicationContext())
                        .load(model.getUserPhoto())
                        .into(viewHolder.m_photo_url);

                viewHolder.m_dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GeneralFunctions.makeToast(getApplicationContext(),"dislike not ready");
                    }
                });
                viewHolder.m_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GeneralFunctions.makeToast(getApplicationContext(),"like function not ready");
                    }
                });

            }
        };
        comment_list.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsingToolBar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getIntent().getStringExtra(Constants.EVENTNAME));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
    private  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static class viewholder extends RecyclerView.ViewHolder{
        private TextView m_comment;
        private TextView m_comment_date;
        private TextView m_username;
        private CircleImageView m_photo_url;
        private ImageButton m_like;
        private ImageButton m_dislike;
        private TextView m_count;

        public viewholder(View itemView) {
            super(itemView);
            m_comment = (TextView) itemView.findViewById(R.id.comment);
            m_comment_date = (TextView) itemView.findViewById(R.id.date);
            m_username = (TextView) itemView.findViewById(R.id.username);
            m_photo_url = (CircleImageView) itemView.findViewById(R.id.accountThumbnail);

            m_like = (ImageButton) itemView.findViewById(R.id.ib_like);
            m_dislike = (ImageButton) itemView.findViewById(R.id.ib_dislike);
            m_count = (TextView) itemView.findViewById(R.id.tv_count);
        }
    }
}
