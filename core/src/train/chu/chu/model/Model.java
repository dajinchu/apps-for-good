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

    private Array<BaseNode> selected = new Array<>();
    private ExpressionNode selectedExpression;

    public Model(ModelListener listener){
        expressions = new Array<>();
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
    public Array<BaseNode> getSelection(){
        return selected;
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
        //Reset selection first
        for(BaseNode node : selected){
            node.setSelected(false);
        }
        selected.clear();
        if (selections.size > 0) {
            //Make sure they are from the same expression
            //Also get the left and right most nodes
            selectedExpression = selections.first().getExpression();
            int leftest = selectedExpression.getChildren().size - 1;
            int rightest = 0;
            for (BaseNode node : selections) {
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
                BaseNode node = selectedExpression.getChildren().get(i);
                node.setSelected(true);
                selected.add(node);
            }
        }
        listener.update();
    }
    public void moveSelected(BaseNode to, Side side){
        if(selected.contains(to, true))return;
        for(Node node : selected)node.remove();
        int toIndex = to.getExpression().getChildren().indexOf(to, true)+side.getOffset();
        for(int i = selected.size-1; i >= 0; i--) {
            to.getExpression().getChildren().insert(toIndex, selected.get(i));
            selected.get(i).expression = to.getExpression();
        }
        listener.update();
    }
    public void removeSelected(){
        for(BaseNode node : selected){
            node.remove();
        }
    }
    public void deselect(){
        selectBlocks(new Array<BaseNode>());
    }
    public void parenthesizeSelected(){
        if(selected.size==0)return;
        int firstNodeIndex = selectedExpression.getChildren().indexOf(selected.first(), true);
        Array<BaseNode> nselected = new Array<>(selected);
        nselected.add(new BaseNode("(",selectedExpression,firstNodeIndex, listener));
        nselected.add(new BaseNode(")",selectedExpression, firstNodeIndex+nselected.size, listener));
        selectBlocks(nselected);
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
