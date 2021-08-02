package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.example.chats.Model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView friendsList;
    RelativeLayout loading;
    PullRefreshLayout swip;
    LinearLayoutManager layoutManager;
    TextView messageNo;
    FirebaseUser user;
    ImageView back;
    ProgressBar progress;
    DatabaseReference friendsRef, userRef;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Slidr.attach(this);

        swip = findViewById(R.id.swip);
        friendsList = findViewById(R.id.friendsList);
        messageNo = findViewById(R.id.messageNo);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(FriendsActivity.this).performSlideToRight();
            }
        });
        progress = findViewById(R.id.progress);
        loading = findViewById(R.id.loading);
        friendsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        context = getApplicationContext();
        friendsList.setLayoutManager(layoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        friendsRef = FirebaseDatabase.getInstance().getReference("Friends").child(user.getUid());
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    loading.setVisibility(View.GONE);
                    messageNo.setVisibility(View.VISIBLE);
                }
                else
                    messageNo.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        displayAllFriends();
        swip.setColor(Color.parseColor("#0C89ED"));
        swip.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayAllFriends();
                swip.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        new AcTrans.Builder(FriendsActivity.this).performSlideToRight();
    }

    private void displayAllFriends() {

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendsRef, Friends.class).build();


        FirebaseRecyclerAdapter<Friends, FriendsVH> adapter = new FirebaseRecyclerAdapter<Friends, FriendsVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsVH holder, int position, @NonNull Friends model) {

                friendsList.setVisibility(View.VISIBLE);
                final String userIDs = getRef(position).getKey();

                userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            holder.divider.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            holder.sendReq.setVisibility(View.GONE);
                            holder.accReq.setVisibility(View.GONE);

                            final String userName = dataSnapshot.child("username").getValue().toString();
                            final String userEmail = dataSnapshot.child("email").getValue().toString();
                            final String userImage = dataSnapshot.child("imageURL").getValue().toString();
                            final String id = dataSnapshot.child("id").getValue().toString();

                            holder.nameUser.setText(userName);
                            Glide.with(context).load(userImage).into(holder.img);
                            holder.statusUser.setText(userEmail);
                            holder.statusOnOff.setVisibility(View.GONE);

                            if (id.equals(user.getUid())){
                                holder.itemView.getLayoutParams().height = 0;
                                holder.itemView.setVisibility(View.GONE);
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(FriendsActivity.this, MessageActivity.class);
                                    intent.putExtra("userid", id);
                                    startActivity(intent);
                                    new AcTrans.Builder(FriendsActivity.this).performSlideToLeft();
                                    finish();
                                }
                            });
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(FriendsActivity.this).inflate(R.layout.user_item, parent, false);
                return new FriendsVH(v);
            }
        };
        friendsList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        displayAllFriends();
    }

    public class FriendsVH extends RecyclerView.ViewHolder{
        TextView nameUser, statusUser, timeLast;
        ImageView img, sendReq;
        CircleImageView statusOnOff;
        Button accReq;
        View divider;

        public FriendsVH(@NonNull View itemView) {
            super(itemView);
            accReq = itemView.findViewById(R.id.accReq);
            img = itemView.findViewById(R.id.personAvatar);
            divider = itemView.findViewById(R.id.divider);
            nameUser = itemView.findViewById(R.id.personHolderName);
            statusUser = itemView.findViewById(R.id.personHolderEmail);
            timeLast = itemView.findViewById(R.id.timeLast);
            sendReq = itemView.findViewById(R.id.sendReq);
            statusOnOff = itemView.findViewById(R.id.img_on);
        }
    }

}