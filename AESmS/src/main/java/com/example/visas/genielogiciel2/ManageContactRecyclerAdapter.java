package com.example.visas.genielogiciel2;

import android.content.Context;
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
import com.example.visas.genielogiciel2.Model.Principal.Groupe;

import java.util.ArrayList;

/**
 * Created by visas on 5/16/18.
 */

 public class ManageContactRecyclerAdapter extends RecyclerView.Adapter<ManageContactRecyclerAdapter.GroupesViewHolder> {

     private ArrayList<Groupe> liste =new ArrayList<>();
    private ArrayList<Groupe> groupMembersList;
    private Contact_DAO contact_dao;
    private Groupe_DAO groupe_dao;

    public Context context;

    public ManageContactRecyclerAdapter(ArrayList<Groupe> groupMembersList) {
        this.groupMembersList = groupMembersList;
        this.context = context;
    }

    @Override
    public GroupesViewHolder onCreateViewHolder( final ViewGroup parent, int viewType) {

        View view;
        final GroupesViewHolder holder;
        contact_dao=new Contact_DAO(parent.getContext());
        groupe_dao=new Groupe_DAO(parent.getContext());

            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_item, parent, false);

            holder = new GroupesViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final GroupesViewHolder holder, final int position) {

        holder.groupinitials.setText(groupMembersList.get(position).getGroupInitials());
        holder.groupeName.setText(groupMembersList.get(position).getGroupName());
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

    @Override
    public int getItemCount() {
        return groupMembersList.size();
    }

    public ArrayList<Groupe> getCheckedGroupes(){
        return liste;
    }


    public static class GroupesViewHolder extends RecyclerView.ViewHolder{

        private TextView groupeName;
        private CircularTextView groupinitials;
        private CheckBox checkBox;

        public GroupesViewHolder(View itemView) {
            super(itemView);

            groupeName = itemView.findViewById(R.id.new_message_group_name);
            groupinitials=itemView.findViewById(R.id.new_message_group_initials);
            checkBox = itemView.findViewById(R.id.groupe_checkbox);

        }
    }

}
