package train.chu.chu;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 6/8/2016.
 */
public interface Block {
    void move(BaseNode to, Side side);
    void trash();
}
