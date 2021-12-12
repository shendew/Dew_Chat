package com.kingdews.dewchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String verificationcodeBySystem;
    Button verify;
    EditText codebyuser;
    LinearLayout verifing,loading;
    TextView loadmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        mAuth=FirebaseAuth.getInstance();
        verify=findViewById(R.id.btnVerify);
        codebyuser=findViewById(R.id.textCode);
        codebyuser.setInputType(InputType.TYPE_CLASS_NUMBER);
        verifing=findViewById(R.id.verifing);
        loading=findViewById(R.id.loading);
        loadmsg=findViewById(R.id.loadmsg);

        String phoneNu=getIntent().getStringExtra("phoneNu");
        if (!phoneNu.isEmpty()){
            PhoneAuthActivity(phoneNu);
        }

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String catchedCode=codebyuser.getText().toString();
                if (catchedCode.isEmpty() || catchedCode.length()<6){
                    codebyuser.setError("Required");
                }
                else {
                    verifyCode(catchedCode);
                }
            }
        });

    }

    private void PhoneAuthActivity(String phoneNu) {


        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code=phoneAuthCredential.getSmsCode();
                if (code != null){
                    verifyCode(code);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(VerifyActivity.this, ""+e.getMessage().toString(), Toast.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", e.getMessage());
                clipboard.setPrimaryClip(clip);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationcodeBySystem=s;
                loading.setVisibility(View.INVISIBLE);
                verifing.setVisibility(View.VISIBLE);
                Toast.makeText(VerifyActivity.this, "Code Sent Successfully", Toast.LENGTH_SHORT).show();
            }
        };


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNu)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private void verifyCode(String code) {

        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationcodeBySystem,code);
        verifing.setVisibility(View.INVISIBLE);
        loadmsg.setText("Verifying");
        signinWithCredential(credential);
    }

    private void signinWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    CreateDatabase(task.getResult().getUser().getUid(),task.getResult().getUser().getPhoneNumber());

                }else {
                    Toast.makeText(VerifyActivity.this, "Some Errors", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CreateDatabase(String uid, String phoneNumber) {
        DatabaseReference dr= FirebaseDatabase.getInstance().getReference(uid);
        dr.child("number").setValue(phoneNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(VerifyActivity.this,getDetails_activity.class));
                    finish();
                }else {
                    Toast.makeText(VerifyActivity.this, "Upload Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}