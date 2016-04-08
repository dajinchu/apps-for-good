package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * Created by Arun on 4/6/2016.
 */
public class BlockCreator {

    public static Block BlockCreator(String str, Skin skin){

        String name=ButtonCreator.generateString(str);

        Block block;
        block = new Block();
        Label second = new Label(name,skin);
        second.setColor(Color.BLACK);
        block.addActor(second);

        return block;
    }

}
