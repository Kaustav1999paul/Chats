package com.example.chats.Adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chats.Model.Contacts;
import com.example.chats.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    Activity activity;
    ArrayList<Contacts> arrayList;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");


    public ContactsAdapter(Activity activity, ArrayList<Contacts> arrayList){
        this.activity = activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {

        Contacts model = arrayList.get(position);

        String phoneNo = model.getNumber();
        phoneNo = phoneNo.replaceAll("\\s", "");

        holder.show_name.setText(model.getName());
        holder.show_message.setText(phoneNo);
        holder.notific.setVisibility(View.GONE);
        holder.accReq.setVisibility(View.GONE);
        holder.sendReq.setVisibility(View.GONE);
        holder.timeLast.setVisibility(View.GONE);
        holder.img_on.setVisibility(View.GONE);

       Query query = userRef.orderByChild("phno").equalTo(phoneNo);
        query.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (!snapshot.exists()){
                   holder.itemView.getLayoutParams().height = 0;
                   holder.itemView.setVisibility(View.GONE);
               }else {

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message, show_name, timeLast;
        public ImageView personAvatarR, sendReq;
        public CircleImageView img_on,notific;
        public Button accReq;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accReq = itemView.findViewById(R.id.accReq);
            show_message = itemView.findViewById(R.id.personHolderEmail);
            show_name = itemView.findViewById(R.id.personHolderName);
            personAvatarR = itemView.findViewById(R.id.personAvatar);
            timeLast = itemView.findViewById(R.id.timeLast);
            img_on = itemView.findViewById(R.id.img_on);
            notific = itemView.findViewById(R.id.notific);
            sendReq = itemView.findViewById(R.id.sendReq);
        }
    }
}
