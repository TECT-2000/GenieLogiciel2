package com.example.visas.genielogiciel2.Model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.visas.genielogiciel2.Model.BD.ConnexionBD;
import com.example.visas.genielogiciel2.Model.Principal.Message;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/*
     A MODIFIER
 */

public class Message_DAO extends DAOBase{

        Groupe_DAO groupeDao;
    /*
     *créer un contact
     */
    public long enregisterMessage(Message message){
        SQLiteDatabase db= this.conn.getWritableDatabase();
        long message_res=0;
        ContentValues values=new ContentValues();
        values.put(ConnexionBD.getTITRE(),message.getMessageTitle());
        values.put(ConnexionBD.getCONTENU(),message.getMessageInfo());
        values.put(ConnexionBD.getDateModification(), String.valueOf(Calendar.getInstance().getTime()));

        if(message.isMessageIsSent()) {
            values.put(ConnexionBD.getDateEnvoi(), String.valueOf(Calendar.getInstance().getTime()));
            values.put(ConnexionBD.getSENT(),"oui");
        }
        else{
            values.put(ConnexionBD.getSENT(),"non");
        }
        //insertion dans la BD
            message_res=db.insert(ConnexionBD.getTableMessage(),null,values);
        System.out.println(message.isMessageIsSent());

        this.closeDB();

        return message_res;
    }


    /*
     *récupérer un message de la bd à partir de son identifiant
     */
    public Message selectionnerMessage(long message_id){
        SQLiteDatabase db=this.conn.getReadableDatabase();

        String query="SELECT * FROM "+ConnexionBD.getTableMessage()+" WHERE "+ConnexionBD.getKeyId()+" = "+message_id;

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        if(c !=null)
            c.moveToFirst();
        Date dateModif=(Date)Calendar.getInstance().getTime(); ;
        SimpleDateFormat format=new SimpleDateFormat("'yyyy-MM-dd HH:mm;ss'");
        try {

            dateModif = (Date)format.parse(c.getString(c.getColumnIndex(ConnexionBD.getDateModification())));
        }catch(ParseException e){
            e.printStackTrace();
        }
        Message message=new Message(
                c.getString(c.getColumnIndex(ConnexionBD.getCONTENU())),
                String.valueOf(dateModif),
                c.getString(c.getColumnIndex(ConnexionBD.getTITRE())));
            c.close();
        return message;

    }
    public ArrayList<Message> selectionnerMessages(){
        SQLiteDatabase db=this.conn.getReadableDatabase();
        ArrayList<Message> messages=new ArrayList<>();
        String query="SELECT * FROM "+ConnexionBD.getTableMessage();

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        if(c.moveToFirst()) {
            do{
            Date dateModif = (Date) Calendar.getInstance().getTime();
            Date dateEnvoi=null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(c.getString(c.getColumnIndex(ConnexionBD.getSENT()))=="oui"){
            try {
                dateEnvoi=format.parse(c.getString(c.getColumnIndex(ConnexionBD.getDateEnvoi())));
                dateModif = format.parse(c.getString(c.getColumnIndex(ConnexionBD.getDateModification())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            }
            Message message = new Message(
                    c.getString(c.getColumnIndex(ConnexionBD.getCONTENU())),
                    format.format(dateModif),
                    c.getString(c.getColumnIndex(ConnexionBD.getTITRE())));
            if(dateEnvoi!=null) {
                message.setMessageIsSent(true);
                message.setDateEnvoi(format.format(dateEnvoi));
            }
            else
                message.setMessageIsSent(false);
            messages.add(message);
                System.out.println(message.isMessageIsSent());
        }while(c.moveToNext());
        }
        c.close();
        return messages;

    }

    /*
     *modifier tous les champs d'un message
     */
    public int modifierToutLeMessage(Message message) {
        SQLiteDatabase db=this.conn.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(ConnexionBD.getCONTENU(),message.getMessageInfo());
        values.put(ConnexionBD.getDateModification(), String.valueOf(Calendar.getInstance().getTime()));

        //mise à jour de l'enregistrement
        return db.update(ConnexionBD.getTableMessage(),values,ConnexionBD.getKeyId()+" = ?",
                new String[]{String.valueOf(selectionnerIdMessage(message.getMessageTitle()))});
    }

    /*
     *supprimer un message de la bd à partir de son identifiant
     */
    public boolean supprimerMessage(Message message){
        SQLiteDatabase db=this.conn.getWritableDatabase();

        int reponse=db.delete(ConnexionBD.getTableMessage(),ConnexionBD.getKeyId()+" = ?",
                new String []{String.valueOf(selectionnerIdMessage(message.getMessageTitle()))});

        return (reponse!=0);
    }

    /*
        recupérer l'id d'un message à partir de son titre
     */
    public int selectionnerIdMessage(String titre){
        SQLiteDatabase db=this.conn.getReadableDatabase();

        String query="SELECT * FROM "+ConnexionBD.getTableMessage()+" WHERE "+ConnexionBD.getTITRE()+" = '"+titre+"'";

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        if(c !=null)
            c.moveToFirst();

        int id=c.getInt(c.getColumnIndex(ConnexionBD.getKeyId()));
        c.close();
        return id;
    }

    public Message_DAO(Context context) {
            super(context);
            groupeDao=new Groupe_DAO(context);
    }
}
