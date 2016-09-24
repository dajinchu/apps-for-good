package train.chu.chu.model;

import com.badlogic.gdx.utils.StringBuilder;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Da-Jin on 6/5/2016.
 * One level below root in expression tree
 * Calculates final result
 * Stores its position in sandbox world
 */
public class ExpressionNode implements Positioned, Serializable {

    private float x;
    private float y;
    private ArrayList<BaseNode> children;

    protected ExpressionNode(float x, float y) {
        this.x = x;
        this.y = y;
        this.children = new ArrayList<>();
    }

    public void move(float x, float y){
        this.x=x;
        this.y=y;
        Model.INSTANCE.update();
    }

    public void remove(){
        Model.INSTANCE.getExpressions().remove(this);
        Model.INSTANCE.update();

        //Insertion point should always be somewhere
        //It may be a good idea to move this to model.update, but this is the only known point where
        // insertionPoint can be trashed

        //Check if number of expressions
        if(Model.INSTANCE.getExpressions().size()> 0) {
            boolean insertionInExistence = Model.INSTANCE.getExpressions().contains(Model.INSTANCE.getInsertionPoint().getExpression());
            if (!insertionInExistence) {
                Model.INSTANCE.getInsertionPoint().move(Model.INSTANCE.getExpressions().get(0).getChildren().get(0), Side.RIGHT);
            }
        }
        //Additionally, trashing the whole expression can leave the selection on non-existent things
        Model.INSTANCE.deselect();
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public ArrayList<BaseNode> getChildren() {
        return children;
    }

    public String getResult(){
        StringBuilder sb = new StringBuilder();
        for(BaseNode child : getChildren()){
            sb.append(child.getData());
        }
        try {
            Expression expression = new ExpressionBuilder(sb.toString()).build();
            if (expression.validate().isValid()) {
                return String.valueOf(expression.evaluate());
            }
        }catch(Exception e){}
        return "false";
    }
}
