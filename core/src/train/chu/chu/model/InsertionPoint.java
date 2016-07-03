package train.chu.chu.model;

/**
 * Created by Da-Jin on 6/15/2016.
 */
public class InsertionPoint extends BaseNode{

    protected InsertionPoint(ExpressionNode expression, Model model) {
        super("", expression, model);
    }

    @Override
    public void move(BaseNode to, Side side) {
        if(to==this)return;
        expression.getChildren().removeValue(this, true);
        int toIndex = to.getExpression().getChildren().indexOf(to, true)+side.getOffset();
        to.getExpression().getChildren().insert(toIndex,this);
        this.expression = to.getExpression();
        model.update();
    }

    //Do nothing
    @Override
    protected void setSelected(boolean selected) {

    }
    @Override
    public void remove() {

    }
}
