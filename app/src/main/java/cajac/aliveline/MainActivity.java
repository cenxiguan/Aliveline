package cajac.aliveline;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/* may be helpful to look into sliding tabs in the future
   because TabListener is deprecated.
 */
public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles and icons
    private String[] tabs = { "Home", "Calendar", "Timer", "Settings" };
    private int[] icons = {R.drawable.ic_home,
            R.drawable.ic_calendar,
            R.drawable.ic_timer,
            R.drawable.ic_settings};
    /*private int[] tabLayouts = { R.layout.tab_home,
            R.layout.tab_calendar,
            R.layout.tab_timer,
            R.layout.tab_settings};
    private int[] tabTextViews = { R.id.tab_home,
            R.id.tab_calendar,
            R.id.tab_timer,
            R.id.tab_settings};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Button made by Jon for new ToDo
           commenting for now to make the swipe tabs work

        Button b = (Button) findViewById(R.id.addTodo);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,addTodo.class));
            }
        }); */

        // Initialization for tabs
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs with Icons
        for (int i=0; i < tabs.length; i++){
            actionBar.addTab(actionBar.newTab().setText(tabs[i])
                    .setIcon(getResources().getDrawable(icons[i]))
                    .setTabListener(this));
        }
        /* creates custom views for ech tab, with icons on top and text on bottom
         * layout is slightly weird, may fix in the future if there's time
        for (int i=0; i<tabs.length; i++) {
            createTab(actionBar, tabLayouts[i], tabTextViews[i], tabs[i]);
        }*/


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
    }

    /* creates custom view tab with image on top, text on bottom
     * will be useful if we decide to use custom tabs
    public void createTab(ActionBar actionbar, int view, int titleView, String title) {
        ActionBar.Tab tab = actionbar.newTab();
        tab.setTabListener(this);
        tab.setCustomView(view);
        actionbar.addTab(tab);
        ((TextView) findViewById(titleView)).setText(title);
    }*/

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // empty
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // empty
    }
}
