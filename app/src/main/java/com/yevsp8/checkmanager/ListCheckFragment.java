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
import com.yevsp8.checkmanager.data.CheckAdapter;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.util.Common;
import com.yevsp8.checkmanager.view.CheckDetailsActivity;
import com.yevsp8.checkmanager.viewModel.CheckListViewModel;

import java.util.List;

import javax.inject.Inject;

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

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(getContext()))
                .applicationModule(new ApplicationModule(getActivity().getApplication()))
                .build();
        component.injectCheckViewModel(this);
    }

//    //TODO only for testing
//    void generateDemoData()
//    {
//        if(viewModel.getCheckList().==0) {
//            Date date = Calendar.getInstance().getTime();
//            database.checkDAO().insertCheck(new Check("01301823", date.getTime(), 1250, "Főtáv", date.getTime(), false));
//            database.checkDAO().insertCheck(new Check("471145743", date.getTime(), 1250, "Telekom", date.getTime(), false));
//            database.checkDAO().insertCheck(new Check("963349038", date.getTime(), 8900, "Upc", date.getTime(), false));
//            database.checkDAO().insertCheck(new Check("459231004", date.getTime(), 22340, "Közművek", date.getTime(), false));
//        }
//    }

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
