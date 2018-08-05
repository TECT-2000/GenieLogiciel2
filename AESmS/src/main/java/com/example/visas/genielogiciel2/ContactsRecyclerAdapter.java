package com.example.visas.genielogiciel2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Contact_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by visas on 5/16/18.
 */

public class ContactsRecyclerAdapter  extends
        RecyclerView.Adapter<ContactsRecyclerAdapter.ContactsViewHolder>{

    private ArrayList<Contact> contactsDataList;
    private Context context;
    private Contact_DAO contact_dao;
    private AlertDialog.Builder deleteConfirmationDialog;
    private Dialog editContactDialog;
    RecyclerView.LayoutManager dialogLayoutManager;
    RecyclerView.Adapter dialogAdapter;
    ArrayList<Groupe>  addGroupeList;
    private Groupe_DAO groupe_dao;

    RecyclerView  dialogRecyclerView;

    private EditText contactName, contactNumber;

    public ContactsRecyclerAdapter(Context context,ArrayList<Contact> contactsDataList) {
        this.contactsDataList = contactsDataList;
        this.context = context;

    }

    @Override
    public ContactsViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                     .inflate(R.layout.contact_card, parent, false);
        context=parent.getContext();
        groupe_dao=new Groupe_DAO(parent.getContext());
        contact_dao=new Contact_DAO(parent.getContext());
        final ContactsViewHolder holder = new ContactsViewHolder(view);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    deleteConfirmationDialog = new AlertDialog.Builder(parent.getContext(),
                            R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                }
                else{
                    deleteConfirmationDialog = new AlertDialog.Builder(parent.getContext());
                }

                deleteConfirmationDialog.setMessage("Voulez vous vraiment suprimmer "+holder.contactName.getText()+" parmi vos contacts?")
                        .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                contact_dao.supprimerContact(contactsDataList.get(holder.getAdapterPosition()));
                                FragmentGroups.onresu=true;

                                int currentPosition = holder.getAdapterPosition();
                                contactsDataList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                notifyItemRangeChanged(currentPosition, contactsDataList.size());
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
        });
        holder.contactsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(parent.getContext());
                CharSequence colors[] = new CharSequence[]{"Ajouter à un groupe", "Modifier",};

                builder.setTitle("Choisir une option");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            displayAddGroupeDialog(contactsDataList.get(holder.getAdapterPosition()));

                        } else if (which == 1) {
                            editContactDialog = new Dialog(parent.getContext());
                            buildEditContactDialog(editContactDialog, holder);
                        }
                    }
                });
                builder.show();
            }

        });

        return holder;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayAddGroupeDialog(final Contact contact){
        fillAddGroupesList();


        final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.add_contacts_dialog);

        dialogRecyclerView = dialog.findViewById(R.id.add_contacts_dialog_recycler_view);

        dialogAdapter = new ManageContactRecyclerAdapter(addGroupeList);
        dialogLayoutManager = new LinearLayoutManager(context);
        dialogLayoutManager.setAutoMeasureEnabled(false);

        dialogRecyclerView.setHasFixedSize(true);
        dialogRecyclerView.setLayoutManager(dialogLayoutManager);
        dialogRecyclerView.setAdapter(dialogAdapter);

        Button cancelButton = dialog.findViewById(R.id.add_contacts_cancel_btn);
        Button saveButton = dialog.findViewById(R.id.add_contacts_save_btn);
        TextView titre=dialog.findViewById(R.id.add_textview);

        titre.setText("Selectionnez le(s) groupe(s)                                        ");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Groupe> groupes=((ManageContactRecyclerAdapter)dialogRecyclerView.getAdapter()).getCheckedGroupes();
                for(Groupe g : groupes){
                        groupe_dao.enregistrerContactGroupe(contact,groupe_dao.selectionnerIdGroupe(g.getGroupName()));
                }
                FragmentGroups.onresu=true;

                dialog.dismiss();
            }
        });

        dialog.show();

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fillAddGroupesList(){

        addGroupeList = new ArrayList<>();
        addGroupeList = groupe_dao.selectionnerGroupes();


        Toast.makeText(context,"size : "+addGroupeList.size(),Toast.LENGTH_LONG).show();

    }
    @Override
    public void onBindViewHolder(ContactsViewHolder holder, final int position) {

        holder.contactName.setText(contactsDataList.get(position).getContactName());
        holder.contactNumber.setText(contactsDataList.get(position).contactNumberToString()+" - "+contactsDataList.get(position).getOperateur());

    }

    @Override
    public int getItemCount() {
        return contactsDataList.size();
    }

    private void buildEditContactDialog(final Dialog dialog, final ContactsViewHolder holder){

        dialog.setContentView(R.layout.new_contact_dialog);

        contactName = dialog.findViewById(R.id.new_contact_name);
        contactNumber = dialog.findViewById(R.id.new_contact_number);
        Button saveButton = dialog.findViewById(R.id.new_contact_save_btn);
        Button cancelButton = dialog.findViewById(R.id.new_contact_cancel_btn);
        TextView heading = dialog.findViewById(R.id.contact_dialog_heading);

        contactName.setText(holder.contactName.getText());
        contactName.setSelection(contactName.getText().length());
        contactNumber.setText(holder.contactNumber.getText());
        contactNumber.setSelection(contactNumber.getText().length());

        heading.setText("Modifier");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact=new Contact();
                contact.setContactName(contactName.getText().toString());
                contact.setContactNumber(contactsDataList.get(holder.getAdapterPosition()).getContactNumber());
                long id=contact_dao.modifierNomContact(contact);
                contact.setContactNumber(contactNumber.getText().toString());
                long rep=contact_dao.modifierNumeroContact(contact);

                if(id!=0 && rep!=0) {
                    contactsDataList.set(holder.getAdapterPosition(),contact);
                    notifyItemChanged(holder.getAdapterPosition());
                }
                FragmentContacts.onresu=true;
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView contactName, contactNumber;
        ImageView deleteButton;
        LinearLayout contactsCard;


        public ContactsViewHolder(View itemView) {
            super(itemView);

            contactsCard = itemView.findViewById(R.id.contacts_linear_layout);
            contactName = itemView.findViewById(R.id.contact_name);
            contactNumber = itemView.findViewById(R.id.contact_number);
            deleteButton = itemView.findViewById(R.id.delete_contact);

        }
    }
}
