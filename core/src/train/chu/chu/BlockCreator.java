package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Arun on 4/6/2016.
 */
public class BlockCreator {

    public static Block BlockCreator(String str, Skin skin){

        String name=ButtonCreator.generateString(str);

        Block block;
        block = new Block();
        block.setTouchable(Touchable.enabled);
        Label second = new Label(name,skin);
        second.setTouchable(Touchable.disabled);
        second.setColor(Color.BLACK);
        block.addActor(second);

        return block;
    }

}
