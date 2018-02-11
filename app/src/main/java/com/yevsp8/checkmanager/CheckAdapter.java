package com.yevsp8.checkmanager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gergo on 2018. 02. 11..
 */

public class CheckAdapter extends BaseAdapter {

    private List<Check> items;

    public CheckAdapter(List<Check> list) {
        this.items = list;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Check getItem(int i) {
        return items != null ? items.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View newView = view;
        if (newView == null) {
            newView = View.inflate(
                    viewGroup.getContext(),
                    R.layout.listitem_check,
                    null
            );
        }
        Check check = getItem(i);

        TextView id = newView.findViewById(R.id.check_paidto);
        TextView uploaded = newView.findViewById(R.id.check_isuploaded);
        id.setText(check.getCheckId());
        uploaded.setText(!check.getIsUploaded() ? "0" : "1");

        return newView;
    }
}
