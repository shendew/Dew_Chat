package com.kingdews.dewchat.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kingdews.dewchat.ChatPageActivity;
import com.kingdews.dewchat.R;
import com.kingdews.dewchat.models.HomeChat;

import java.util.ArrayList;

public class homeChatAdapter extends RecyclerView.Adapter<homeChatAdapter.ViewHolder> {

    Activity activity;
    ArrayList<HomeChat> chats;

    public homeChatAdapter(Activity activity, ArrayList<HomeChat> chats) {
        this.activity = activity;
        this.chats = chats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.home_chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Toast.makeText(activity, ""+chats.get(position), Toast.LENGTH_SHORT).show();
        holder.text.setText(chats.get(position).getName());
        Glide.with(activity).load(chats.get(position).getPhoto()).circleCrop().into(holder.image);
        holder.recent.setText(chats.get(position).getRecentmsg());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, ChatPageActivity.class);
                intent.putExtra("number",chats.get(position).getNumber());
                intent.putExtra("photo",chats.get(position).getPhoto());
                intent.putExtra("name",chats.get(position).getName());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text,recent;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text=itemView.findViewById(R.id.chat);
            recent=itemView.findViewById(R.id.recentChat);
            image=itemView.findViewById(R.id.image);
        }
    }
}
