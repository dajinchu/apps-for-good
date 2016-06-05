package train.chu.chu;

import java.util.Map;

/**
 * Created by Da-Jin on 5/22/2016.
 */
public interface AnalyticsProvider {

    public void logEvent(String event, Map<String,String> params);
    public void logEvent(String event);
}
