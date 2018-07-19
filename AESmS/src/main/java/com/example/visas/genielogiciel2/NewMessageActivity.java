package com.example.visas.genielogiciel2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.DAO.Groupe_DAO;
import com.example.visas.genielogiciel2.Model.DAO.Message_DAO;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;
import com.example.visas.genielogiciel2.Model.Principal.Message;

import java.util.ArrayList;

public class NewMessageActivity extends AppCompatActivity {

    private RecyclerView nmRecyclerView;
    private Groupe_DAO groupe_dao;
    private Message_DAO message_dao;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Groupe> groupsList;

    private ImageButton sendNewMessage, saveNewMessage, clearNewMessage;
    private EditText draftText, messageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        groupe_dao = new Groupe_DAO(getApplicationContext());
        message_dao = new Message_DAO(getApplicationContext());
        groupsList = new ArrayList<>();

        groupsList = groupe_dao.selectionnerGroupes();

        nmRecyclerView = (RecyclerView) findViewById(R.id.new_message_recycler_view);
        messageTitle = findViewById(R.id.message_title);
        adapter = new NewMessageRecyclerAdapter(groupsList);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        nmRecyclerView.setAdapter(adapter);
        nmRecyclerView.setLayoutManager(layoutManager);

        clearNewMessage = (ImageButton) findViewById(R.id.clear_new_message);
        saveNewMessage = (ImageButton) findViewById(R.id.save_new_message);
        sendNewMessage = (ImageButton) findViewById(R.id.send_new_message);

        draftText = (EditText) findViewById(R.id.drafted_text);

        //Setting action events for buttons in this view
        clearNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftText.setText("");
            }
        });

        saveNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code for saving message

                Message message = new Message();
                message.setMessageTitle(messageTitle.getText().toString());
                message.setMessageInfo(draftText.getText().toString());
                message.setMessageIsSent(false);
                createMessage(message);

            }
        });

        sendNewMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //Code for sending message
                Message message = new Message();
                message.setMessageTitle(messageTitle.getText().toString());
                message.setMessageInfo(draftText.getText().toString());

                ArrayList<Groupe> groupes = ((NewMessageRecyclerAdapter) nmRecyclerView.getAdapter()).getCheckedGroupes();
                message.setGroupes(groupes);

                if (groupes != null && sendMessage(message)){
                    message.setMessageIsSent(true);
                    Toast.makeText(getApplicationContext(), "message envoyé", Toast.LENGTH_LONG).show();
                    createMessage(message);

                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Message message = new Message();
        message.setMessageTitle(messageTitle.getText().toString());
        message.setMessageInfo(draftText.getText().toString());
        createMessage(message);*/
    }

    private boolean createMessage(Message message) {
        long id = message_dao.enregisterMessage(message);

        if (id != 0) {
            Toast.makeText(getApplicationContext(), "Message enregistré ", Toast.LENGTH_LONG).show();
            FragmentMessages.onresu = true;
        }

        return id != 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("HardwareIds")
    private boolean sendMessage(final Message message) {

        final String SENT = "SMS_SENT";
        String[] permissions = {Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            Log.d("permission", "permission denied to SEND_SMS - requesting it");

            requestPermissions(permissions, 1);
            for (Groupe g : message.getGroupes()) {
                for (Contact c : g.getContacts()) {
                    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
                    SharedPreferences pref=getSharedPreferences(c.getOperateur(),0);
                    int slot=pref.getInt("id",1);
                    Toast.makeText(getApplicationContext(),"Slot utilisé : "+slot+" numéro : "+c.getContactNumber(),Toast.LENGTH_LONG).show();
                    SmsManager.getSmsManagerForSubscriptionId(slot).sendTextMessage(c.contactNumberToString(), null, "***** "+message.getMessageTitle()+" *****  \n"+message.getMessageInfo(), sentPI, null);

                }
            }
            //---when the SMS has been sent---
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    if (getResultCode() == Activity.RESULT_OK) {
                        message.setMessageIsSent(true);
                        createMessage(message);
                        Toast.makeText(getApplicationContext(),"Message envoyé",Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Echec envoi",Toast.LENGTH_LONG).show();

                }

            }, new IntentFilter(SENT));
        }

        return message.isMessageIsSent();

    }
}