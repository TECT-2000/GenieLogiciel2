package com.example.visas.genielogiciel2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Contact_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;
import com.venus.app.IO.Asyncable;
import com.venus.app.Utils.Utils;

import java.sql.SQLOutput;
import java.util.ArrayList;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by visas on 5/10/18.
 */

public class FragmentContacts extends Fragment {

    View view;
    public static boolean onresu=false;
    private RecyclerView cRecyclerView;
    private RecyclerView.Adapter cAdapter;
    private RecyclerView.LayoutManager cLayoutManager;
    private FloatingActionButton fab;
    private Contact_DAO contact_dao;
    private Dialog newContactDialog;
    private Button cancelButton, saveButton;
    private EditText nameField, numberField;
    private ArrayList<Contact> contactsList;


    public FragmentContacts() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.contacts_fragment, container, false);


       cRecyclerView = view.findViewById(R.id.contacts_recycler_view);

        cAdapter = new ContactsRecyclerAdapter(getContext(),contactsList);

        cLayoutManager = new LinearLayoutManager(getActivity());

        fab = view.findViewById(R.id.fab_contacts);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildNewContactDialog();
            }
        });

        cRecyclerView.setHasFixedSize(true);
        cRecyclerView.setLayoutManager(cLayoutManager);
        cRecyclerView.setAdapter(cAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contact_dao=new Contact_DAO(getContext());
        contactsList=contact_dao.selectionnerTousLesContacts();

    }


    @Override
    public void onResume() {
        super.onResume();
        if(onresu){

            cAdapter = new ContactsRecyclerAdapter(getContext(),contact_dao.selectionnerTousLesContacts());
            cRecyclerView.setAdapter(cAdapter);

        }

    }
    private void buildNewContactDialog(){

        newContactDialog = new Dialog(getContext());
        newContactDialog.setContentView(R.layout.new_contact_dialog);

        TextView heading = newContactDialog.findViewById(R.id.contact_dialog_heading);
        final RadioGroup operateurs=newContactDialog.findViewById(R.id.choix_operateur);
        final RadioButton[] radioSelected = new RadioButton[1];

        final TextView nom= newContactDialog.findViewById(R.id.new_contact_name);
        final TextView numero=newContactDialog.findViewById(R.id.new_contact_number);
        saveButton = newContactDialog.findViewById(R.id.new_contact_save_btn);
        cancelButton = newContactDialog.findViewById(R.id.new_contact_cancel_btn);

        heading.setText("Nouveau Contact");
        nameField = newContactDialog.findViewById(R.id.new_contact_name);
        numberField = newContactDialog.findViewById(R.id.new_contact_number);

        operateurs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                radioSelected[0] =(RadioButton) group.findViewById(operateurs.getCheckedRadioButtonId());
                Toast.makeText(getContext(),"radio selected : "+ radioSelected[0].getText().toString(),Toast.LENGTH_LONG).show();

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioSelected[0]!=null) {

                    Contact contact = new Contact(nom.getText().toString(), numero.getText().toString(), radioSelected[0].getText().toString());
                    if (saveContact(contact)) {
                        Toast.makeText(getActivity(), "Contact " + contact.getContactName() + " Enregistré avec succès", LENGTH_LONG).show();
                        Groupe_DAO groupe_dao=new Groupe_DAO(getContext());
                        if(groupe_dao.selectionnerIdGroupe(contact.getOperateur())!=0){

                            groupe_dao.enregistrerContactGroupe(contact,groupe_dao.selectionnerIdGroupe(contact.getOperateur()));
                        }
                    }
                    newContactDialog.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newContactDialog.dismiss();
            }
        });

        newContactDialog.show();
    }

    private boolean saveContact(Contact contact){

        long id=contact_dao.enregisterContact(contact);

        //Code to save contact.
        if(id!=0) {
            contactsList.add(contact);
            cAdapter.notifyDataSetChanged();
        }
        //on ajoute le contact à son groupe d'opérateur
        Groupe_DAO groupe_dao=new Groupe_DAO(getContext());

                if(groupe_dao.selectionnerGroupe(contact.getOperateur())!=null && groupe_dao.selectionnerGroupe(contact.getOperateur()).getContacts()!=null)
                    groupe_dao.selectionnerGroupe(contact.getOperateur()).getContacts().add(contact);
                else{
                    Groupe groupe=new Groupe();
                    groupe.setGroupName(contact.getOperateur());
                    groupe.setGroupInitials(contact.getOperateur().substring(0,3));

                    ArrayList<Contact> contacts=new ArrayList<>();
                    contacts.add(contact);

                    groupe.setContacts(contacts);

                    long identifiant=groupe_dao.enregisterGroupe(groupe);
                    Toast.makeText(getContext(),"retour groupe : "+identifiant,Toast.LENGTH_LONG).show();
                }



        return (id!=0);
    }

}
