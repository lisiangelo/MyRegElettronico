package it.android.j940549.myreg_elettronico.voti;

/**
 * Created by J940549 on 30/12/2017.
 */
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject_Medie> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements PieChart
            .OnClickListener {
        TextView materia;
        TextView docente;
        TextView mediaVoti;
        PieChart pie_media;

        public DataObjectHolder(View itemView) {
            super(itemView);
            materia= (TextView) itemView.findViewById(R.id.materia);
            docente= (TextView) itemView.findViewById(R.id.docete);
            mediaVoti= (TextView) itemView.findViewById(R.id.mediavoti);
            pie_media=itemView.findViewById(R.id.pie_media);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataObject_Medie> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_media_voti, parent, false);
        PieChart pie_media=view.findViewById(R.id.pie_media);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.materia.setText(mDataset.get(position).getMateria());
        holder.materia.setPaintFlags(holder.materia.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        holder.materia.setTextColor(Color.parseColor("#29b6f6"));
        holder.docente.setText(mDataset.get(position).getDocente());
        holder.mediaVoti.setText(""+mDataset.get(position).getMediaVoti());

        String voto=""+mDataset.get(position).getMediaVoti();
        creaPie(holder.pie_media,mDataset.get(position).getMediaVoti(),holder.materia.getText().toString());

/*        if(Float.parseFloat(voto)<6){
            holder.materia.setTextColor(Color.RED);
        }
        else if(Float.parseFloat(voto)>=6&&Float.parseFloat(voto)<7){
            holder.materia.setTextColor(Color.rgb(255,165,0));
        }
        else if(Float.parseFloat(voto)>=7&&Float.parseFloat(voto)<8){
            holder.materia.setTextColor(Color.YELLOW);
        }
        else if(Float.parseFloat(voto)>=8){
            holder.materia.setTextColor(Color.GREEN);
        }*/
    }

    public void addItem(DataObject_Medie dataObj, int index) {
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

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    private void creaPie(PieChart pieChart,double voto, String materia){

        float voto_f=(float) voto;
        ArrayList<Entry> Yvals = new ArrayList<>();
        Yvals.add(new Entry(10-voto_f ,0));
        Yvals.add(new Entry(voto_f,1));
        PieDataSet dataset = new PieDataSet(Yvals, " ");
        dataset.setDrawValues(false);

        ArrayList year = new ArrayList<>();
        year.add("");
        year.add(materia);
        PieData data = new PieData(year, dataset);
        data.setDrawValues(false);

        pieChart.setData(data);
        data.setValueTextSize(10);//Formatter(new PercentFormatter());
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.TRANSPARENT);
        if(voto_f<6){
            colors.add(Color.RED);
            pieChart.setCenterTextColor(Color.RED);
        }
        else if(voto_f>=6&&voto_f<7){
            colors.add(Color.rgb(255,165,0));
            pieChart.setCenterTextColor(Color.rgb(255,165,0));
        }
        else if(voto_f>=7&&voto_f<8){
            colors.add(Color.YELLOW);
            pieChart.setCenterTextColor(Color.YELLOW);
        }
        else if(voto_f>=8){
            colors.add(Color.GREEN);
            pieChart.setCenterTextColor(Color.GREEN);
        }

        dataset.setColors(colors);
        pieChart.setData(data);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterText(""+voto_f);
        pieChart.setDescription("");
        pieChart.setRotationAngle(270f);
        pieChart.setDrawSliceText(false);
        pieChart.setPadding(0,0,0,0);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.animateX(1500);
        pieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "&&&click" );
            }
        });

    }
}
