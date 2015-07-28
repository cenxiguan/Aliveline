

package cajac.aliveline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

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

    private BarChart mChart;
    private View rootView;
    long days_ids[];



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        noTodos = (TextView)rootView.findViewById(R.id.empty_list);
        dbh = new DatabaseHelper(getActivity());

        //this array wil display all the dates that have been made for each event
        List<String> list = new ArrayList<String>();
        list=dbh.getAllDates();

        //the array will display be the x-axis


        //i will set the X-axis to have a setting to see all the dates but have a starting limit of 7
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
                a.show(ft, "addTodo");
            }
        });
        final Intent intent1= new Intent(getActivity(), StackBarChart.class);
        Button button = (Button)rootView.findViewById(R.id.button8);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent1);
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

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        addTodo a = new addTodo();

                        //send data to the dialog
                        String theTitle = ((CardAdapter)mAdapter).getTitle(position);
                        String theDay = ((CardAdapter)mAdapter).getDay(position);
                        String theMonth = ((CardAdapter)mAdapter).getMonth(position);
                        String theYear = ((CardAdapter)mAdapter).getYear(position);
                        String theEstTime = ((CardAdapter)mAdapter).getEstTime(position);
                        String theWorkDays = ((CardAdapter)mAdapter).getWorkDays(position);
                        int theTimeUsage = ((CardAdapter)mAdapter).getTimeUsage(position);
                        int theId = ((CardAdapter)mAdapter).getId(position);
                        a.setInitialValues(theTitle, theDay, theMonth, theYear, theEstTime, theWorkDays, theTimeUsage, theId);

                        //open the dialog
                        a.show(ft, "addTodo");
                    }
                })
        );
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
       // todaysList = dbh.getAllToDosByDay(today);
        todaysList = dbh.getAllToDosByDay(new Date());
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
