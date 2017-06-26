package com.example.abdulsamad.dictionaryapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
/**
 * Created by DELL on 24-08-2016.
 */
public class ResultAdapter extends BaseAdapter {
    Context context;
    ArrayList<ResultProvider> serviceManualArrayList;
    LayoutInflater inflater;
    public ResultAdapter(Context context, ArrayList<ResultProvider> serviceManualArrayList) {
        this.context = context;
        this.serviceManualArrayList = serviceManualArrayList;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return serviceManualArrayList.size();
    }
    @Override
    public Object getItem(int i) {
        return i;
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder;
        if (view == null) {
            holder = new Holder();
            view = inflater.inflate(R.layout.customview_results, viewGroup, false);
            holder.word = (TextView) view.findViewById(R.id.word);
            holder.meaning = (TextView) view.findViewById(R.id.meaning);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        ResultProvider d = serviceManualArrayList.get(i);
        holder.word.setText(d.getWord());
        String definations="";
        String lines[] = d.getMeaning().toString().split("[\\r\\n]+");
        for(int j=0;j<lines.length;j++)
        {
            definations=definations+(j+1)+") "+lines[j]+"\n";
        }
        //Toast.makeText(context, lines.length, Toast.LENGTH_SHORT).show();
        holder.meaning.setText(definations);
        return view;
    }
    class Holder {
        TextView word,meaning;
    }
}