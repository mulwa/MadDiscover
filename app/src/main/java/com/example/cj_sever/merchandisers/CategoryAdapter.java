package com.example.cj_sever.merchandisers;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cj-sever on 9/25/16.
 */
public class CategoryAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> header;
    private HashMap<String, List<String>> childData;

    public CategoryAdapter(Context context, List<String> header, HashMap<String, List<String>> childData) {
        this.context = context;
        this.header = header;
        this.childData = childData;
    }

    @Override
    public int getGroupCount() {
        return header.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childData.get(header.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return header.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int  childPosition) {
        return childData.get(header.get(groupPosition)).get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int  childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String) getGroup(groupPosition);
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.categoty_header,null);

        }
        TextView txtHeader = (TextView) view.findViewById(R.id.lblListHeader);
        txtHeader.setTypeface(null, Typeface.BOLD);
        txtHeader.setText(headerTitle);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int  childPosition, boolean b, View view, ViewGroup viewGroup) {
        String childTitle = (String) getChild(groupPosition,childPosition);
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view = inflater.inflate(R.layout.category_child,null);
        }
        TextView txtchildData = (TextView) view.findViewById(R.id.lblListItem);
        txtchildData.setText(childTitle);

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int  childPosition) {
        return true;
    }
}
