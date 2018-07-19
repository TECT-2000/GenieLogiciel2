package com.example.visas.genielogiciel2;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.visas.genielogiciel2.Model.DAO.Contact_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;

import java.util.ArrayList;

/**
 * Created by visas on 5/16/18.
 */

 public class ManageGroupRecyclerAdapter  extends RecyclerView.Adapter<ManageGroupRecyclerAdapter.ContactsViewHolder> {
     private ArrayList<Contact> liste =new ArrayList<>();
    private ArrayList<Contact> groupMembersList;
    private Contact_DAO contact_dao;
    private Groupe_DAO groupe_dao;
    private final boolean isForDialog;
    private AlertDialog.Builder deleteConfirmationDialog;
    private String nomGroupe;

    public ManageGroupRecyclerAdapter(ArrayList<Contact> groupMembersList, boolean isForDialog,String nom) {
        this.groupMembersList = groupMembersList;
        this.isForDialog = isForDialog;
        this.nomGroupe=nom;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder( final ViewGroup parent, int viewType) {

        View view;
        final ContactsViewHolder holder;
        contact_dao=new Contact_DAO(parent.getContext());
        groupe_dao=new Groupe_DAO(parent.getContext());
        if(!isForDialog) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_card, parent, false);

            holder = new ContactsViewHolder(view);

            holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Code to delete contact from group
                    deleteConfirmationDialog = new AlertDialog.Builder(parent.getContext(),
                            R.style.ThemeOverlay_AppCompat_Dialog_Alert);

                    deleteConfirmationDialog.setMessage("Supprimer "+holder.contactName.getText()+" du groupe ?")
                            .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //Code to delete contact from group
                                    contact_dao.supprimerContactsGroupe(groupe_dao.selectionnerIdGroupe(nomGroupe),groupMembersList.get(holder.getAdapterPosition()));
                                    FragmentGroups.onresu=true;
                                    int currentPosition = holder.getAdapterPosition();
                                    groupMembersList.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                    notifyItemRangeChanged(currentPosition, groupMembersList.size());
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

        }
        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_contacts_dialog_item, parent, false);

            holder = new ContactsViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, final int position) {

        holder.contactName.setText(groupMembersList.get(position).getContactName());
        holder.contactNumber.setText(groupMembersList.get(position).contactNumberToString()+" - "+groupMembersList.get(position).getOperateur());

       if(isForDialog){
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    liste.add(groupMembersList.get(position));
                }
                else
                    liste.remove(groupMembersList.get(position));
            }
        });
       }

    }

    @Override
    public int getItemCount() {
        return groupMembersList.size();
    }

    public ArrayList<Contact> getCheckedContacts(){
        return liste;
    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        private TextView contactName, contactNumber;
        private ImageView deleteIcon;
        private CheckBox checkBox;

        public ContactsViewHolder(View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.contact_name);
            contactNumber = itemView.findViewById(R.id.contact_number);
            deleteIcon = itemView.findViewById(R.id.delete_contact);
            checkBox = itemView.findViewById(R.id.add_contact_checkbox);

        }
    }

}
