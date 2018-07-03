package com.example.visas.genielogiciel2;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Contact_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;

import java.util.ArrayList;

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
    private Dialog newContactDialog, editContactDialog;
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

        cAdapter = new ContactsRecyclerAdapter(contactsList);

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

            cAdapter = new ContactsRecyclerAdapter(contact_dao.selectionnerTousLesContacts());
            cRecyclerView.setAdapter(cAdapter);

        }

    }
    private void buildNewContactDialog(){

        newContactDialog = new Dialog(getContext());
        newContactDialog.setContentView(R.layout.new_contact_dialog);

        TextView heading = newContactDialog.findViewById(R.id.contact_dialog_heading);
        final TextView nom= newContactDialog.findViewById(R.id.new_contact_name);
        final TextView numero=newContactDialog.findViewById(R.id.new_contact_number);
        saveButton = newContactDialog.findViewById(R.id.new_contact_save_btn);
        cancelButton = newContactDialog.findViewById(R.id.new_contact_cancel_btn);

        heading.setText("Nouveau Contact");
        nameField = newContactDialog.findViewById(R.id.new_contact_name);
        numberField = newContactDialog.findViewById(R.id.new_contact_number);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact=new Contact(nom.getText().toString(),Integer.parseInt(numero.getText().toString()));
                if(saveContact(contact)){
                    Toast.makeText(getActivity(),"Contact "+contact.getContactName()+" Enregistré avec succès", LENGTH_LONG).show();
                }
                newContactDialog.dismiss();
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
        return (id!=0);
    }


}
