package cajac.aliveline;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Jonathan Maeda on 7/15/2015.
 */
public class pickTime extends DialogFragment {

    private AlertDialog dialog;
    EditText hour, minute, second;
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.pick_time, null);
        builder.setView(view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton(R.string.set_time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = builder.create();
        dialog.setTitle("Set Timer Time");

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

    public void setEditTexts(View view) {
        hour = (EditText) view.findViewById(R.id.hour_field);
        minute = (EditText) view.findViewById(R.id.minute_field);
        second = (EditText) view.findViewById(R.id.second_field);
    }
}
