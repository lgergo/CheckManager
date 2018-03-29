package com.yevsp8.checkmanager.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yevsp8.checkmanager.R;


public class HelpFragment extends Fragment {

    View rootView;
    TextView helpText;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_help, container, false);

        helpText = getActivity().findViewById(R.id.help_textView);

        return rootView;
    }

}
