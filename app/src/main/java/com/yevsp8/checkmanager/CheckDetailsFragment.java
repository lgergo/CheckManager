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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.util.Converter;
import com.yevsp8.checkmanager.util.Enums;
import com.yevsp8.checkmanager.viewModel.CheckViewModel;

import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckDetailsFragment extends Fragment {

    View rootView;
    Check check;
    String checkId;
    String[] recognisedText;

    EditText id;
    EditText created;
    EditText amount;
    EditText paidto;
    EditText paiddate;

    boolean isSaveEnabled = false;
    Button button_edit_save;
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

        Bundle args = getArguments();
        checkId = args.getString("selected_check_id");
        recognisedText = args.getStringArray("result_array");

        button_edit_save = rootView.findViewById(R.id.button_details_edit_save);
        button_edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSaveEnabled = !isSaveEnabled;
                setEditingTo();
            }
        });


        button_upload = rootView.findViewById(R.id.button_details_upload);
        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBeforeUpload();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckViewModel.class);
        viewModel.getCheckById(checkId).observe(this, new Observer<Check>() {
            @Override
            public void onChanged(@Nullable Check checkParam) {
                if (CheckDetailsFragment.this.check == null) {
                    setTextViewValues(checkParam);
                }
            }
        });
    }

    private void setTextViewValues(Check check) {

        CheckDetailsFragment.this.check = check;

        if (checkId == null) {
            int amountValue;
            String trimmed = recognisedText[1].replaceAll("(\\*| )", "");
            try {
                amountValue = Integer.getInteger(trimmed);
            } catch (Exception ex) {
                amountValue = 0;
            }
            long today = Calendar.getInstance().getTime().getTime();
            String month = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            check = new Check(recognisedText[0], today, amountValue, recognisedText[2], month);
        }

        id = rootView.findViewById(R.id.check_details_id);
        created = rootView.findViewById(R.id.check_details_create);
        amount = rootView.findViewById(R.id.check_details_amount);
        paidto = rootView.findViewById(R.id.check_details_paidto);
        paiddate = rootView.findViewById(R.id.check_details_paiddate);

        id.setText(check.getCheckId());
        created.setText(Converter.longDateToString(check.getCreationDate()));
        amount.setText(String.valueOf(check.getAmount()));
        paidto.setText(check.getPaidTo());
        paiddate.setText(check.getPaidDate());
    }

    private void setEditingTo() {
        id.setEnabled(isSaveEnabled);
        amount.setEnabled(isSaveEnabled);
        paidto.setEnabled(isSaveEnabled);
        paiddate.setEnabled(isSaveEnabled);
        button_upload.setEnabled(!isSaveEnabled);
        if (isSaveEnabled) {
            button_edit_save.setText("Save");
        } else {
            button_edit_save.setText("Edit");
        }
    }

    private void checkBeforeUpload() {
        if (id.getText().length() > 0 && amount.getText().length() > 0 && paidto.getText().length() > 0 && paiddate.getText().length() > 0) {
            Intent intent = new Intent(getContext(), GoogleApiActivity.class);
            intent.putExtra("callType", Enums.APICallType.Update_data);
            startActivity(intent);
        } else {
            Toast t = Toast.makeText(getContext(), "Üres mezővel nem lehetséges a feltöltés!", Toast.LENGTH_LONG);
            t.show();
        }
    }
}
