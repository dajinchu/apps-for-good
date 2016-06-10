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
    Model model;
    boolean selected = false;

    protected BaseNode(String data, ExpressionNode expression, Model model) {
        this(data, expression, expression.getChildren().size, model);
    }
    protected BaseNode(String data, ExpressionNode expression, int index, Model model) {
        this.data = data;
        this.expression = expression;
        this.model = model;
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
        model.update();
    }

    @Override
    public void moveInto(BlankNode into) {
        this.remove();
        into.getExpression().getChildren().add(this);
        this.expression = into.getExpression();
        into.remove();
        model.update();
    }

    @Override
    public void remove(){
        expression.getChildren().removeValue(this, true);
        model.update();
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
