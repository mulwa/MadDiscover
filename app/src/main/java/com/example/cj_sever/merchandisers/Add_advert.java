package com.example.cj_sever.merchandisers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj_sever.merchandisers.Models.Advert;
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
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Add_advert extends AppCompatActivity {
    private EditText edtAdverTitle, edtAdvertDescription, edtAmount,edtMobile;
    private TextInputLayout tilAdvertTitle,
            tilAdvertDescription,
             tilAmount,
            tilMobile;
    private Button btnsubmit;
    private CheckBox chkApprove;
    private Toolbar toolbar;
    private LocationDialog locationDialog;
    private TextView edtAdvertCategory;
    private ImageView  mymage;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String userName;
    private String userEmail;
    private GalleryPhoto galleryPhoto;
    private final int GALLERY_REQUEST = 200;
    private Uri uploadPhoto;

    private DatabaseReference mDatabaseReference;
    private StorageReference storageReference;



    private ProgressDialog progressDialog;
    private CoordinatorLayout coordinatorLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advert);
        initializeUI();
        setSupportActionBar(toolbar);


        galleryPhoto = new GalleryPhoto(getApplicationContext());

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        checkAuthentication();



        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_sell));

        }




        edtAdvertCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatDilalog();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                if(validate()){
                    showDialog();
                    if(mymage.getDrawable()!=null){
                        saveImage(uploadPhoto);
                    }else {
                        saveAdvert("Null",edtAdvertCategory.getText().toString().trim(),edtAdverTitle.getText().toString(),
                                edtAdvertDescription.getText().toString().trim(),edtAmount.getText().toString().trim(),edtMobile.getText().toString().trim()
                                ,userName);
                    }



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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkAuthentication(){
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){
                    userName = user.getDisplayName();
                    userEmail = user.getEmail();

                }else {
                    openLogin();
                }

            }
        };
    }

    private void openLogin() {
        startActivity(new Intent(Add_advert.this,Login.class));
    }

    private void initializeUI(){

        mymage = (ImageView) findViewById(R.id.imageThumbnail);
        //editText
        edtAdverTitle = (EditText) findViewById(R.id.advertTitle);
        edtAdvertCategory = (TextView) findViewById(R.id.advertCategory);
        edtAdvertDescription = (EditText) findViewById(R.id.advertDescription);
        edtAmount = (EditText) findViewById(R.id.amount);
        edtMobile = (EditText)findViewById(R.id.ownermobile);


        edtAdverTitle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edtAdvertCategory.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edtAdvertDescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);



        //textInputLayout
        tilAdvertTitle = (TextInputLayout) findViewById(R.id.tiladvertTitle);
        tilAdvertDescription = (TextInputLayout) findViewById(R.id.tiladvertDiscription);
//        tilLocation = (TextInputLayout) findViewById(R.id.tillocation);
        tilAmount = (TextInputLayout) findViewById(R.id.tilamount);
        tilMobile = (TextInputLayout) findViewById(R.id.tilownermobile);


//        button
        btnsubmit = (Button) findViewById(R.id.btnPost);
//        checkbox
        chkApprove = (CheckBox) findViewById(R.id.approve);
//        toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);


        mymage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(galleryPhoto.openGalleryIntent(),GALLERY_REQUEST);

            }
        });


    }//initilizeUI

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == GALLERY_REQUEST){
            uploadPhoto = data.getData();
            galleryPhoto.setPhotoUri(uploadPhoto);
            String photoPath = galleryPhoto.getPath();
            try {
                Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(520,520).getBitmap();
                mymage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Toast.makeText(Add_advert.this,"problem loading gallery photo",Toast.LENGTH_SHORT).show();

            }


        }
    }


    private void creatDilalog(){
        locationDialog = new LocationDialog();
        locationDialog.show(getFragmentManager(),"location dialog");
    }
    private boolean validate(){
        if(TextUtils.isEmpty(edtAdvertCategory.getText().toString().trim())){
            edtAdvertCategory.setError(getString(R.string.error_advertCategory));
            requestFocus(edtAdvertCategory);
            return false;
        }else {
            edtAdvertCategory.setError(null);
        }
        if(TextUtils.isEmpty(edtAdverTitle.getText().toString().trim())){
            tilAdvertTitle.setError(getString(R.string.error_advertTitle));
            requestFocus(edtAdverTitle);
            return false;
        }else {
            tilAdvertTitle.setErrorEnabled(false);
        }

        if(TextUtils.isEmpty(edtAdvertDescription.getText().toString().trim())){
            tilAdvertDescription.setError(getString(R.string.error_advertDescription));
            requestFocus(edtAdvertDescription);
            return false;
        }else{
            tilAdvertDescription.setErrorEnabled(false);
        }
        if(edtAdvertDescription.getText().toString().length()<5){
            tilAdvertDescription.setError(getString(R.string.error_lessAdvertDiscription));
            requestFocus(edtAdvertDescription);
            return false;
        }else{
            tilAdvertDescription.setErrorEnabled(false);
        }
        if(TextUtils.isEmpty(edtAmount.getText().toString().trim())){
            tilAmount.setError(getString(R.string.error_amount));
            requestFocus(edtAmount);
            return false;
        }else{
            tilAmount.setErrorEnabled(false);
        }
        if(TextUtils.isEmpty(edtMobile.getText().toString().trim())){
            tilMobile.setError(getString(R.string.error_mobile));
            requestFocus(edtMobile);
            return false;
        }else{
            tilMobile.setErrorEnabled(false);
        }

        if(!chkApprove.isChecked()){
            Snackbar.make(coordinatorLayout,getString(R.string.terms),Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(mymage.getDrawable()==null){
            Snackbar.make(coordinatorLayout,getString(R.string.no_image),Snackbar.LENGTH_LONG).show();
        }
        if(!isNetworkAvailable()){
            Snackbar snackbar = Snackbar.make(coordinatorLayout,"No internet connection ", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    private void saveImage(Uri photoUri){
        StorageReference filePath = storageReference.child("Adverts").child(photoUri.getLastPathSegment()) ;
        filePath.putFile(photoUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                stopDialog();
                Toast.makeText(Add_advert.this,"Upload Failled:"+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl= String.valueOf(taskSnapshot.getDownloadUrl());

//                    save advert after image upload
                saveAdvert(downloadUrl,edtAdvertCategory.getText().toString().trim(),
                        edtAdverTitle.getText().toString(),
                        edtAdvertDescription.getText().toString().trim(),
                        edtAmount.getText().toString().trim(),
                        edtMobile.getText().toString().trim(),
                        userName);


            }
        });

    }
    private void clearInput(){
        mymage.setImageBitmap(null);
        edtAdvertCategory.setText("");
        edtAdverTitle.setText("");
        edtAdvertDescription.setText("");
        edtAmount.setText("");
        edtMobile.setText("");



    }

    private void saveAdvert(String image, String category, String title, String description,
                            String amount, String mobileNumber, String username){
        Advert advert = new Advert(image,category,title,description,amount,mobileNumber,username);

        mDatabaseReference.child("Posts").child(username).push().setValue(advert, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                stopDialog();
                if( databaseError != null){
                    Toast.makeText(Add_advert.this,"Error:"+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }else {
                    clearInput();
                    Snackbar.make(coordinatorLayout,"Your product Has Successfully Be Saved",Snackbar.LENGTH_LONG).show();


                }

            }
        });

    }

    private void showDialog(){
        progressDialog.setMessage("Saving Your Advert");
        progressDialog.show();
    }
    private void stopDialog(){
        progressDialog.dismiss();
    }

//    inner class for dialog

    public class LocationDialog extends DialogFragment{
        private ExpandableListView expandableListView;
        private List<String> listHeader;
        private HashMap<String,List<String>> listData;
        public CategoryAdapter adapter;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootview = inflater.inflate(R.layout.category_dialog,container,false);

            expandableListView= (ExpandableListView) rootview.findViewById(R.id.exList);
            AddData();
            adapter = new CategoryAdapter(getActivity(),listHeader,listData);
//            set dialog title
            getDialog().setTitle("SELECT ITEM CATEGORY");
            expandableListView.setAdapter(adapter);
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                    edtAdvertCategory.setText(listHeader.get(groupPosition)+":"+ listData.get(listHeader.get(groupPosition)).get(childPosition));
                    getDialog().dismiss();
                    return false;
                }
            });


            return rootview;
        }

        private void AddData(){
            listHeader = new ArrayList<String>();
            listData = new HashMap<String, List<String>>();

            listHeader.add("ELECTRONICS");
            listHeader.add("FURNITURE");
            listHeader.add("UNTENSILS");
            listHeader.add("STATIONATY");
//        adding child data
            List<String> Electronics = new ArrayList<String>();
            Electronics.add("Television");
            Electronics.add("Laptop");
            Electronics.add("DVD Player");
            Electronics.add("Music System");
            Electronics.add("Mobile Phone");

            List<String> Furniture = new ArrayList<>();
            Furniture.add("Study Table");
            Furniture.add("Dinning Table");
            Furniture.add("Bed");
            Furniture.add("Wall Unit");

            List<String> untensils = new ArrayList<>();
            untensils.add("Cooking Gas");
            untensils.add("Others");

            List<String> Stationary = new ArrayList<>();
            Stationary.add("Books");
            Stationary.add("Others");

            listData.put(listHeader.get(0),Electronics);
            listData.put(listHeader.get(1),Furniture);
            listData.put(listHeader.get(2),untensils);
            listData.put(listHeader.get(3),Stationary);

        }
    }

}
