package com.yevsp8.checkmanager.view;

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

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.data.CheckAdapter;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.util.Constants;
import com.yevsp8.checkmanager.viewModel.CheckListViewModel;

import java.util.List;

import javax.inject.Inject;

public class CheckListFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private View rootView;
    private List<Check> checkList;
    private CheckListViewModel viewModel;

    public CheckListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(getContext()))
                .applicationModule(new ApplicationModule(getActivity().getApplication()))
                .build();
        component.injectCheckListViewModel(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_check_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckListViewModel.class);

        viewModel.getCheckList().observe(this, new Observer<List<Check>>() {
            @Override
            public void onChanged(@Nullable List<Check> checks) {
                if (CheckListFragment.this.checkList == null)
                    setListData(checks);
            }
        });
    }

    private void setListData(List<Check> checks) {
        checkList = checks;

        final CheckAdapter adapter = new CheckAdapter(checkList);
        ListView listView = rootView.findViewById(R.id.listview_check);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Check selected = adapter.getItem(i);

                Intent intent = new Intent(getContext(), CheckDetailsActivity.class);
                intent.putExtra(Constants.SelectedCheckId, selected.getCheckId());
                startActivity(intent);

            }
        });
    }
}
