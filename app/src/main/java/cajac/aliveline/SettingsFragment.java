package cajac.aliveline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Christine on 6/1/2015.
 * Fragment for the fourth tab: SETTINGS
 * layout file in fragment_settings.xml
 */
public class SettingsFragment extends Fragment {

    ListView settings;
    String[] items = new String[]{"Calendar", "Notifications", "Feedback", "About"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        settings = (ListView) getView().findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, items);
        settings.setAdapter(adapter);

        setListeners();
    }

    private void setListeners() {
        settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // position is item index
                String item = (String) settings.getItemAtPosition(position);

                Toast.makeText(getActivity().getApplicationContext(),
                        "You have clicked on " + item, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
