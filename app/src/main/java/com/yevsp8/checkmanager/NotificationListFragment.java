package com.yevsp8.checkmanager;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.yevsp8.checkmanager.data.Notification;
import com.yevsp8.checkmanager.data.NotificationAdapter;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.view.CreateNotificationActivity;
import com.yevsp8.checkmanager.viewModel.NotificationListViewModel;

import java.util.List;

import javax.inject.Inject;


public class NotificationListFragment extends Fragment {

    View lastClickedView;
    int lastClickedIndex = -1;
    @Inject
    CustomNotificationManager notManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    NotificationListViewModel viewModel;
    private View rootView;
    private List<Notification> notificationList;
    private FloatingActionButton button_createNotification;

    public NotificationListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(getContext()))
                .applicationModule(new ApplicationModule(getActivity().getApplication()))
                .build();
        component.injectNotificationListViewmodel(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NotificationListViewModel.class);
        viewModel.getNotificationList().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable List<Notification> notifications) {
                if (NotificationListFragment.this.notificationList == null)
                    setListData(notifications);
            }
        });
    }

    private void setListData(List<Notification> notifications) {
        notificationList = notifications;

        final NotificationAdapter adapter = new NotificationAdapter(notificationList);
        View emptyView = rootView.findViewById(R.id.listview_notification_emptyText);
        ListView listView = rootView.findViewById(R.id.listview_notification);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemClicked(adapter, view, i);
            }
        });

        button_createNotification = rootView.findViewById(R.id.create_notification_floatingButton);
        button_createNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateNotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void itemClicked(final NotificationAdapter adapter, View view, final int i) {
        if (lastClickedView == null) {
            lastClickedView = view;
            lastClickedIndex = i;
        }
        ImageView deleteIcon = lastClickedView.findViewById(R.id.company1_deleteIcon);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.deleteNotification(adapter.getItem(lastClickedIndex));
                notificationList.remove(i);
                adapter.notifyDataSetChanged();
                notManager.deleteNotification(getContext());
            }
        });

        if (lastClickedIndex == i) {
            //ImageView deleteIcon = lastClickedView.findViewById(R.id.company1_deleteIcon);
            if (deleteIcon.getVisibility() == View.VISIBLE) {
                lastClickedView.setBackgroundColor(Color.TRANSPARENT);
                deleteIcon.setVisibility(View.GONE);
            } else {
                lastClickedView.setBackgroundColor(Color.GRAY);
                deleteIcon.setVisibility(View.VISIBLE);
            }
            lastClickedView = view;
            lastClickedIndex = i;
        } else {
            //ImageView deleteIcon = lastClickedView.findViewById(R.id.company1_deleteIcon);
            if (deleteIcon.getVisibility() == View.VISIBLE) {
                lastClickedView.setBackgroundColor(Color.TRANSPARENT);
                deleteIcon.setVisibility(View.GONE);
            }
            view.setBackgroundColor(Color.GRAY);
            deleteIcon = view.findViewById(R.id.company1_deleteIcon);
            deleteIcon.setVisibility(View.VISIBLE);
            lastClickedView = view;
            lastClickedIndex = i;
        }
    }
}
