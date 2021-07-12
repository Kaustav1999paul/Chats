package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chats.Model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView notifList;
    LinearLayoutManager layoutManager;
    FirebaseUser user;
    DatabaseReference friendsReqRef, userRef,friendRef;
    public static Context context;
    String saveCurrentDate;
    ImageView back;
    TextView messageNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Slidr.attach(this);
        notifList = findViewById(R.id.notifList);
        messageNo = findViewById(R.id.messageNo);
        notifList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        context = getApplicationContext();
        notifList.setLayoutManager(layoutManager);
        back = findViewById(R.id.back);
        user = FirebaseAuth.getInstance().getCurrentUser();
        friendsReqRef = FirebaseDatabase.getInstance().getReference("FriendRequests");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        friendRef = FirebaseDatabase.getInstance().getReference("Friends");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(NotificationsActivity.this).performSlideToRight();
            }
        });
        messageNo.setVisibility(View.VISIBLE);


        displayRequestNotifications();

    }

    private void displayRequestNotifications() {
        DatabaseReference fr = friendsReqRef.child(user.getUid());
        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(fr, Friends.class).build();

        FirebaseRecyclerAdapter<Friends, NotifVH> adapter = new FirebaseRecyclerAdapter<Friends, NotifVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotifVH holder, int position, @NonNull Friends model) {
                final String userIDs = getRef(position).getKey();
                fr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(userIDs)){
                            String request_type = dataSnapshot.child(userIDs).child("request_type").getValue().toString();

                            if (request_type.equals("received")){

                                userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            messageNo.setVisibility(View.GONE);
                                            final String userName = snapshot.child("username").getValue().toString();
                                            final String userEmail = snapshot.child("email").getValue().toString();
                                            final String userImage = snapshot.child("imageURL").getValue().toString();
                                            final String id = snapshot.child("id").getValue().toString();

                                            holder.notifMessage.setText(userName);
                                            Glide.with(context).load(userImage).into(holder.personAvatar);
                                            holder.accReq.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    acceptRequest(id);
                                                }
                                            });
                                        }else {
                                            messageNo.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }else {
                                holder.lay.getLayoutParams().height = 0;
                                holder.lay.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public NotifVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(NotificationsActivity.this).inflate(R.layout.notif_friend_request, parent, false);
                return new NotifVH(v);
            }
        };
        notifList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        new AcTrans.Builder(NotificationsActivity.this).performSlideToRight();
    }

    private void acceptRequest(String receiverId) {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        friendRef.child(user.getUid())
                .child(receiverId)
                .child("date")
                .setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            friendRef.child(receiverId)
                                    .child(user.getUid())
                                    .child("date")
                                    .setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                friendsReqRef.child(user.getUid())
                                                        .child(receiverId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){
                                                                    friendsReqRef.child(receiverId)
                                                                            .child(user.getUid())
                                                                            .removeValue();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public class NotifVH extends RecyclerView.ViewHolder{
        TextView notifMessage;
        ImageView personAvatar;
        FloatingActionButton accReq;
        RelativeLayout lay;


        public NotifVH(@NonNull View itemView) {
            super(itemView);
            accReq = itemView.findViewById(R.id.acceptButton);
            personAvatar = itemView.findViewById(R.id.personAvatar);
            notifMessage = itemView.findViewById(R.id.notifMessage);
            lay = itemView.findViewById(R.id.lay);
        }
    }
}