package cajac.aliveline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chungyuk Takahashi on 6/5/2015.
 */
public class CalendarFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;

    private Fragment currentCal;
    private Date selectedDate = Calendar.getInstance().getTime();
    private Switch dayOrMonth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        if(currentCal instanceof CalendarDayFragment) {
            switchFrame(new CalendarDayFragment());
        }else {
            switchFrame(new CalendarMonthFragment());
            Log.w("CalFrag", currentCal.toString());
        }


        dayOrMonth = (Switch) view.findViewById(R.id.day_month_switch);
        dayOrMonth.setChecked(false);
        dayOrMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchFrame(new CalendarDayFragment());
                } else {
                    switchFrame(new CalendarMonthFragment());
                }
            }
        });

        return view;
    }

    public void switchFrame(Fragment fragment) {
        currentCal = fragment;
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.calendar_frame, currentCal).commit();
    }

    public Fragment getCurrentCal() {
        return currentCal;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    private class CalendarMonthFragment extends Fragment {
        private CaldroidFragment caldroidFragment;

        private String formattedSelectDate;

        private View view;

        public CalendarMonthFragment() {}

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_calendar_month, container, false);

            final TextView textView = (TextView) view.findViewById(R.id.textview);
            final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
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
                cal.setTime(selectedDate);
                args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
                args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
                args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
                args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
                Date date = new Date(cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                formattedSelectDate = formatter.format(date);
                textView.setText(formattedSelectDate);

                caldroidFragment.setBackgroundResourceForDate(R.color.selected, selectedDate);
                caldroidFragment.setTextColorForDate(R.color.white, selectedDate);
                caldroidFragment.refreshView();
                Log.w("CalFrag", "Createview");
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
                        dayOrMonth.setChecked(true);
                        CalendarDayFragment dayView = new CalendarDayFragment();
                        Log.w("CalFrag", "date selected");
                        switchFrame(dayView);
                        return;
                    }

                    caldroidFragment.clearBackgroundResourceForDate(selectedDate);
                    caldroidFragment.clearTextColorForDate(selectedDate);
                    selectedDate = date;
                    caldroidFragment.setBackgroundResourceForDate(R.color.selected, selectedDate);
                    caldroidFragment.setTextColorForDate(R.color.white, selectedDate);
                    caldroidFragment.refreshView();

                    formattedSelectDate = formatter.format(selectedDate);

//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
//
//                if(sharedPref.contains(formattedSelectDate)) {
//                    textView.setText(sharedPref.getString(formattedSelectDate, "ERROR!!!"));
////                    Toast.makeText(getActivity().getApplicationContext(), sharedPref.getString(formattedSelectDate, null),
////                            Toast.LENGTH_SHORT).show();
//
//                }else {
//                    textView.setText(formattedSelectDate);
////                    Toast.makeText(getActivity().getApplicationContext(), formattedSelectDate,
////                            Toast.LENGTH_SHORT).show();
//                }
                }

                @Override
                public void onChangeMonth(int month, int year) {
                    String text = "month: " + month + " year: " + year;
//                Toast.makeText(getActivity().getApplicationContext(), "onChangeMonth",
//                        Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClickDate(Date date, View view) {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        "Long click " + formatter.format(date),
//                        Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCaldroidViewCreated() {
                    if (caldroidFragment.getLeftArrowButton() != null) {
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            "Caldroid view is created", Toast.LENGTH_SHORT)
//                            .show();
                    }
                }

            };

            // Setup Caldroid
            caldroidFragment.setCaldroidListener(listener);

            Button save = (Button) view.findViewById(R.id.save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                EditText input = (EditText) view.findViewById(R.id.edit);
//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
//                SharedPreferences.Editor edit = sharedPref.edit();
//
//                edit.putString(formattedSelectDate, input.getText().toString());
//                edit.commit();
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

    private class CalendarDayFragment extends Fragment {
        private View view;

        public CalendarDayFragment() {}

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            view = inflater.inflate(R.layout.fragment_calendar_day, container, false);

            TextView txt = (TextView) view.findViewById(R.id.txt);
            txt.setText(selectedDate.toString());


            return view;
        }

    }



}
