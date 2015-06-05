package cajac.aliveline;

import android.support.v4.app.Fragment;

/**
 * Created by Chungyuk Takahashi on 6/4/2015.
 */
public interface FragmentChangeListener {
    public void replaceFragment(Fragment fragment);

    public void replaceFragment(Fragment fragment, String string);
}
