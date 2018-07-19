package com.example.visas.genielogiciel2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Contact_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;

import java.util.ArrayList;



public class FragmentGroups extends Fragment {

    private View view;
    private Groupe_DAO groupe_dao;
    private Contact_DAO contact_dao;
    private RecyclerView gRecyclerView;
    private RecyclerView.Adapter gAdapter;
    private RecyclerView.LayoutManager gLayoutManager;
    private FloatingActionButton fab;
    public static boolean onresu=false;

    public static ArrayList<Groupe> groupsList;

    public FragmentGroups() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.groups_fragment, container, false);

        gRecyclerView = view.findViewById(R.id.groups_recycler_view);

        groupsList=groupe_dao.selectionnerGroupes();

        gAdapter = new GroupsRecyclerAdapter(groupsList);

        fab = view.findViewById(R.id.fab_groups);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), NewGroupActivity.class);
                startActivity(intent);
            }
        });

        gLayoutManager = new LinearLayoutManager(getActivity());

        gRecyclerView.setHasFixedSize(true);
        gRecyclerView.setLayoutManager(gLayoutManager);
        gRecyclerView.setAdapter(gAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(onresu) {
            gAdapter = new GroupsRecyclerAdapter(groupe_dao.selectionnerGroupes());
            gRecyclerView.setAdapter(gAdapter);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contact_dao=new Contact_DAO(getContext());
        ArrayList<Contact> contacts=contact_dao.selectionnerTousLesContacts();
        ArrayList<Contact> or=new ArrayList<>(),mt=new ArrayList<>(),net=new ArrayList<>(),aut=new ArrayList<>();

        Groupe orange=new Groupe();
        orange.setGroupInitials("Ora");
        orange.setGroupName("Orange");
        Groupe mtn=new Groupe();
        mtn.setGroupInitials("MTN");
        mtn.setGroupName("MTN");
        Groupe nextell=new Groupe();
        nextell.setGroupInitials("Nex");
        nextell.setGroupName("Nextell");
        Groupe autre=new Groupe();
        autre.setGroupInitials("Aut");
        autre.setGroupName("Autre");
        for(Contact c: contacts){
            if(c.getOperateur().matches("Orange")){
                or.add(c);
            }
            if(c.getOperateur().matches("MTN")){
                mt.add(c);
            }
            if(c.getOperateur().matches("Nextell")){
                net.add(c);
            }if(c.getOperateur().matches("Autre")){
                aut.add(c);
            }

        }
        orange.setContacts(or);
        mtn.setContacts(mt);
        autre.setContacts(aut);
        nextell.setContacts(net);
        groupe_dao=new Groupe_DAO(getContext());
        if(or.size()>0)
            groupe_dao.enregisterGroupe(orange);
        if(mt.size()>0){
        groupe_dao.enregisterGroupe(mtn);
        }
        if(net.size()>0){
        groupe_dao.enregisterGroupe(nextell);
        }
        if(aut.size()>0){
        groupe_dao.enregisterGroupe(autre);
        }
        groupsList=groupe_dao.selectionnerGroupes();
    }
}
