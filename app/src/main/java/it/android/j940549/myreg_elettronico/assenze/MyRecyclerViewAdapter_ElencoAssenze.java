package it.android.j940549.myreg_elettronico.assenze;

/**
 * Created by J940549 on 30/12/2017.
 */
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;

public class MyRecyclerViewAdapter_ElencoAssenze extends RecyclerView
        .Adapter<MyRecyclerViewAdapter_ElencoAssenze
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject_Assenze> mDataset;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{
    //        implements View
  //          .OnClickListener {
        TextView data;
        TextView tipoassenza;
        TextView giustificazione;

        public DataObjectHolder(View itemView) {
            super(itemView);
            data= (TextView) itemView.findViewById(R.id.data_assenza);
            tipoassenza= (TextView) itemView.findViewById(R.id.tipo_assenza);
            giustificazione= (TextView) itemView.findViewById(R.id.giustificazione);
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

    public MyRecyclerViewAdapter_ElencoAssenze(ArrayList<DataObject_Assenze> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_assenza, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.data.setText(mDataset.get(position).getData());
        holder.tipoassenza.setText(mDataset.get(position).getTipoassenza());
        String giust=mDataset.get(position).getGiustificazione();
        Log.i("giust", giust);
        if(giust.contains("Si")){
           // giust="SI";
            holder.giustificazione.setTextColor(Color.GREEN);
        }else{
            giust="NO";
            holder.giustificazione.setTextColor(Color.RED);
        }
        holder.giustificazione.setText(giust);

    }

    public void addItem(DataObject_Assenze dataObj, int index) {
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
