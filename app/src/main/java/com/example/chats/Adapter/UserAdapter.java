package com.example.chats.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chats.AESUtils;
import com.example.chats.Login;
import com.example.chats.MessageActivity;
import com.example.chats.Model.Chat;
import com.example.chats.Model.User;
import com.example.chats.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;
    private boolean ischat;
    String lastMessage, lastT, type, us;
    FirebaseUser uss;

    public UserAdapter(Context mContext, List<User> mUser, boolean ischat) {
        this.mUser = mUser;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent,false);
        uss = FirebaseAuth.getInstance().getCurrentUser();
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUser.get(position);
        holder.show_name.setText(user.getUsername());
        Glide.with(mContext).load(user.getImageURL()).into(holder.personAvatarR);
        holder.sendReq.setVisibility(View.GONE);
        holder.accReq.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
                new AcTrans.Builder(mContext).performSlideToLeft();
            }
        });

        if (ischat){
            lastMessage(user.getId(), holder.show_message, holder.timeLast, holder.notific);
        }else {
            holder.show_message.setVisibility(View.GONE);
        }

        if (ischat){
            if (user.getStatus().equals("online")){
                holder.img_on.setColorFilter(mContext.getResources().getColor(R.color.online));
            }else {
                holder.img_on.setColorFilter(mContext.getResources().getColor(R.color.offline));
            }
        }else {
            holder.img_on.setVisibility(View.GONE);
        }
    }

    private void lastMessage(String userid, TextView last_msg, TextView last_time, CircleImageView notific){
        lastMessage = "default";
        lastT = "11";

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){

                        if (chat.getSender().equals(userid) && chat.isIsseen()){
                            notific.setVisibility(View.GONE);
                        }

                        if (chat.getSender().equals(userid) && !chat.isIsseen()){
                            notific.setVisibility(View.VISIBLE);
                        }

                        lastMessage = chat.getMessage();
                        type = chat.getType();
                        lastT = chat.getTime();
                        us = chat.getSender();
                    }
                }


                if (type.equals("text")){
//                    Text
                    switch (lastMessage){
                        case "default":
                            last_msg.setText("No Message");
                            break;
                        default:
                            if (us.equals(uss.getUid())){
                                last_msg.setText(" You: "+lastMessage);
                            }else {
                                last_msg.setText(lastMessage);
                            }
                                last_time.setText(lastT);
                            break;
                    }

                    lastMessage = "default";

                }else if (type.equals("docs")){
//                    Text
                    switch (lastMessage){
                        case "default":
                            last_msg.setText("No Message");
                            break;
                        default:
                            if (us.equals(uss.getUid())){
                                last_msg.setText(" You:  Docs");
                            }else {
                                last_msg.setText("  Docs");
                            }
                            last_msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_insert_drive_file_24, 0, 0, 0);
                            last_time.setText(lastT);
                            break;
                    }

                    lastMessage = "default";

                }else if (type.equals("video")){
//                    Video
                    switch (lastMessage){
                        case "default":
                            last_msg.setText("No Message");
                            break;
                        default:
                            if (us.equals(uss.getUid())){
                                last_msg.setText(" You:  Video");
                            }else {
                                last_msg.setText("  Video");
                            }

                            last_msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_videocam_24, 0, 0, 0);
                            last_time.setText(lastT);
                            break;
                    }

                    lastMessage = "default";

                }else if (type.equals("location")){
//                    Video
                    switch (lastMessage){
                        case "default":
                            last_msg.setText("No Message");
                            break;
                        default:
                            if (us.equals(uss.getUid())){
                                last_msg.setText(" You:  Location");
                            }else {
                                last_msg.setText("  Location");
                            }

                            last_msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_gps_fixed_24, 0, 0, 0);
                            last_time.setText(lastT);
                            break;
                    }

                    lastMessage = "default";

                }else {
//                    Image

                    switch (lastMessage){
                        case "default":
                            last_msg.setText("No Message");
                            break;
                        default:
                            if (us.equals(uss.getUid())){
                                last_msg.setText(" You:  Photo");
                            }else {
                                last_msg.setText("  Photo");
                            }
                                last_msg.setText("  Photo");
                                last_msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_photo_24, 0, 0, 0);
                                last_time.setText(lastT);
                            break;
                    }

                    lastMessage = "default";
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
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
