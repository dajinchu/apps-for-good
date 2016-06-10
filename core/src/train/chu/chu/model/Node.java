package train.chu.chu.model;

/**
 * Created by Da-Jin on 6/8/2016.
 */
public interface Node {
    void move(BaseNode to, Side side);

    void moveInto(BlankNode into);

    void remove();

    ExpressionNode getExpression();
}
