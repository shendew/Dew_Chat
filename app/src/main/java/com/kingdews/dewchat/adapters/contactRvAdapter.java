package com.kingdews.dewchat.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingdews.dewchat.ChatPageActivity;
import com.kingdews.dewchat.R;
import com.kingdews.dewchat.models.Contact;

import java.util.ArrayList;

public class contactRvAdapter extends RecyclerView.Adapter<contactRvAdapter.ViewHolder> {
    Activity activity;
    ArrayList<Contact> arrayList;

    public contactRvAdapter(Activity activity, ArrayList<Contact> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.contact_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact=arrayList.get(position);
        String number=contact.getNumber();
        holder.name.setText(contact.getName());
        holder.number.setText(number);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String verified=verifyNumber(number);

                Intent intent=new Intent(activity, ChatPageActivity.class);
                intent.putExtra("number",verified);
                activity.startActivity(intent);

            }
        });

    }

    private String verifyNumber(String number) {

        String replaced = number,country;
        if (number.startsWith("(") || number.endsWith(")")){
            replaced=number.substring(1,-1);
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
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.itemNa);
            number=itemView.findViewById(R.id.itemNo);
        }
    }
}
