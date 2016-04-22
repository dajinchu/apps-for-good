package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by Arun on 4/6/2016.
 */
public class ButtonCreator {


    static Button inputButton;

    private static boolean txt;


    public static Button ButtonCreator(String str, Skin skin){

        String orig=str;
        String name= generateString(str);
        if(name.equals("img")){
            inputButton= imageButtonCreator(orig);
            return inputButton;

        }else{
            inputButton = new TextButton(name, skin);
            return inputButton;
        }


    }
    public static String generateString(String str){
        String returnThis;
        //All the special cases
        switch (str){
            case "sqrt":returnThis="img";
                break;
            default:returnThis=str;
        }
        return returnThis;
    }

    public static Button imageButtonCreator(String str){
        Button button;
        Drawable pic;
        //All the special cases that return an image.
        switch (str){
            case "sqrt":
                pic=new Image(new Texture("delete.png")).getDrawable();
                button = new ImageButton(pic);
                break;
            default:
                pic=new Image(new Texture("green.png")).getDrawable();
                button = new ImageButton(pic);
        }
        return button;
    }
}
