package com.example.chats.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.example.chats.MessageActivity;
import com.example.chats.Model.Friends;
import com.example.chats.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;

public class FriendsFragment extends Fragment {

    public FriendsFragment() {
        // Required empty public constructor
    }

    private RecyclerView friendsList;
    RelativeLayout loading;
    PullRefreshLayout swip;
    LinearLayoutManager layoutManager;
    TextView messageNo;
    FirebaseUser user;
    DatabaseReference friendsRef, userRef;
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        swip = view.findViewById(R.id.swip);
        friendsList = view.findViewById(R.id.friendsList);
        messageNo = view.findViewById(R.id.messageNo);
        loading = view.findViewById(R.id.loading);
        friendsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        context = getActivity().getApplicationContext();
        friendsList.setLayoutManager(layoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        swip.setColor(Color.parseColor("#0C89ED"));
        swip.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayAllFriends();
                swip.setRefreshing(false);
            }
        });
        friendsRef = FirebaseDatabase.getInstance().getReference("Friends").child(user.getUid());
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
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



        return view;
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


                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), MessageActivity.class);
                                    intent.putExtra("userid", id);
                                    startActivity(intent);
                                    new AcTrans.Builder(getContext()).performSlideToLeft();
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
                View v = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
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