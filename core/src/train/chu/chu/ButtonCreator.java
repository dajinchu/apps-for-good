package train.chu.chu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by Arun on 4/6/2016.
 */
public class ButtonCreator {


    static Button inputButton;
    private static boolean txt;


    /**
     * Creates a Button
     * @param str takes the string for the button
     * @param skin the skin for the Skin
     * @return an ImageButton or TextButton
     */
    public static Button ButtonCreator(String str, Skin skin){

        String orig=str;
        String name= generateString(str);
        if(name.equals("img")){
            inputButton= imageButtonCreator(orig);
            return inputButton;

        }else{
            TextButton tmp = new TextButton(name, skin);
            tmp.getLabel().setFontScale(.36f);
            inputButton = tmp;
            return inputButton;
        }


    }

    /**
     * Takes the given string and outputs actual string
     * @param str the given string id
     * @return the string to be displayed
     */
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

    /**
     * Takes the string and returns the appropriate image.
     * @param str the string id
     * @return the image for the ImageButton
     */
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
