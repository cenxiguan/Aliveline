package cajac.aliveline;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

/**
 * Created by Chungyuk Takahashi on 8/21/2015.
 */

public class OvertimeDialog extends DialogFragment {

    private AlertDialog dialog;
    private View view;
    private List<Double> overtime;
    public static final int RESULT_OVERTIME = 1;

    public OvertimeDialog(List<Double> overtime) {
        this.overtime = overtime;
        Log.w("OvertimeDiaConstructor", "over " + overtime.size());
        overtime.set(overtime.size() - 1,
                Math.abs(overtime.get(overtime.size() - 1)) );
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.overtime_dialog, null);
        builder.setView(view);

        builder.setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(0);
            }
        });

        builder.setPositiveButton(R.string.cont, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(RESULT_OVERTIME);
            }
        });

        dialog = builder.create();
        dialog.setTitle("Overtime work");
        dialog.setCanceledOnTouchOutside(false);

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

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            Log.e("OvertimeDialog", "TargetFragment is null");
            return;
        }
        Log.w("OvertimeDialog", "sendResult");
        Log.w("OvertimeDialog", "over " + overtime.size());
        Intent i = new Intent();
        double[] convert = new double[overtime.size()];
        for(int j = 0; j < convert.length; j++) { convert[j] = overtime.get(j); }
        i.putExtra("OVERTIME", convert);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

}
