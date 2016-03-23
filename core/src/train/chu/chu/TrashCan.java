package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * Created by Da-Jin on 3/23/2016.
 */
public class TrashCan extends Image {
    public TrashCan(DragAndDrop dad){
        dad.addTarget(new DragAndDrop.Target(this){
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                System.out.println("dropped");
                if(source.getActor().getClass()==Block.class){
                    System.out.println("remove");
                    source.getActor().remove();
                }
            }
        });
    }
}
