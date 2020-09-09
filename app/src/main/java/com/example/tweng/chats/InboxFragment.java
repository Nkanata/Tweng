package com.example.tweng.chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tweng.R;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment implements ChatsAdapter.OnChatListener {
    private List<Chats> chatsList;
    private ChatsAdapter chatsAdapter;
    private RecyclerView recyclerView;

    private InboxViewModel mViewModel;

    public static InboxFragment newInstance() {
        return new InboxFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inbox_fragment, container, false);
        recyclerView = view.findViewById(R.id.chats_recycler);
        chatsList = new ArrayList<>();
        chatsAdapter = new ChatsAdapter(getContext(), chatsList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatsAdapter);

        //fecth();
        return view;
    }

    private void fecth() {
        Chats chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chats = new Chats("@Enos", "I have done just a few", "Just Now", "");
        chatsList.add(chats);
        chatsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(InboxViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onChatClickListener(int position) {
        chatsList.get(position);
        Intent intent = new Intent(getContext(), MessageList.class);
        startActivity(intent);
    }
}
