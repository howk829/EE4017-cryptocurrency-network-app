package com.example.ee4017;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Auth extends AppCompatActivity {


    private int RC_SIGN_IN = 1;
    public static String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        SignInButton signInButton = findViewById(R.id.sign_in_button);



        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn()
    {
        Intent signInIntent = MainActivity.mGoogleSignClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){

        try{
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);

            Toast.makeText(Auth.this,"Sign In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);

            // go back to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        catch(ApiException e){
            Toast.makeText(Auth.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }

    }


    private void FirebaseGoogleAuth(GoogleSignInAccount acc){
        try{
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
            MainActivity.mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        MainActivity.currentUser = MainActivity.mAuth.getCurrentUser();
                        updateUI(MainActivity.currentUser);
                    }
                    else
                    {
                        Toast.makeText(Auth.this,"Failed",Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void updateUI(FirebaseUser user){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account != null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            userID = MainActivity.mAuth.getUid();
            System.out.println(userID);

            Toast.makeText(Auth.this,"Welcome " + personName,Toast.LENGTH_SHORT).show();
        }

    }
}
