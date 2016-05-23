package train.chu.chu;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

/**
 * Created by Da-Jin on 5/22/2016.
 */
public class AndroidFirebaseAnalytics implements AnalyticsProvider {

    private final FirebaseAnalytics firebaseAnalytics;

    public AndroidFirebaseAnalytics(FirebaseAnalytics firebaseAnalytics){
        this.firebaseAnalytics = firebaseAnalytics;
    }
    @Override
    public void logEvent(String event, Map<String, String> params) {
        Bundle bundle = new Bundle();
        for(Map.Entry<String,String> e: params.entrySet()){
            bundle.putString(e.getKey(),e.getValue());
        }
        firebaseAnalytics.logEvent(event,bundle);
    }

    @Override
    public void logEvent(String event) {
        firebaseAnalytics.logEvent(event,Bundle.EMPTY);
    }
}
