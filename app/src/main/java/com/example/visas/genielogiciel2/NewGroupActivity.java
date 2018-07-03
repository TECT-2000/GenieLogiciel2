package com.example.visas.genielogiciel2;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Contact_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;

import java.util.ArrayList;

public class NewGroupActivity extends AppCompatActivity {

    View view;
    private Groupe_DAO groupe_dao;
    private Contact_DAO contact_dao;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ActionBar actionBar;

    EditText groupNameEntry, groupInitialsEntry;
    Button cancelButton, saveButton;

    ArrayList<Contact> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        groupInitialsEntry=(EditText)findViewById(R.id.new_group_initials);
        groupNameEntry= (EditText)findViewById(R.id.new_group_name);

        groupe_dao=new Groupe_DAO(getApplicationContext());
        contact_dao=new Contact_DAO(getApplicationContext());
        view = LayoutInflater.from(this).inflate(R.layout.new_group_action_bar, null);

        actionBar = getSupportActionBar();

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(view);

        fillContactsList();
        recyclerView = (RecyclerView) findViewById(R.id.new_group_recycler_view);
        adapter = new ManageGroupRecyclerAdapter(contactsList, true,"");
        layoutManager = new LinearLayoutManager(this);
        cancelButton = (Button)findViewById(R.id.new_group_cancel_btn);
        saveButton = (Button)findViewById(R.id.new_group_save_btn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Groupe groupe =new Groupe();
                groupe.setGroupName(groupNameEntry.getText().toString());
                groupe.setGroupInitials(groupInitialsEntry.getText().toString());
                ArrayList<Contact> contacts=((ManageGroupRecyclerAdapter)recyclerView.getAdapter()).getCheckedContacts();
                groupe.setContacts(contacts);
                saveNewGroup(groupe);
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }


    private void fillContactsList(){

        contactsList = new ArrayList<>();
        contactsList=contact_dao.selectionnerTousLesContacts();

    }

    private boolean saveNewGroup(Groupe groupe){

        long id=groupe_dao.enregisterGroupe(groupe);

        if(id!=0){

            Toast.makeText(getApplicationContext(),"Groupe Enregistré avec succès",Toast.LENGTH_LONG).show();
            FragmentGroups.onresu=true;
            finish();

        }
        return (id!=0);
    }
}
