package cajac.aliveline;

import java.util.Date;

/**
 * Created by Amauris on 6/3/2015.
 */
public class Todo {
    public String title;
    public String description;
    public Date dueDate;
    public int estimatedTime;
    public int timeUsage;
    int id;

    public Todo(){

    }

    public Todo(String title, String description, Date dueDate, int estimatedTime){
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.estimatedTime = estimatedTime;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public Date getDueDate(){
        return dueDate;
    }

    public void setTitle(String newTitle){
        this.title = newTitle;
    }

    public void setDescription(String newDesc){
        this.description = newDesc;
    }

    public void setDueDate(Date newDate){
        this.dueDate = newDate;
    }


    //this part ofthe code and some of the fields editted by Alex.
    //at the moment, the google doc says that the estimated time is in minutes.
    public int getEstimatedTime() {
        return this.estimatedTime;
    }


    public void setEstimatedTime(int newEstimate) {
        this.estimatedTime = newEstimate;
    }


    public int getTimeUsage() {
        return timeUsage;
    }

    public void setTimeUsage() {

    }

    public long getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
