package train.chu.chu.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Da-Jin on 6/5/2016.
 * The model operates on the data and exposes it to the view
 * It is also responsible for saving and restoring state
 */
public class Model {
    private Array<ExpressionNode> expressions;

    public Model(){
        expressions = new Array<>();
        ExpressionNode expressionNode = new ExpressionNode(0, 0);
        expressionNode.getChildren().add(new Node("142",expressionNode));
        expressionNode.getChildren().add(new Node("*",expressionNode));
        expressionNode.getChildren().add(new Node("5",expressionNode));
        expressions.add(expressionNode);
    }

    public Array<ExpressionNode> getExpressions(){
        return expressions;
    }

    public void addBlock(String data, ExpressionNode target){
        new Node(data,target);
    }

    public void selectBlocks(Array<Node> selections){
        Gdx.app.log("Model","selectBlocks");
    }
    public void deselect(){

    }
    public void removeSelected(){

    }
    public void moveSelected(Node to, Side side) {

    }
    public void parenthesizeSelected(){

    }
    public void addExpression(int x, int y) {

    }
    public void moveExpression(int x, int y){

    }
    public void removeExpression(ExpressionNode remove){

    }
    public String getResult(ExpressionNode from){
        return "result";
    }
    public void undo(){

    }
    public void redo(){

    }
    public boolean canUndo(){
        return false;
    }
    public boolean canRedo(){
        return false;
    }
}
