

package cajac.aliveline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;


/**
 * Created by Christine on 6/1/2015.
 * Fragment for the first tab: HOME
 * layout file in fragment_home.xml
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    private static ListView list1;

    Button barGraph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

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
        barGraph = (Button)rootView.findViewById(R.id.button8);
        barGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent graphMode = new Intent();
            }
        });



        String[] group = {"whatever", "what", "hangout", "skype", "the", "hnkjdfs", "heroes", "mine", "youtube"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, group);
        list1 = (ListView)rootView.findViewById(R.id.list1);
        list1.setAdapter(adapter);

        return rootView;
    }


}
