package com.example.ju_group.health_assist;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.Arrays;
import java.util.List;


/*Displaying symptoms list using recycler view*/

class SymptomsRecyclerViewAdapter extends RecyclerView.Adapter<SymptomsRecyclerViewAdapter.SymptomsDataViewHolder> {

    private static final String TAG = "SymptomsRecyclerViewAda";
    private List<Symptoms> mSymptoms;
    private int[] checked;

    static class SymptomsDataViewHolder extends RecyclerView.ViewHolder {

        CheckBox mCheckBox = null;

        public SymptomsDataViewHolder(View itemView) {
            super(itemView);
            this.mCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }

    public SymptomsRecyclerViewAdapter(List<Symptoms> symptoms) {
        mSymptoms = symptoms;
    }

    @Override
    public int getItemCount() {
        if (mSymptoms != null)
            return mSymptoms.size();
        else
            return 0;
    }

    @Override
    public SymptomsDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        Log.d(TAG, "onCreateViewHolder: here");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check, parent, false);
        return (new SymptomsDataViewHolder(view));
    }

    @Override
    public void onBindViewHolder(SymptomsDataViewHolder holder, int position) {

        if (mSymptoms != null && mSymptoms.size() != 0) {

//            Log.d(TAG, "onBindViewHolder: " + mSymptoms.get(position).getSymptomName());
            holder.mCheckBox.setText(mSymptoms.get(position).getSymptomName());
            /*check if already checked earlier by user*/
            if (checked[position] == 0) {

                holder.mCheckBox.setChecked(false);
            } else {
                holder.mCheckBox.setChecked(true);
            }
        }
    }

    void loadNewData(List<Symptoms> symptoms) {
        mSymptoms = symptoms;
        checked = new int[mSymptoms.size()];
        Arrays.fill(checked, 0);
        //Log.d(TAG, "loadNewData: " + mSymptoms);
        notifyDataSetChanged();
    }
    /*To save user input*/
    void preserveChecked(int position, int value) {

        checked[position] = value;
    }
}
