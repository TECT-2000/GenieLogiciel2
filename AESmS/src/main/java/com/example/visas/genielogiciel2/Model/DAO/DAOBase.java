package com.example.visas.genielogiciel2.Model.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.visas.genielogiciel2.Model.BD.ConnexionBD;

public abstract class DAOBase {

    //version de la base de données
    protected static final int DATABASE_VERSION=8;

    //nom de la base de données
    protected static final String DATABASE_NAME="groupMessageApp.db";

    protected SQLiteDatabase db=null;
    protected ConnexionBD conn;

    public DAOBase(Context context) {
        this.conn = new ConnexionBD(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public SQLiteDatabase open(){
        db=conn.getWritableDatabase();
        return db;
    }
    public void closeDB(){
         this.db=conn.getReadableDatabase();
        if(db !=null && db.isOpen())
            db.close();
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
