package lk.javainstitute.automotivex.model;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE login_history (\n" +
                "    id               INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                "                             NOT NULL,\n" +
                "    email            TEXT    NOT NULL,\n" +
                "    first_name       TEXT    NOT NULL,\n" +
                "    last_name        TEXT    NOT NULL,\n" +
                "    logged_date_time TEXT    NOT NULL,\n" +
                "    status           TEXT    NOT NULL\n" +
                ");\n");
        Log.i("AutoMotiveXLog","SQLite On Create Called");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i("AutoMotiveXLog","SQLite On Upgrade Called");
    }
}
