package com.app.gittest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.gittest.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OnConnectionFailedListener {

    public String TAG = "MainActivity";
    public MainActivity activity;
    public ActivityMainBinding binding;

    private static final int RC_SIGN_IN = 9001;
    public GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activity = MainActivity.this;

//        AppCenter.start(getApplication(), "b32cbe31-648f-405f-b0ad-982e6b589700",
//                Analytics.class, Crashes.class);

        // Configure sign-in to request the user's ID, email address, and basic
       // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        init();
    }

    private void init()
    {
        Log.e(TAG, "init()");

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

        binding.btnSignUp.setOnClickListener(this);
        binding.btnLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnSignUp:
                signIn();
                break;

            case R.id.btnLogOut:
                signOut();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        }
        catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut()
    {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null)
        {
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));
//
            binding.btnSignUp.setVisibility(View.GONE);
            binding.btnLogOut.setVisibility(View.VISIBLE);
            binding.llWelcome.setVisibility(View.VISIBLE);

            Log.e(TAG, "updateUI Email:- " + account.getEmail());
            Log.e(TAG, "updateUI Display Name:- " + account.getDisplayName());
            Log.e(TAG, "updateUI Photo :- " + account.getPhotoUrl());

            binding.tvEmail.setText(account.getEmail().trim());
            binding.tvDisplayName.setText(account.getDisplayName().trim());

            if (account.getPhotoUrl() != null &&
                    !account.getPhotoUrl().equals("") &&
                    !account.getPhotoUrl().equals("null"))
            {
                Picasso.get()
                        .load(account.getPhotoUrl())
                        .into(binding.ivProfile, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            {
                                Log.e(TAG, "updateUI Photo load");
                                binding.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e)
                            {
                                Log.e(TAG, "updateUI Photo not load");
                                binding.progressBar.setVisibility(View.GONE);
                                binding.ivProfile.setImageResource(R.drawable.ic_place_holder_profile);
                            }
                        });
            }
            else
            {
                binding.progressBar.setVisibility(View.GONE);
                binding.ivProfile.setImageResource(R.drawable.ic_place_holder_profile);
            }
        }
        else
        {
            Log.e(TAG, "updateUI logout");
//            mStatusTextView.setText(R.string.signed_out);

            binding.btnSignUp.setVisibility(View.VISIBLE);
            binding.btnLogOut.setVisibility(View.GONE);
            binding.llWelcome.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.e(TAG, "onConnectionFailed() connectionResult:- " + connectionResult);
    }
}
