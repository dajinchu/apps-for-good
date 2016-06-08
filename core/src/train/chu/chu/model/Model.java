package train.chu.chu.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Da-Jin on 6/5/2016.
 * The model operates on the data and exposes it to the view
 * It is also responsible for saving and restoring state
 */
public class Model {

    private final ModelListener listener;
    private Array<ExpressionNode> expressions;

    private Array<Node> selected = new Array<>();
    private ExpressionNode selectedExpression;
    private int leftest, rightest;

    public Model(ModelListener listener){
        expressions = new Array<>();
        this.listener = listener;
        ExpressionNode expressionNode = new ExpressionNode(0, 0, listener);
        new Node("142",expressionNode,listener);
        new Node("*",expressionNode,listener);
        new Node("5",expressionNode,listener);
        expressions.add(expressionNode);
    }

    public Array<ExpressionNode> getExpressions(){
        return expressions;
    }

    public void addBlock(String data, ExpressionNode target){
        new Node(data,target,listener);
        listener.update();
    }

    public void selectBlocks(Array<Node> selections){
        //Reset selection first
        for(Node node : selected){
            node.setSelected(false);
        }
        selected.clear();
        if (selections.size > 0) {
            //Make sure they are from the same expression
            //Also get the left and right most nodes
            selectedExpression = selections.first().getExpression();
            leftest = selectedExpression.getChildren().size - 1;
            rightest = 0;
            for (Node node : selections) {
                if (node.getExpression() != selectedExpression) {
                    //Node has different parent! Abort!
                    return;
                }
                //Check node expands the selection range by being more right or left
                int index = selectedExpression.getChildren().indexOf(node, true);
                if (index < leftest) {
                    leftest = index;
                }
                if (index > rightest) {
                    rightest = index;
                }
            }
            //Add everything in selection range to the selected array
            for (int i = leftest; i <= rightest; i++) {
                Node node = selectedExpression.getChildren().get(i);
                node.setSelected(true);
                selected.add(node);
            }
        }
        listener.update();
        Gdx.app.log("Model","selectBlocks");
    }
    public void deselect(){
        selectBlocks(new Array<Node>());
    }
    public void removeSelected(){

    }

    public void moveSelected(Node to, Side side) {

    }
    public void parenthesizeSelected(){

    }
    public void addExpression(int x, int y) {

    }
    public void removeExpression(ExpressionNode remove){

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
