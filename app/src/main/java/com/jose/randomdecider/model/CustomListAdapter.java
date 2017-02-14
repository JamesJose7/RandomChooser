package com.jose.randomdecider.model;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jose.randomdecider.MainActivity;
import com.jose.randomdecider.R;

import java.util.List;

/**
 * Created by jose on 2/5/2017.
 */

public class CustomListAdapter extends ArrayAdapter <String> {
    private Context mContext;
    private int id;
    private List<String> items ;

    public CustomListAdapter(Context context, int textViewResourceId , List<String> list )
    {
        super(context, textViewResourceId, list);
        mContext = context;
        id = textViewResourceId;
        items = list ;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        final TextView text = (TextView) mView.findViewById(R.id.item_text);

        if(items.get(position) != null )
        {

            text.setText(items.get(position));
            text.setTypeface(MainActivity.mPangolinFont);

            final int index = position;
            text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Delete item?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    items.remove(index);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialog.show();

                    return true;
                }
            });

        }

        return mView;
    }
}
