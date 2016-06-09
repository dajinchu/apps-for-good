package train.chu.chu.model;

/**
 * Created by Da-Jin on 6/5/2016.
 * Previously referred to vaguely as Block
 * Node models one element of the sandbox
 * eg. operators, operands, fractions
 */
public class BaseNode implements Node {
    private String data;
    ExpressionNode expression;
    ModelListener listener;
    boolean selected = false;

    protected BaseNode(String data, ExpressionNode expression, ModelListener listener) {
        this(data, expression, expression.getChildren().size, listener);
    }
    protected BaseNode(String data, ExpressionNode expression, int index, ModelListener listener) {
        this.data = data;
        this.expression = expression;
        this.listener = listener;
        expression.getChildren().insert(index, this);
    }
    protected void setSelected(boolean selected){
        this.selected = selected;
    }

    @Override
    public void move(BaseNode to, Side side){
        if(to==this)return;
        this.remove();
        int toIndex = to.getExpression().getChildren().indexOf(to, true)+side.getOffset();
        to.getExpression().getChildren().insert(toIndex,this);
        this.expression = to.getExpression();
        listener.update();
    }

    @Override
    public void remove(){
        expression.getChildren().removeValue(this, true);
        listener.update();
    }

    public boolean isSelected(){
        return selected;
    }

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    public String getData() {
        return data;
    }
}
