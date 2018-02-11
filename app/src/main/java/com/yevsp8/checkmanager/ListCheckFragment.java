package com.yevsp8.checkmanager;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
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
        return inflater.inflate(R.layout.fragment_list_check, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //adapter adatokkal való feltöltése

        List<Check> items = new ArrayList<>();
        //TODO adatbázisból való feltöltés
        Date date = new Date(2018, 1, 2);
        for (int i = 0; i < 5; i++) {
            items.add(new Check(
                    Integer.toString(i),
                    date,
                    i * 1000,
                    i + ". szervezet",
                    date,
                    false
            ));
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
