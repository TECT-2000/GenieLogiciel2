package com.example.visas.genielogiciel2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Message_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;
import com.example.visas.genielogiciel2.Model.Principal.Message;

import java.util.ArrayList;

/**
 * Created by visas on 5/12/18.
 */

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.MessageViewHolder>{

    private ArrayList<Message> messagesDataList;

    RecyclerView recyclerView, dialogRecyclerView;
    RecyclerView.LayoutManager layoutManager, dialogLayoutManager;
    RecyclerView.Adapter adapter;
    private ArrayList<Groupe> addGroupeList;
    public Context context;
    private Message_DAO message_dao;
    private Groupe_DAO groupe_dao;
    Dialog draftDialog;


    // Provide a suitable constructor (depends on the kind of dataset)
    public MessagesRecyclerAdapter(Context context, ArrayList<Message> messagesDataList) {

        this.context = context;
        this.messagesDataList = messagesDataList;
        draftDialog = new Dialog(context);
        message_dao=new Message_DAO(context);
        groupe_dao=new Groupe_DAO(context);

    }

    @Override
    public MessageViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context)
                .inflate(R.layout.message_card, parent, false);
        final MessageViewHolder vh = new MessageViewHolder(v);

        vh.messageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                if(!messagesDataList.get(vh.getAdapterPosition()).isMessageIsSent()) {
                    CharSequence colors[] = new CharSequence[]{"Envoyer","Editer", "supprimer"};

                    builder.setTitle("Choisir une option");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {

                                displayAddGroupeDialog(vh.getAdapterPosition());

                            } else if(which==1){
                                createDraftDialog(vh,messagesDataList.get(vh.getAdapterPosition()));
                            }
                            else{
                                message_dao.supprimerMessage(messagesDataList.get(vh.getAdapterPosition()));
                                messagesDataList.remove(vh.getAdapterPosition());
                                notifyItemRemoved(vh.getAdapterPosition());
                            }
                        }
                    });
                }
                else{
                    CharSequence colors[] = new CharSequence[]{"Envoyer", "supprimer"};
                    builder.setTitle("Choisir une option");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                createSendDialog(vh, messagesDataList.get(vh.getAdapterPosition()));
                            } else if(which==1){
                                message_dao.supprimerMessage(messagesDataList.get(vh.getAdapterPosition()));
                                messagesDataList.remove(vh.getAdapterPosition());
                                notifyItemRemoved(vh.getAdapterPosition());
                            }
                        }
                    });
                }
                builder.show();
            }

        });

        return vh;
    }
    private void displayAddGroupeDialog(final int position){
        fillAddGroupeList();

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_groupes_dialog);

        dialogRecyclerView = dialog.findViewById(R.id.add_contacts_dialog_recycler_view);

        adapter = new NewMessageRecyclerAdapter(addGroupeList);
        dialogLayoutManager = new LinearLayoutManager(context);
        dialogLayoutManager.setAutoMeasureEnabled(false);

        dialogRecyclerView.setHasFixedSize(true);
        dialogRecyclerView.setLayoutManager(dialogLayoutManager);
        dialogRecyclerView.setAdapter(adapter);

        Button cancelButton = dialog.findViewById(R.id.add_group_cancel_btn);
        Button saveButton = dialog.findViewById(R.id.add_group_save_btn);

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
                Message message = new Message();
                message.setMessageTitle(messagesDataList.get(position).getMessageTitle());
                message.setMessageInfo(messagesDataList.get(position).getMessageInfo());
                ArrayList<Groupe> groupes = ((NewMessageRecyclerAdapter) dialogRecyclerView.getAdapter()).getCheckedGroupes();
                message.setGroupes(groupes);

                if (groupes!=null && sendMessage(message)) {
                    message.setMessageIsSent(true);
                    Toast.makeText(context, "message envoyé", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }
    private boolean sendMessage(Message message) {

        SmsManager smsManager = SmsManager.getDefault();
        for(Groupe g: message.getGroupes()) {
            for(Contact c:g.getContacts())
                smsManager.sendTextMessage(c.contactNumberToString(), null, message.getMessageInfo(), null, null);
        }
        return true;
    }

    //method to fill list of available contacts to add
    private void fillAddGroupeList(){

        addGroupeList = new ArrayList<>();
        addGroupeList = groupe_dao.selectionnerGroupes();

    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.messageTitle.setText(messagesDataList.get(position).getMessageTitle());
        holder.messageInfo.setText(messagesDataList.get(position).getMessageInfo());
        holder.messageTime.setText(messagesDataList.get(position).getMessageTime());
        holder.groupInitials.setText(messagesDataList.get(position).getMessageTitle().substring(0,3));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messagesDataList.size();
    }

    private void createSendDialog(MessageViewHolder vh, Message dataProvider){

        draftDialog.setContentView(R.layout.dialog_send);

        TextView groupInitials = draftDialog.findViewById(R.id.sent_group_initials);
        TextView groupName = draftDialog.findViewById(R.id.sent_group_name);
        TextView sentTime = draftDialog.findViewById(R.id.sent_time);
        TextView textDetails = draftDialog.findViewById(R.id.sent_text_details);

        groupInitials.setText(dataProvider.getMessageTitle().substring(0,3));
        groupName.setText(dataProvider.getMessageTitle());
        sentTime.setText(dataProvider.getMessageTime());
        textDetails.setText(dataProvider.getMessageInfo());

        draftDialog.show();
    }

    private void createDraftDialog(final MessageViewHolder vh, Message dataProvider){

        draftDialog.setContentView(R.layout.draft_dialog);


        TextView groupInitials = draftDialog.findViewById(R.id.dialog_group_initials);
        final TextView groupName = draftDialog.findViewById(R.id.dialog_message_title);
        final EditText textDetails = draftDialog.findViewById(R.id.dialog_drafted_text);

        ImageButton clearDraft = draftDialog.findViewById(R.id.clear_draft);
        ImageButton saveDraft = draftDialog.findViewById(R.id.save_draft);
        ImageButton sendDraft = draftDialog.findViewById(R.id.send_draft);

        groupInitials.setText(dataProvider.getMessageTitle().substring(0,3));
        groupName.setText(dataProvider.getMessageTitle());
        textDetails.setText(dataProvider.getMessageInfo());
        textDetails.setSelection(textDetails.getText().length());


//        Setting action events for Dialog buttons
        clearDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDetails.setText("");
            }
        });

        saveDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to save draft
                Message message=new Message();
                message.setMessageTitle(groupName.getText().toString());
                message.setMessageInfo(textDetails.getText().toString());
                message.setMessageIsSent(false);
                modifierMessage(message,vh.getAdapterPosition());
                draftDialog.dismiss();
            }
        });

        sendDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to send draft
            displayAddGroupeDialog(vh.getAdapterPosition());
            }
        });

        draftDialog.show();

    }
    private boolean modifierMessage(Message message,int position){
        long id=message_dao.modifierToutLeMessage(message);

        if(id!=0){
            messagesDataList.set(position,message);
            notifyItemChanged(position);
        }

        return id!=0;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        private TextView groupInitials, messageTitle, messageInfo, messageTime;
        private LinearLayout messageCard;

        public MessageViewHolder(View view) {
            super(view);

            messageCard = view.findViewById(R.id.message_card);
            groupInitials = (CircularTextView) view.findViewById(R.id.message_initials);
            messageTitle = view.findViewById(R.id.message_title);
            messageInfo = view.findViewById(R.id.message_info);
            messageTime = view.findViewById(R.id.message_time);

        }

    }
}