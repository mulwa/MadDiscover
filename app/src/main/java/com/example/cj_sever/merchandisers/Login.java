package com.example.cj_sever.merchandisers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN =  200 ;
    private Toolbar toolbar;
    private ProgressDialog progress;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private  FirebaseAuth auth;
    private GoogleSignInAccount mGoogleAccount;
    private SignInButton signInButton;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeUi();
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.title_login));
        }
        checkAuthentication();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            auth.removeAuthStateListener(mAuthListener);

        }
    }

    private void initializeUi(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        signInButton = (SignInButton)findViewById(R.id.login_with_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);


        // Initialize and Configure Google Sign In option
        googleSignInOptions  = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        auth = FirebaseAuth.getInstance();

    }
    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN );
        showDialog();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if (requestCode == RC_SIGN_IN ) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
                if(result.isSuccess()){
                    mGoogleAccount= result.getSignInAccount();//
                    firebaseWithGoogle(mGoogleAccount);

                }else{
                    if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                        showToast("The sign in was cancelled. Make sure you're connected to the internet and try again.");
                    } else {
                        showToast("Error handling the sign in: " + result.getStatus().getStatusMessage());
                    }
                    stopDialog();
                }
            }
            }

    }
    private void checkAuthentication(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    openHome();
                }

            }
        };
    }

    private void openHome() {
        startActivity(new Intent(Login.this,TopEvents.class));
        finish();
    }

    private void firebaseWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    showToast("You have successfully signed in");
                }
                stopDialog();

            }
        });

    }

    private void showDialog(){
        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.progress_dialog_loading));
        progress.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        progress.setCancelable(false);
        progress.show();
    }
    private void stopDialog(){
        progress.dismiss();
    }
    private void showToast(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
