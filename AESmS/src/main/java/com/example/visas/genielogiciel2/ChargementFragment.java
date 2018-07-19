package com.example.visas.genielogiciel2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.venus.app.IO.Asyncable;
import com.venus.app.IO.IO.FetchArray;

import java.util.ArrayList;

public  class ChargementFragment extends AppCompatDialogFragment {
    //TODO: Ajouter les demandes de permission
    private static final String ARG_OP = "operation";
    private static ArrayList<Contact> contactslist;

    public static ChargementFragment newInstance(String op,ArrayList<Contact> contacts) {

        Bundle args = new Bundle();
        args.putString(ARG_OP, op);
        ChargementFragment fragment = new ChargementFragment();
        fragment.setArguments(args);
        contactslist=contacts;
        return fragment;
    }

    @SuppressLint("ResourceType")
    private void charger() {

        new ProgressTask((FetchArray) getActivity(),contactslist).execute();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = new LinearLayout(getContext());
        ((LinearLayout) v).setOrientation(LinearLayout.VERTICAL);
        AlertDialog dialog =  new AlertDialog.Builder(getContext())
                .setView(v)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                dismiss();
                charger();

            }
        });


        if (dialog == null) dialog = new AlertDialog.Builder(getContext()).create();
        return dialog;
    }
}
