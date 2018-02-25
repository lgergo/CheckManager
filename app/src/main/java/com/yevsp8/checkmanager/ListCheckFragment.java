package com.yevsp8.checkmanager;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.util.Common;
import com.yevsp8.checkmanager.viewModel.CheckListViewModel;

import java.util.List;

import javax.inject.Inject;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListCheckFragment extends Fragment {

    View rootView;
    List<Check> checkList;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    CheckListViewModel viewModel;

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
    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        ((CheckManagerApplication) getActivity().getApplication())
                .getApplicationComponenet()
                .inject(this);
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

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckListViewModel.class);

        viewModel.getCheckList().observe(this, new Observer<List<Check>>() {
            @Override
            public void onChanged(@Nullable List<Check> checks) {
                if (ListCheckFragment.this.checkList == null)
                    ListCheckFragment.this.checkList = checks;
            }
        });
//        List<Check> items = new ArrayList<>();
//        //TODO adatbázisból való feltöltés
//        //TODO date long konverzió agyalás
//       /* Date date=Calendar.getInstance().getTime();
//        for (int i = 0; i < 5; i++) {
//            items.add(new Check(
//                    Integer.toString(i),
//                    date.getTime(),
//                    i * 1000,
//                    i + ". szervezet",
//                    date.getTime(),
//                    false
//            ));
//        }*/
//        //TODO provider rátegen keresztül jöjjön már csak a lista
//        //TODO dependency injection
//        //TODO oszlopnevek kiemel
//
//        Cursor cursor = DbHandler.getInstance(getContext()).getNotUploadedCheckList();
//        while (!cursor.isAfterLast()) {
//            items.add(new Check(
//                    cursor.getString(cursor.getColumnIndex("_id")),
//                    cursor.getLong(cursor.getColumnIndex("created")),
//                    cursor.getInt(cursor.getColumnIndex("amount")),
//                    cursor.getString(cursor.getColumnIndex("paid_to")),
//                    cursor.getLong(cursor.getColumnIndex("paid_date")),
//                    cursor.getInt(cursor.getColumnIndex("is_uploaded")) != 0
//            ));
//            cursor.moveToNext();
//        }
//
        final CheckAdapter adapter = new CheckAdapter(checkList);
        ListView listView = rootView.findViewById(R.id.listview_check);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Check selected = adapter.getItem(i);

                //TODO parcelable ??
                Intent intent = new Intent(getActivity(), CheckDetailsActivity.class);
                intent.putExtra("selected_check_id", selected.getCheckId());
                String created = Common.longDateToString(selected.getCreationDate());
                intent.putExtra("selected_check_created", created);
                intent.putExtra("selected_check_amount", Integer.toString(selected.getAmount()));
                intent.putExtra("selected_check_paidTo", selected.getPaidTo());
                String paidDate = Common.longDateToString(selected.getPaidDate());
                intent.putExtra("selected_check_paidDate", paidDate);
                intent.putExtra("selected_check_isUploaded", selected.getIsUploaded() ? "igen" : "nem");

                startActivity(intent);
            }
        });

    }
}
