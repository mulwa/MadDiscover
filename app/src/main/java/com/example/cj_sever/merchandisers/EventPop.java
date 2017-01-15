package com.example.cj_sever.merchandisers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class EventPop extends AppCompatActivity {
    private ImageView myImage;
    private TextView myEventName;
    private String imageUrl;
    private ProgressBar progressBar;
    private String TAG ="pop";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pop);



        imageUrl = getIntent().getStringExtra(Constants.EVENTIMAGE);
        Log.d(TAG,imageUrl);
        myImage = (ImageView) findViewById(R.id.popImage);
        myEventName =(TextView) findViewById(R.id.tvEventName);
        progressBar =(ProgressBar) findViewById(R.id.progressBar);



        myEventName.setText(getIntent().getStringExtra(Constants.EVENTNAME));


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        get the image Resolution;
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.9) ,(int)(height*0.5));

        Picasso.with(EventPop.this)
                .load(imageUrl)
                .into(myImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                        Bitmap bitmap = ((BitmapDrawable)myImage.getDrawable()).getBitmap();
                        Palette.from(bitmap).maximumColorCount(14).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch vibrant = palette.getDarkVibrantSwatch();
                                if(vibrant != null){
                                    myEventName.setBackgroundColor(vibrant.getRgb());
                                    myEventName.setTextColor(vibrant.getTitleTextColor());
                                }

                            }
                        });

                    }

                    @Override
                    public void onError() {
//                        Toast.makeText(getApplicationContext(),"Error Loading image",Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            myEventName.setTextColor(getColor(R.color.white));
                            myEventName.setBackgroundColor(getColor(R.color.transparentBg));
                        }

                    }
                });
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventPop.this,MoreEventDetails.class);
                intent.putExtra(Constants.EVENTNAME,getIntent().getStringExtra(Constants.EVENTNAME));
                intent.putExtra(Constants.EVENTIMAGE,getIntent().getStringExtra(Constants.EVENTIMAGE));
                intent.putExtra(Constants.EVENTDESCRIPTION,getIntent().getStringExtra(Constants.EVENTDESCRIPTION));
                intent.putExtra(Constants.EVENTORGANIZER, getIntent().getStringExtra(Constants.EVENTORGANIZER));
                intent.putExtra(Constants.EVENT_DAY_FROM, getIntent().getStringExtra(Constants.EVENT_DAY_FROM));
                intent.putExtra(Constants.EVENT_DAY_TO,getIntent().getStringExtra(Constants.EVENT_DAY_TO));
                intent.putExtra(Constants.EVENT_START_TIME, getIntent().getStringExtra(Constants.EVENT_START_TIME));
                intent.putExtra(Constants.EVENTVENUE,getIntent().getStringExtra(Constants.EVENTVENUE));
                intent.putExtra(Constants.EVENTORGANIZER,getIntent().getStringExtra(Constants.EVENTORGANIZER));
                intent.putExtra(Constants.EVENTOWNER,getIntent().getStringExtra(Constants.EVENTOWNER));
                intent.putExtra(Constants.OWNER_MOBILE,getIntent().getStringExtra(Constants.OWNER_MOBILE));
                intent.putExtra(Constants.EVENT_CATEGORY,getIntent().getStringExtra(Constants.EVENT_CATEGORY));
                intent.putExtra(Constants.EVENT_KEY,getIntent().getStringExtra(Constants.EVENT_KEY));
                startActivity(intent);
                finish();


            }
        });
    }

}
