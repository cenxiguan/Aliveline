package cajac.aliveline;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
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
 * Created by alexsuk on 6/22/15.
 */
public class StackBarChart extends FragmentActivity implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    DatabaseHelper dbh;

    private BarChart mChart;
    private SeekBar mSeekBarX; //mSeekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);
        dbh = new DatabaseHelper(this);

        tvX = (TextView) findViewById(R.id.tvXMax);
   //     tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarX.setOnSeekBarChangeListener(this);

       // mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
       // mSeekBarY.setOnSeekBarChangeListener(this);

        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

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

        List<String> datesXAxis= new ArrayList<String>();
        datesXAxis = dbh.getAllDates();
        int maxDataX = datesXAxis.size();
        // setting data
        //setProgress is +1 from what you put in the parenthesis
        mSeekBarX.setProgress(maxDataX);
     //   mSeekBarY.setProgress(100);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        // mChart.setDrawLegend(false);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }
    */
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (DataSet<?> set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.isHighlightEnabled())
                    mChart.setHighlightEnabled(false);
                else
                    mChart.setHighlightEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleHighlightArrow: {
                if (mChart.isDrawHighlightArrowEnabled())
                    mChart.setDrawHighlightArrow(false);
                else
                    mChart.setDrawHighlightArrow(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStartzero: {
                mChart.getAxisLeft().setStartAtZero(!mChart.getAxisLeft().isStartAtZeroEnabled());
                mChart.getAxisRight().setStartAtZero(!mChart.getAxisRight().isStartAtZeroEnabled());
                mChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {

                mChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionToggleFilter: {

                Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 25);

                if (!mChart.isFilteringEnabled()) {
                    mChart.enableFiltering(a);
                } else {
                    mChart.disableFiltering();
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionSave: {
                if (mChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();
                break;
            }
        }
        return true;
    }

 */


    @Override
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
            float[]  newEntries= new float[stackItem.size()];
            float stackTotal = 0;
            stackLength = stackItem.size();

            //a forLoop that will add all the hours from the todoActivites from stackItem to yVals1
            for(int j = 0; j < stackItem.size(); j++ ) {
                int minsRequired = stackItem.get(j).getTimeRequired();
                int minsCompleted= stackItem.get(j).getTimeCompleted();
                int timeDisplayed = minsRequired - minsCompleted;
                float timeInHours = getHours(timeDisplayed);
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
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

  /*      FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
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
        a.show(ft, "addTodo");*/
    }

    @Override
    public void onNothingSelected() {
        // TODO Auto-generated method stub

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



    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    private float getHours(int minutes) {
        float hours = minutes / 60;
        hours += (minutes % 60) / 60.0;
        return hours;
    }
}