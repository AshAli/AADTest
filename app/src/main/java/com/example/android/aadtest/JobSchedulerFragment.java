package com.example.android.aadtest;


import android.app.job.JobInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobSchedulerFragment extends Fragment {


    public JobSchedulerFragment() {
        // Required empty public constructor
    }

    // Switches for setting job options.
    private Switch mDeviceIdleSwitch;
    private Switch mDeviceChargingSwitch;
    private RadioGroup mNetworkOptions;

    // Override deadline seekbar.
    private SeekBar mSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView =  inflater.inflate(R.layout.fragment_job_scheduler, container, false);

        mDeviceIdleSwitch = myView.findViewById(R.id.idleSwitch);
        mDeviceChargingSwitch = myView.findViewById(R.id.chargingSwitch);
        mNetworkOptions = myView.findViewById(R.id.networkOptions);
        mSeekBar = myView.findViewById(R.id.seekBar);

        final TextView seekBarProgress = myView.findViewById(R.id.seekBarProgress);

        // Updates the TextView with the value from the seekbar.
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i > 0) {
                    seekBarProgress.setText(getString(R.string.seconds, i));
                } else {
                    seekBarProgress.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button button8 = (Button) myView.findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setConstraints();
            }
        });

        /*
        SHARED PREFERENCES
        */
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mSeekBar.setProgress((pref.getInt("seekBarInteger", 0))/1000);
        mNetworkOptions.check(pref.getInt("selectedNetworkOption", 0));
        mDeviceIdleSwitch.setChecked(pref.getBoolean("mDeviceIdleSwitch",false));
        mDeviceChargingSwitch.setChecked(pref.getBoolean("mDeviceChargingSwitch",false));

        return myView;
    }

    public void setConstraints() {

        int selectedNetworkID = mNetworkOptions.getCheckedRadioButtonId();

        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        switch (selectedNetworkID) {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        int seekBarInteger = mSeekBar.getProgress();
        boolean seekBarSet = seekBarInteger > 0;

        if (seekBarSet) {
            seekBarInteger *= 1000;
        }

        boolean constraintSet = selectedNetworkOption
                != JobInfo.NETWORK_TYPE_NONE
                || mDeviceChargingSwitch.isChecked()
                || mDeviceIdleSwitch.isChecked()
                || seekBarSet;

        if (constraintSet) {

            /*
            SHARED PREFERENCES
            */
            SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putInt("seekBarInteger", seekBarInteger);
            edt.putInt("selectedNetworkOption", selectedNetworkID);
            edt.putBoolean("mDeviceIdleSwitch",mDeviceIdleSwitch.isChecked());
            edt.putBoolean("mDeviceChargingSwitch",mDeviceChargingSwitch.isChecked());
            edt.apply();

            Intent intent  = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("OVERRIDE_DEADLINE_ID", seekBarInteger);
            intent.putExtra("NETWORK_TYPE_ID", selectedNetworkOption);
            intent.putExtra("REQUIRE_IDLE_ID", mDeviceIdleSwitch.isChecked());
            intent.putExtra("REQUIRE_CHARGING_ID", mDeviceChargingSwitch.isChecked());
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "No Constraint Set", Toast.LENGTH_SHORT).show();
        }
    }

}
