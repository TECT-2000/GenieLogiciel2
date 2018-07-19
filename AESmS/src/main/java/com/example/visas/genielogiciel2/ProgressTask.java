package com.example.visas.genielogiciel2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.venus.app.IO.Asyncable;
import com.venus.app.IO.IO.FetchArray;
import com.venus.app.Utils.Utils;

import java.util.ArrayList;

public  class ProgressTask extends AsyncTask<String, Void, ArrayList<Contact>> {

    private ProgressDialog dialog;
    private ArrayList<Contact> contacts;
    private FetchArray activity;

    public ProgressTask(FetchArray activity, ArrayList<Contact> contacts) {
        this.activity = activity;
        this.contacts=contacts;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(((Activity) activity).getLocalClassName().equals("NewGroupActivity"))
            dialog = Utils.newLoadingDialog(NewGroupActivity.tempContext);
        else
            dialog=Utils.newLoadingDialog(ManageGroupActivity.context);
        this.dialog.setMessage("Chargement des contacts ...");
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(final ArrayList<Contact> contacts) {

            dialog.dismiss();
        if (!contacts.isEmpty()) {
            Toast.makeText(((Activity)activity), "OK", Toast.LENGTH_LONG).show();
            activity.fetchOnlineResult(contacts);
        } else {
            Toast.makeText(((Activity)activity), "Error", Toast.LENGTH_LONG).show();
        }
    }

    protected ArrayList<Contact>  doInBackground(final String... args) {

        ContentResolver cr = ((Activity)activity).getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        phoneNo=phoneNo.replace("+237","");
                        phoneNo=phoneNo.replace("+33","");
                        phoneNo=phoneNo.replace(" ","");
                        Contact contact=new Contact(name,phoneNo,"Autre");
                        contacts.add(contact);

                    }
                    pCur.close();
                }

            }
            return contacts;
        }
        if(cur!=null){
            cur.close();
        }
        return null;
    }
}
