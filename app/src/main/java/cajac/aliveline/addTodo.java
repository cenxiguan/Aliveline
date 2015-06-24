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

/**
 * Created by Jonathan Maeda on 5/31/2015.
 */
public class addTodo extends DialogFragment {

    private AlertDialog dialog;
    EditText title, dueDay, dueMonth, dueYear, estTime;
    Button sun,mon,tue,wed,thu,fri,sat,buttonPos;
    ImageButton up,flat,down;
    View view;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3){
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkFieldsForEmptyValues(buttonPos);
        }
        @Override
        public void afterTextChanged(Editable editable) {
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
        checkFieldsForEmptyValues(buttonPos);
    }

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
            }
        });
        mon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeSelected(mon, v);
            }
        });
        tue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeSelected(tue, v);
            }
        });
        wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelected(wed, v);
            }
        });
        thu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelected(thu, v);
            }
        });
        fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelected(fri, v);
            }
        });
        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelected(sat, v);
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up.setSelected(true);
                adjustOtherSelected(up,v);
            }
        });

        flat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flat.setSelected(true);
                adjustOtherSelected(flat,v);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down.setSelected(true);
                adjustOtherSelected(down,v);
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

    public void adjustOtherSelected(ImageButton b, View v){
        comparison(b, up, v);
        comparison(b, flat, v);
        comparison(b, down, v);
    }

    public void changeSelected(Button b,View v){
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
        } else {
            return 3;
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

    private  void checkFieldsForEmptyValues(Button pos) {
        String s1 = title.getText().toString();
        String s2 = dueDay.getText().toString();
        String s3 = dueMonth.getText().toString();
        String s4 = dueYear.getText().toString();
        String s5 = estTime.getText().toString();

        if (s1.equals("") || s2.equals("") || s3.equals("") || s4.equals("") || s5.equals("")) {
            pos.setEnabled(false);
        } else {
            pos.setEnabled(true);
        }
    }
    //CONVERSTION TO INT IF NECESSARY
    //int intTodoEstTime;
    //try {
    //    intTodoEstTime = Integer.parseInt(todoEstTime);
    //} catch(NumberFormatException nfe) {
    //    throw new RuntimeException (nfe);
    //}
}
