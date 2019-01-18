package com.waki.ezpzmath;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import android.content.ServiceConnection;


public class  MainActivity extends AppCompatActivity {
    private Button Guest_button;
    private ImageButton exit_button;
    SignInButton loginbtn; //google api btn
    FirebaseAuth mAuth; //using firebase auth system
    private final static int RC_SIGN_IN = 2;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean mIsBound = false;        //is MusicService bound
    private MusicService mServ = new MusicService();  //to control the Music service
    private ServiceConnection Scon = new ServiceConnection() {              //a connection to the Music Service

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;

        }
    };
    ImageButton soundBtn;
    boolean isPlaying;

    @Override
    protected void onStart() {
        super.onStart();
        doBindService();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (mAuth.getCurrentUser() != null)
        {
            mAuth.getCurrentUser().delete();
        }
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        if(!mServ.isPlaying() && isPlaying){ //to handel the case when the user press home button and open the app again
            mServ.resumeMusic();
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(mIsBound) {          //unbound the connection to the music service and stop playing music
            doUnbindService();
        }

    }
    @Override
    protected void onDestroy(){ //on destroy stop playing music, stop the servece(to avoid playing music when the app is closed) and unbound connection to the service
        super.onDestroy();
        mServ.stopMusic();
        mServ.stopSelf();
        doUnbindService();
    }
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        startService(music);
        isPlaying = true;

        soundBtn = (ImageButton)findViewById(R.id.imageButton10);
        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSound();
            }
        });
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Intent intent =  new Intent(MainActivity.this, ModesActivity.class);
                    intent.putExtra("isPlaying",isPlaying);
                    startActivity(intent);
                }
            }
        };

        loginbtn = findViewById(R.id.sign_in_button);
        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                signIn();
            }
        });

        Guest_button = findViewById(R.id.button3);
        Guest_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openModesActivity(isPlaying);
            }
        });

        exit_button = findViewById(R.id.imageButton9);
        exit_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        GoogleSignInOptions gso;
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }
    void doBindService(){ //bind the activity to the Music service
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    void doUnbindService() //unbind the Music Service
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }
    public void openModesActivity(boolean isPlaying){
        Intent intent =  new Intent(this, ModesActivity.class);
        intent.putExtra("isPlaying", isPlaying);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                mGoogleSignInClient.signOut();
            } catch (ApiException e) {
                Toast.makeText(this,"Could not log in API EXC", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserToDB(user); //add user when login
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    //adds a user to db if it does not exist
    public void addUserToDB(FirebaseUser user){
        DocumentReference check = db.collection("users").document(user.getEmail());
        final String mail = user.getEmail();
        final String displayname = user.getDisplayName();
        check.get().addOnCompleteListener((new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        //check if user exists by looking if we got result from db
                        Log.d("User add", "User already exist");
                    }
                    else{
                        //we did not get result for db => user does not exist, add user
                        Log.d("User add", "User does not exist, lets add");
                        Map<String, Object> newuser = new HashMap<String,Object>();
                        newuser.put("displayname", displayname);
                        newuser.put("gmail", mail);
                        db.collection("users").document(mail).set(newuser)
                                .addOnSuccessListener((new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("User add", "User got added");
                                    }
                                }))
                                .addOnFailureListener((new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("User add", "User NOT added");
                                    }
                                }));
                    }
                }
                else{
                    Log.d("User add", "Database call not successful");
                }
            }
        }));
    }
    public void setSound(){
        if(mServ.isPlaying()) {
            mServ.pauseMusic();
            isPlaying = false;
            soundBtn.setImageResource(R.drawable.sound_off);
        }
        else{
            mServ.resumeMusic();
            isPlaying = true;
            soundBtn.setImageResource(R.drawable.sound_on);
        }
    }
}
