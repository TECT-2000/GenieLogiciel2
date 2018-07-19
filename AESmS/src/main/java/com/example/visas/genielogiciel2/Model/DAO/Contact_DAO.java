package com.example.visas.genielogiciel2.Model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.visas.genielogiciel2.Model.BD.ConnexionBD;
import com.example.visas.genielogiciel2.Model.Principal.Contact;

import java.util.ArrayList;

public class Contact_DAO extends DAOBase{

    /*
    *créer un contact
     */
    public long enregisterContact(Contact contact){
        SQLiteDatabase db= this.open();
        long contact_res=0;
             ContentValues values = new ContentValues();
            values.put(ConnexionBD.getNomContact(), contact.getContactName());
            values.put(ConnexionBD.getNumeroContact(), contact.getContactNumber());
            values.put(ConnexionBD.getOPERATEUR(),contact.getOperateur());

            //insertion dans la BD
        if(selectionnerIDContactByNumber(contact)==0)
             contact_res = db.insert(ConnexionBD.getTableContact(), null, values);
        else
            return 0;

            this.closeDB();
            return contact_res;

        //faut encore vérifier que le contact n'existe pas dans la bd

    }

    /*
     *récupérer un contact de la bd à partir de son identifiant
     */
    public Contact selectionnerContact(long contact_id){
        SQLiteDatabase db=this.conn.getReadableDatabase();

        String query="SELECT * FROM "+ConnexionBD.getTableContact()+" WHERE "+ConnexionBD.getKeyId()+" = "+contact_id;

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);
        Contact contact = new Contact();
        if(c.moveToFirst()) {

            contact = new Contact(c.getInt(c.getColumnIndex(ConnexionBD.getKeyId())),
                    c.getString(c.getColumnIndex(ConnexionBD.getNomContact())),
                    c.getString(c.getColumnIndex(ConnexionBD.getNumeroContact())),
                    c.getString(c.getColumnIndex(ConnexionBD.getOPERATEUR())));

        }
        this.closeDB();
            return contact;
    }
    /*
     *récupérer l' ID contact de la bd à partir de son numero
     */
    public int selectionnerIDContactByNumber(Contact contact){
        SQLiteDatabase db=this.conn.getReadableDatabase();

        String query="SELECT id FROM "+ConnexionBD.getTableContact()+" WHERE "+ConnexionBD.getNumeroContact()+" = "+contact.getContactNumber();

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        int id=0;

        if(c.moveToFirst()){
            id=c.getInt(c.getColumnIndex(ConnexionBD.getKeyId()));
        }
        Log.d("id","id :"+id);
        return id;
    }
    /*
     *récupérer l' ID contact de la bd à partir de son numero
     */
    public long selectionnerIDContactByName(Contact contact){
        SQLiteDatabase db=this.conn.getReadableDatabase();

        String query="SELECT id FROM "+ConnexionBD.getTableContact()+" WHERE "+ConnexionBD.getNomContact()+" = '"+contact.getContactName()+"'";

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        long id=0;
        if(c.moveToFirst()){
            id=c.getLong(c.getColumnIndex(ConnexionBD.getKeyId()));
        }

        return id;
    }
    /*
     *modifier le nom d' un contact de la bd
     */
    public int modifierNomContact(Contact contact) {
        SQLiteDatabase db=this.open();

        ContentValues values=new ContentValues();
        values.put(ConnexionBD.getNomContact(),contact.getContactName());

        //mise à jour de l'enregistrement
        return db.update(ConnexionBD.getTableContact(),values,ConnexionBD.getKeyId()+" = ?",
                new String[]{String.valueOf(selectionnerIDContactByNumber(contact))});
    }

    /*
     *modifier le numero d'un contact de la bd
     */
    public int modifierNumeroContact(Contact contact) {
        SQLiteDatabase db=this.open();

        ContentValues values=new ContentValues();
        values.put(ConnexionBD.getNumeroContact(),contact.getContactNumber());

        //mise à jour de l'enregistrement
        return db.update(ConnexionBD.getTableContact(),values,ConnexionBD.getKeyId()+" = ?",
                new String[]{String.valueOf(selectionnerIDContactByName(contact))});

    }

    /*
     *supprimer un contact de la bd à partir de son identifiant
     */
    public boolean supprimerContact(Contact contact){
        SQLiteDatabase db=this.open();

        int reponse=db.delete(ConnexionBD.getTableContact(),ConnexionBD.getKeyId()+" = ?",
                new String []{String.valueOf(selectionnerIDContactByNumber(contact))});
        db.delete(ConnexionBD.getTableGroupeContact(),ConnexionBD.getGcIdContact()+"= ?",
                new String []{String.valueOf(selectionnerIDContactByNumber(contact))});
        this.closeDB();
        return (reponse !=0);
    }


    /*
     *récupérer tous les contacts de la bd
     */
    public ArrayList<Contact> selectionnerTousLesContacts(){
        SQLiteDatabase db=this.conn.getReadableDatabase();
        ArrayList<Contact> contacts=new ArrayList<Contact>();
        String query="SELECT * FROM "+ConnexionBD.getTableContact();

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        if(c.moveToFirst()){
            do{
                Contact contact=new Contact();
                contact.setContactName(c.getString(c.getColumnIndex(ConnexionBD.getNomContact())));
                contact.setContactNumber(c.getString(c.getColumnIndex(ConnexionBD.getNumeroContact())));
                contact.setOperateur(c.getString(c.getColumnIndex(ConnexionBD.getOPERATEUR())));

                contacts.add(contact);
            }while(c.moveToNext());
        }

        c.close();
        this.closeDB();
        return contacts;
    }
    /*
     *récupérer tous les contacts d'un groupe
     */
    public ArrayList<Contact> selectionnerContactsGroupe(int groupe_id){
        SQLiteDatabase db=this.conn.getReadableDatabase();
        ArrayList<Contact> contacts=new ArrayList<>();
        String query="SELECT * FROM "+ConnexionBD.getTableGroupeContact()+" WHERE "+ConnexionBD.getGcIdGroupe()+" = "+groupe_id;

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        if(c.moveToFirst()){
            do{
                System.out.println(c.getInt(c.getColumnIndex(ConnexionBD.getGcIdContact())));
                Contact contact=selectionnerContact(c.getInt(c.getColumnIndex(ConnexionBD.getGcIdContact())));
                System.out.println(contact.getContactNumber());
                if(contact!=null && contact.getContactNumber()!=null)
                    contacts.add(contact);
            }while(c.moveToNext());
        }

        c.close();
        return contacts;
    }
    public int selectionnerContactGroupe(int groupe_id,int contact_id){
        SQLiteDatabase db=this.conn.getReadableDatabase();
        String query="SELECT id FROM "+ConnexionBD.getTableGroupeContact()+" WHERE "+ConnexionBD.getGcIdGroupe()+" = "+groupe_id+" and "+ConnexionBD.getGcIdContact()+" = "+contact_id;

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        if(c.moveToFirst()){

                return c.getInt(c.getColumnIndex(ConnexionBD.getKeyId()));
        }

        c.close();
        return 0;
    }
    public int supprimerContactsGroupe(int groupe_id, Contact contact){
        SQLiteDatabase db=this.open();

         return db.delete(ConnexionBD.getTableGroupeContact(),ConnexionBD.getGcIdContact()+"= ? AND "+ConnexionBD.getGcIdGroupe()+"= ?",
                new String []{String.valueOf(selectionnerIDContactByNumber(contact)),String.valueOf(groupe_id)});
    }

    public Contact_DAO(Context context) {
        super(context);
    }
}
