package cajac.aliveline;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexsuk on 6/22/15.
 */
public class StackBarChart extends FragmentActivity implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {
    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

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

        // setting data
        mSeekBarX.setProgress(12);
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

        tvX.setText("" + (mSeekBarX.getProgress() + 1));
      //  tvY.setText("" + (mSeekBarY.getProgress()));

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < mSeekBarX.getProgress() + 1; i++) {
            xVals.add(mMonths[i % mMonths.length]);
        }

        Set<Integer> redBars = new HashSet<Integer>();
        int barCounter = 1;
        int totalValues = 0;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        float val1 = 0;
        float val2 = 0;
        float val3 = 0;

        for (int i = 0; i < mSeekBarX.getProgress() + 1; i++) {
            //float mult = (mSeekBarY.getProgress() + 1);
            val1 = (float) (Math.random() * 24);
            val2 = (float) 3;
            val3 = (float) 3;

            yVals1.add(new BarEntry(new float[]{
                    val1, val2, val3
            }, i));

            float stackTotal = val1 + val2 + val3;


            if (stackTotal >= 24) {
                redBars.add(barCounter);
            }
            barCounter = barCounter + 1;
            totalValues = totalValues + 3;
        }





        BarDataSet set1 = new BarDataSet(yVals1, "Statistics Vienna 2014");
        getColors(set1, redBars, totalValues, barCounter);
    //      getColors(set1);
        set1.setStackLabels(new String[] {
                "Births", "Divorces", "Marriages"
        });


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

    //    BarEntry entry = (BarEntry) e;
    //    Log.i("VAL SELECTED",
    //            "Value: " + entry.getVals()[h.getStackIndex()]);


        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        addTodo a = new addTodo();
        a.show(ft, "addTodo");
    }

    @Override
    public void onNothingSelected() {
        // TODO Auto-generated method stub

    }

    private void getColors(BarDataSet set, Set<Integer> redBars, int totalYBars, int totalLength) {

        int barLengthCounter = 1;

        int []colors = new int[totalYBars];

        int colorsCounter = 0;

        //set all the colors at once for the entire colors array (which is the size of the total number of bars)

        for(int i = 0; i < totalLength - 1; i++) {
            //if I am on a stack that should be red...
            if(redBars.contains(barLengthCounter)) {
                for(int j = 0; j < 3; j++) {
                    colors[colorsCounter] = Color.rgb(255,0,0);
                    colorsCounter++;
                }
                barLengthCounter = barLengthCounter + 1;
            }else {
                for(int j = 0; j < 3; j++) {
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
}