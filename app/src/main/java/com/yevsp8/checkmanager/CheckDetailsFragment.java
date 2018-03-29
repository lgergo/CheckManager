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
import com.yevsp8.checkmanager.view.MainActivity;
import com.yevsp8.checkmanager.viewModel.CheckViewModel;

import java.util.Calendar;

import javax.inject.Inject;


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
    Button button_delete;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    CheckViewModel viewModel;

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
        rootView = inflater.inflate(R.layout.fragment_check_details, container, false);

        Bundle args = getArguments();
        checkId = args.getString("selected_check_id");
        recognisedText = args.getStringArray("result_array");

        button_edit_save = rootView.findViewById(R.id.button_details_edit_save);
        button_edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSaveEnabled = !isSaveEnabled;
                editSaveClicked();
            }
        });

        button_upload = rootView.findViewById(R.id.button_details_upload);
        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadClicked();
            }
        });

        button_delete = rootView.findViewById(R.id.button_details_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteClicked();
            }
        });

        id = rootView.findViewById(R.id.check_details_id);
        created = rootView.findViewById(R.id.check_details_create);
        amount = rootView.findViewById(R.id.check_details_amount);
        paidto = rootView.findViewById(R.id.check_details_paidto);
        paiddate = rootView.findViewById(R.id.check_details_paiddate);

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
            long today = Calendar.getInstance().getTime().getTime();
            check = new Check(recognisedText[0], today, Integer.parseInt(recognisedText[1]), recognisedText[2], Converter.longDateToString(today));
            viewModel.insertCheck(check);
        }

        id.setText(check.getCheckId());
        created.setText(Converter.longDateToString(check.getCreationDate()));
        amount.setText(String.valueOf(check.getAmount()));
        paidto.setText(check.getPaidTo());
        paiddate.setText(check.getPaidDate());
    }

    private void editSaveClicked() {
        id.setEnabled(isSaveEnabled);
        amount.setEnabled(isSaveEnabled);
        paidto.setEnabled(isSaveEnabled);
        paiddate.setEnabled(isSaveEnabled);
        button_upload.setEnabled(!isSaveEnabled);
        button_delete.setEnabled(!isSaveEnabled);
        if (isSaveEnabled) {
            button_edit_save.setText(R.string.button_save);
        } else {
            button_edit_save.setText(R.string.button_edit);
            long creationDate = check.getCreationDate();
            check = new Check(id.getText().toString(), creationDate, Integer.parseInt(amount.getText().toString()), paidto.getText().toString(), paiddate.getText().toString());
            viewModel.insertCheck(check);
        }
    }

    private void uploadClicked() {
        if (id.getText().length() > 0 && amount.getText().length() > 0 && paidto.getText().length() > 0 && paiddate.getText().length() > 0) {
            Intent intent = new Intent(getContext(), GoogleApiActivity.class);
            intent.putExtra("callType", Enums.APICallType.Update_data);
            String[] param = viewModel.checkDetailsToGoogleRequestFormat(check);
            intent.putExtra("result_array", param);
            startActivity(intent);
        } else {
            Toast t = Toast.makeText(getContext(), R.string.upload_emptyField, Toast.LENGTH_LONG);
            t.show();
        }
    }

    private void deleteClicked() {
        viewModel.deleteCheck(check);
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
}
