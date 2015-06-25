package cajac.aliveline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.CharArrayReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Jonathan Maeda on 5/31/2015.
 */
public class addTodo extends DialogFragment {

    //Initialize Constants
    private AlertDialog dialog;
    Button sun,mon,tue,wed,thu,fri,sat,buttonPos;
    EditText title, dueDay, dueMonth, dueYear, estTime;
    ImageButton up,flat,down;
    View view;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3){
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
            checkSubmitButtonConditions(buttonPos);
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.addtodo, null);
        builder.setView(view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //get text from edittexts
                String todoTitle = title.getText().toString();
                String todoEstTime = estTime.getText().toString();

                //get selected Curve and Days
                int selectedCurve = getSelectedCurve();
                String selectedDays = getSelectedDays();

                //create and send 2do to database

            }
        });
        dialog = builder.create();
        dialog.setTitle("Todo Settings");

        setButtons(view);
        setOnClickListeners(view);
        setEditTexts(view);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        //Scaling dialog
        int dialogWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        int dialogHeight = getActivity().getResources().getDisplayMetrics().heightPixels;
        getDialog().getWindow().setLayout((int) (dialogWidth * .9), (int) (dialogHeight * .7));

        //getting positive button and initial check
        buttonPos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        checkSubmitButtonConditions(buttonPos);
    }

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    //////////////Methods to help set up the view///////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    public void setButtons(View view){
        sun = (Button)view.findViewById(R.id.button);
        mon = (Button)view.findViewById(R.id.button2);
        tue = (Button)view.findViewById(R.id.button3);
        wed = (Button)view.findViewById(R.id.button4);
        thu = (Button)view.findViewById(R.id.button5);
        fri = (Button)view.findViewById(R.id.button6);
        sat = (Button)view.findViewById(R.id.button7);

        up = (ImageButton)view.findViewById(R.id.up_curve);
        flat = (ImageButton)view.findViewById(R.id.no_curve);
        down = (ImageButton)view.findViewById(R.id.down_curve);
    }

    public void setOnClickListeners(View view){
        sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelected(sun, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });
        mon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeSelected(mon, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });
        tue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeSelected(tue, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });
        wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelected(wed, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });
        thu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelected(thu, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });
                fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelected(fri, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });
        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelected(sat, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up.setSelected(true);
                adjustOtherSelected(up, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });

        flat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flat.setSelected(true);
                adjustOtherSelected(flat, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down.setSelected(true);
                adjustOtherSelected(down, v);
                checkSubmitButtonConditions(buttonPos);
            }
        });
    }

    public void setEditTexts(View view){
        title = (EditText) view.findViewById(R.id.title_field);
        dueDay = (EditText) view.findViewById(R.id.day_field);
        dueMonth = (EditText) view.findViewById(R.id.month_field);
        dueYear = (EditText) view.findViewById(R.id.year_field);
        estTime = (EditText) view.findViewById(R.id.estimated_time_field);

        title.addTextChangedListener(textWatcher);
        dueDay.addTextChangedListener(textWatcher);
        dueMonth.addTextChangedListener(textWatcher);
        dueYear.addTextChangedListener(textWatcher);
        estTime.addTextChangedListener(textWatcher);
    }

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    /////////Dealing with selected and unselected buttons///////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    public void adjustOtherSelected(ImageButton b, View v){
        comparison(b, up, v);
        comparison(b, flat, v);
        comparison(b, down, v);
    }

    public void changeSelected(Button b, View v) {
        b.setSelected(!b.isSelected());

        if (b.isSelected()) {
            b.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.time_usage_selected));
        } else {
            b.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.time_usage_unselected));
        }
    }

    public void comparison(ImageButton b, ImageButton compare, View v) {
        if (b.getId() == compare.getId()) {
            compare.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.time_usage_selected));
        } else {
            compare.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.time_usage_unselected));
            compare.setSelected(false);
        }
    }

    public int getSelectedCurve(){
        if(up.isSelected()){
            return 1;
        } else if (flat.isSelected()){
            return 2;
        } else if (down.isSelected()){
            return 3;
        } else {
            return 0;
        }
    }

    public String getSelectedDays(){
        String days = checkButton(sun) + checkButton(mon) + checkButton(tue) + checkButton(wed)
                + checkButton(thu) + checkButton(fri) + checkButton(sat);
        return days;
    }

    public String checkButton(Button day){
        if(day.isSelected()){
            return "1";
        } else {
            return "0";
        }
    }

    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    ///////////////////Making sure input is correct//////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    private  void checkSubmitButtonConditions(Button pos) {
        String s1 = title.getText().toString();
        int i2 = convertToInt(dueDay.getText().toString());
        int i3 = convertToInt(dueMonth.getText().toString());
        int i4 = convertToInt(dueYear.getText().toString());
        int i5 = convertToInt(estTime.getText().toString());

        if (s1.length() > 0 && dateCheck(i2,i3,i4) && atLeast(i5,1) && daysSelected()) {
            pos.setEnabled(true);
        } else {
            pos.setEnabled(false);
        }
    }

    public int convertToInt(String s){
        int i;
        try{
            i = Integer.parseInt(s);
        } catch(NumberFormatException nfe) {
            return 0;
        }

        return i;
    }

    public boolean atLeast(int test, int min){
        return test > min - 1;
    }

    public boolean between(int test, int min, int max){
        return test > min - 1 && test < max + 1;
    }

    public boolean dateCheck(int day, int month, int year){
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);

        if (validDate(day,month,year)&& atLeast(year,currentYear)){
            Date today = new Date(currentYear, currentMonth, currentDate);
            Date test = new Date(year,month,day);
            return test.compareTo(today) > 0;
        } else {
            return false;
        }
    }

    public boolean validDate(int date, int month, int year){
        switch(month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (date > 0 && date < 32){
                    return true;
                } else {
                    return false;
                }
            case 4:
            case 6:
            case 9:
            case 11:
                if (date > 0 && date < 31){
                    return true;
                } else {
                    return false;
                }
            case 2:
                if(date > 0 && date < 29 && year % 4 != 0){
                    return true;
                } else if (date > 0 && date < 30 && year % 4 == 0){
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    public boolean daysSelected(){
        int curves = getSelectedCurve();
        String days = getSelectedDays();
        if(curves == 0 || days.equals("0000000")){
            return false;
        } else {
            return true;
        }
    }
}
