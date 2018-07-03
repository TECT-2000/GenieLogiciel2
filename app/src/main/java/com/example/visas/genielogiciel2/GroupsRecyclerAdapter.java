package com.example.visas.genielogiciel2;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.visas.genielogiciel2.Model.Principal.Groupe;

import java.util.ArrayList;



public class GroupsRecyclerAdapter extends RecyclerView.Adapter<GroupsRecyclerAdapter.GroupViewHolder> {

    private ArrayList<Groupe> groupsDataList;
    public static final String GROUP_INITIALS = "com.example.visas.GROUP_INITIALS";
    public static final String GROUP_NAME = "com.example.visas.GROUP_NAME";

    public GroupsRecyclerAdapter(ArrayList<Groupe> groupsDataList) {
        this.groupsDataList = groupsDataList;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_card, parent, false);

        final GroupViewHolder holder = new GroupViewHolder(view);

        holder.groupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ManageGroupActivity.class);
                String groupInitials =(String) holder.groupInitials.getText();
                String groupName =(String) holder.groupName.getText();

                intent.putExtra(GROUP_INITIALS, groupInitials);
                intent.putExtra(GROUP_NAME, groupName);

                v.getContext().startActivity(intent);

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {

        holder.groupInitials.setText(groupsDataList.get(position).getGroupInitials());
        holder.groupName.setText(groupsDataList.get(position).getGroupName());
        if(groupsDataList.get(position).getContacts()!=null){
            holder.groupSize.setText(String.valueOf(groupsDataList.get(position).getContacts().size())+" membre");
        }
        else
            holder.groupSize.setText(groupsDataList.get(position).getGroupSize());
    }

    @Override
    public int getItemCount() {
        return groupsDataList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        private CircularTextView groupInitials;
        private TextView groupName, groupSize;
        private LinearLayout groupCard;

        public GroupViewHolder(View view) {
            super(view);

            groupCard = view.findViewById(R.id.groups_card);
            groupInitials = view.findViewById(R.id.group_initials2);
            groupName = view.findViewById(R.id.group_name);
            groupSize = view.findViewById(R.id.group_size);
        }
    }
}
