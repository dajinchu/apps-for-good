package train.chu.chu.model;

import java.io.Serializable;

/**
 * Created by Da-Jin on 6/15/2016.
 */
public class InsertionPoint extends BaseNode implements Serializable{

    private int index;
    protected InsertionPoint(ExpressionNode expression) {
        super("", expression);
    }

    @Override
    public void move(BaseNode to, Side side) {
        if(to==this)return;
        expression.getChildren().remove(this);
        int toIndex = to.getExpression().getChildren().indexOf(to)+side.getOffset();
        index=toIndex;
        to.getExpression().getChildren().add(toIndex,this);
        this.expression = to.getExpression();
        Model.INSTANCE.update();
    }

    //Do nothing
    @Override
    protected void setSelected(boolean selected) {

    }
    @Override
    public void remove() {

    }

    public int getIndex(){
        return index;
    }
}
