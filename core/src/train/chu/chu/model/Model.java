package train.chu.chu.model;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Da-Jin on 6/5/2016.
 * The model operates on the data and exposes it to the view
 * It is also responsible for saving and restoring state
 */
public class Model {

    private final ModelListener listener;
    private Array<ExpressionNode> expressions;

    private SelectionContainerNode selection;

    public Model(ModelListener listener){
        expressions = new Array<>();
        selection = new SelectionContainerNode(listener);
        this.listener = listener;
        ExpressionNode expressionNode = new ExpressionNode(0, 0, listener);
        new BaseNode("142",expressionNode,listener);
        new BaseNode("*",expressionNode,listener);
        new BaseNode("5",expressionNode,listener);
        expressions.add(expressionNode);
    }

    public Array<ExpressionNode> getExpressions(){
        return expressions;
    }
    public SelectionContainerNode getSelection(){
        return selection;
    }

    public void addBlock(String data, ExpressionNode target){
        new BaseNode(data,target,listener);
        listener.update();
    }

    public void insertBlock(String data, ExpressionNode target, int index){
        new BaseNode(data,target,index,listener);
        listener.update();
    }

    public void selectBlocks(Array<BaseNode> selections){
        selection.setSelection(selections);
    }
    public void deselect(){
        selectBlocks(new Array<BaseNode>());
    }
    public void parenthesizeSelected(){
        if(selection.getSelected().size==0)return;
        int firstNodeIndex = selection.getExpression().getChildren().indexOf(selection.getFirstNode(), true);
        Array<BaseNode> selected = new Array<>(selection.getSelected());
        selected.add(new BaseNode("(",selection.getExpression(),firstNodeIndex, listener));
        selected.add(new BaseNode(")",selection.getExpression(),firstNodeIndex+selected.size, listener));
        selectBlocks(selected);
        listener.update();
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
