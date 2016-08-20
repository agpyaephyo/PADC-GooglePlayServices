package xyz.aungpyaephyo.gps.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.aungpyaephyo.gps.R;
import xyz.aungpyaephyo.gps.utils.FAUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class LocationFragment extends Fragment {

    public static final String TAG = LocationFragment.class.getSimpleName();

    private ControllerLocationScreen mLocationController;

    @BindView(R.id.tv_location)
    TextView tvLocation;

    @BindView(R.id.tv_likely_places)
    TextView tvLikelyPlaces;

    private Location mLocation;

    public LocationFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLocationController = (ControllerLocationScreen) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mLocation != null) {
            setLocation(mLocation);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocation = null;
    }

    public void setLocation(Location location) {
        this.mLocation = location;

        if (tvLocation != null)
            tvLocation.setText(location.getLatitude() + ", " + location.getLongitude());
    }

    @OnClick(R.id.btn_get_current_place_name)
    public void onTapGetCurrentPlaceName(View view) {
        FAUtils.getInstance().logAppEvent(FAUtils.ACTION_TAP_GET_CURRENT_PLACE_NAME);

        mLocationController.onTapGetCurrentPlaceName();
    }

    public void setLikelyNamesForCurrentPlace(PlaceLikelihoodBuffer likelyPlaces) {
        StringBuffer stringBuffer = new StringBuffer();
        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
            stringBuffer.append(String.format("Place '%s' has likelihood: %g\n",
                    placeLikelihood.getPlace().getName(),
                    placeLikelihood.getLikelihood()));
        }

        if (!TextUtils.isEmpty(stringBuffer)) {
            tvLikelyPlaces.setText(stringBuffer.toString());
        } else {
            tvLikelyPlaces.setText("There is no likely places for current location yet.");
        }

        likelyPlaces.release();
    }

    public interface ControllerLocationScreen {
        void onTapGetCurrentPlaceName();
    }
}
