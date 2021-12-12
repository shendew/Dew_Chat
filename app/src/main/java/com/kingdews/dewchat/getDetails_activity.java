package com.kingdews.dewchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class getDetails_activity extends AppCompatActivity {
    private static final int PICK_IMAGE = 10;
    ImageView pic;
    EditText Name;
    Button next;
    FirebaseUser user;
    Uri local_url;
    ConstraintLayout coldata,settingup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details);
        pic=findViewById(R.id.image);
        Name=findViewById(R.id.editName);
        next=findViewById(R.id.btnNext);
        user= FirebaseAuth.getInstance().getCurrentUser();
        coldata=findViewById(R.id.coldata);
        settingup=findViewById(R.id.settingup);


        Glide.with(this)
                .load(R.drawable.dummy)
                .circleCrop()
                .into(pic);

        pic.setOnClickListener(view -> {

                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);


        });

        next.setOnClickListener(view -> {
            String uname=Name.getText().toString();
            if (uname.isEmpty()){
                Name.setError("Required");
            }else {
                coldata.setVisibility(View.GONE);
                settingup.setVisibility(View.VISIBLE);
                getUploadUrl(uname);




            }
        });
    }

    private void getUploadUrl(String uname) {

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("UserImages").child(user.getUid()).child("profilePhoto.jpg");


        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(
                    local_url);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bmp = BitmapFactory.decodeStream(imageStream);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 20, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
            stream = null;
        } catch (IOException e) {

            e.printStackTrace();
        }



        UploadTask uploadTask= storageRef.putBytes(byteArray);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        updateData(uri,uname);
                    }
                });

            }
        });


    }

    private void updateData(Uri url,String uname) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(uname)
                .setPhotoUri(url)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(getDetails_activity.this,home.class));
                    finish();
                }else {
                    Toast.makeText(getDetails_activity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseDatabase.getInstance().getReference(user.getUid())
        .child("name").setValue(uname).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(getDetails_activity.this,getDetails_activity.class));
                    finish();
                }else {
                    Toast.makeText(getDetails_activity.this, "Upload Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        FirebaseDatabase.getInstance().getReference(user.getUid())
        .child("photo").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(getDetails_activity.this,getDetails_activity.class));
                    finish();
                }else {
                    Toast.makeText(getDetails_activity.this, "Upload Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            local_url = data.getData();

            Glide.with(this)
                    .load(local_url)
                    .circleCrop()
                    .into(pic);
        }
    }

}