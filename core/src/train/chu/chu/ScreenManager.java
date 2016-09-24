package train.chu.chu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Da-Jin on 9/10/2016.
 */
public class ScreenManager extends Game {
    private final AnalyticsProvider analy;

    public ScreenManager(AnalyticsProvider analy) {
        this.analy = analy;
    }

    public void startMain(Skin skin){
        setScreen(new Main(analy, skin));
    }

    @Override
    public void create() {
        setScreen(new LoadingScreen(this));
    }
}
