package cajac.aliveline;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;

/**
 * Created by Jonathan Maeda on 7/15/2015.
 */
public class pickTime extends DialogFragment {

    private AlertDialog dialog;
    EditText hour, minute, second;
    long elapsedTime = 0;
    public static final int RESULT_INT = 2;
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
            editElapsedTime();
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.pick_time, null);
        builder.setView(view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(0);
            }
        });

        builder.setPositiveButton(R.string.set_time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(RESULT_INT);
            }
        });

        dialog = builder.create();
        dialog.setTitle("Set Timer Time");
        dialog.setCanceledOnTouchOutside(false);

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
    }

    private final void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            Log.e("TimerFrag", "TargetFragment is null");
            return;
        }
        Intent i = new Intent();
        i.putExtra("TIMER_TIME", elapsedTime);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    public void setEditTexts(View view) {
        hour = (EditText) view.findViewById(R.id.hour_field);
        minute = (EditText) view.findViewById(R.id.minute_field);
        second = (EditText) view.findViewById(R.id.second_field);

        hour.addTextChangedListener(textWatcher);
        minute.addTextChangedListener(textWatcher);
        second.addTextChangedListener(textWatcher);
    }

    public void editElapsedTime(){
        int hours = convertToInt(hour.getText().toString());
        int minutes = convertToInt(minute.getText().toString());
        int seconds = convertToInt(second.getText().toString());

        elapsedTime = 1000 * (seconds + (60 * minutes) + (3600 * hours));
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
}
