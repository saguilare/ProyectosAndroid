package org.example.test.facebook;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.example.test.facebook.Models.HabitacionDB;
import org.example.test.facebook.Pojos.Habitacion;

/**
 * Adapter to bind a ToDoItem List to a view
 */
public class HabitacionItemAdapter extends ArrayAdapter<HabitacionDB> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public HabitacionItemAdapter(Context context, int layoutResourceId) {
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

        final HabitacionDB currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        //CURRENT ITEM ES EL OBJETO DE LA LISTA QUE SE ITERA OSE CADA HABITACION EN EL ARRAY
        row.setTag(currentItem);
        final Button btnIr = row.findViewById(R.id.btnEditarHabitacion);
        btnIr.setText("Editar");
        final TextView txtDescripcion = row.findViewById(R.id.txtDescripcion);
        txtDescripcion.setText(currentItem.getDescripcion());

        btnIr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                    if (mContext instanceof HabitacionActivity) {
                        HabitacionActivity activity = (HabitacionActivity) mContext;
                        activity.EditarHabitacion(currentItem);
                    }

            }
        });

        return row;
    }

}