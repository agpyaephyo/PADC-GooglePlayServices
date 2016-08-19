package xyz.aungpyaephyo.gps.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.aungpyaephyo.gps.R;
import xyz.aungpyaephyo.gps.controllers.ControllerActivityRecognition;
import xyz.aungpyaephyo.gps.utils.UserActivityUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityRecognitionFragment extends Fragment {

    @BindView(R.id.tv_activity)
    TextView tvActivity;

    @BindView(R.id.btn_request_activity_update)
    Button btnRequestActivityUpdate;

    @BindView(R.id.btn_remove_activity_update)
    Button btnRemoveActivityUpdate;

    private ControllerActivityRecognition controllerActivityRecognition;
    private ControllerActivityRecognitionScreen controllerActivityRecognitionScreen;

    public static final String TAG = ActivityRecognitionFragment.class.getSimpleName();

    public ActivityRecognitionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controllerActivityRecognition = (ControllerActivityRecognition) context;
        controllerActivityRecognitionScreen = (ControllerActivityRecognitionScreen) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_activity_recognition, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        onTapRemoveActivityUpdate(null);
    }

    @OnClick(R.id.btn_request_activity_update)
    public void onTapRequestActivityUpdate(View view) {
        controllerActivityRecognition.requestActivityUpdate();
        btnRequestActivityUpdate.setEnabled(false);
        btnRemoveActivityUpdate.setEnabled(true);
    }

    @OnClick(R.id.btn_remove_activity_update)
    public void onTapRemoveActivityUpdate(View view) {
        controllerActivityRecognition.removeActivityUpdate();
        btnRequestActivityUpdate.setEnabled(true);
        btnRemoveActivityUpdate.setEnabled(false);
    }

    public void setDetectedActivities(ArrayList<DetectedActivity> detectedActivities) {
        StringBuilder activityTextBuilder = new StringBuilder();
        for (DetectedActivity detectedActivity : detectedActivities) {
            activityTextBuilder.append(UserActivityUtils.getActivityByType(detectedActivity.getType()) + " - " + detectedActivity.getConfidence() + "% \n");
        }

        tvActivity.setText(activityTextBuilder.toString());
    }

    public interface ControllerActivityRecognitionScreen {

    }
}
