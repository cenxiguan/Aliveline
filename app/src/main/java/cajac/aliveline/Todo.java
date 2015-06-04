package cajac.aliveline;

import java.util.Date;

/**
 * Created by Amauris on 6/3/2015.
 */
public class Todo {
    public String title;
    public String description;
    public Date dueDate;


    public Todo(){

    }

    public Todo(String title, String description, Date dueDate){
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
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
}
