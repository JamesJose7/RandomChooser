package com.jose.randomdecider.model;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jose.randomdecider.MainActivity;
import com.jose.randomdecider.R;

import java.util.List;

/**
 * Created by jose on 2/7/2017.
 */

public class SavedListsAdapter extends ArrayAdapter<UserList> {
    private Context mContext;
    private int id;
    private List<UserList> items;

    public SavedListsAdapter(Context context, int textViewResourceId , List<UserList> userLists)
    {
        super(context, textViewResourceId, userLists);
        mContext = context;
        id = textViewResourceId;
        items = userLists;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        final TextView text = (TextView) mView.findViewById(R.id.item_text);

        if(items.get(position) != null )
        {

            text.setText(items.get(position).getListName());
            text.setTypeface(MainActivity.mPangolinFont);

            final int index = position;

            //Load items
            /*text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.mUserList = items.get(position);
                    MainActivity.mCurrentListAdapter.notifyDataSetChanged();
                }
            });*/

            //Delete saved items
            /*text.setOnLongClickListener(new View.OnLongClickListener() {
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
            });*/

        }

        return mView;
    }
}
