package cajac.aliveline;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Christine on 6/1/2015.
 * Adapter for paging tabs
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {super(fm);}

    // returns the correct fragment for tab selected
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                // Home fragment activity
                return new HomeFragment();
            case 1:
                // Calendar fragment activity
                return new CalendarFragment();
            case 2:
                // Timer fragment activity
                return new TimerFragment();
            case 3:
                // Settings fragment activity
                return new SettingsFragment();
        }
        return null;
    }

    // simply returns count of tabs
    @Override
    public int getCount() {
        return 4;
    }


}
