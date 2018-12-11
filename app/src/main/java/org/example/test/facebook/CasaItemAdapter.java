package org.example.test.facebook;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.example.test.facebook.Models.CasaDB;

/**
 * Adapter to bind a ToDoItem List to a view
 */
public class CasaItemAdapter extends ArrayAdapter<CasaDB> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public CasaItemAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final CasaDB currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        final Button btnIr = row.findViewById(R.id.btnCasaItem);
        btnIr.setText("IR");
        final TextView txtCasa = row.findViewById(R.id.txtCasaItem);
        txtCasa.setText(currentItem.getDescripcion());

        btnIr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                    if (mContext instanceof CasaActivity) {
                        CasaActivity activity = (CasaActivity) mContext;
                        //activity.checkItem(currentItem);
                        activity.irACasa(currentItem);
                    }

            }
        });

        return row;
    }

}