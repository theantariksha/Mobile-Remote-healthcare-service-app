package com.example.ju_group.health_assist;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


class SymptomsRecyclerClickListener extends RecyclerView.SimpleOnItemTouchListener {


    private static final String TAG = "SymptomsRecyclerClickLi";

    interface onRecyclerClickListener {

        void onItemClick(View view, int position);
    }

    private final onRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public SymptomsRecyclerClickListener(Context context, final RecyclerView recyclerView, onRecyclerClickListener listener) {

        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {

//                Log.d(TAG, "onSingleTapUp: one tap");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView!=null && mListener!=null){

                    mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {


        if (mGestureDetector != null) {
            boolean result = mGestureDetector.onTouchEvent(e);
//            Log.d(TAG, "onInterceptTouchEvent: returned" + result);
            return result;
        } else {
//            Log.d(TAG, "onInterceptTouchEvent: returned false");
            return false;
        }
//        return super.onInterceptTouchEvent(rv, e);
    }
}
