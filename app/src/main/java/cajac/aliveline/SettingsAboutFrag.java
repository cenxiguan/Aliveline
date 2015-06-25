package cajac.aliveline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Christine on 6/24/2015.
 * Fragment for the About section
 */
public class SettingsAboutFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_settings_about, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView t = (TextView) getActivity().findViewById(R.id.text);
        t.setText("Aliveline was created by CAJAC.\n" +
                "Icons from Icons8.\n" +
                "Please love us!");
    }

}
