package com.example.visas.genielogiciel2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.visas.genielogiciel2.Model.DAO.Message_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Message;

import java.util.ArrayList;

/**
 * Created by visas on 5/10/18.
 */

public class FragmentMessages extends Fragment {

    private View view;
    static boolean onresu=false;
    private Activity activity;
    private Message_DAO message_dao;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Message> messagesDataProviderList;


    public FragmentMessages() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.messages_fragment, container, false);


        mRecyclerView = view.findViewById(R.id.messages_recycler_view);

        mAdapter = new MessagesRecyclerAdapter(view.getContext(), messagesDataProviderList);


        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_messages);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), NewMessageActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message_dao=new Message_DAO(getContext());
        messagesDataProviderList=message_dao.selectionnerMessages();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onResume() {
        super.onResume();
        if(onresu){

            mAdapter = new MessagesRecyclerAdapter(view.getContext(), message_dao.selectionnerMessages());
            mRecyclerView.setAdapter(mAdapter);

        }

    }
}
