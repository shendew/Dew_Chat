package com.kingdews.dewchat.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingdews.dewchat.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    Activity activity;
    ArrayList<String> arrayList;
    String mynumber;

    public ChatAdapter(Activity activity, ArrayList<String> arrayList,String mynumber) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.mynumber=mynumber;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String msg= arrayList.get(position);

        if (msg.substring(0,12).equals(mynumber)){
            holder.received.setVisibility(View.INVISIBLE);
            holder.send.setVisibility(View.VISIBLE);
            holder.send.setText(msg.substring(13));
        }else {
            holder.send.setVisibility(View.INVISIBLE);
            holder.received.setVisibility(View.VISIBLE);
            holder.received.setText(msg.substring(13));
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView received;
        TextView send;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            received=itemView.findViewById(R.id.received);
            send=itemView.findViewById(R.id.send);
        }
    }
}
