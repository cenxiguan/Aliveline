package cajac.aliveline;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.sephiroth.android.library.util.v11.MultiChoiceModeListener;
import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

/**
 * Created by Chungyuk Takahashi on 6/5/2015.
 */
public class CalendarFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;

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

    private class CalendarDayFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
        private View view;
        HListView listView;
        InfiniteAdapter mAdapter;
        private static final String LOG_TAG = "CalendarDay";

        public CalendarDayFragment() {}

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            view = inflater.inflate(R.layout.fragment_calendar_day, container, false);
            listView = (HListView) view.findViewById( R.id.hListView1 );
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            cal.add(Calendar.DAY_OF_YEAR, -30);

            Log.w(LOG_TAG, "Created View");

            List<String> items = new ArrayList<String>();

            for(int i = 0; i < 61; i++) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
                items.add(formatter.format(cal.getTime().toString()));
            }
//            // Add the most recent past 30 days
//            for(int i = 30; i > 0; i--) {
//                other.add(Calendar.DATE, -i);
//
//            }
//
//            // Add the selected Date
//            items.add(selectedDate.toString());
//
//            // Add the future 30 days after selected date
//            for( int i = 0; i < 30; i++ ) {
//                items.add( String.valueOf( i ) );
//            }
            mAdapter = new InfiniteAdapter( getActivity(), R.layout.test_item_1, android.R.id.text1, items );
            listView.setHeaderDividersEnabled(true);
            listView.setFooterDividersEnabled(true);

            if( listView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL ) {
                listView.setMultiChoiceModeListener( new MultiChoiceModeListener() {

                    @Override
                    public boolean onPrepareActionMode( ActionMode mode, Menu menu ) {
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode( ActionMode mode ) {
                    }

                    @Override
                    public boolean onCreateActionMode( ActionMode mode, Menu menu ) {
                        menu.add( 0, 0, 0, "Delete" );
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked( ActionMode mode, MenuItem item ) {
                        Log.d( LOG_TAG, "onActionItemClicked: " + item.getItemId() );

                        final int itemId = item.getItemId();
                        if( itemId == 0 ) {
                            deleteSelectedItems();
                        }

                        mode.finish();
                        return false;
                    }

                    @Override
                    public void onItemCheckedStateChanged( ActionMode mode, int position, long id, boolean checked ) {
                        mode.setTitle( "What the fuck!" );
                        mode.setSubtitle( "Selected items: " + listView.getCheckedItemCount() );
                    }
                } );
            } else if( listView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE ) {
                listView.setOnItemClickListener( this );
            }


            listView.setAdapter(mAdapter);
		    listView.setSelection(30);

            listView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to your AdapterView
//                    addElements();
                    customLoadMoreDataFromApi(page);
                    // or customLoadMoreDataFromApi(totalItemsCount);
                }

                public void onScrollStateChanged(AbsHListView View, int scrollState) {}
            });

            TextView txt = (TextView) view.findViewById(R.id.txt);
            txt.setText(selectedDate.toString());


            return view;
        }

        public void onClick( View v ) {
            final int id = v.getId();

        }

        private void addElements() {
            for( int i = 0; i < 10; i++ ) {
//			mAdapter.mItems.add( Math.min( mAdapter.mItems.size(), 2), String.valueOf( mAdapter.mItems.size() ) );
                mAdapter.mItems.add(String.valueOf(mAdapter.mItems.size()));
            }
            mAdapter.notifyDataSetChanged();
        }

        private void removeElements() {
            for( int i = 0; i < 5; i++ ) {
                if( mAdapter.mItems.size() > 0 ) {
                    mAdapter.mItems.remove(mAdapter.mItems.size()-1);
                }
            }
            mAdapter.notifyDataSetChanged();
        }

        private void deleteSelectedItems() {
            SparseArrayCompat<Boolean> checkedItems = listView.getCheckedItemPositions();
            ArrayList<Integer> sorted = new ArrayList<Integer>( checkedItems.size() );

            Log.i( LOG_TAG, "deleting: " + checkedItems.size() );

            for( int i = 0; i < checkedItems.size(); i++ ) {
                if( checkedItems.valueAt( i ) ) {
                    sorted.add( checkedItems.keyAt( i ) );
                }
            }

            Collections.sort(sorted);

            for( int i = sorted.size()-1; i >= 0; i-- ) {
                int position = sorted.get( i );
                Log.d( LOG_TAG, "Deleting item at: " + position );
                mAdapter.mItems.remove( position );
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
            Log.i(LOG_TAG, "onItemClick: " + position );
            Log.d(LOG_TAG, "checked items: " + listView.getCheckedItemCount() );
            Log.d(LOG_TAG, "checked positions: " + listView.getCheckedItemPositions());
        }

        class InfiniteAdapter extends ArrayAdapter<String> {

            List<String> mItems;
            LayoutInflater mInflater;
            int mResource;
            int mTextResId;

            public InfiniteAdapter( Context context, int resourceId, int textViewResourceId, List<String> objects ) {
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
            public int getViewTypeCount() {
                return 3;
            }

            @Override
            public int getItemViewType( int position ) {
                return position%3;
            }

            @Override
            public View getView( int position, View convertView, ViewGroup parent ) {

                position%=mItems.size();

                View front= convertView;

                if( null == convertView ) {
                    convertView = mInflater.inflate( mResource, parent, false );
                }

                TextView textView = (TextView) convertView.findViewById( mTextResId );
                textView.setText( getItem( position ) );

                int type = getItemViewType( position );

                ViewGroup.LayoutParams params = convertView.getLayoutParams();
                if( type == 0 ) {
                    params.width = getResources().getDimensionPixelSize( R.dimen.item_size_1 );
                } else if( type == 1 ) {
                    params.width = getResources().getDimensionPixelSize( R.dimen.item_size_2 );
                } else {
                    params.width = getResources().getDimensionPixelSize( R.dimen.item_size_3 );
                }

                return convertView;
            }

//		public int getCount() {
//			return Integer.MAX_VALUE;
//		}

            @Override
            public String getItem(int position) {
                if ( mItems.size()==0 ) {
                    return null;
                }

                // mod the list index to the actual element count
                return mItems.get( position%mItems.size() );
            }

        }

    }



}
