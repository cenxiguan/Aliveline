package cajac.aliveline;

//import android.app.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Date;
import java.util.List;
import java.util.Timer;

/* may be helpful to look into sliding tabs in the future
   because TabListener is deprecated.
   SO link on Deprecated ActionBar --> http://stackoverflow.com/questions/24473213/action-bar-navigation-modes-are-deprecated-in-android-l
   Google link on Material Design --> http://www.google.com/design/spec/components/tabs.html
 */
public class MainActivity extends AppCompatActivity implements
        ActionBar.TabListener, addTodo.OnTodoAdditionListener {



    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles and icons
    private String[] tabs = { "Home", "Calendar", "Timer", "Settings" };
    private int[] icons = {R.drawable.ic_home,
            R.drawable.ic_calendar,
            R.drawable.ic_timer,
            R.drawable.ic_settings};
    private int[] tabLayouts = { R.layout.tab_home,
            R.layout.tab_calendar,
            R.layout.tab_timer,
            R.layout.tab_settings};
    private int[] tabTextViews = { R.id.tab_home,
            R.id.tab_calendar,
            R.id.tab_timer,
            R.id.tab_settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialization for tabs
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(),this);

        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(3);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));

        if(isScreenNormal()) {
            //creates custom views for ech tab, with icons on top and text on bottom
            for (int i = 0; i < tabs.length; i++) {
                createTab(actionBar, tabLayouts[i], tabTextViews[i], tabs[i]);
            }
        } else {
            // Adding Tabs with Icons
            for (int i=0; i < tabs.length; i++){
                actionBar.addTab(actionBar.newTab().setText(tabs[i])
                        .setIcon(getResources().getDrawable(icons[i]))
                        .setTabListener(this));
            }
        }


        // Swiping the viewpager make respective tab selected
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // on changing the page make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }


        });


        //setting orientation
        if(isScreenLarge()) {
            // width > height, better to use Landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    //creates custom view tab with image on top, text on bottom
    public void createTab(ActionBar actionbar, int view, int titleView, String title) {
        ActionBar.Tab tab = actionbar.newTab();
        tab.setTabListener(this);
        tab.setCustomView(view);
        actionbar.addTab(tab);
        ((TextView) findViewById(titleView)).setText(title);
    }

    //getting screen information for orientation
    public boolean isScreenLarge() {
        final int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public boolean isScreenNormal() {
        final int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void OnAddedTodo() {
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();

        HomeFragment hfrag = (HomeFragment)allFragments.get(0);
        hfrag.createChart();
        hfrag.getTodaysList();
        hfrag.createRecyclerView();
        hfrag.showList();

        //calendarfrag update
        //CalendarFragment cfrag = (CalendarFragment)allFragments.get(1);
        //List<Fragment> calendarFragments = cfrag.getFragManager().getFragments();
        //CalendarFragment.CalendarMonthFragment calMonFrag = (CalendarFragment.CalendarMonthFragment) calendarFragments.get(0);
        //calMonFrag.setSelectedDate(new Date());

        TimerFragment tfrag = (TimerFragment)allFragments.get(2);
        tfrag.populateListView();

        //Intent intent = getIntent();
        //finish();
        //startActivity(intent);
    }
}