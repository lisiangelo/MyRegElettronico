package it.android.j940549.myreg_elettronico.voti;

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

public class MyRecyclerViewAdapter_ElencoVoti extends RecyclerView.Adapter<MyRecyclerViewAdapter_ElencoVoti.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject_elencoVoti> mDataset;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{
    //        implements View
  //          .OnClickListener {
        TextView data;
        TextView materia;
        TextView tipo;
        TextView voto;

        public DataObjectHolder(View itemView) {
            super(itemView);
            data= (TextView) itemView.findViewById(R.id.data_voto);
            materia= (TextView) itemView.findViewById(R.id.materia_voto);
            tipo= (TextView) itemView.findViewById(R.id.tipo_voto);
            voto= (TextView) itemView.findViewById(R.id.voto);
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

    public MyRecyclerViewAdapter_ElencoVoti(ArrayList<DataObject_elencoVoti> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_media_elencovoti, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
       String data=mDataset.get(position).getData();
               String datagm[]=data.split("-");
        //data=datagm[2]+"/"+datagm[1]+"/"+datagm[0];
        holder.data.setText(data);
        holder.materia.setText(mDataset.get(position).getMateria());
        holder.tipo.setText(mDataset.get(position).getTipo());
        String voto=""+mDataset.get(position).getVoto();
        holder.voto.setText(""+voto);
        if(Float.parseFloat(voto)<6){
            holder.voto.setTextColor(Color.RED);
        }
        else if(Float.parseFloat(voto)>=6&&Float.parseFloat(voto)<7){
            holder.voto.setTextColor(Color.rgb(255,165,0));
        }
        else if(Float.parseFloat(voto)>=7&&Float.parseFloat(voto)<8){
            holder.voto.setTextColor(Color.YELLOW);
        }
        else if(Float.parseFloat(voto)>=8){
            holder.voto.setTextColor(Color.GREEN);
        }

    }

    public void addItem(DataObject_elencoVoti dataObj, int index) {
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
