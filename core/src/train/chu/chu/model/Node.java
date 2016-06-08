package train.chu.chu.model;

import com.badlogic.gdx.Gdx;

/**
 * Created by Da-Jin on 6/5/2016.
 * Previously referred to vaguely as Block
 * Node models one element of the sandbox
 * eg. operators, operands, fractions
 */
public class Node {
    private String data;
    private ExpressionNode expression;

    protected Node(String data, ExpressionNode expression) {
        this.data = data;
        this.expression = expression;
    }

    public void move(Node to, Side side){
        Gdx.app.log("Node","move");
    }

    public void remove(){
        Gdx.app.log("Node","remove");
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public String getData() {
        return data;
    }
}
