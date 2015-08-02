

package cajac.aliveline;

import android.content.Intent;
import android.graphics.Color;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by Christine on 6/1/2015.
 * Fragment for the first tab: HOME
 * layout file in fragment_home.xml
 */

public class HomeFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    DatabaseHelper dbh;
    List<Todo> todaysList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    TextView noTodos;


    private SeekBar mSeekBarX;
    private TextView tvX;
    private BarChart mChart;
    private View rootView;

    long days_ids[];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        noTodos = (TextView)rootView.findViewById(R.id.empty_list);

        dbh = new DatabaseHelper(getActivity());

        //calling the method that will create the graph on the homepage
        createChart();

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
                        String theTitle = ((CardAdapter) mAdapter).getTitle(position);
                        String theDay = ((CardAdapter) mAdapter).getDay(position);
                        String theMonth = ((CardAdapter) mAdapter).getMonth(position);
                        String theYear = ((CardAdapter) mAdapter).getYear(position);
                        String theEstTime = ((CardAdapter) mAdapter).getEstTime(position);
                        String theWorkDays = ((CardAdapter) mAdapter).getWorkDays(position);
                        int theTimeUsage = ((CardAdapter) mAdapter).getTimeUsage(position);
                        int theId = ((CardAdapter) mAdapter).getId(position);
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



    public void createChart() {

        Intent intent = getActivity().getIntent();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().setContentView(R.layout.activity_barchart);


        tvX = (TextView)rootView.findViewById(R.id.tvXMax);

        mSeekBarX = (SeekBar)rootView.findViewById(R.id.seekBar1);
        mSeekBarX.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);
        mSeekBarX.setVisibility(View.GONE);


        mChart = (BarChart)rootView.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // if false values are only drawn for the stack sum, else each value is
        // drawn
        mChart.setDrawValuesForWholeStack(true);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(false);

        // change the position of the y-labels
        YAxis yLabels = mChart.getAxisLeft();
        // yLabels.setPosition(YLabelPosition.BOTH_SIDED);
//        yLabels.setLabelCount(5);
        yLabels.setValueFormatter(new MyValueFormatter());

        mChart.getAxisRight().setValueFormatter(new MyValueFormatter());
        mChart.getAxisRight().setDrawGridLines(false);

        XAxis xLabels = mChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.TOP);

        // mChart.setDrawXLabels(false);
        // mChart.setDrawYLabels(false);


        //need to find what the max size of hte database is so that the setProgress can be set to it
        List<String> datesXAxis= new ArrayList<String>();
        datesXAxis = dbh.getAllDates();
        int maxDataX = datesXAxis.size();


        // setting data
        //setProgress is +1 from what you put in the parenthesis
        mSeekBarX.setProgress(maxDataX);


        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        // mChart.setDrawLegend(false);
    }


    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        //for each date, get the date and add it to the xVals array
        List<String> xVals = new ArrayList<String>();
        xVals=dbh.getAllDates();

        Set<Integer> redBars = new HashSet<Integer>();
        int barCounter = 1;
        int totalValues = 0;
        int stackLength = 0;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();


        //adds all todoActivities that are for each date (like a stack)
        for (int i = 0; i < mSeekBarX.getProgress(); i++) {
            //for each date, I will have to go through all the todoActivities that correlate with each date
            List<Todo> stackItem;
            Date newDate = dbh.convertStringDate(xVals.get(i));
            //stackItem contains all the toDos for each date. Now the hours for each todoActivity must be gotten
            stackItem = dbh.getAllToDosByDay(new Date());
            float[]  newEntries= new float[stackItem.size()-1];
            float stackTotal = 0;
            stackLength = stackItem.size();

            //a forLoop that will add all the hours from the todoActivites from stackItem to yVals1
            for(int j = 0; j < stackItem.size() - 1; j++ ) {
                String timeInStrings= stackItem.get(j).getRemainingTime();
                int timeInMinutes = dbh.timeInMinutes(timeInStrings);
                float timeInHours = getHours(timeInMinutes);
                newEntries[j] = timeInHours;
                stackTotal += timeInHours;
            }
            yVals1.add(new BarEntry(newEntries,i));
            //a forLoop that will go through the stackItems and see if they are greater than 24hours in a day



            if (stackTotal >= 24) {
                redBars.add(barCounter);
            }
            barCounter = barCounter + 1;
            totalValues = totalValues + 3;
        }


        BarDataSet set1 = new BarDataSet(yVals1, "AliveLine");
        getColors(set1, redBars, totalValues, barCounter, stackLength);
        //      getColors(set1);


        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new MyValueFormatter());

        mChart.setData(data);
        mChart.invalidate();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void getColors(BarDataSet set, Set<Integer> redBars, int totalYBars, int totalLength, int stackLength) {

        int barLengthCounter = 1;

        int []colors = new int[totalYBars];

        int colorsCounter = 0;

        //set all the colors at once for the entire colors array (which is the size of the total number of bars)

        for(int i = 0; i < totalLength - 1; i++) {
            //if I am on a stack that should be red...
            if(redBars.contains(barLengthCounter)) {
                for(int j = 0; j < stackLength; j++) {
                    colors[colorsCounter] = Color.rgb(255, 0, 0);
                    colorsCounter++;
                }
                barLengthCounter = barLengthCounter + 1;
            }else {
                for(int j = 0; j < stackLength; j++) {
                    colors[colorsCounter] = ColorTemplate.VORDIPLOM_COLORS[j];
                    colorsCounter++;
                }
                barLengthCounter = barLengthCounter + 1;
            }
        }
        colorsCounter = colorsCounter - 1;

        set.setColors(colors);

    }

    private float getHours(int minutes) {
        float hours = minutes / 60;
        hours += (minutes % 60) / 60.0;
        return hours;
    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
