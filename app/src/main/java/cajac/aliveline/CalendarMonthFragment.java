package cajac.aliveline;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chungyuk Takahashi on 5/30/2015.
 */
public class CalendarMonthFragment extends Fragment {
    private boolean undo = false;
    private CaldroidFragment caldroidFragment;

    private Date selectedDate = Calendar.getInstance().getTime();
    private String formattedSelectDate;

    private View view;

    public CalendarMonthFragment() {}

    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflator.inflate(R.layout.fragment_calendar_month, container, false);

        final TextView textView = (TextView) view.findViewById(R.id.textview);
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        caldroidFragment = new CaldroidFragment();

        // Setup arguments
        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            Date date = new Date(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            formattedSelectDate = formatter.format(date);
            textView.setText(formattedSelectDate);

            // Uncomment this line to use dark theme
//            args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);

            caldroidFragment.setArguments(args);
        }

        // Attach to the activity
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();


        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                if(date.equals(selectedDate)) {
                    Fragment fragment = new CalendarDayFragment();
                    FragmentChangeListener activity = (FragmentChangeListener) getActivity();
                    activity.replaceFragment(fragment, date.toString());
                }

                caldroidFragment.clearBackgroundResourceForDate(selectedDate);
                caldroidFragment.clearTextColorForDate(selectedDate);
                selectedDate = date;
                caldroidFragment.setBackgroundResourceForDate(R.color.selected, selectedDate);
                caldroidFragment.setTextColorForDate(R.color.white, selectedDate);
                caldroidFragment.refreshView();

                formattedSelectDate = formatter.format(selectedDate);

                Toast.makeText(getActivity().getApplicationContext(), formattedSelectDate,
                        Toast.LENGTH_SHORT).show();

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                if(sharedPref.contains(formattedSelectDate)) {
                    textView.setText(sharedPref.getString(formattedSelectDate, "ERROR!!!"));
                    Toast.makeText(getActivity().getApplicationContext(), sharedPref.getString(formattedSelectDate, null),
                            Toast.LENGTH_SHORT).show();

                }else {
                    textView.setText(formattedSelectDate);
                    Toast.makeText(getActivity().getApplicationContext(), formattedSelectDate,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
                Toast.makeText(getActivity().getApplicationContext(), "onChangeMonth",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Long click " + formatter.format(date),
                        Toast.LENGTH_SHORT).show();

                String formattedDate = formatter.format(date);
//                Intent intent = new Intent(getActivity().getApplicationContext(), DayView.class);
//                intent.putExtra(String.valueOf(R.string.day), formattedDate);
//                startActivity(intent);
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Caldroid view is created", Toast.LENGTH_SHORT)
                            .show();
                }
            }

        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);



        Button save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) view.findViewById(R.id.edit);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor edit = sharedPref.edit();

                edit.putString(formattedSelectDate, input.getText().toString());
                edit.commit();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

    }


}
