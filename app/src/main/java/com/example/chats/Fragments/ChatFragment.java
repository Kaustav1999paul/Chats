package com.example.chats.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.chats.Adapter.UserAdapter;
import com.example.chats.Model.Chat;
import com.example.chats.Model.User;
import com.example.chats.Notifications.Token;
import com.example.chats.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    private UserAdapter userAdapter;
    private RecyclerView recentChatList;
    private List<User> mUser;
    FirebaseUser fuser;
    RelativeLayout loading;
    DatabaseReference reference;
    private List<String> userList;
    LinearLayoutManager layoutManager;
    PullRefreshLayout swip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        swip = view.findViewById(R.id.swip);
        recentChatList = view.findViewById(R.id.chatList);
        loading = view.findViewById(R.id.loading);
        recentChatList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recentChatList.setLayoutManager(layoutManager);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                userList.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(fuser.getUid())){
                        userList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(fuser.getUid())){
                        userList.add(chat.getSender());
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        swip.setColor(Color.parseColor("#0C89ED"));
        swip.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readChats();
                swip.setRefreshing(false);
            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);

    }
    @Override
    public void onStart() {
        super.onStart();
        recentChatList.setAdapter(userAdapter);
    }

    private void readChats() {
        mUser = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

//                    Display 1 user from chats
                    for (String id : userList){
                        assert user != null;
                        if (user.getId().equals(id)){
                            if (mUser.size() != 0){
                                int flag=0;
                                for(User u : mUser) {
                                    if (user.getId().equals(u.getId())){
                                        flag = 1;
                                        break;
                                    }
                                }
                                if(flag==0)
                                    mUser.add(0, user);
                            }else {
                                mUser.add(0, user);
                            }
                        }
                    }
                }
                loading.setVisibility(View.GONE);
                recentChatList.setVisibility(View.VISIBLE);
                userAdapter = new UserAdapter(getContext(), mUser, true);
                recentChatList.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}