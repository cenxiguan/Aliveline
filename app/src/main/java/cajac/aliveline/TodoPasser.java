package cajac.aliveline;

/**
 * Created by Jonathan Maeda on 7/27/2015.
 */
public class TodoPasser {
    private static TodoPasser todoPasser = null;

    private TodoPasser(){}

    public static TodoPasser getInstance() {
        if (todoPasser == null)
            todoPasser = new TodoPasser();
        return todoPasser;
    }

    private Todo aTodo;

    public Todo getTodo() {
        return aTodo;
    }

    public void setTodo(Todo aTodo){
        this.aTodo = aTodo;
    }
}
