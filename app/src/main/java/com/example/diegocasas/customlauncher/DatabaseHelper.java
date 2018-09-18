package com.example.diegocasas.customlauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mcc.sq3";
    //private static final String DATABASE_NAME = "test.db3";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    SQLiteDatabase db;

    //private static final String DATABASE_PATH = "/data/data/com.example.diegocasas.customlauncher/databases/";
    private static final String DATABASE_PATH = "/storage/emulated/0/mcc/db/";
    private final String USER_TABLE = "TR_SEGURIDAD";
    //private final String USER_TABLE = "user";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        createDb();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void createDb(){
        boolean dbExist = checkDbExist();

        if(!dbExist){
            this.getReadableDatabase();
            copyDatabase();
        }
    }
    private boolean checkDbExist(){
        SQLiteDatabase sqLiteDatabase = null;

        try{
            String path = DATABASE_PATH + DATABASE_NAME;
            sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception ex){
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if(sqLiteDatabase != null){
            sqLiteDatabase.close();
            return true;
        }
        return false;
    }

    private void copyDatabase(){
        try {
            InputStream inputStream = context.getAssets().open(DATABASE_NAME);

            String outFileName = DATABASE_PATH + DATABASE_NAME;

            OutputStream outputStream = new FileOutputStream(outFileName);

            byte[] b = new byte[1024];
            int length;

            while ((length = inputStream.read(b)) > 0){
                outputStream.write(b, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private SQLiteDatabase openDatabase(){

        String path = DATABASE_PATH + DATABASE_NAME;
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        return db;
    }

    public void close(){
        if(db != null){
            db.close();
        }
    }

    public boolean checkUserSup(String CVEOPER, String  CONTRA){
        String[] columns = {"CVEOPER"};
        //String[] columns = {"username"};
        db = openDatabase();

        //String selection = "username = ? and password = ?";
        //String[] selectionArgs = {username, password};

        String selection = "CVEOPER = ? and CONTRA = ? and TIPO = ?";
        String[] selectionArgs = {CVEOPER, CONTRA, "SUPERVISOR"};

        Cursor cursor = db.query(USER_TABLE, columns, selection, selectionArgs, null, null, null);

        int count = cursor.getCount();

        cursor.close();
        close();

        if(count > 0){
            return true;
        } else {
            return false;
        }
    }
    public void updateAccessSup(String CVEOPER, String  CONTRA){
        clearColumn();
        db = openDatabase();

        String selection = "CVEOPER = ? and CONTRA = ? and TIPO = ?";
        String[] selectionArgs = {CVEOPER, CONTRA, "SUPERVISOR"};

        ContentValues values = new ContentValues();
        values.put("UACCESO","1");
        db.update("TR_SEGURIDAD",values,selection, selectionArgs);
    }

    public boolean checkUserEnt(String CVEOPER, String  CONTRA){
        String[] columns = {"CVEOPER"};
        //String[] columns = {"username"};
        db = openDatabase();

        //String selection = "username = ? and password = ?";
        //String[] selectionArgs = {username, password};

        String selection = "CVEOPER = ? and CONTRA = ? and TIPO = ?";
        String[] selectionArgs = {CVEOPER, CONTRA, "ENTREVISTADOR"};

        Cursor cursor = db.query(USER_TABLE, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();

        cursor.close();
        close();

        if(count > 0){
            return true;
        } else {
            return false;
        }
    }
    public void updateAccessEnt(String CVEOPER, String CONTRA){
        clearColumn();
        db = openDatabase();

        String selection = "CVEOPER = ? and CONTRA = ? and TIPO = ?";
        String[] selectionArgs = {CVEOPER, CONTRA, "ENTREVISTADOR"};

        ContentValues values = new ContentValues();
        values.put("UACCESO","1");
        db.update("TR_SEGURIDAD",values,selection, selectionArgs);
    }

    public void clearColumn(){
        db = openDatabase();
        ContentValues values = new ContentValues();
        values.put("UACCESO", (String) null);
        db.update("TR_SEGURIDAD", values, null, null);
    }
}
