package cajac.aliveline;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Christine on 6/1/2015.
 * Adapter for paging tabs
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    public TabsPagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        context = c;
    }

    // returns the correct fragment for tab selected
    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case 0:
                // Home fragment activity
                //old version:: return new HomeFragment();
                fragment = Fragment.instantiate(context, HomeFragment.class.getName());
                break;
            case 1:
                // Calendar fragment activity
                fragment = Fragment.instantiate(context, CalendarFragment.class.getName());
                break;
            case 2:
                // Timer fragment activity
                fragment = Fragment.instantiate(context, TimerFragment.class.getName());
                break;
            case 3:
                // Settings fragment activity
                fragment = Fragment.instantiate(context, SettingsFragment.class.getName());
                break;
        }
        return fragment;
    }

    // simply returns count of tabs
    @Override
    public int getCount() {
        return 4;
    }


}
