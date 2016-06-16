package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * Created by Da-Jin on 3/23/2016.
 */
public class TrashCan extends Image {
    public TrashCan(){
        DragAndDrop dad = Main.dragAndDrop;
        //Trashcan Actor is really simple. It's just an Image that registers itself with drag and drop
        dad.addTarget(new DragAndDrop.Target(this){
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                //Return true to indicate that the payload can be dropped on this can.
                //Some things, like drag and drop keypad blocks should not be dropped on here, but
                // it makes no difference either way
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if(source instanceof Trashable){
                    //Make SURE the source is a block before deleting it, don't want to be deleting keypad buttons
                    ((Trashable) source).trash();
                }
            }
        });
    }
}
