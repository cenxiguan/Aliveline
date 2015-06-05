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
    public static final String TABLE_TODO = "Todos";
    public static final String TABLE_DATES = "Dates";
    public static final String TABLE_TODO_DATES = "todo_dates";


    //Column names for Todo table
    public static final String KEY_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "desc";
    public static final String COLUMN_DUEDATE = "due_date";
    public static final String COLUMN_ESTIMATEDTIME = "est_time";
    public static final String COLUMN_TIME_USAGE = "time_usage";

    //Column names for Dates table
    public static final String COLUMN_DATES = "dates";

    //Column names for todo_dates table
    public static final String KEY_TODO_ID = "todo_id";
    public static final String KEY_DATES_ID = "dates_id";

    public static final double HOURS = "hours";
    public static final boolean LOCK = "lock";

    public static final String CREATE_TODO_TABLE = "CREATE TABLE " +  TABLE_PRODUCTS + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TITLE + " TEXT, " + COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_DUEDATE + " DATETIME, " + COLUMN_ESTIMATEDTIME + " TEXT, " + COLUMN_TIME_USAGE + " TEXT" + ")";

    public static final String CREATE_DATE_TABLE = "CREATE TABLE " + TABLE_DATES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATES + " DATETIME" + ")";

    //What columns should be included in relaional?
    public static final String CREATE_TODO_DATES_TABLE = "CREATE TABLE " + "(" + KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TODO_ID + "INTEGER," + KEY_DATES_ID + "INTEGER," + HOURS + "INTEGER," + LOCK + "INTEGER" +  ")";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATE_TABLE);
        db.execSQL(CREATE_TODO_TABLE);
        db.execSQL(CREATE_TODO_DATES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_DATE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TODO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TODO_DATES_TABLE);

        // create new tables
        onCreate(db);
    }


    //need to edit based on how the days_ids are being formed
    public long createToDo(Todo todo, long[] days_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, todo.getTitle());
        values.put(COLUMN_DESCRIPTION, todo.getDescription());
        values.put(COLUMN_ESTIMATEDTIME, todo.getEsimatedTime());
        values.put(COLUMN_TIME_USAGE, todo.getTimeUsage());
        values.put(COLUMN_DUEDATE, todo.getDueDate());
        

        // insert row
        long todo_id = db.insert(TABLE_TODO, null, values);

        // assigning tags to todo
        for (long days_id : days_ids) {
            createTodoTag(todo_id, days_id);
        }

        return todo_id;
    }

    /*
    * get single todo
    */
    public Todo getTodo(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TODO + " WHERE "
                + KEY_ID + " = " + todo_id;

        // Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Todo td = new Todo();
        td.setId(c.getInt(c.getColumnIndex("id")));
        td.setTitle(c.getString(c.getColumnIndex("title")));
        td.setDescription(c.getString(c.getColumnIndex("desc")));
        td.setDueDate(c.getDate(c.getColumnIndex("due_date")));
        td.setEstimatedTime(c.getInt(c.getColumnIndex("est_time")));
        td.setTimeUsage(c.getInt(c.getColumnIndex("time_usage")));

        return td;
    }

    public List<Todo> getAllToDos() {
        List<Todo> todos = new ArrayList<Todo>();
        String selectQuery = "SELECT  * FROM " + TABLE_TODO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Todo td = new Todo();
                td.setId(c.getInt(c.getColumnIndex("id")));
                td.setTitle(c.getString(c.getColumnIndex("title")));
                td.setDescription(c.getString(c.getColumnIndex("desc")));
                td.setDueDate(c.getDate(c.getColumnIndex("due_date")));
                td.setEstimatedTime(c.getInt(c.getColumnIndex("est_time")));
                td.setTimeUsage(c.getInt(c.getColumnIndex("time_usage")));

                // adding to todo list
                todos.add(td);
            } while (c.moveToNext());
        }

        return todos;
    }

    //getting all the toDo's that are under a specific day
    //not sure how to accept the day name
    public List<Todo> getAllToDosByDay(String day_name) {
        List<Todo> todos = new ArrayList<Todo>();

        String selectQuery = "SELECT  * FROM " + TABLE_TODO + " td, "
                + TABLE_DATES + " , " + TABLE_TODO_DATES + " tt WHERE tg."
                + COLUMN_DATES + " = '" + tag_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_TODO_ID;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Todo td = new Todo();
                td.setId(c.getInt(c.getColumnIndex("id")));
                td.setTitle(c.getString(c.getColumnIndex("title")));
                td.setDescription(c.getString(c.getColumnIndex("desc")));
                td.setDueDate(c.getDate(c.getColumnIndex("due_date")));
                td.setEstimatedTime(c.getInt(c.getColumnIndex("est_time")));
                td.setTimeUsage(c.getInt(c.getColumnIndex("time_usage")));

                // adding to todo list
                todos.add(td);
            } while (c.moveToNext());
        }

        return todos;
    }


    public int updateToDo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, todo.getTitle());
        values.put(COLUMN_DESCRIPTION, todo.getDescription());
        values.put(COLUMN_ESTIMATEDTIME, todo.getEsimatedTime());
        values.put(COLUMN_TIME_USAGE, todo.getTimeUsage());
        values.put(COLUMN_DUEDATE, todo.getDueDate());
        // updating row
        return db.update(TABLE_TODO, values, KEY_ID + " = ?",
                new String[] { String.valueOf(todo.getId()) });
    }

    public void deleteToDo(long todo_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, KEY_ID + " = ?",
                new String[] { String.valueOf(todo_id) });
    }

}
