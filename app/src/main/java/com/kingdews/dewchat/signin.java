package com.kingdews.dewchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class signin extends AppCompatActivity {
    Button next;
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //FirebaseApp.initializeApp(this);

        next=findViewById(R.id.btnNext);
        phone=findViewById(R.id.textPhone);
        phone.setInputType(InputType.TYPE_CLASS_PHONE);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumb=phone.getText().toString();
                if (phoneNumb.isEmpty()){
                    phone.setError("Required");
                }else {
                    Intent intent=new Intent(signin.this,VerifyActivity.class);
                    intent.putExtra("phoneNu","+94"+phoneNumb);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}