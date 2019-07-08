package it.android.j940549.myreg_elettronico.orari;

/**
 * Created by J940549 on 30/12/2017.
 */

import android.app.Activity;
import android.database.SQLException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;

public class MyRecyclerViewAdapter_Elenco_Materie extends RecyclerView.Adapter<MyRecyclerViewAdapter_Elenco_Materie.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter_ElencoMaterie";
    private ArrayList<DataObject_Materia> mDataset;
    private Activity myActivity;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        TextView materia, id_materia;
        TextView insegnante;


        public DataObjectHolder(View itemView) {
            super(itemView);
            id_materia = (TextView) itemView.findViewById(R.id.id_materia);
            materia = (TextView) itemView.findViewById(R.id.materia);
            insegnante = (TextView) itemView.findViewById(R.id.insegnante);

        }

    }


    public MyRecyclerViewAdapter_Elenco_Materie(ArrayList<DataObject_Materia> myDataset, Activity activity) {
        mDataset = myDataset;
        myActivity = activity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_materie, parent, false);
        Button btn =view.findViewById(R.id.btn_canc);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView id = view.findViewById(R.id.id_materia);

                cancella_materia(id.getText().toString());
            }
        });
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        //String materia=mDataset.get(position).getMateria();
        holder.id_materia.setText("" + mDataset.get(position).getId_materia());
        holder.materia.setText(mDataset.get(position).getMateria());
        holder.insegnante.setText(mDataset.get(position).getDocente());

    }

    public void addItem(DataObject_Materia dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void cancella_materia(final String id_materia) {
                int id = Integer.parseInt(id_materia);
                DBLayer dbLayer = null;

                try {
                    dbLayer = new DBLayer(myActivity);
                    dbLayer.open();
                    dbLayer.deleteMateria(id);


                } catch (SQLException ex) {
                    Toast.makeText(myActivity, "" + ex.toString(), Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(myActivity, "dato cancellato!" + id_materia, Toast.LENGTH_SHORT).show();


                dbLayer.close();
                myActivity.recreate();
            }
}
