package cajac.aliveline;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_ESTIMATED_TIME = "est_time";
    public static final String COLUMN_TIME_USAGE = "time_usage";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_REMAINING_TIME = "time_remaining";
    //Column names for Dates table
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTES = "notes";
    //Column names for todo_dates table
    public static final String KEY_TODO_ID = "todo_id";
    public static final String KEY_DATES_ID = "dates_id";

    public static final String LOCK = "lock";
    public static final String LOCKS = "locks";
    public static final String COLUMN_TIME_REQUIRED = "time_required";
    public static final String COLUMN_TIME_COMPLETED = "time_completed";

    public static final String CREATE_TODO_TABLE = "CREATE TABLE " +  TABLE_TODO + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TITLE + " TEXT, " +
        COLUMN_DUE_DATE + " DATETIME, " + COLUMN_ESTIMATED_TIME + " TEXT, " + COLUMN_TIME_USAGE + " TEXT, " + COLUMN_START_TIME + " TEXT, " + COLUMN_REMAINING_TIME
    + " TEXT, " + LOCKS + " TEXT" +  ")";

    public static final String CREATE_DATE_TABLE = "CREATE TABLE " + TABLE_DATES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE + " DATETIME UNIQUE, " + COLUMN_NOTES + " TEXT" + ")";

    //What columns should be included in relational?
    public static final String CREATE_TODO_DATES_TABLE = "CREATE TABLE " + TABLE_TODO_DATES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TODO_ID + " INTEGER," + KEY_DATES_ID + " INTEGER," + LOCK + " INTEGER, " + COLUMN_TIME_REQUIRED + " TEXT, " +
            COLUMN_TIME_COMPLETED + " TEXT" + ")";

    SharedPreferences sharedPreferences;
    private static int maxHours;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        maxHours = sharedPreferences.getInt(context.getString(R.string.max_hours), 8);
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


    public long createToDo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values = setTodoContentValues(todo, values);
        // insert row
        long todo_id = db.insert(TABLE_TODO, null, values);
        addToRemainingTables(todo_id, todo);
        return todo_id;
    }

    public void addToRemainingTables(long todo_id, Todo todo){
        String firstDate = dateToStringFormat(new Date());
        //For now, first day will be the day that Todo is created
        //firstDate = getNextDay(firstDate);
        String lastDate = dateToStringFormat(todo.getDueDate());
        /*
        Get list of Dates and their hours
        May need to sort the Dates before getting hours though
        Maybe we can just get the dates and then sort them and then just go through the list of dates

         */

        // Checks to see if all of the dates included in range exist.
        // Basically try creating the new row (date) and if it fails that just means it already exists
        checkDates(firstDate, lastDate);

        // Go through dates table and select dates that are in the range and are not locked
        // availableDates are the ids, not the dates themselves
        // SELECT where between first and last AND where locked is 1
        List<Integer> availableDates = getAvailableDates(firstDate, lastDate, todo.getLocks());
        double estimatedTime = minToHours(timeInMinutes(todo.getRemainingTime()));

        //Should distribute time
        Distributor dist = new Distributor(estimatedTime, availableDates.size(), maxHours, todo.getTimeUsage());
        List<Double> distributedHours = dist.distribute();

        // Get sum of each day's hours and make list
        List<Double> hoursInDatabase = getAvailableDateHours(availableDates);

        // Should combine the numbers in the two lists. If there isn't enough time in a day to fit the
        // schedule for the toDo, the database takes time from the todo on that day and moves it to others
        dist.addTimes(hoursInDatabase, distributedHours);

        // Here I think we're adding the rows to the relational table
        String distributedTime;
        for (int i = 0; i < availableDates.size(); i++) {
            distributedTime = timeInHours((int) (distributedHours.get(i) * 60));
            addTodoDate(todo_id, availableDates.get(i), 1, distributedTime, "00:00");
        }

//        while (!firstDayString.equals(lastDay)){
//            Date firstDayDate = convertStringDate(firstDayString);
//            long date_id = createDate(firstDayDate, null);
//            int lock = Integer.parseInt(locks.substring(boolPos, boolPos + 1));
//            String timeRequired = getTimeForDay(locks, lock, todo);
//            addTodoDate(todo_id, date_id, lock, timeRequired, "00:00");
//            firstDayString = getNextDay(firstDayString);
//            boolPos++;
//        }
    }

    // Don't need because of Distributor class
    // Keep this for now so other parts of code works
    public String getTimeForDay(String locks, int lock, Todo todo){
        int numOfDaysWorking = locks.length() - locks.replace("1","").length();
        String total_hours = todo.getStartTime();
        int minutes = timeInMinutes(total_hours);
        if (lock == 0){
            return "00:00";
        } else {
            int dist = minutes/numOfDaysWorking;
            String distIntHours = timeInHours(dist);
            return distIntHours;
        }
    }

    /**
     * Makes sure that all dates between two parameters are in the database.
     * @param firstDate
     * @param lastDate
     */
    public void checkDates(String firstDate, String lastDate) {
        // Make sure that the range will go from past to future to prevent infinite loop
        if (convertStringDate(firstDate).after(convertStringDate(lastDate))) {
            String temp = firstDate;
            firstDate = lastDate;
            lastDate = temp;
        }
        while(!firstDate.equals(lastDate)) {
            Date firstDayDate = convertStringDate(firstDate);
            createDate(firstDayDate, null);
        }
    }

    public List<Integer> getAvailableDates(String firstDate, String lastDate, String locks) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_DATES + " WHERE "
                + COLUMN_DATE  + " BETWEEN " + firstDate + " AND " + lastDate
                + " ORDER BY " + COLUMN_DATE;
        Cursor c = db.rawQuery(selectQuery, null);
        List<Integer> availableDateIds = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        String date;
        int dateId;
        if (c.moveToFirst()){
            do {
                date = c.getString(c.getColumnIndex(COLUMN_DATE));
                cal.setTime(convertStringDate(date));
                // Filtering out the days here based on the locks selected in addToDo
                if (locks.charAt(cal.get(Calendar.DAY_OF_WEEK) - 1) == 0) {
                    dateId = c.getInt(c.getColumnIndex(KEY_ID));
                    availableDateIds.add(dateId);
                }
            }while (c.moveToNext());
        }

        return availableDateIds;
    }

    public List<Double> getAvailableDateHours(List<Integer> availableDateIds) {
        List<Double> availableDateHours = new ArrayList<>();
        for (int id : availableDateIds) {
            availableDateHours.add(getTotalTimeForDate(id));
        }
        return availableDateHours;
    }

    public double getTotalTimeForDate(int dateId) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TODO_DATES + " WHERE "
                + KEY_DATES_ID + " = " + dateId;
        Cursor c = db.rawQuery(selectQuery, null);
        String requiredTime;
        double sum = 0;
        if (c.moveToFirst()) {
            do {
                requiredTime = c.getString(c.getColumnIndex(COLUMN_TIME_REQUIRED));
                sum += minToHours(timeInMinutes(requiredTime));
            }while (c.moveToNext());
        }
        return sum;
    }

    public int timeInMinutes(String hoursString){
        String[] hours_min = hoursString.split(":");
        int hours = Integer.parseInt(hours_min[0]);
        int min = Integer.parseInt(hours_min[1]);
        min = min + hours * 60;
        return min;
    }

    private double minToHours(int minutes) {
        double hours = minutes / 60;
        hours += (minutes % 60) / 60.0;
        return hours;
    }

    public String timeInHours(int minutes){
        int hours = minutes / 60;
        int minutesRemainder = minutes % 60;
        String hoursStr = String.valueOf(hours);
        String minutesRemainStr = String.valueOf(minutesRemainder);
        if (hoursStr.length() == 1){
            hoursStr = "0" + hoursStr;
        }
        if (minutesRemainStr.length() == 1){
            minutesRemainStr = "0" + minutesRemainStr;
        }
        String hoursAndMin = hoursStr + ":" + minutesRemainStr;
        return hoursAndMin;
    }


    public String getNextDay(String day){
        Date date = convertStringDate(day);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(sdf.parse(day));
        } catch(ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);
        day = sdf.format(c.getTime());
        return day;
    }

    public long createDate(Date date, String notes){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        String dateString = dateToStringFormat(date);
        contentValues.put(COLUMN_DATE, dateString);
        contentValues.put(COLUMN_NOTES, notes);

        long date_id = db.insert(TABLE_DATES, null, contentValues);

        return date_id;
    }

    public Date getDate(long date_id){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_DATES + " WHERE "
                + KEY_ID + " = "  + date_id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
        }
        String dateString = c.getString(c.getColumnIndex(COLUMN_DATE));
        Date date = convertStringDate(dateString);
        return date;
    }
    public String getNotes(String date){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_DATES + " WHERE " +
                COLUMN_DATE + " = " + date;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
        }
        String notes = c.getString(c.getColumnIndex(COLUMN_NOTES));

        return notes;
    }
    //Testing purposes
    public List<String> getAllDates(){
        ArrayList<String> dates = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_DATES;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            do {
                int id = c.getInt(c.getColumnIndex(KEY_ID));
                String date = c.getString(c.getColumnIndex(COLUMN_DATE));
                String date_with_id = id + ":" + date;
                dates.add(date_with_id);
            }while (c.moveToNext());
        }
        return dates;
    }

    public int getDateID(Date date){
        SQLiteDatabase db = getReadableDatabase();
        String dateString = dateToStringFormat(date);
        int date_id = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_DATES + " WHERE "
                + COLUMN_DATE + " = '" + dateString + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            date_id = c.getInt(c.getColumnIndex(KEY_ID));
        }
        return date_id;
    }

    public String dateToStringFormat(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        return dateString;
    }

    public void deleteDate(Date date) {
        String dateString = dateToStringFormat(date);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DATES, KEY_ID + " = ?",
                new String[] {String.valueOf(dateString)});
    }

    /*
    * get single todo
    */
    public Todo getTodo(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TODO + " WHERE "
                + KEY_ID + " = " + todo_id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Todo td = new Todo();
        td = setTodoValues(td, c);
        return td;
    }

    public Date convertStringDate(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);

        }catch (ParseException e){
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return date;
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
                td = setTodoValues(td, c);
                // adding to todo list
                todos.add(td);
            } while (c.moveToNext());
        }
        return todos;
    }

    //getting all the toDo's that are under a specific day
    //not sure how to accept the day name

    public List<Todo> getAllToDosByDay(String givenDay) {

        List<Todo> todos = new ArrayList<Todo>();
        Date date = convertStringDate(givenDay);
        int date_id = getDateID(date);


        String selectQuery = "SELECT  * FROM " + TABLE_TODO_DATES + " WHERE "
                + KEY_DATES_ID + " = " + date_id + " AND " + LOCK + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                int todo_id = c.getInt(c.getColumnIndex(KEY_TODO_ID));
                String timeRequired = c.getString(c.getColumnIndex(COLUMN_TIME_REQUIRED));
                String timeCompleted = c.getString(c.getColumnIndex(COLUMN_TIME_COMPLETED));
                Todo td = getTodo(todo_id);
                String timeLeftToday = getTimeLeftforToday(timeRequired, timeCompleted);
                td.setTodaysTimeLeft(timeLeftToday);
                todos.add(td);
            } while (c.moveToNext());
        }
        return todos;
    }


    public int updateToDo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values = setTodoContentValues(todo, values);
        int updated = db.update(TABLE_TODO, values, KEY_ID + " = ?",
                new String[] { String.valueOf(todo.getId()) });
        // updating row
        String selectQuery = "SELECT  * FROM " + TABLE_TODO_DATES + " WHERE " + KEY_TODO_ID + " = '"
                + todo.getId() + "'";
        String startDay = dateToStringFormat(new Date());
        startDay = getNextDay(startDay);
        String lastDay = dateToStringFormat(todo.getDueDate());
        Cursor c = db.rawQuery(selectQuery, null);
        int lock_ind = 0;
        String locks = todo.getLocks();
        if (c.moveToFirst()){
            do {
                long id = c.getLong(c.getColumnIndex(KEY_ID));
                long todo_id = c.getLong(c.getColumnIndex(KEY_TODO_ID));
                Date firstDayDate = convertStringDate(startDay);
                long date_id = createDate(firstDayDate, null);
                int lock = Integer.parseInt(locks.substring(lock_ind, lock_ind + 1));
                String timeRequired = getTimeForDay(locks, lock, todo);
                updateTodoDate(id, todo_id, date_id, lock, timeRequired, "00:00");
                startDay = getNextDay(startDay);
                lock_ind++;
            } while(c.moveToNext() && !startDay.equals(lastDay));
        }
        if (!c.moveToNext() && !startDay.equals(lastDay)){
            addMoreRows(todo, lock_ind, startDay, lastDay);
        }else if (startDay.equals(lastDay)){
            removeExtraRows(c);
        }
        return updated;
    }

    public void addMoreRows(Todo todo, int lock_ind, String startDay, String lastDay){
        while (!startDay.equals(lastDay)){
            Date firstDayDate = convertStringDate(startDay);
            String locks = todo.getLocks();
            long date_id = createDate(firstDayDate, null);
            int lock = Integer.parseInt(locks.substring(lock_ind, lock_ind + 1));
            String timeRequired = getTimeForDay(locks, lock, todo);
            addTodoDate(todo.getId(), date_id, lock, timeRequired, "00:00");
            startDay = getNextDay(startDay);
            lock_ind++;
        }
    }

    public void removeExtraRows(Cursor c){
        //Cursor moves back twice in order to start in the first row that must be deleted
        for (int i = 0; i < 2; i++) {
            c.moveToPrevious();
        }
        while (c.moveToNext()){
            long todo_dateID = c.getInt(c.getColumnIndex(KEY_ID));
            deleteTodoDateRow(todo_dateID);
        }
    }

    public void deleteToDo(long todo_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, KEY_ID + " = ?",
                new String[] { String.valueOf(todo_id) });
        String selectQuery = "SELECT  * FROM " + TABLE_TODO_DATES + " WHERE " +
                KEY_TODO_ID + " = '" + todo_id + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            do {
                long todo_dateRowId = c.getLong(c.getColumnIndex(KEY_ID));
                deleteTodoDateRow(todo_dateRowId);
            } while (c.moveToNext());
        }
    }

    public long addTodoDate(long todo_id, long date_id, int lock, String timeRequired, String timeCompleted ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TODO_ID, todo_id);
        values.put(KEY_DATES_ID, date_id);
        values.put(LOCK, lock);
        values.put(COLUMN_TIME_REQUIRED, timeRequired);
        values.put(COLUMN_TIME_COMPLETED, timeCompleted);
        long todo_date_id = db.insert(TABLE_TODO_DATES, null, values);
        return todo_date_id;
    }
    //THis is for testing purposes, will be removed later on
    public List<String> getAllTodoDates(){
        SQLiteDatabase db = getReadableDatabase();
        List<String> todoDates = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_TODO_DATES;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do {
                int id = c.getInt(c.getColumnIndex(KEY_ID));
                int date_id = c.getInt(c.getColumnIndex(KEY_DATES_ID));
                int todo_id = c.getInt(c.getColumnIndex(KEY_TODO_ID));
                String timeRequired = c.getString(c.getColumnIndex(COLUMN_TIME_REQUIRED));
                String timeComp = c.getString(c.getColumnIndex(COLUMN_TIME_COMPLETED));
                int lock = c.getInt(c.getColumnIndex(LOCK));
                String todo_date = KEY_ID + " " + id + " date_id " + date_id + " todo_id " + todo_id
                        + " Time required " + timeRequired + " " + " Time compl. " + timeComp + " lock " + lock;
                todoDates.add(todo_date);

            }while (c.moveToNext());
        }
        return todoDates;
    }

    public long updateTodoDate(long id, long todo_id, long date_id, int lock, String timeRequired, String timeCompleted){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TODO_ID, todo_id);
        values.put(KEY_DATES_ID, date_id);
        values.put(LOCK, lock);
        values.put(COLUMN_TIME_REQUIRED, timeRequired);
        values.put(COLUMN_TIME_COMPLETED, timeCompleted);
        long todo_date_id = db.update(TABLE_TODO_DATES, values, KEY_ID + " = ?" ,
                new String[] {String.valueOf(id)});
        return todo_date_id;
    }
    public void deleteTodoDateRow(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO_DATES, KEY_ID + " = ?",
                new String[] {String.valueOf(id)});
    }
    //setTodo values will be called to be used any time a todos values must be set, to avoid repetition
    public Todo setTodoValues(Todo td, Cursor c){
        td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        td.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
        String dateString = c.getString(c.getColumnIndex(COLUMN_DUE_DATE));
        td.setLocks(c.getString(c.getColumnIndex(LOCKS)));
        Date dueDate = convertStringDate(dateString);
        td.setDueDate(dueDate);
        td.setEstimatedTime(c.getString(c.getColumnIndex(COLUMN_ESTIMATED_TIME)));
        td.setTimeUsage(c.getInt(c.getColumnIndex(COLUMN_TIME_USAGE)));
        td.setStartTime(c.getString(c.getColumnIndex(COLUMN_START_TIME)));
        td.setRemainingTime(c.getString(c.getColumnIndex(COLUMN_REMAINING_TIME)));
        return td;
    }
    //setTodoContentValues sets the content values for todos, created to get rid of repetition
    public ContentValues setTodoContentValues(Todo todo, ContentValues values){
        values.put(COLUMN_TITLE, todo.getTitle());
        values.put(LOCKS, todo.getLocks());
        values.put(COLUMN_ESTIMATED_TIME, todo.getEstimatedTime());
        values.put(COLUMN_TIME_USAGE, todo.getTimeUsage());
        values.put(COLUMN_DUE_DATE, dateToStringFormat(todo.getDueDate()));
        values.put(COLUMN_START_TIME, todo.getStartTime());
        values.put(COLUMN_REMAINING_TIME, todo.getRemainingTime());
        return values;
    }

    private String getTimeLeftforToday(String timeRequired, String timeCompleted){
        int timeRqdInt = timeInMinutes(timeRequired);
        int timeCmpltInt = timeInMinutes(timeCompleted);
        int timeLeft = timeRqdInt - timeCmpltInt;
        int hoursLeft = timeLeft / 60;
        int minutesLeft = timeLeft % 60;
        return hoursLeft + " Hours, " + minutesLeft + " Minutes left ";
    }
}
