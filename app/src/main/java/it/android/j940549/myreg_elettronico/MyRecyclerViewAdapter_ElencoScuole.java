package it.android.j940549.myreg_elettronico;

/**
 * Created by J940549 on 30/12/2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.Scuola;
import it.android.j940549.myreg_elettronico.voti.MyRecyclerViewAdapter;

public class MyRecyclerViewAdapter_ElencoScuole extends RecyclerView.Adapter<MyRecyclerViewAdapter_ElencoScuole.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Scuola> mDataset;
    Activity myActivity;
    //    private static MyClickListener myClickListener;

    public class DataObjectHolder extends RecyclerView.ViewHolder{
        //        implements View
        //          .OnClickListener {
        TextView tipo_istituto;
        TextView nome_istituto;
        TextView cf_istituto;
        TextView indirizzo;

        public DataObjectHolder(View itemView) {
            super(itemView);
            tipo_istituto= (TextView) itemView.findViewById(R.id.tipo_istituto);
            nome_istituto= (TextView) itemView.findViewById(R.id.nome_istituto);
            cf_istituto= (TextView) itemView.findViewById(R.id.cf_istituto);
            indirizzo= (TextView) itemView.findViewById(R.id.indirizzo);

        }


        /*@Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }*/
    }


    /*public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }*/

    public MyRecyclerViewAdapter_ElencoScuole(ArrayList<Scuola> myDataset, Activity activity) {
        mDataset = myDataset;
        myActivity=activity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_elenco_scuola, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tipo_istituto= (TextView) view.findViewById(R.id.tipo_istituto);
                TextView nome_istituto= (TextView) view.findViewById(R.id.nome_istituto);
                TextView cf_istituto= (TextView) view.findViewById(R.id.cf_istituto);
                TextView indirizzo= (TextView) view.findViewById(R.id.indirizzo);

               // Toast.makeText(myActivity, "cf_istituto..."+cf, Toast.LENGTH_SHORT).show();
                    if(cf_istituto.getText().toString()!=null||!cf_istituto.getText().toString().equals("")) {
                    Intent return_cf = new Intent();
                    return_cf.putExtra("cf_istituto", cf_istituto.getText().toString());
                    return_cf.putExtra("tipo_istituto", tipo_istituto.getText().toString());
                    return_cf.putExtra("nome_istituto", nome_istituto.getText().toString());
                    return_cf.putExtra("indirizzo", indirizzo.getText().toString());

                    myActivity.setResult(100,return_cf);
                    myActivity.finish();

                }else{
                    Toast.makeText(myActivity, "errore su cf_istituto", Toast.LENGTH_SHORT).show();
                }


            }
        });


        DataObjectHolder dataObjectHolder = new DataObjectHolder(itemView);
        return dataObjectHolder;


    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.tipo_istituto.setText(mDataset.get(position).getTipo());
        holder.nome_istituto.setText(mDataset.get(position).getNomescuola());
        holder.cf_istituto.setText(mDataset.get(position).getCfscuola());
        holder.indirizzo.setText(mDataset.get(position).getIndirizzo()+" "+mDataset.get(position).getComune());
    }

    public void addItem(Scuola dataObj, int index) {
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

    private void ritornaResult(String cf){
        Intent intent_result= new Intent();
        intent_result.putExtra("cf_istituto", cf);
        myActivity.setResult(100,intent_result);
        myActivity.finish();
    }
}
