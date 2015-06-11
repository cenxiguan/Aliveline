package cajac.aliveline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

/**
 * Created by Chungyuk Takahashi on 6/5/2015.
 */
public class CalendarFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;
    protected FragmentActivity mActivity;

    private Fragment currentCal;
    private Date selectedDate = Calendar.getInstance().getTime();
    private String formattedSelectDate;
    final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    public Fragment getCurrentCal() {
        return currentCal;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    private class CalendarMonthFragment extends Fragment {
        private CaldroidFragment caldroidFragment;
        private View view;

        public CalendarMonthFragment() {}

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_calendar_month, container, false);

            final TextView textView = (TextView) view.findViewById(R.id.textview);
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
            FragmentTransaction t = mActivity.getSupportFragmentManager().beginTransaction();
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

    private class CalendarDayFragment extends Fragment implements AdapterView.OnItemClickListener {
        private View dayView;
        private HListView listView;
        private InfiniteAdapter mAdapter;

        private static final String LOG_TAG = "CalendarDay";
        private final int width = mActivity.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        private final int day_center = width / 2 - 120;

        private Calendar past = Calendar.getInstance();
        private Calendar future = Calendar.getInstance();
        private int first;
        private int last;

        public CalendarDayFragment() {}

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            dayView = inflater.inflate(R.layout.fragment_calendar_day, container, false);
            listView = (HListView) dayView.findViewById( R.id.hListView1 );
            past.setTime(selectedDate);
            past.add(Calendar.DAY_OF_YEAR, -30);
            future.setTime(selectedDate);
            future.add(Calendar.DAY_OF_YEAR, 30);

            List<Date> items = new ArrayList<>();

            for(int i = 1; i <= 61; i++) {
                items.add(past.getTime());
                past.add(Calendar.DAY_OF_YEAR, 1);
            }
            first = 0;
            last = items.size() - 1;
            past.setTime(selectedDate);
            past.add(Calendar.DAY_OF_YEAR, -30);

            mAdapter = new InfiniteAdapter( mActivity, R.layout.test_item_1, android.R.id.text1, items );
            listView.setHeaderDividersEnabled(true);
            listView.setFooterDividersEnabled(true);
            listView.setOnItemClickListener(this);
            listView.setAdapter(mAdapter);
            listView.setSelectionFromLeft(Integer.MAX_VALUE / 2 + 32, day_center); //Zooms and aligns center

            listView.setOnScrollListener(new EndlessScrollListener(items.size()) {
                @Override
                public void onLoadMore(boolean direction) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to your AdapterView
//                    addElements();
                    if (direction)
                        addFuture();
                    else
                        addPast();
                }

                public void onScrollStateChanged(AbsHListView View, int scrollState) {
                }
            });

            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View p_v, MotionEvent p_event) {
                    // this will disallow the touch request for parent scroll on touch of child view
                    p_v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            TextView txt = (TextView) dayView.findViewById(R.id.txt);
            txt.setText(selectedDate.toString());

            return dayView;
        }


        private void addPast() {
            for (int i = 0; i < 7; i++) {
                past.add(Calendar.DAY_OF_YEAR, -1);
                future.add(Calendar.DAY_OF_YEAR, -1);
                mAdapter.addPast(past.getTime());
            }
            mAdapter.notifyDataSetChanged();
        }

        private void addFuture() {
            for (int i = 0; i < 7; i++) {
                past.add(Calendar.DAY_OF_YEAR, 1);
                future.add(Calendar.DAY_OF_YEAR, 1);
                mAdapter.addFuture(future.getTime());
            }
            mAdapter.notifyDataSetChanged();
        }

        // Append more data into the adapter
        public void customLoadMoreDataFromApi(int offset) {
            // This method probably sends out a network request and appends new data items to your adapter.
            // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
            // Deserialize API response and then construct new objects to append to the adapter
        }

        private void scrollList() {
            listView.smoothScrollBy(1500, 300);
        }

        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
            Date date = mAdapter.getItem(position);
            selectedDate = date;
            TextView txt = (TextView) dayView.findViewById(R.id.txt);
            txt.setText(formatter.format(date));
            listView.post(new ScrollRunnable(position));
        }

        private class ScrollRunnable implements Runnable{
            private int position;

            public ScrollRunnable(int position) { this.position = position; }

            public void run() {
                listView.smoothScrollToPositionFromLeft(position, day_center, 1000);
            }
        }

        private class InfiniteAdapter extends ArrayAdapter<Date> {

            private List<Date> mItems;
            private LayoutInflater mInflater;
            private int mResource;
            private int mTextResId;
            ViewGroup parent;

            public InfiniteAdapter( Context context, int resourceId, int textViewResourceId, List<Date> objects ) {
                super( context, resourceId, textViewResourceId, objects );
                mInflater = LayoutInflater.from( context );
                mResource = resourceId;
                mTextResId = textViewResourceId;
                mItems = objects;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public long getItemId( int position ) {
                return getItem( position ).hashCode();
            }

            @Override
            public View getView( int position, View convertView, ViewGroup parent ) {
                this.parent = parent;
                position%=mItems.size();

                if( null == convertView ) {
                    convertView = mInflater.inflate( mResource, parent, false );
                }

                TextView textView = (TextView) convertView.findViewById( mTextResId );
                textView.setText( formatter.format(getItem( position )) );

                ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.width = 200;

                return convertView;
            }

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Date getItem(int position) {
                if ( mItems.size()==0 ) {
                    return null;
                }
                return mItems.get( position%mItems.size() );
            }

            public void addFuture(Date date) {
                mItems.set(first, date);
                int size = mItems.size();
                first = (first + 1) % size;
                last = (last + 1) % size;
            }

            public void addPast(Date date) {
                mItems.set(last, date);
                int size = mItems.size();
                first = (first + (size - 1)) % size;
                last = (last + (size - 1)) % size;
            }

        }

    }



}
