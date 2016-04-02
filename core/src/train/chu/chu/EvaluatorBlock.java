package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * Created by Da-Jin on 3/25/2016.
 */
public class EvaluatorBlock extends Block{
    public EvaluatorBlock(DragAndDrop dad) {
        super(dad);
        setSelectedBlock(this);
    }
}
