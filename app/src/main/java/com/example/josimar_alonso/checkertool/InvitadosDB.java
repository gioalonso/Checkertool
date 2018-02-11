package com.example.josimar_alonso.checkertool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by josimar_alonso on 1/5/2018.
 */

public class InvitadosDB extends SQLiteOpenHelper {

    private static final String NombreDB = "invitados.db";
    private static final int VERSION_DB=1;
    private static final String TablaDatos = "CREATE TABLE Invitados (NUMEMP TEXT PRIMARY KEY, NOMBREMP TEXT)";

    public InvitadosDB(Context context) {
        super(context, NombreDB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TablaDatos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+TablaDatos);
        sqLiteDatabase.execSQL(TablaDatos);
    }


    // Drops the table, so new data can be added
    public void limpiarData(){
        SQLiteDatabase db= getWritableDatabase();
        if (db != null){
            db.execSQL("DROP TABLE Invitados");
            //db.execSQL("CREATE TABLE Invitados (NUMEMP TEXT PRIMARY KEY, NOMBREMP TEXT)");
            //db.execSQL("INSERT INTO Invitados VALUES ('"+numemp+"','"+nombremp+"')");
            db.close();
        }
    }

    // Creates Invitados Table (guests list)
    public void creaTabla(){
        SQLiteDatabase db= getWritableDatabase();
        if (db != null){
            //db.execSQL("DROP TABLE Invitados");
            db.execSQL("CREATE TABLE Invitados (NUMEMP TEXT, NOMBREMP TEXT)");
            //db.execSQL("INSERT INTO Invitados VALUES ('"+numemp+"','"+nombremp+"')");
            db.close();
        }
    }

    // Inserts Excels data as table in local database
    public void agregarDatos(String numemp, String nombremp){
        SQLiteDatabase db= getWritableDatabase();
        if (db != null){
            //db.execSQL("DROP TABLE Invitados");
            //db.execSQL("CREATE TABLE Invitados (NUMEMP TEXT PRIMARY KEY, NOMBREMP TEXT)");
            db.execSQL("INSERT INTO Invitados VALUES ('"+numemp+"','"+nombremp+"')");
            db.close();
        }
    }


    // Validates if the person number is in the guests list
    public String returndata(String linea) {
        SQLiteDatabase db= getReadableDatabase();
        String Query = "Select NOMBREMP from  Invitados WHERE NUMEMP = '"+linea+"'";
        String nomemp = null;

        try {
        Cursor cursor = db.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                nomemp = cursor.getString(0);
            }cursor.close();

        }catch (Exception e){
            nomemp = null;
            return nomemp;
        }

        return nomemp;
    }

}
