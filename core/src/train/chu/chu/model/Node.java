package train.chu.chu.model;

/**
 * Created by Da-Jin on 6/5/2016.
 * Previously referred to vaguely as Block
 * Node models one element of the sandbox
 * eg. operators, operands, fractions
 */
public class Node {
    private String data;
    private ExpressionNode expression;
    ModelListener listener;
    boolean selected = false;

    protected Node(String data, ExpressionNode expression, ModelListener listener) {
        this.data = data;
        this.expression = expression;
        this.listener = listener;
        expression.getChildren().add(this);
    }
    protected void setSelected(boolean selected){
        this.selected = selected;
    }

    public void move(Node to, Side side){
        if(to==this)return;
        this.remove();
        int toIndex = to.getExpression().getChildren().indexOf(to, true)+side.getOffset();
        to.getExpression().getChildren().insert(toIndex,this);
        this.expression = to.getExpression();
        listener.update();
    }

    public void remove(){
        expression.getChildren().removeValue(this, true);
        listener.update();
    }

    public boolean isSelected(){
        return selected;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public String getData() {
        return data;
    }
}
