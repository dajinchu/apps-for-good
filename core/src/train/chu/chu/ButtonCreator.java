package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by Arun on 4/6/2016.
 */
public class ButtonCreator {


    static TextButton inputButton;
    public static TextButton ButtonCreator(String str, Skin skin){

        String name= generateString(str);
        inputButton=new TextButton(name, skin);
        return inputButton;
    }
    public static String generateString(String str){
        String returnThis;
        switch (str){
            default:returnThis=str;
        }
        return returnThis;
    }
}
