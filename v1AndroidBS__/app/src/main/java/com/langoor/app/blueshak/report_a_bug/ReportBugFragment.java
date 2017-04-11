
package com.langoor.app.blueshak.report_a_bug;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.divrt.co.R;
import com.langoor.app.blueshak.MainActivity;
/*import com.langoor.app.blueshak.home.HomeMainFragment;*/

public class ReportBugFragment extends Fragment {
    public static final String TAG = "ReportBugFragment";

    Context context;
    EditText content;
    Button continue_shopping;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.thank_u_for_reporting_the_bug, container, false);
        continue_shopping=(Button)view.findViewById(R.id.continue_shopping);
     /*   continue_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.replaceFragment(new HomeMainFragment(),HomeMainFragment.TAG);
            }
        });
        context = getActivity();*/

        return view;
    }


}

