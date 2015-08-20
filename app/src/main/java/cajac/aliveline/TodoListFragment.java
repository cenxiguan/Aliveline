package cajac.aliveline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Date;
import java.util.List;

/**
 * Created by Chungyuk Takahashi on 7/26/2015.
 */
public class TodoListFragment extends Fragment {

    private View view;
    private Date selectedDate;

    DatabaseHelper dbh;
    private RecyclerView todosRecyclerV;
    private RecyclerView.Adapter recAdapter;
    private List<Todo> recTodos;

    public TodoListFragment() { selectedDate = CalendarDay.today().getDate(); }

    public TodoListFragment(Date selectedDate) { this.selectedDate = selectedDate; }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_todolist, container, false);

        createRecyclerView(this.selectedDate);

        return view;
    }

    private void createRecyclerView(Date selectedDate){
        dbh = new DatabaseHelper(getActivity());
        //String selectedDateStr = dbh.dateToStringFormat(selectedDate);
        recTodos = dbh.getAllToDosByDay(selectedDate);
        todosRecyclerV = (RecyclerView) view.findViewById(R.id.toDoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        todosRecyclerV.setLayoutManager(layoutManager);
        recAdapter = new CardAdapter(recTodos);
        todosRecyclerV.setAdapter(recAdapter);
        dbh.close();
    }

    public void updateRecyclerAdapter(Date selectedDate){
        //String selectedDateStr = dbh.dateToStringFormat(selectedDate);
        recTodos.clear();
        recAdapter.notifyDataSetChanged();
        recTodos.addAll(dbh.getAllToDosByDay(selectedDate));
        recAdapter.notifyItemRangeChanged(0, recTodos.size());
    }

    public void fadeIn(int milliseconds) {
        view.animate().alpha(1f).setDuration(milliseconds).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    public void fadeOut(int milliseconds) {
        view.animate().alpha(0f).setDuration(milliseconds).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
    }

}
