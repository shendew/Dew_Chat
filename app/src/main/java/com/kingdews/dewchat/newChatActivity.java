package com.kingdews.dewchat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingdews.dewchat.adapters.contactRvAdapter;
import com.kingdews.dewchat.models.Contact;

import java.util.ArrayList;

public class newChatActivity extends AppCompatActivity {
    RecyclerView contactRv;
    ArrayList<Contact> arrayList=new ArrayList<>();
    ArrayList<String> systemList=new ArrayList<>();
    ProgressDialog progressDialog;
    ArrayList<Contact> filteredList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Collecting Data" );
        progressDialog.setMessage("Loading.");
        progressDialog.show();
        contactRv=findViewById(R.id.contactRv);
        contactRv.setHasFixedSize(true);


        //getSystemContactList();

        checkPermissions();

    }

    private void getSystemContactList() {

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                systemList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    String contact=ds.child("number").getValue(String.class);
                    systemList.add(contact);

                }
                if (!filteredList.isEmpty()){
                filteredList.clear();
                }
                Contact c=new Contact();
                c.setName("Add New Contact");
                c.setNumber("");
                filteredList.add(c);
                for (String sc:systemList){
                    for (int i=0;i < arrayList.size();i++) {
                        if (arrayList.get(i).getNumber().equals(sc)){
                            filteredList.add(arrayList.get(i));
                        }
                    }

                }
                contactRv.setLayoutManager(new LinearLayoutManager(newChatActivity.this));

                contactRvAdapter adapter=new contactRvAdapter(newChatActivity.this,filteredList);
                contactRv.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(newChatActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(newChatActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},100);

        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoadContact();
                }
            },1000);

        }
    }

    private void LoadContact() {

        Uri uri=ContactsContract.Contacts.CONTENT_URI;

        String sort= ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC";

        Cursor cursor=getContentResolver().query(
                uri, null,null,null,sort
        );

        if (cursor.getCount() > 0){

            while (cursor.moveToNext()){

                @SuppressLint("Range") String id= cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("display_name"));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor phoneCursor = getContentResolver().query(uriPhone, null, "contact_id =?", new String[]{id}, null);
                if (phoneCursor.moveToNext()) {
                    @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex("data1"));

                    Contact model = new Contact();
                    model.setName(name);
                    model.setNumber(verifyNumber(number));
                    this.arrayList.add(model);
                    phoneCursor.close();
                }




            }
            cursor.close();
        }

        getSystemContactList();





    }
    private String verifyNumber(String number) {

        String replaced = number,country;
        if (number.startsWith("(") || number.endsWith(")")){
            //replaced=number.substring(1,-1);
        }

        if (number.contains(" ")){
            replaced=replaced.replace(" ","");

        }
        else {
            replaced=number;
        }
        if (replaced.startsWith("0")){

            country=replaced.substring(1);
            country="+94"+country;
            //country=replaced.replace(replaced.substring(0,1),"+94");
        }
        else {
            country=replaced;
        }
        return country;

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoadContact();
                }
            },1);
        }else {
            Toast.makeText(newChatActivity.this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            checkPermissions();
        }
    }
}