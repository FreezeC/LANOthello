package frezc.lanothello.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by freeze on 2015/4/17.
 */
public class SearcherDialog extends DialogFragment {
    private OnClickListenr onClickListenr;

    public interface OnClickListenr{
        void onPositiveClick();
        void onNegativeClick();
    }

    public void setOnClickListenr(OnClickListenr onClickListenr){
        this.onClickListenr = onClickListenr;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_searcher, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_host);
        tv.setText(getArguments().getString("hostIp"));

        builder.setView(view)
                .setPositiveButton("connect",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(onClickListenr != null){
                                    onClickListenr.onPositiveClick();
                                }
                            }
                        });
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(onClickListenr != null){
                            onClickListenr.onNegativeClick();
                        }
                    }
                });
        builder.setTitle("Found server");
        builder.setCancelable(false);
        return builder.create();

    }
}
