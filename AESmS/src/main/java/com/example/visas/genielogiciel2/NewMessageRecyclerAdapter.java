package com.example.visas.genielogiciel2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.visas.genielogiciel2.Model.Principal.Groupe;

import java.util.ArrayList;



public class NewMessageRecyclerAdapter extends RecyclerView.Adapter<NewMessageRecyclerAdapter.GroupViewHolder> {

    private ArrayList<Groupe> arrayList;
    private ArrayList<Groupe> liste =new ArrayList<>();

    public NewMessageRecyclerAdapter(ArrayList<Groupe> arrayList){

        this.arrayList = arrayList;

    }

    @Override
    public NewMessageRecyclerAdapter.GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);

        NewMessageRecyclerAdapter.GroupViewHolder holder = new NewMessageRecyclerAdapter.GroupViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewMessageRecyclerAdapter.GroupViewHolder holder, final int position) {

        Groupe groupe = arrayList.get(position);
        holder.groupInitials.setText(groupe.getGroupInitials());
        holder.groupName.setText(groupe.getGroupName());
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    liste.add(arrayList.get(position));
                }
                else
                    liste.remove(arrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        TextView groupInitials, groupName;
        CheckBox checkBox;

        public GroupViewHolder(View view) {
            super(view);

            groupInitials = view.findViewById(R.id.new_message_group_initials);
            groupName = view.findViewById(R.id.new_message_group_name);
            checkBox=view.findViewById(R.id.groupe_checkbox);
        }
    }
    public ArrayList<Groupe> getCheckedGroupes(){
        return liste;
    }
}
