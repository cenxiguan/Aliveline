package cajac.aliveline;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Christine on 6/1/2015.
 * Fragment for the first tab: HOME
 * layout file in fragment_home.xml
 */
public class HomeFragment extends Fragment{
    DatabaseHelper dbh;
    List<Todo> todaysList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    TextView noTodos;
    private View rootView;
    long days_ids[];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        noTodos = (TextView)rootView.findViewById(R.id.empty_list);

        dbh = new DatabaseHelper(getActivity());
//        Todo test = new Todo();
//        test.setTitle("Test 2");
//        Date today = new Date();
//        String todayString = dbh.dateToStringFormat(today);
//        for (int i = 0; i < 5; i++) {
////            Log.e("", "" + todayString);
//            todayString = dbh.getNextDay(todayString);
//        }
//        test.setDueDate(dbh.convertStringDate(todayString));
//        test.setStartTime("60:00");
//        test.setRemainingTime("60:00");
//        test.setEstimatedTime("3:00");
//        test.setTimeUsage(0);
//        test.setLocks("10111");
       // dbh.createToDo(test, days_ids);
        if (savedInstanceState != null) {
            todaysList = savedInstanceState.getParcelableArrayList("list");
        } else {
            getTodaysList();
        }
        createRecyclerView();
        showList();

        //add new todo button
        ImageButton b = (ImageButton)rootView.findViewById(R.id.add_todo_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                addTodo a = new addTodo();
                a.show(ft,"addTodo");
            }
        });


        return rootView;
    }

    public void createRecyclerView(){
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CardAdapter(todaysList);
        recyclerView.setAdapter(mAdapter);
        setSwipeDismiss();
    }

    public void setSwipeDismiss(){
        ItemTouchHelper mIth = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
                        return false;
                    }
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
                        final int pos = viewHolder.getAdapterPosition();
                        final Todo holdTodo = ((CardAdapter)mAdapter).removeItem(pos);
                        Snackbar.make(rootView.findViewById(R.id.recycler_home), "Removed", Snackbar.LENGTH_SHORT)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((CardAdapter) mAdapter).insert(holdTodo, pos);
                                    }
                                }).show();
                    }
                });
        mIth.attachToRecyclerView(recyclerView);
    }

    public void getTodaysList(){
        todaysList = new ArrayList<Todo>();
        String today = dbh.dateToStringFormat(new Date());
        todaysList = dbh.getAllToDosByDay(today);
    }

    public void showList() {
        if (todaysList.isEmpty()) {
            noTodos.setText(R.string.no_todos);
            recyclerView.setBackgroundColor(getResources().getColor(R.color.transparent));
        }else {
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelableArrayList("list" ,(ArrayList) todaysList);

        super.onSaveInstanceState(savedInstanceState);
    }
}
