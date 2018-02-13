package com.yevsp8.checkmanager;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListCheckFragment extends Fragment {

    View rootView;

    public ListCheckFragment() {
        // Required empty public constructor
    }

    public static ListCheckFragment newInsatce() {
        Bundle args = new Bundle();
        ListCheckFragment fragment = new ListCheckFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_list_check, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //adapter adatokkal való feltöltése

        List<Check> items = new ArrayList<>();
        //TODO adatbázisból való feltöltés
        //TODO date long konverzió agyalás
       /* Date date=Calendar.getInstance().getTime();
        for (int i = 0; i < 5; i++) {
            items.add(new Check(
                    Integer.toString(i),
                    date.getTime(),
                    i * 1000,
                    i + ". szervezet",
                    date.getTime(),
                    false
            ));
        }*/
        //TODO provider rátegen keresztül jöjjön már csak a lista
        //TODO dependency injection
        //TODO oszlopnevek kiemel

        Cursor cursor = DbHandler.getInstance(getContext()).getNotUploadedCheckList();
        while (!cursor.isAfterLast()) {
            items.add(new Check(
                    cursor.getString(cursor.getColumnIndex("_id")),
                    cursor.getLong(cursor.getColumnIndex("created")),
                    cursor.getInt(cursor.getColumnIndex("amount")),
                    cursor.getString(cursor.getColumnIndex("paid_to")),
                    cursor.getLong(cursor.getColumnIndex("paid_date")),
                    cursor.getInt(cursor.getColumnIndex("is_uploaded")) != 0
            ));
            cursor.moveToNext();
        }

        final CheckAdapter adapter = new CheckAdapter(items);
        ListView listView = rootView.findViewById(R.id.listview_check);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Check selected = adapter.getItem(i);

                Intent intent = new Intent(getActivity(), CheckDetailsActivity.class);
                intent.putExtra("selected_check_id", selected.getCheckId());
                startActivity(intent);
            }
        });

    }
}
