package cajac.aliveline;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Amauris on 6/3/2015.
 */
public class Todo implements Parcelable {
    public String title;
    public Date dueDate;
    public String estimatedTime;
    public int timeUsage;
    int id;
    public String startTime;
    public String remainingTime;
    public String locks;
    public int mData;
    public HashMap<String, String> relationalValues;
    public String todaysTimeLeft;
    String dueDateString;


    public Todo(){

    }

    public Todo(String title, Date dueDate, String estimatedTime){
        this.title = title;
        this.dueDate = dueDate;
        this.estimatedTime = estimatedTime;
    }

    public String getTitle(){
        return title;
    }


    public Date getDueDate(){
        return dueDate;
    }

    public void setTitle(String newTitle){
        this.title = newTitle;
    }


    public void setDueDate(Date newDate){
        this.dueDate = newDate;
        setDueDateString(newDate);
    }


    //this part of the code and some of the fields edited by Alex.
    //at the moment, the google doc says that the estimated time is in minutes.
    public String getEstimatedTime() {
        return this.estimatedTime;
    }


    public void setEstimatedTime(String newEstimate) {
        this.estimatedTime = newEstimate;
    }


    public int getTimeUsage() {
        return timeUsage;
    }

    public void setTimeUsage(int option) {
        timeUsage = option;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public String getStartTime(){
        return startTime;
    }

    public void setRemainingTime(String remainingTime){
        this.remainingTime = remainingTime;
    }

    public String getRemainingTime(){
        return remainingTime;
    }

    public void setLocks(String locks){
        this.locks = locks;
    }

    public String getLocks(){
        return locks;
    }

    public void setTodaysTimeLeft(String timeLeft){
        this.todaysTimeLeft = timeLeft;
    }

    public String getTodaysTimeLeft(){
        return todaysTimeLeft;
    }

    public String getDueDateString(){
        return dueDateString;
    }

    public void setDueDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        String strDate = sdf.format(date);
        dueDateString = strDate;
    }

    public String getDateString(){
        return dueDateString;
    }



    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(mData);
    }

    public int describeContents(){
        return 0;
    }


    public static final Parcelable.Creator<Todo> CREATOR=
            new Parcelable.Creator<Todo>() {
                public Todo createFromParcel(Parcel in) {
                    return new Todo(in);
                }

                public Todo[] newArray(int size){
                    return new Todo[size];
                }
            };
    private Todo(Parcel in){
        mData = in.readInt();
    }
}
