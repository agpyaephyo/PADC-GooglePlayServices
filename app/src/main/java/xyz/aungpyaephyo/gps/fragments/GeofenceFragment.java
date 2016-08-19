package xyz.aungpyaephyo.gps.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.aungpyaephyo.gps.R;

/**
 * Created by aung on 3/7/16.
 */
public class GeofenceFragment extends Fragment {

    public static final String TAG = GeofenceFragment.class.getSimpleName();

    private ControllerGeofenceScreen controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ControllerGeofenceScreen) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geofence, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.btn_add_geofences)
    public void onTapAddGeofences(View view) {
        controller.onTapAddGeofences();
    }

    @OnClick(R.id.btn_start_place_picker)
    public void onTapStartPlacePicker(View view) {
        controller.onTapStartPlacePicker();
    }

    public interface ControllerGeofenceScreen {
        void onTapAddGeofences();
        void onTapStartPlacePicker();
    }
}
