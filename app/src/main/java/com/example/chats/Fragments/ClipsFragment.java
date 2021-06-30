package com.example.chats.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chats.Adapter.ClipAdapter;
import com.example.chats.Model.Clips;
import com.example.chats.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class ClipsFragment extends Fragment {

    public ClipsFragment() {
        // Required empty public constructor
    }

    ViewPager2 clips;
    ClipAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clips, container, false);
        clips = view.findViewById(R.id.clips);

        FirebaseRecyclerOptions<Clips> options = new FirebaseRecyclerOptions.Builder<Clips>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Clips"), Clips.class)
                .build();

        adapter = new ClipAdapter(options, getActivity().getApplicationContext());
        clips.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}