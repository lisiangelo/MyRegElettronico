package it.android.j940549.myreg_elettronico.navigationDrawer;

/**
 * Created by J940549 on 30/12/2017.
 */
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.model.Comunicazione;
import it.android.j940549.myreg_elettronico.model.ConvertiData;
import it.android.j940549.myreg_elettronico.orari.DataObject_OrarioLezioni;

public class MyRecyclerViewAdapter_ElencoComunicazioni extends RecyclerView
        .Adapter<MyRecyclerViewAdapter_ElencoComunicazioni
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter_OrarioLezioni";
    private ArrayList<Comunicazione> mDataset;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{
    //        implements View
  //          .OnClickListener {
        TextView data;
        TextView testo;


        public DataObjectHolder(View itemView) {
            super(itemView);
            data= (TextView) itemView.findViewById(R.id.data_comunicazione);
            testo= (TextView) itemView.findViewById(R.id.testo_comunicazione);
            Log.i(LOG_TAG, "Adding Listener");
           // itemView.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }*/
    }

    /*public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }*/

    public MyRecyclerViewAdapter_ElencoComunicazioni(ArrayList<Comunicazione> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_comunicazioni, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
       //String materia=mDataset.get(position).getMateria();

        holder.data.setText(new ConvertiData().da_Millis_a_String(mDataset.get(position).getData()));
        holder.testo.setText(mDataset.get(position).getTesto());

    }

    public void addItem(Comunicazione dataObj, int index) {
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

    /*public interface MyClickListener {
        public void onItemClick(int position, View v);
    }*/
}
