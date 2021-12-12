package com.kingdews.dewchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kingdews.dewchat.frags.ChatFrag;
import com.kingdews.dewchat.frags.StoryFrag;
import com.skydoves.elasticviews.ElasticImageView;

public class home extends AppCompatActivity {

    FloatingActionButton add;
    ElasticImageView chat,story;
    TextView toolbarTitle;
    ElasticImageView toolbarsearch,toolbarmore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        add=findViewById(R.id.float_add);
        chat=findViewById(R.id.btn_chat);
        story=findViewById(R.id.btn_story);
        toolbarTitle=findViewById(R.id.head);
        toolbarsearch=findViewById(R.id.search);
        toolbarmore=findViewById(R.id.more);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.frameLayout, new ChatFrag())
                    .commit();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(home.this,newChatActivity.class));
                Intent intent=new Intent(home.this, newChatActivity.class);
                startActivity(intent);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragManager(new ChatFrag());
                toolbarTitle.setText("Dew Chat");
            }
        });
        story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragManager(new StoryFrag());
                toolbarTitle.setText("Stories");
            }
        });


    }

    private void FragManager(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}