package com.kingdews.dewchat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingdews.dewchat.adapters.ChatAdapter;
import com.skydoves.elasticviews.ElasticImageView;

import java.util.ArrayList;

public class ChatPageActivity extends AppCompatActivity {

    EditText chatEdit;
    ElasticImageView chatSend;
    FirebaseDatabase database;
    RecyclerView chatRv;
    DatabaseReference mRef;
    FirebaseUser user;
    String id;
    String mynumber;
    int key;
    ArrayList<String> chatHistory=new ArrayList<>();
    ImageView toolbarImage;
    TextView toolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        String number= getIntent().getStringExtra("number");
        String photo= getIntent().getStringExtra("photo");
        String name= getIntent().getStringExtra("name");
        toolbarImage=findViewById(R.id.pic);
        toolbarTitle=findViewById(R.id.head);


        chatRv=findViewById(R.id.chatRV);
        chatEdit=findViewById(R.id.chatInput);
        chatSend=findViewById(R.id.chatSend);
        chatRv.setHasFixedSize(true);

        user= FirebaseAuth.getInstance().getCurrentUser();
        mynumber=user.getPhoneNumber();
        database=FirebaseDatabase.getInstance();
        mRef=database.getReference().child(user.getUid()).child("Chats");

        Glide.with(ChatPageActivity.this).load(photo).circleCrop().into(toolbarImage);
        toolbarTitle.setText(name);


        DatabaseReference df=FirebaseDatabase.getInstance().getReference();

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds:snapshot.getChildren()){
                    String serNo=ds.child("number").getValue(String.class);

                    if (serNo.equals(number)){

                        id=ds.getKey();
                        Toast.makeText(ChatPageActivity.this, "Success", Toast.LENGTH_SHORT).show();


                        mRef.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                key= Math.toIntExact(snapshot.getChildrenCount() + 1);
                                if (chatHistory.size()!=0){
                                    chatHistory.clear();
                                }
                                for (DataSnapshot ds:snapshot.getChildren()){
                                        chatHistory.add(ds.getValue(String.class));
                                }
                                getChat();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatPageActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
            }
        });





        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=chatEdit.getText().toString();

                if (text.isEmpty()){

                }else {
                    mRef.child(id).child(String.valueOf(key)).setValue(mynumber+"/"+text).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChatPageActivity.this, "msg sent", Toast.LENGTH_SHORT).show();
                                chatEdit.setText("");
                            }else {
                                Toast.makeText(ChatPageActivity.this, "msg sent faild", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child(id).child("Chats").child(user.getUid()).child(String.valueOf(key)).setValue(mynumber+"/"+text).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });


                }
            }
        });



    }

    private void getChat() {
        chatRv.setLayoutManager(new LinearLayoutManager(ChatPageActivity.this));
        ChatAdapter chatAdapter=new ChatAdapter(ChatPageActivity.this,chatHistory,mynumber);
        chatRv.setAdapter(chatAdapter);

    }
}