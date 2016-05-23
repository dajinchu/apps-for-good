package train.chu.chu;

import com.badlogic.gdx.Gdx;

import java.util.Map;

import train.chu.chu.AnalyticsProvider;

/**
 * Created by Da-Jin on 5/22/2016.
 */
public class BlankAnalytics implements AnalyticsProvider{
    @Override
    public void logEvent(String event, Map<String, String> params) {
        Gdx.app.log("FakeAnalytics",event+" params:"+params);
    }

    @Override
    public void logEvent(String event) {
        Gdx.app.log("FakeAnalytics",event);
    }
}
