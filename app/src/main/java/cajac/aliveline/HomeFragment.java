

package cajac.aliveline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class HomeFragment extends ListFragment{
    DatabaseHelper dbh;
    List<Todo> todaysList;
    ArrayAdapter<Todo> cardAdapter;
    TextView noTodos;
    private BarChart mChart;

    //this will be replaced by actual dates from the database i think
    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        noTodos = (TextView)rootView.findViewById(R.id.empty_list);

        dbh = new DatabaseHelper(getActivity());
        if (savedInstanceState != null) {
            todaysList = savedInstanceState.getParcelableArrayList("list");
        } else {
            getTodaysList();
        }

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

    public void getTodaysList(){
        todaysList = new ArrayList<Todo>();
        String today = dbh.dateToStringFormat(new Date());
        todaysList = dbh.getAllToDosByDay(today);
    }

    public void showList() {
        if (todaysList.isEmpty()) {
            noTodos.setText(R.string.no_todos);
        }else {
            ListView listView = getListView();
            cardAdapter = new CardArrayAdapter(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, todaysList);
            listView.setAdapter(cardAdapter);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelableArrayList("list" ,(ArrayList) todaysList);

        super.onSaveInstanceState(savedInstanceState);
    }





}