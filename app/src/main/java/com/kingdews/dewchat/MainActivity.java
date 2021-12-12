package com.kingdews.dewchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser fU= FirebaseAuth.getInstance().getCurrentUser();
                if (fU == null){
                    startActivity(new Intent(MainActivity.this,signin.class));
                    finish();
                }else {
                    startActivity(new Intent(MainActivity.this,home.class));
                    finish();
                }


            }
        },3000);
    }
}