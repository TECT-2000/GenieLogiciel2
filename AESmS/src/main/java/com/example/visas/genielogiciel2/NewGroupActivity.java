package com.example.visas.genielogiciel2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Contact_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;
import com.venus.app.IO.Asyncable;
import com.venus.app.IO.IO.FetchArray;
import com.venus.app.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NewGroupActivity extends AppCompatActivity implements FetchArray{

    View view;
    private Groupe_DAO groupe_dao;
    private Contact_DAO contact_dao;
    RecyclerView recyclerView;
    RecyclerView.Adapter<ManageGroupRecyclerAdapter.ContactsViewHolder> adapter;
    RecyclerView.LayoutManager layoutManager;
    ActionBar actionBar;
    EditText groupNameEntry, groupInitialsEntry;
    Button cancelButton, saveButton;
    public static Context tempContext;
    ArrayList<Contact> contactsList;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
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
                for(Contact c : contacts){
                    if(c.getOperateur().matches("Autre")){
                        contact_dao.enregisterContact(c);
                    }
                }
                FragmentContacts.onresu=true;
                groupe.setContacts(contacts);
                saveNewGroup(groupe);
            }
        });
        tempContext = this;

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fillContactsList(){

        contactsList = new ArrayList<>();
        contactsList=contact_dao.selectionnerTousLesContacts();
        new Thread(new Runnable() {

            @Override
            public void run() {

                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
                    String [] permissions={Manifest.permission.READ_CONTACTS};
                    requestPermissions(permissions,1);
                }
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){

                   new ChargementFragment().newInstance("",contactsList).show(getSupportFragmentManager(),"");

                }
            }
        }).start();
    }

    @Override
    public void fetchOnlineResult(ArrayList<Contact> result) {
        contactsList.addAll(result);
    }


    private boolean saveNewGroup(Groupe groupe) {

        long id = groupe_dao.enregisterGroupe(groupe);

        if (id != 0) {

            Toast.makeText(getApplicationContext(), "Groupe Enregistré avec succès", Toast.LENGTH_LONG).show();
            FragmentGroups.onresu = true;
            finish();

        }
        return (id != 0);
    }


}

