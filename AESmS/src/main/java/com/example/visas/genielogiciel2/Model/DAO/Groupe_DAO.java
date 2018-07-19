package com.example.visas.genielogiciel2.Model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.visas.genielogiciel2.Model.BD.ConnexionBD;
import com.example.visas.genielogiciel2.Model.Principal.Contact;
import com.example.visas.genielogiciel2.Model.Principal.Groupe;

import java.util.ArrayList;

/*
     A MODIFIER
 */

public class Groupe_DAO extends DAOBase{
    //méthodes pour communiquer avec la table Groupe
    Contact_DAO contactDao;
    /*
     *créer un groupe avec contacts
     */
    public long enregisterGroupe(Groupe groupe){
        SQLiteDatabase db= this.conn.getWritableDatabase();
        long groupe_res=0;
        ContentValues values=new ContentValues();
        values.put(ConnexionBD.getInitialsGroupe(),groupe.getGroupInitials());
        values.put(ConnexionBD.getNomGroupe(),groupe.getGroupName());

        //insertion dans la BD
        if(selectionnerIdGroupe(groupe.getGroupName())==0) {
            groupe_res = db.insert(ConnexionBD.getTableGroupe(), null, values);

            if (groupe.getContacts() != null)
                enregistrerContactsGroupe(groupe.getContacts(), (int) groupe_res);
        }else
            return 0;

        this.closeDB();
        return groupe_res;
    }

    /*
     *récupérer un groupe de la bd à partir de son identifiant
     */
    public Groupe selectionnerGroupeByID(int groupe_id){
        SQLiteDatabase db=this.conn.getReadableDatabase();

        String query="SELECT * FROM "+ConnexionBD.getTableGroupe()+" WHERE "+ConnexionBD.getKeyId()+" = '"+groupe_id+"'";
        Groupe groupe;
        ArrayList<Contact> contacts=new ArrayList<>();
        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        if(c.moveToFirst()) {

            contacts = contactDao.selectionnerContactsGroupe(groupe_id);
        }
            groupe = new Groupe(groupe_id, c.getString(c.getColumnIndex(ConnexionBD.getNomGroupe())), contacts);

        c.close();
        return groupe;
    }

    /*
     *récupérer un groupe de la bd à partir de son identifiant
     */
    public ArrayList<Groupe> selectionnerGroupes(){
        SQLiteDatabase db=this.conn.getReadableDatabase();

        String query="SELECT * FROM "+ConnexionBD.getTableGroupe();
        ArrayList<Groupe> groupes=new ArrayList<>();
        ArrayList<Contact> contacts;
        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        if(c.moveToFirst()) {
            do {

                contacts = contactDao.selectionnerContactsGroupe(c.getInt(c.getColumnIndex(ConnexionBD.getKeyId())));
                Groupe groupe = new Groupe(c.getInt(c.getColumnIndex(ConnexionBD.getKeyId())),c.getString(c.getColumnIndex(ConnexionBD.getInitialsGroupe())) ,c.getString(c.getColumnIndex(ConnexionBD.getNomGroupe())), contacts);

                groupes.add(groupe);
            }while(c.moveToNext());
        }

        c.close();
        return groupes;
    }
    public Groupe selectionnerGroupe(String nom){
        SQLiteDatabase db=this.conn.getReadableDatabase();

        String query="SELECT * FROM "+ConnexionBD.getTableGroupe()+" WHERE "+ConnexionBD.getNomGroupe()+" = '"+nom+"'";
        ArrayList<Groupe> groupes=new ArrayList<>();
        ArrayList<Contact> contacts;
        Log.e(ConnexionBD.getLOG(),query);
        Groupe groupe=null;
        Cursor c=db.rawQuery(query,null);

        if(c.moveToFirst()) {


                contacts = contactDao.selectionnerContactsGroupe(c.getInt(c.getColumnIndex(ConnexionBD.getKeyId())));
                groupe=new Groupe(c.getInt(c.getColumnIndex(ConnexionBD.getKeyId())),c.getString(c.getColumnIndex(ConnexionBD.getInitialsGroupe())) ,c.getString(c.getColumnIndex(ConnexionBD.getNomGroupe())), contacts);

        }

        c.close();
        return groupe;
    }
    /*
     *récupérer l'identifiant d'un groupe de la bd à partir de son nom
     */
    public int selectionnerIdGroupe(String nomGroupe){
        SQLiteDatabase db=this.conn.getReadableDatabase();
        int id=0;
        String query="SELECT * FROM "+ConnexionBD.getTableGroupe()+" WHERE "+ConnexionBD.getNomGroupe()+" = '"+nomGroupe+"'";

        Log.e(ConnexionBD.getLOG(),query);

        Cursor c=db.rawQuery(query,null);

        if(c.moveToFirst()) {

            id = c.getInt(c.getColumnIndex(ConnexionBD.getKeyId()));
            System.out.println("groupe id : "+id);
        }
        c.close();
        return id;
    }
    /*
     *modifier un contact de la bd
     */
    public int modifierNomGroupe(String ancienNom,String nouveauNom,String initials) {
        SQLiteDatabase db=this.open();

        ContentValues values=new ContentValues();
        values.put(ConnexionBD.getNomGroupe(),nouveauNom);
        values.put(ConnexionBD.getInitialsGroupe(),initials);

        //mise à jour de l'enregistrement
        return db.update(ConnexionBD.getTableGroupe(),values,ConnexionBD.getKeyId()+" = ?",
                new String[]{String.valueOf(selectionnerIdGroupe(ancienNom))});
    }

    /*
        supprimer un membre à un groupe
     */
    public boolean supprimerContactGroupe(Contact contact,String nomGroupe){
        SQLiteDatabase db=this.conn.getWritableDatabase();

        long resp=db.delete(ConnexionBD.getTableGroupeContact(),ConnexionBD.getGcIdContact()+" = ?",
        new String[]{String.valueOf(contactDao.selectionnerIDContactByNumber(contact))});

        this.closeDB();
        return (resp!=0);
    }
    /*
     *supprimer un contact de la bd à partir de son identifiant
     */
    public int supprimerGroupe(String nomGroupe){
        SQLiteDatabase db=this.open();
        int idgroupe=selectionnerIdGroupe(nomGroupe);
        System.out.println(idgroupe);

        db.delete(ConnexionBD.getTableGroupeContact(),ConnexionBD.getGcIdGroupe()+" = ?",new String[]{String.valueOf(idgroupe)});

        return db.delete(ConnexionBD.getTableGroupe(),ConnexionBD.getKeyId()+" = ?",
                new String []{String.valueOf(idgroupe)});
    }

    public long enregistrerContactGroupe(Contact contact, int idGroupe){

        SQLiteDatabase db= this.conn.getWritableDatabase();
        long contact_res=0;
        ContentValues values = new ContentValues();
            int id=contactDao.selectionnerIDContactByNumber(contact);
            if(contactDao.selectionnerContactGroupe(idGroupe,id)==0){
                values.put(ConnexionBD.getGcIdContact(), id);
                values.put(ConnexionBD.getGcIdGroupe(), idGroupe);

                //insertion dans la BD
                contact_res = db.insert(ConnexionBD.getTableGroupeContact(), null, values);
            }

        this.closeDB();


        return contact_res;
    }
    public long enregistrerContactsGroupe(ArrayList<Contact> contacts, int idGroupe){

        SQLiteDatabase db= this.conn.getWritableDatabase();
        long contact_res=0;
        ContentValues values = new ContentValues();
        for(Contact c: contacts) {
            int id=contactDao.selectionnerIDContactByNumber(c);
            if(contactDao.selectionnerContactGroupe(idGroupe,id)==0){
                values.put(ConnexionBD.getGcIdContact(), id);
                values.put(ConnexionBD.getGcIdGroupe(), idGroupe);

                //insertion dans la BD
                contact_res = db.insert(ConnexionBD.getTableGroupeContact(), null, values);
        }
        }
        this.closeDB();


        return contact_res;
    }

    public Groupe_DAO(Context context) {
        super(context);
    contactDao=new Contact_DAO(context);
    }
}
