package joeltio.pulse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import joeltio.pulse.Fragments.CalibrateFragment;
import joeltio.pulse.Fragments.CalibrateIntroFragment;

public class CalibrateActivity extends AppCompatActivity {

    public static String EXTRA_MEAN_VALUE = "joeltio.pulse.CalibrateActivity.EXTRA_MEAN_VALUE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        getSupportActionBar().hide();

        CalibrateIntroFragment calibrateIntroFragment = new CalibrateIntroFragment();
        getFragmentManager().beginTransaction().add(
                R.id.calibrate_fragment,
                calibrateIntroFragment,
                calibrateIntroFragment.getTag()
        ).commit();
    }

    public void startCalibration() {
        CalibrateFragment calibrateFragment = new CalibrateFragment();
        getFragmentManager().beginTransaction().replace(
                R.id.calibrate_fragment,
                calibrateFragment,
                calibrateFragment.getTag()
        ).commit();
    }

    public void stopCalibration(Double meanValue) {
        startNextActivity(meanValue);
    }

    private void startNextActivity(Double meanValue) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MEAN_VALUE, meanValue);
        startActivity(intent);
    }
}
