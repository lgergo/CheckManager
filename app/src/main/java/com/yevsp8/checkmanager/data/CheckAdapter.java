package com.yevsp8.checkmanager.data;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.util.Converter;

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

        TextView paidTo = newView.findViewById(R.id.check_paidto);
        TextView paidDate = newView.findViewById(R.id.check_paiddate);
        paidTo.setText(check.getPaidTo());
        paidDate.setText(Converter.longDateToString(check.getPaidDate()));

        return newView;
    }
}
