package com.example.visas.genielogiciel2.Model.BD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class ConnexionBD extends SQLiteOpenHelper {

    public static String getLOG() {
        return LOG;
    }

    //logcat tag
    private static final String LOG="ConnexionBD";

    //version de la base de données
    /*private static final int DATABASE_VERSION=1;

    //nom de la base de données
    private static final String DATABASE_NAME="groupMessageApp";*/

    //nom des tables
    private static final String TABLE_CONTACT="Contact";
    private static final String TABLE_GROUPE="Groupe";
    private static final String TABLE_MESSAGE="Message";
    private static final String TABLE_GROUPE_CONTACT="Groupe_Contact";
    private static final String TABLE_MESSAGE_GROUPE="Message_Groupe";

    //colonne commune
    private static final String KEY_ID="id";

    //Table Contact - nom des colonnes
    private static final String NOM_CONTACT="nomContact";
    private static final String NUMERO_CONTACT="numero";
    private static final String OPERATEUR="operateur";

    //Table Groupe
    private static final String NOM_GROUPE="nomGroupe";
    private static final String INITIALS_GROUPE="initiales";

    //Table Message
    private static final String TITRE="titre";
    private static final String DATE_ENVOI="dateEnvoi";
    private static final String DATE_MODIFICATION="dateModification";
    private static final String CONTENU="contenu";
    private static final String SENT="Sent";

    //table groupe - contact
    private static final String GC_ID_GROUPE="idGroupe";
    private static final String GC_ID_CONTACT="idContact";

    //table Message - groupe
    private static final String MG_ID_MESSAGE="idMessage";
    private static final String MG_ID_GROUPE="idGroupe";

    //requete de création des tables
    //requete création table Contact
    public static final String CREATE_TABLE_CONTACT=
            "CREATE TABLE "+TABLE_CONTACT
            +" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +NOM_CONTACT+" TEXT NOT NULL,"
                    +OPERATEUR+" TEXT NOT NULL,"
            +NUMERO_CONTACT+" INTEGER UNIQUE NOT NULL);";

    //requete création de la table Groupe
    public static final String CREATE_TABLE_GROUPE="CREATE TABLE "+TABLE_GROUPE
            +" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +INITIALS_GROUPE+" TEXT,"
            +NOM_GROUPE+" TEXT UNIQUE NOT NULL);";

    //requete création de la table Message
    public static final String CREATE_TABLE_MESSAGE="CREATE TABLE "+TABLE_MESSAGE
            +" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +TITRE+" TEXT UNIQUE NOT NULL,"
            +CONTENU+" TEXT,"
            +SENT+" TEXT,"
            +DATE_ENVOI+" TEXT,"
            +DATE_MODIFICATION+" TEXT NOT NULL);";

    //requete création de la table Groupe_Contact
    public static final String CREATE_TABLE_GROUPE_CONTACT="CREATE TABLE "+TABLE_GROUPE_CONTACT
            +" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +GC_ID_CONTACT+" INTEGER ,"
            +GC_ID_GROUPE+" INTEGER);";

    //requete création de la table Message_Groupe
    public static final String CREATE_TABLE_MESSAGE_GROUPE="CREATE TABLE "+TABLE_MESSAGE_GROUPE
            +" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +MG_ID_MESSAGE+" INTEGER ,"
            +MG_ID_GROUPE+" INTEGER);";

    public ConnexionBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //création des tables
        db.execSQL(CREATE_TABLE_CONTACT);
        Log.d("DBHelper","On created sql : "+TABLE_CONTACT);
        db.execSQL(CREATE_TABLE_GROUPE);
        Log.d("DBHelper","On created sql : "+TABLE_GROUPE);
        db.execSQL(CREATE_TABLE_MESSAGE);
        Log.d("DBHelper","On created sql : "+TABLE_MESSAGE);
        Log.d("requete de création ",CREATE_TABLE_MESSAGE);
        db.execSQL(CREATE_TABLE_GROUPE_CONTACT);
        Log.d("DBHelper","On created sql : "+TABLE_GROUPE_CONTACT);
        db.execSQL(CREATE_TABLE_MESSAGE_GROUPE);
        Log.d("DBHelper","On created sql : "+TABLE_MESSAGE_GROUPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //en cas de mise à jour suppression des anciennes tables
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_GROUPE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_GROUPE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_MESSAGE_GROUPE);

        Log.d("DBHelper","On Updated");
        //création des nouvelles tables
        onCreate(db);
    }

    public static String getInitialsGroupe() {
        return INITIALS_GROUPE;
    }

    public static String getSENT() {
        return SENT;
    }

    public static String getOPERATEUR() {
        return OPERATEUR;
    }

    public static String getTableContact() {
        return TABLE_CONTACT;
    }

    public static String getTableGroupe() {
        return TABLE_GROUPE;
    }

    public static String getTableMessage() {
        return TABLE_MESSAGE;
    }

    public static String getKeyId() {
        return KEY_ID;
    }

    public static String getNomContact() {
        return NOM_CONTACT;
    }

    public static String getNumeroContact() {
        return NUMERO_CONTACT;
    }

    public static String getNomGroupe() {
        return NOM_GROUPE;
    }

    public static String getTITRE() {
        return TITRE;
    }

    public static String getDateEnvoi() {
        return DATE_ENVOI;
    }

    public static String getDateModification() {
        return DATE_MODIFICATION;
    }

    public static String getCONTENU() {
        return CONTENU;
    }

    public static String getTableGroupeContact() {
        return TABLE_GROUPE_CONTACT;
    }

    public static String getTableMessageGroupe() {
        return TABLE_MESSAGE_GROUPE;
    }

    public static String getGcIdGroupe() {
        return GC_ID_GROUPE;
    }

    public static String getGcIdContact() {
        return GC_ID_CONTACT;
    }

    public static String getMgIdMessage() {
        return MG_ID_MESSAGE;
    }

    public static String getMgIdGroupe() {
        return MG_ID_GROUPE;
    }
}
