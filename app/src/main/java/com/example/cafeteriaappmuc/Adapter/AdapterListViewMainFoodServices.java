package com.example.cafeteriaappmuc.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cafeteriaappmuc.MyDataListMain;
import com.example.cafeteriaappmuc.R;

import java.util.ArrayList;

/**
 * Adapter used to make listView fir list over food services in MainActivity.
 */

public class AdapterListViewMainFoodServices extends BaseAdapter {



    private Context context;

    private ArrayList<MyDataListMain> arrayList;

   // private TextView textViewFoodService, textViewEstimatedTimeToWalk, textViewExpectedQueueWaitingTime;

    public AdapterListViewMainFoodServices(Context context, ArrayList<MyDataListMain> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    //inner class to hold views for each row
    public class ViewHolder{
        TextView textViewFoodService;
        TextView textViewEstimatedTimeToWalk;
        TextView textViewExpectedQueueWaitingTime;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater inflater;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView= inflater.inflate(R.layout.list_item_food_services_main, null);
        }

        // viewholder object
        final ViewHolder holder = new ViewHolder();

        //initialize view
        holder.textViewFoodService=convertView.findViewById(R.id.tvFoodServiceListView);
        holder.textViewEstimatedTimeToWalk=convertView.findViewById(R.id.tvEstimatedTimeToWalkListView);
        holder.textViewExpectedQueueWaitingTime=convertView.findViewById(R.id.tvExpectedQueueWaitingTimeListView);

        //assign data
        String textFoodService = arrayList.get(position).getFoodService();
        holder.textViewFoodService.setText(textFoodService);
        String textWaitingTime = context.getResources().getString(R.string.expected_queue_waiting_time) + (arrayList.get(position).getQueueTime());
        holder.textViewExpectedQueueWaitingTime.setText(textWaitingTime);
        String textTimeToWalk =  context.getResources().getString(R.string.estimated_time_to_walk) + (arrayList.get(position).getTimeToWalk());
        holder.textViewEstimatedTimeToWalk.setText(textTimeToWalk);

        return convertView;
    }
}
