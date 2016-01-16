package org.denovogroup.rangzen.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.HeaderViewListAdapter;

import org.denovogroup.rangzen.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liran on 1/11/2016.
 */
public class HelpFragment extends Fragment{

    private ExpandableListView listView;

    List<String> headers;
    Map<String, List<String>> data;

    boolean partial = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.help_fragment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.drawer_menu_help);

        listView = (ExpandableListView) view.findViewById(R.id.listView);

        ViewGroup footer = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.help_list_footer, null, false);
        footer.findViewById(R.id.feedback_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(),FeedbackActivity.class);
                //startActivity(intent);
                openEmailSendingForm(true);
            }
        });

        View more = footer.findViewById(R.id.more_button);
        more.setVisibility(partial ? View.VISIBLE : View.GONE);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partial = false;
                prepareHelpContent();
                v.setVisibility(View.GONE);
                listView.setAdapter(new HelpExpandableListAdapter(getActivity(), headers, data));
            }
        });

        listView.addFooterView(footer);

        prepareHelpContent();
        listView.setAdapter(new HelpExpandableListAdapter(getActivity(), headers, data));

        return view;
    }

    private void prepareHelpContent() {

        //make hot topics

        headers = new ArrayList<>();
        data = new HashMap<>();

        String header1 = getString(R.string.help_title1);
        List<String> body1 = new ArrayList<>();
        body1.add(getString(R.string.help_content1));
        headers.add(header1);
        data.put(header1, body1);

        String header2 = getString(R.string.help_title2);
        List<String> body2 = new ArrayList<>();
        body2.add(getString(R.string.help_content2));
        headers.add(header2);
        data.put(header2, body2);

        if(partial) return;
        //make all other stuff

        String header3 = getString(R.string.help_title3);
        List<String> body3 = new ArrayList<>();
        body3.add(getString(R.string.help_content3));
        headers.add(header3);
        data.put(header3, body3);

    }

    private void openEmailSendingForm(boolean includeLog){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.feedback_email), null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Rangzen Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, "Dear Rangzen support representative");


        if(includeLog) {
            File log_filename = new File(Environment.getExternalStorageDirectory() + "/device_log.txt");
            log_filename.delete();

            //get device info
            String userData = "";

            try {
                PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                userData += "Application: Ranzgen v" + info.versionName + " (" + info.versionCode + ")\n";
            } catch (PackageManager.NameNotFoundException e) {
            }

            userData += "OS version: " + android.os.Build.VERSION.SDK_INT + "\n";
            userData += "Device: " + android.os.Build.DEVICE + "\n";
            userData += "Model: " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")\n";

            try {
                log_filename.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(log_filename, true));
                writer.write(userData);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                //get log from device
                String cmd = "logcat -d -f" + log_filename.getAbsolutePath();
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }

            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(log_filename));
        }

        startActivity(Intent.createChooser(intent, "Send mail using..."));
    }
}
