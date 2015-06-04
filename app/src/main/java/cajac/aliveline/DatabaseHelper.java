package cajac.aliveline;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alexsuk on 5/30/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    //name of table
    private static final int DATABASE_VERSION = 1;
    //name of the database file
    private static final String DATABASE_NAME = "aliveline.db";
    //Table Names
    public static final String TABLE_PRODUCTS = "Todos";
    public static final String TABLE_DATES = "Dates";
    public static final String TABLE_TODO_TAG = "todo_tags";



    public static final String COLUMN_TITLE =
    public static final String COLUMN_DESCRIPTION=
    public static final String COLUMN_DUEDATE=
    public static final String COLUMN_ESTIMATEDTIME=
    public static final String COLUMN_TIMEUSAGE = ""


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
