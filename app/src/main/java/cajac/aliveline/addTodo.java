package cajac.aliveline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Jonathan Maeda on 5/31/2015.
 */
public class addTodo extends DialogFragment {

    Button sun,mon,tue,wed,thu,fri,sat;
    ImageButton up,flat,down;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.addtodo, null);
        builder.setView(view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //delete previous records if they exist
                //get code from textviews and create a 2do
                //send 2do to database
                //call algorithm for time distribution and create relational database entries
            }
        });

        Dialog dialog = builder.create();
        dialog.setTitle("Todo Settings");

        setButtons(view);

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
                adjustSelected(up,v);
            }
        });

        flat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flat.setSelected(true);
                adjustSelected(flat,v);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down.setSelected(true);
                adjustSelected(down,v);
            }
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        int dialogWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        int dialogHeight = getActivity().getResources().getDisplayMetrics().heightPixels;

        getDialog().getWindow().setLayout((int) (dialogWidth * .9), (int) (dialogHeight * .7));
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

    public void adjustSelected(ImageButton b, View v){
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
}
