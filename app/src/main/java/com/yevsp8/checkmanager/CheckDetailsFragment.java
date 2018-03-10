package com.yevsp8.checkmanager;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.util.Converter;
import com.yevsp8.checkmanager.viewModel.CheckViewModel;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckDetailsFragment extends Fragment {

    View rootView;
    Check check;
    String checkId;
    String recognisedText;

    TextView id;
    TextView created;
    TextView amount;
    TextView paidto;
    TextView paiddate;
    TextView isuploaded;

    Button button_edit;
    Button button_upload;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    CheckViewModel viewModel;


    public CheckDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(getContext()))
                .applicationModule(new ApplicationModule(getActivity().getApplication()))
                .build();
        component.injectCheckViewModel(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_check_details, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckViewModel.class);
        viewModel.getCheckById(checkId).observe(this, new Observer<Check>() {
            @Override
            public void onChanged(@Nullable Check check) {
                if (CheckDetailsFragment.this.check == null) {
                    setTextViewValues(check);
                }
            }
        });
    }


    private void setTextViewValues(Check check) {

        CheckDetailsFragment.this.check = check;

        button_edit = getActivity().findViewById(R.id.button_details_edit);
        //TODO google api meghívása
        button_upload = getActivity().findViewById(R.id.button_details_upload);

        Bundle args = getArguments();
        checkId = args.getString("selected_check_id");
        recognisedText = args.getString("recognised_text");
        if (check == null) {
            //TODO valós adatokkal
            check = new Check(recognisedText, 20180303, 1500, "Minta Kft", 20180303, false);
        }

        id = rootView.findViewById(R.id.check_details_id);
        created = rootView.findViewById(R.id.check_details_create);
        amount = rootView.findViewById(R.id.check_details_amount);
        paidto = rootView.findViewById(R.id.check_details_paidto);
        paiddate = rootView.findViewById(R.id.check_details_paiddate);
        isuploaded = rootView.findViewById(R.id.check_details_isUploaded);

        id.setText(check.getCheckId());
        created.setText(Converter.longDateToString(check.getCreationDate()));
        amount.setText(String.valueOf(check.getAmount()));
        paidto.setText(check.getPaidTo());
        paiddate.setText(Converter.longDateToString(check.getPaidDate()));
        isuploaded.setText(check.getIsUploaded() ? "igen" : "nem");
    }
}
