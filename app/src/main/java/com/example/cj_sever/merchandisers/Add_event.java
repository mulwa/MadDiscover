package com.example.cj_sever.merchandisers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cj_sever.merchandisers.Models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Calendar;

public class Add_event extends AppCompatActivity {
    private EditText edtEventName,edtEventVenue,edtEventDescription,edtEventOrganizers, edtEventOrganizerContact;
    private TextView EventDateFrom,EventDateTo ,tvEventCategory;
    private ImageView imgEventImage;
    private Button btnSubmit;
    private TextInputLayout til_eventName,til_eventVenue,til_eventOrganizers,til_eventOrganizerContact;
    private TextInputLayout til_eventDescription;
    private Toolbar toolbar;

    private GalleryPhoto galleryPhoto;
    private final int GALLERY_REQUEST = 200;
    private Uri uploadPhoto;
    private Calendar calendar ;
    private TextView startTime,endTime;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String userName;

    private LinearLayout  linearLayoutDateFrom;
    private LinearLayout linearLayoutDateTo;

    private LinearLayout linearLayoutTimeFrom, linearLayoutTimeTo;
    private CoordinatorLayout coordinatorLayout;

    private DatabaseReference mDatabaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
//to be used for saving
    private String e_Key;
    private String e_title;
    private String e_category;
    private String e_description;
    private String e_venue;
    private String e_dateFrom;
    private String e_dateTo;
    private String e_startTime;
    private String e_EndTime;
    private String e_organiser;
    private String e_onewrMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        findUiView();
        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        checkAuthentication();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
                if(validateInput()){
                    showDialog();
                    if (imgEventImage.getDrawable() != null){
                        saveImage(uploadPhoto);
                    }else {
                        Toast.makeText(getApplicationContext(),"please provide an Event Banner",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //        for making the textArea scroll easily
        edtEventDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        imgEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(galleryPhoto.openGalleryIntent(),GALLERY_REQUEST);

            }
        });
        tvEventCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventCategoryDialog();
            }
        });

        linearLayoutDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogTo();
            }
        });

        linearLayoutTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerFrom();
            }
        });
        linearLayoutTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerTo();
            }
        });

        linearLayoutDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogFrom();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void getData(){
        e_title = edtEventName.getText().toString().trim();
        e_category = tvEventCategory.getText().toString().trim();
        e_description = edtEventDescription.getText().toString().trim();
        e_venue = edtEventVenue.getText().toString().trim();
        e_dateFrom = EventDateFrom.getText().toString().trim();
        e_dateTo = EventDateTo.getText().toString().trim();
        e_startTime = startTime.getText().toString().trim();
        e_EndTime = endTime.getText().toString().trim();
        e_organiser = edtEventOrganizers.getText().toString().trim();
        e_onewrMobile = edtEventOrganizerContact.getText().toString().trim();

    }
    private void showEventCategoryDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Add_event.this);
        final String[] category = getResources().getStringArray(R.array.eventCategory);
        alertDialogBuilder.setTitle(getString(R.string.eventDialogTitle));
        alertDialogBuilder.setItems(R.array.eventCategory, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvEventCategory.setText(category[i]);


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

    private void showDatePickerDialogFrom(){
        new DatePickerDialog(Add_event.this,listener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            EventDateFrom.setText(""+dayOfMonth+"/"+(monthOfYear+1)+"/"+year);


        }
    };

    private void showDatePickerDialogTo(){
        new DatePickerDialog(Add_event.this,toListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
   DatePickerDialog.OnDateSetListener toListener = new DatePickerDialog.OnDateSetListener(){

       @Override
       public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
           EventDateTo.setText(""+dayOfMonth+"/"+(monthOfYear+1)+"/"+year);

       }
   };
    private void  showTimePickerFrom(){
        DialogFragment newFragment = new timepickerFragmentFrom();
        newFragment.show(getFragmentManager(), "timePickerFrom");

    }
    private void  showTimePickerTo(){
        DialogFragment newFragment = new timepickerFragmentTo();
        newFragment.show(getFragmentManager(), "timePickerTo");

    }
    private void findUiView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_event));
        }
        galleryPhoto = new GalleryPhoto(getApplicationContext());

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        edtEventName = (EditText) findViewById(R.id.eventTitle);
        edtEventVenue = (EditText) findViewById(R.id.eventVenue);
        edtEventDescription = (EditText) findViewById(R.id.mdescription);
        edtEventOrganizers = (EditText) findViewById(R.id.eventOrganizer);
        edtEventOrganizerContact = (EditText) findViewById(R.id.eventOrganizerContact);
        tvEventCategory = (TextView) findViewById(R.id.eventCategory);

        edtEventName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edtEventVenue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
//        edtEventDescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edtEventOrganizers.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        EventDateTo = (TextView) findViewById(R.id.tveventDateTo);
        EventDateFrom = (TextView) findViewById(R.id.tveventDateFrom);


        startTime = (TextView) findViewById(R.id.tvStartTime);
        endTime = (TextView) findViewById(R.id.tvEndTime);


        linearLayoutDateFrom = (LinearLayout) findViewById(R.id.linearLayoutDateFrom);
        linearLayoutDateTo = (LinearLayout) findViewById(R.id.linearLayoutDateTo);

        linearLayoutTimeFrom = (LinearLayout) findViewById(R.id.linearLayoutTimeFrom);
        linearLayoutTimeTo = (LinearLayout) findViewById(R.id.linearLayoutTimeTo);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);




        til_eventName = (TextInputLayout) findViewById(R.id.tilEventTitle);
        til_eventDescription = (TextInputLayout)findViewById(R.id.tilEventDescription);
        til_eventVenue = (TextInputLayout) findViewById(R.id.tilVenue);
        til_eventOrganizers = (TextInputLayout) findViewById(R.id.tilOrganizer);
        til_eventOrganizerContact = (TextInputLayout)findViewById(R.id.tilContact);

        imgEventImage = (ImageView) findViewById(R.id.eventImage);
        btnSubmit = (Button) findViewById(R.id.btnUploadEvent);



    }
    private boolean validateInput(){
        getData();
        if(TextUtils.isEmpty(e_title)){
            til_eventName.setError(getString(R.string.error_eventTitle));
            return false;
        }else{
            til_eventName.setErrorEnabled(false);
        }
        if(TextUtils.isEmpty(edtEventVenue.getText().toString().trim())){
            til_eventVenue.setError(getString(R.string.error_eventVenue));
            return false;
        }else{
            til_eventVenue.setErrorEnabled(false);
        }
        if(TextUtils.isEmpty(edtEventDescription.getText().toString().trim())){
            til_eventDescription.setError(getString(R.string.error_eventDescription));
            return false;
        }else{
            til_eventDescription.setErrorEnabled(false);
        }
        if(TextUtils.isEmpty(EventDateFrom.getText().toString().trim())){
            EventDateFrom.setError(getString(R.string.error_eventDate));
            return false;
        }else{
            EventDateFrom.setError(null);
        }
        if(TextUtils.isEmpty(startTime.getText().toString().trim())){
            startTime.setError(getString(R.string.error_eventTime));
            return false;
        }
        if(TextUtils.isEmpty(edtEventOrganizers.getText().toString().trim())){
            til_eventOrganizers.setError(getString(R.string.error_organizers));
            return false;
        }else {
            til_eventOrganizers.setErrorEnabled(false);
        }
        if(TextUtils.isEmpty(startTime.getText().toString().trim())){
            Toast.makeText(getApplicationContext(),"Enter event time",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(edtEventOrganizerContact.getText().toString().trim())){
            til_eventOrganizerContact.setError(getString(R.string.no_contact_provided));
        }else{
            til_eventOrganizerContact.setErrorEnabled(false);

        }
        if(TextUtils.isEmpty(tvEventCategory.getText().toString().trim())){
            tvEventCategory.setError(getString(R.string.error_category));
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

                }else {
                    openLogin();
                }

            }
        };
    }
    private void openLogin() {
        startActivity(new Intent(Add_event.this,Login.class));

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode==GALLERY_REQUEST){
//          get  uri of the image to be uploaded which will  be passed for storage
            uploadPhoto = data.getData();
            galleryPhoto.setPhotoUri(uploadPhoto);
            String photoPath = galleryPhoto.getPath();

            try {
                Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(520,520).getBitmap();
                imgEventImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Add_event.this,"problem loading gallery photo",Toast.LENGTH_SHORT).show();
            }
        }
    }
//    method to save image to database
    private void saveImage(Uri imageUri){
        getData();
        StorageReference filepath = storageReference.child("Events").child(imageUri.getLastPathSegment());
        filepath.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                stopDialog();
                makeToast(getApplicationContext(),"Image failed to upload"+e.getMessage());

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                makeToast(getApplicationContext(),"success");
                String downloadUrl= String.valueOf(taskSnapshot.getDownloadUrl());

                e_Key = mDatabaseReference.child(Constants.event_node).push().getKey();
                Event event = new Event(e_Key,downloadUrl,e_title,e_category,e_description,e_venue,e_dateFrom,e_dateTo,e_startTime,e_EndTime,e_organiser,userName,e_onewrMobile);
                mDatabaseReference.child(Constants.event_node).push().setValue(event, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        stopDialog();
                        if(databaseError==null){
                            makeSnackbar("Your Event Details Has Been Saved Successfully",coordinatorLayout);
                            clearInput();
                        }else {
                            makeToast(getApplicationContext(),"failed"+databaseError.getMessage());
                        }
                    }
                });




            }
        });
    }
    private void makeToast(Context ctx,String msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_LONG).show();
    }
    private void makeSnackbar(String msg,View view){
        Snackbar.make(view,msg,Snackbar.LENGTH_LONG).show();
    }
    private void showDialog(){
        progressDialog.setMessage("Saving Your Event Details");
        progressDialog.show();
    }
    private void clearInput(){
        edtEventName.setText("");
        edtEventDescription.setText("");
        edtEventVenue.setText("");
        EventDateFrom.setText("");
        EventDateTo.setText("");
        startTime.setText("");
        endTime.setText("");
        edtEventOrganizers.setText("");
    }
    private void stopDialog(){
        progressDialog.dismiss();
    }
    public  class timepickerFragmentFrom extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),this,hour,minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            //Get the AM or PM for current time
            String aMpM = "AM";
            if(hour >11)
            {
                aMpM = "PM";
            }
            //Make the 24 hour time format to 12 hour time format
            int currentHour;
            if(hour>11)
            {
                currentHour = hour - 12;
            }
            else
            {
                currentHour = hour;
            }
            startTime.setText(""+String.valueOf(currentHour)+" : "+ String.valueOf(minute) + " " +aMpM);

        }
    }
    public  class timepickerFragmentTo extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),this,hour,minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            //Get the AM or PM for current time
            String aMpM = "AM";
            if(hour >11)
            {
                aMpM = "PM";
            }
            //Make the 24 hour time format to 12 hour time format
            int currentHour;
            if(hour>11)
            {
                currentHour = hour - 12;
            }
            else
            {
                currentHour = hour;
            }
            endTime.setText(""+String.valueOf(currentHour)+" : "+ String.valueOf(minute) + " " +aMpM);

        }
    }

}
