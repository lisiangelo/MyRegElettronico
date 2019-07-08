package it.android.j940549.myreg_elettronico.orari;

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

public class MyRecyclerViewAdapter_OrarioLezioni extends RecyclerView
        .Adapter<MyRecyclerViewAdapter_OrarioLezioni
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter_OrarioLezioni";
    private ArrayList<DataObject_OrarioLezioni> mDataset;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{
    //        implements View
  //          .OnClickListener {
        TextView materia;
        TextView docente;


        public DataObjectHolder(View itemView) {
            super(itemView);
            materia= (TextView) itemView.findViewById(R.id.materia_orario);
            docente= (TextView) itemView.findViewById(R.id.docente_orario);
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

    public MyRecyclerViewAdapter_OrarioLezioni(ArrayList<DataObject_OrarioLezioni> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_orario_lezioni, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
       //String materia=mDataset.get(position).getMateria();

        holder.materia.setText(mDataset.get(position).getMateria());
        holder.docente.setText(mDataset.get(position).getDocente());

    }

    public void addItem(DataObject_OrarioLezioni dataObj, int index) {
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
