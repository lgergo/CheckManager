package com.yevsp8.checkmanager.data;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.util.Converter;

import java.util.List;

/**
 * Created by Gergo on 2018. 03. 23..
 */

public class NotificationAdapter extends BaseAdapter {

    private List<Notification> items;


    public NotificationAdapter(List<Notification> list) {
        this.items = list;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Notification getItem(int i) {
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
                    R.layout.listitem_notification,
                    null
            );
        }

        Notification notification = getItem(i);
        TextView title = newView.findViewById(R.id.company1_title);
        TextView fromDate = newView.findViewById(R.id.company1_fromDate);
        TextView toDate = newView.findViewById(R.id.company1_toDate);

        title.setText(notification.getTitle());
        fromDate.setText(Converter.longDateToString(notification.getFrom()));
        toDate.setText(Converter.longDateToString(notification.getTo()));

        return newView;
    }
}
