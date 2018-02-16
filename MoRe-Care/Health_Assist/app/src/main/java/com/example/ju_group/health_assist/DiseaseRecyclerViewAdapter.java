package com.example.ju_group.health_assist;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;



/*To load the recycler view items for Show Disease*/

class DiseaseRecyclerViewAdapter extends RecyclerView.Adapter<DiseaseRecyclerViewAdapter.DiseaseDataViewHolder> {

    private static final String TAG = "DiseaseRecyclerViewAdap";
    private List<Symptoms> mDisease;

    static class DiseaseDataViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public DiseaseDataViewHolder(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.diseaseName);
        }
    }

    public DiseaseRecyclerViewAdapter(List<Symptoms> disease) {
        mDisease = disease;
    }

    @Override
    public int getItemCount() {
        if (mDisease != null)
            return mDisease.size();
        else
            return 0;
    }

    @Override
    public DiseaseRecyclerViewAdapter.DiseaseDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: here");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease, parent, false);
        return (new DiseaseRecyclerViewAdapter.DiseaseDataViewHolder(view));
    }

    @Override
    public void onBindViewHolder(DiseaseRecyclerViewAdapter.DiseaseDataViewHolder holder, int position) {

        if (mDisease != null && mDisease.size() != 0) {

            Log.d(TAG, "onBindViewHolder: " + mDisease.get(position).getSymptomName());
            holder.mTextView.setText(mDisease.get(position).getSymptomName());

        }
    }

    void loadNewData(List<Symptoms> disease) {
        mDisease = disease;
        //Log.d(TAG, "loadNewData: " + mSymptoms);
        notifyDataSetChanged();
    }
}
