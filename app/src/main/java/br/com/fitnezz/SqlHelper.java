package br.com.fitnezz;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//função para envelopar toda a parte de criação de tabela e também de registros
public class SqlHelper extends SQLiteOpenHelper {

    private static SqlHelper INSTANCE;

    private static final String DB_NAME = "fitnezz_tracker.db";
    private static final int DB_VERSION = 2;

    static SqlHelper getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new SqlHelper(context);
        return INSTANCE;
    }

    private SqlHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //é disparado quando não houver uma base de dado atual
    @Override
    public void onCreate(SQLiteDatabase db) {
        //criando o db
        db.execSQL(
                "CREATE TABLE calc(id INTEGER primary key, type_calc TEXT, res DECIMAL, created_date DATETIME)"
        );
    }

    //quando subir uma versão do banco de dados, para não perder os dados anteriores
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Teste", "On upgrade disparado");
    }

    List<Register> getRegisterBy(String type) {
        List<Register> registers = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM calc WHERE type_calc = ?", new String[]{ type });

        try {
            if (cursor.moveToFirst()) {
                do {
                    Register register = new Register();

                    register.type = cursor.getString(cursor.getColumnIndexOrThrow("type_calc"));
                    register.response = cursor.getDouble(cursor.getColumnIndexOrThrow("res"));
                    register.createdDate = cursor.getString(cursor.getColumnIndexOrThrow("created_date"));

                    registers.add(register);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return registers;
    }

    long addItem(String type, double response) {
        SQLiteDatabase db = getWritableDatabase();

        //Usando try catch para pegar exception e evitar crash
        long calcId = 0;

        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("type_calc", type);
            values.put("res", response);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pt", "BR"));
            String now = sdf.format(new Date());

            values.put("created_date", now);
            calcId = db.insertOrThrow("calc", null, values);
            //escreve no banco
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (db.isOpen())
                db.endTransaction();
        }
        return calcId;
    }

}
