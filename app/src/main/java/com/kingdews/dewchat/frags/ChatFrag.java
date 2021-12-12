package com.kingdews.dewchat.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingdews.dewchat.R;
import com.kingdews.dewchat.adapters.homeChatAdapter;
import com.kingdews.dewchat.models.HomeChat;

import java.util.ArrayList;


public class ChatFrag extends Fragment {

    RecyclerView chatRV;
    FirebaseUser user;
    ArrayList<HomeChat> chats;
    View view;
    homeChatAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatRV=view.findViewById(R.id.chatRV);
        chatRV.setHasFixedSize(true);
        user= FirebaseAuth.getInstance().getCurrentUser();



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getData() {
        chats=new ArrayList<>();
        DatabaseReference dr=FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Chats");
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Toast.makeText(getActivity(), "first"+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                ArrayList<String> arrayList=new ArrayList<>();
                for (DataSnapshot ds:snapshot.getChildren()){
                    String key=ds.getKey();
                    FirebaseDatabase.getInstance().getReference().child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            Toast.makeText(getActivity(), "sec"+snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                            String name=snapshot.child("name").getValue(String.class);
                            String photo=snapshot.child("photo").getValue(String.class);
                            String number=snapshot.child("number").getValue(String.class);
                            arrayList.add(name);
                            addChat(name,photo,number);




                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "sec faild", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                chatRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
                //chats.add("hellooooo");


                adapter=new homeChatAdapter(getActivity(),chats);
//                Toast.makeText(getActivity(), ""+arrayList.size(), Toast.LENGTH_SHORT).show();
                chatRV.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "first faild", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addChat(String name, String photo, String number) {
        HomeChat chat=new HomeChat();
        chat.setName(name);
        chat.setPhoto(photo);
        chat.setRecentmsg("hello ");
        chat.setNumber(number);
        chats.add(chat);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        user= FirebaseAuth.getInstance().getCurrentUser();
        getData();
    }
}