package org.denovogroup.rangzen.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.denovogroup.rangzen.R;
import org.denovogroup.rangzen.backend.SecurityManager;

/**
 * Created by Liran on 11/17/2015.
 *
 * An Activity which enable the user to set his own preferences such as security
 */
public class SettingsActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener{

    private int security_profiles;
    private float priority_threshold;

    SeekBar privacySeeker;
    SeekBar priorityThresholdSeeker;
    LinearLayout seekerTitles;
    TextView privacyDetailsTv;
    TextView priorityThresholdTv;
    String[] profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        privacySeeker = (SeekBar) findViewById(R.id.privacySeekBar);
        priorityThresholdSeeker = (SeekBar) findViewById(R.id.priorityThresholdSeekBar);
        seekerTitles = (LinearLayout) findViewById(R.id.seekbarTitles);
        privacyDetailsTv = (TextView) findViewById(R.id.schemeDetails);
        priorityThresholdTv = (TextView) findViewById(R.id.priorityThresholdTitle);
        initView();

        getStoredSettings();
    }

    /** build the dynamic view parts such as the security profile seeker */
    private void initView() {
        privacySeeker.setOnSeekBarChangeListener(this);
        profiles = SecurityManager.getInstance().getProfiles();

        seekerTitles.removeAllViews();
        for(int i=0; i<profiles.length; i++){

            TextView tv = new TextView(this);
            tv.setText(profiles[i]);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            if(i == 0){
                tv.setGravity(Gravity.LEFT);
            } else if( i == profiles.length -1){
                tv.setGravity(Gravity.RIGHT);
            } else {
                tv.setGravity(Gravity.CENTER);
            }

            seekerTitles.addView(tv);
        }

        priorityThresholdSeeker.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.privacySeekBar:
                if (fromUser) {
                    int position = convertProgressToIndex(progress);
                    int newProgress = (int) getPrivacySeekerSectionSize() * position;

                    seekBar.setProgress(newProgress);
                    privacyDetailsTv.setText(SecurityManager.getInstance().getProfile(position).getDescription());

                    security_profiles = position;
                }
                break;
            case R.id.priorityThresholdSeekBar:
                priorityThresholdTv.setText(getString(R.string.priority_threshold_title)+" ("+progress+")");
                break;
        }
    }

    private int convertProgressToIndex(int progress){
        double section = getPrivacySeekerSectionSize();
        double prev = Math.floor(progress / section);
        double next = Math.ceil(progress / section);
        return (progress - section * prev > section * next - progress) ? (int) next : (int) prev;
    }

    private double getPrivacySeekerSectionSize(){
        return ((double) privacySeeker.getMax()) / (profiles.length-1);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.privacySeekBar:
                SecurityManager.setCurrentProfile(this, security_profiles);
                priorityThresholdSeeker.setEnabled(SecurityManager.getInstance().getProfile(security_profiles).isAutodelete());
                break;
            case R.id.priorityThresholdSeekBar:
                SecurityManager.setCurrentAutodeleteThreshold(this, seekBar.getProgress() / 100f);
                break;
        }
    }

    public void getStoredSettings(){
        security_profiles = SecurityManager.getCurrentProfile(this);
        privacySeeker.setProgress((int) Math.round(getPrivacySeekerSectionSize() * security_profiles));
        onProgressChanged(privacySeeker, privacySeeker.getProgress(), true);

        priority_threshold = SecurityManager.getCurrentAutodeleteThreshold(this);
        priorityThresholdSeeker.setProgress(Math.round(priority_threshold * 100));
    }
}
