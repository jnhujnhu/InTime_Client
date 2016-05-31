package com.example.kevin.mapapplication.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import java.util.List;

/**
 * Created by Kevin on 5/27/16.
 */

public abstract class CustomListViewAdapter extends ArrayAdapter<CustomListItem> {

    public CustomListViewAdapter(Context context, int resource, List<CustomListItem> items) {
        super(context, resource, items);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return setViewDetail(position, convertView, parent);

    }
    public abstract View setViewDetail(int position, View convertView, ViewGroup parent);
}
