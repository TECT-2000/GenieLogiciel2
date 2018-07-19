package com.example.visas.genielogiciel2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    RecyclerView  dialogRecyclerView;
    RecyclerView.LayoutManager layoutManager, dialogLayoutManager;
    RecyclerView.Adapter adapter;
    private ArrayList<Groupe> addGroupeList;
    private android.support.v7.app.AlertDialog.Builder deleteConfirmationDialog;
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
                            else if(which==2){
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
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("HardwareIds")
    private boolean sendMessage(final Message message) {

        final String SENT = "SMS_SENT";
        String[] permissions = {Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            Log.d("permission", "permission denied to SEND_SMS - requesting it");


            ActivityCompat.requestPermissions((Activity) context,permissions, 1);
            for (Groupe g : message.getGroupes()) {
                for (Contact c : g.getContacts()) {
                    PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
                    SharedPreferences pref=context.getSharedPreferences(c.getOperateur(),Context.MODE_PRIVATE);
                    String sim= PreferenceManager
                            .getDefaultSharedPreferences(context)
                            .getString(c.getOperateur(), "0");
                    int slot=0;
                    Toast.makeText(context,"Slot utilisé : "+sim+" numéro : "+c.getContactNumber(),Toast.LENGTH_LONG).show();

                    SubscriptionManager subs = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    for (SubscriptionInfo s : subs.getActiveSubscriptionInfoList()) {
                        if(s.getDisplayName().toString().matches(sim))
                            Toast.makeText(context,"slot : "+s.getSimSlotIndex(),Toast.LENGTH_LONG).show();
                        slot=s.getSimSlotIndex();
                    }
                    SmsManager.getSmsManagerForSubscriptionId(slot).sendTextMessage(c.contactNumberToString(), null, message.getMessageInfo(), sentPI, null);

                }
            }
            //---when the SMS has been sent---
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    if (getResultCode() == Activity.RESULT_OK) {
                        message.setMessageIsSent(true);
                        createMessage(message);
                        Toast.makeText(context,"Message envoyé",Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(context,"Echec envoi",Toast.LENGTH_LONG).show();

                }

            }, new IntentFilter(SENT));
        }

        return message.isMessageIsSent();

    }
    private boolean createMessage(Message message) {
        long id = message_dao.enregisterMessage(message);

        if (id != 0) {
            Toast.makeText(context, "Message Envoyé ", Toast.LENGTH_LONG).show();
            FragmentMessages.onresu = true;
        }

        return id != 0;
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
        if(messagesDataList.get(position).getMessageTitle().length()<3)
            holder.groupInitials.setText(messagesDataList.get(position).getMessageTitle());
        else
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
        ImageView deleteButton=draftDialog.findViewById(R.id.dialog_delete_draft);

        groupInitials.setText(dataProvider.getMessageTitle().substring(0,3));
        groupName.setText(dataProvider.getMessageTitle());
        textDetails.setText(dataProvider.getMessageInfo());
        textDetails.setSelection(textDetails.getText().length());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    deleteConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(context,
                            R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                }
                else{
                    deleteConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(context);
                }

                deleteConfirmationDialog.setMessage("Voulez vous vraiment suprimmer "+vh.messageTitle.getText()+" parmi vos messages ?")
                        .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                message_dao.supprimerMessage(messagesDataList.get(vh.getAdapterPosition()));
                                FragmentMessages.onresu=true;

                                int currentPosition = vh.getAdapterPosition();
                                messagesDataList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                notifyItemRangeChanged(currentPosition, messagesDataList.size());
                                draftDialog.dismiss();
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