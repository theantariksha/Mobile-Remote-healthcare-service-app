package com.example.ju_group.health_assist;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class SelectedSymptomsRViewAdapter extends RecyclerView.Adapter<SelectedSymptomsRViewAdapter.SymptomsDataViewHolder> {

    private static final String TAG = "SelectedSymptomsRViewAd";
    private List<String> mSymptoms;
    private List<Boolean> danger;

    static class SymptomsDataViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public SymptomsDataViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.diseaseName);
        }
    }

    public SelectedSymptomsRViewAdapter(HashMap<String, String> symptoms) {

        mSymptoms = new ArrayList<>();
        danger = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        if (mSymptoms != null)
            return mSymptoms.size();
        else
            return 0;
    }

    @Override
    public SelectedSymptomsRViewAdapter.SymptomsDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        Log.d(TAG, "onCreateViewHolder: here");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease, parent, false);
        return (new SelectedSymptomsRViewAdapter.SymptomsDataViewHolder(view));
    }

    @Override
    public void onBindViewHolder(SelectedSymptomsRViewAdapter.SymptomsDataViewHolder holder, int position) {

        if (mSymptoms != null && mSymptoms.size() != 0) {

            holder.mTextView.setText(mSymptoms.get(position));
            if (danger.get(position))
                holder.mTextView.setTextColor(Color.RED);
            else
                holder.mTextView.setTextColor(Color.DKGRAY);

        }
    }

    void loadNewData(HashMap<String, String> symptoms) {


        Iterator it = symptoms.entrySet().iterator();
//        Log.d(TAG, "loadNewData: map" + symptoms);
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            mSymptoms.add(pair.getKey().toString());
            try {
                if (pair.getValue().toString().equalsIgnoreCase("True"))
                    danger.add(true);
                else
                    danger.add(false);
            } catch (NullPointerException e) {

                Log.e(TAG, "loadNewData: No value for dangerous field, default to false");
                danger.add(false);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        notifyDataSetChanged();
    }
}
