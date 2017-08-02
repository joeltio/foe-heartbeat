package joeltio.pulse.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import joeltio.pulse.CalibrateActivity;
import joeltio.pulse.R;

public class CalibrateIntroFragment extends Fragment {
    public CalibrateIntroFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calibrate_intro, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button calibrateButton = getActivity().findViewById(R.id.calibrate_intro_button);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CalibrateActivity) getActivity()).startCalibration();
            }
        });
    }
}
