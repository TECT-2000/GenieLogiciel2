package com.example.visas.genielogiciel2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Contact_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;

import java.util.ArrayList;

public class ManageGroupActivity extends AppCompatActivity {

    View view;
    ActionBar actionBar;
    private Groupe_DAO groupe_dao;
    private Contact_DAO contact_dao;
    RecyclerView recyclerView, dialogRecyclerView;
    RecyclerView.Adapter adapter, dialogAdapter;
    RecyclerView.LayoutManager layoutManager, dialogLayoutManager;

    ArrayList<Contact> arrayList, addContactsList;

    CircularTextView groupInitials;
    TextView nomGroupe;

    AlertDialog.Builder deleteGroupConfirmationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);

        groupe_dao=new Groupe_DAO(getApplicationContext());
        contact_dao=new Contact_DAO(getApplicationContext());
        view = LayoutInflater.from(this).inflate(R.layout.manage_group_action_bar, null);

        groupInitials = view.findViewById(R.id.manage_group_initials);
        nomGroupe = view.findViewById(R.id.manage_group_name);

        actionBar = getSupportActionBar();

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(view);

        getGroupMembers();


        Intent intent = getIntent();

        String groupInitials = intent.getStringExtra(GroupsRecyclerAdapter.GROUP_INITIALS);
        String groupName = intent.getStringExtra(GroupsRecyclerAdapter.GROUP_NAME);

        this.groupInitials.setText(groupInitials);
        this.nomGroupe.setText(groupName);

        recyclerView = (RecyclerView) findViewById(R.id.manage_group_recycler_view);

        adapter = new ManageGroupRecyclerAdapter(arrayList, false,nomGroupe.getText().toString());
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_contacts) {

            displayAddContactsDialog();
            return true;

        }
        else if(id == R.id.edit_group){

            displayEditGroupDialog();
            return true;

        }

        else if(id == R.id.delete_group){
            displayDeleteGroupDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //method to fill list of available contacts to add
    private void fillAddContactsList(){

        addContactsList = new ArrayList<>();
        addContactsList = contact_dao.selectionnerTousLesContacts();

    }

    //code to get group members
    private void getGroupMembers(){

            arrayList = new ArrayList<>();
            arrayList=contact_dao.selectionnerContactsGroupe(groupe_dao.selectionnerIdGroupe(getIntent().getStringExtra(GroupsRecyclerAdapter.GROUP_NAME)));

    }

    private void displayAddContactsDialog(){
        fillAddContactsList();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_contacts_dialog);

        dialogRecyclerView = dialog.findViewById(R.id.add_contacts_dialog_recycler_view);

        dialogAdapter = new ManageGroupRecyclerAdapter(addContactsList, true,this.nomGroupe.getText().toString());
        dialogLayoutManager = new LinearLayoutManager(this);
        dialogLayoutManager.setAutoMeasureEnabled(false);

        dialogRecyclerView.setHasFixedSize(true);
        dialogRecyclerView.setLayoutManager(dialogLayoutManager);
        dialogRecyclerView.setAdapter(dialogAdapter);

        Button cancelButton = dialog.findViewById(R.id.add_contacts_cancel_btn);
        Button saveButton = dialog.findViewById(R.id.add_contacts_save_btn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to add contacts
                ArrayList<Contact> contacts=((ManageGroupRecyclerAdapter)dialogRecyclerView.getAdapter()).getCheckedContacts();

                ajouterContacts(nomGroupe.getText().toString(),contacts);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void displayEditGroupDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modify_group_dialog);

        Button cancelButton = dialog.findViewById(R.id.modify_group_cancel_btn);
        Button saveButton = dialog.findViewById(R.id.modify_group_save_btn);

        final EditText groupName = dialog.findViewById(R.id.modify_group_name);
        final EditText initials = dialog.findViewById(R.id.modify_group_initials);

        System.out.println(this.nomGroupe.getText().toString());
        groupName.setText(this.nomGroupe.getText());
        initials.setText(this.groupInitials.getText());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to add contacts


                Groupe groupe=new Groupe();
                groupe.setGroupInitials(initials.getText().toString());
                groupe.setGroupName(groupName.getText().toString());
                System.out.println(groupName.getText().toString());
                modifierGroupe(groupe);
                nomGroupe.setText(groupName.getText().toString());
                groupInitials.setText(initials.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void displayDeleteGroupDialog(){

        deleteGroupConfirmationDialog = new AlertDialog.Builder(this,
                R.style.ThemeOverlay_AppCompat_Dialog_Alert);


        deleteGroupConfirmationDialog.setMessage("Voulez vous vraiment supprimer le groupe "
                +nomGroupe.getText()+"?")
                .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Code to delete contact from group
                        Groupe groupe= new Groupe();
                        groupe.setGroupName(nomGroupe.getText().toString());
                        boolean rep=deleteGroupe(groupe);
                        if(rep) {
                            Toast.makeText(getApplicationContext(), "Groupe supprimé", Toast.LENGTH_LONG).show();
                            FragmentGroups.onresu=true;
                        }
                        finish();

                    }
                })
                .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private boolean deleteGroupe(Groupe groupe){

        long id=groupe_dao.supprimerGroupe(groupe.getGroupName());

        //Code to delete groupe
        return (id!=0);
    }
    private boolean modifierGroupe(Groupe groupe){

        long id=groupe_dao.modifierNomGroupe(nomGroupe.getText().toString(),groupe.getGroupName(),groupe.getGroupInitials());

        if(id!=0){
            Toast.makeText(getApplicationContext(),"modification réussie",Toast.LENGTH_LONG).show();
            FragmentGroups.onresu=true;
            adapter.notifyDataSetChanged();
        }
        return (id!=0);
    }
    private boolean ajouterContacts(String nomGroupe,ArrayList<Contact> contacts){

        int id=groupe_dao.selectionnerIdGroupe(nomGroupe);
         long rep=groupe_dao.entregistrerContactsGroupe(contacts,id);
         if(rep!=0) {
             arrayList.addAll(contacts);
             Toast.makeText(getApplicationContext(),"contacts ajoutés",Toast.LENGTH_LONG).show();
             FragmentGroups.onresu=true;
             adapter.notifyDataSetChanged();
         }
         return rep!=0;
    }
}
