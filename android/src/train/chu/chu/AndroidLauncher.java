package train.chu.chu;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;

public class AndroidLauncher extends AndroidApplication {

    private FirebaseAnalytics firebaseAnalytics;

    @Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Main(new AndroidFirebaseAnalytics(firebaseAnalytics)), config);
	}
}
