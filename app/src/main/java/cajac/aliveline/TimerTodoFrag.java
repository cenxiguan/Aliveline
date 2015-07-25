package cajac.aliveline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Christine on 6/4/2015.
 * The list portion of the Timer fragment
 */
public class TimerTodoFrag extends Fragment {

    DatabaseHelper dbh;
    List<Todo> todaysList;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_timer_todo, container, false);
        populateListView();

        return view;
    }

    private void populateListView(){
        //get list of todos
        dbh = new DatabaseHelper(getActivity());
        todaysList = new ArrayList<Todo>();
        todaysList = dbh.getAllToDosByDay(new Date());

        //Build Adapter
        ArrayAdapter<Todo> adapter = new TodoListAdapter();

        ListView list = (ListView) view.findViewById(R.id.listView1);
        list.setAdapter(adapter);
    }

    private class TodoListAdapter extends ArrayAdapter<Todo> {
        public TodoListAdapter(){
            super(getActivity(),R.layout.todo_list_item,todaysList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            //make sure working with view
            View itemView = convertView;
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.todo_list_item, parent, false);
            }

            //the 2do we're working with
            Todo currentTodo = todaysList.get(position);

            //fill the view
            TextView title = (TextView) itemView.findViewById(R.id.todo_title);
            TextView dueDate = (TextView) itemView.findViewById(R.id.todo_due_date);
            TextView timeDone = (TextView) itemView.findViewById(R.id.todo_time_done);

            title.setText(currentTodo.getTitle());
            dueDate.setText("Due: " + currentTodo.getDueDateString());
            timeDone.setText(currentTodo.getStartTime());

            return itemView;
        }
    }
}
