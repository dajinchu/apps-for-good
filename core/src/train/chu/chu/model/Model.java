package train.chu.chu.model;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

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

    private InsertionPoint insertionPoint;

    public Model(ModelListener listener){
        expressions = new Array<>();
        this.listener = listener;
        ExpressionNode expressionNode = new ExpressionNode(0, 0, this);
        expressions.add(expressionNode);
        insertionPoint = new InsertionPoint(expressionNode, this);

    }

    public Array<ExpressionNode> getExpressions(){
        return expressions;
    }
    public Array<BaseNode> getSelection(){
        return selected;
    }
    public InsertionPoint getInsertionPoint(){
        return insertionPoint;
    }

    public BaseNode addBlock(String data, ExpressionNode target){
        BaseNode baseNode = insertBlock(data,target,insertionPoint,Side.LEFT);
        return baseNode;
    }

    public BaseNode insertBlock(String data, ExpressionNode target, BaseNode to, Side side){
        BaseNode baseNode = new BaseNode(data, target, this);
        baseNode.move(to,side);
        return baseNode;
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
        update();
    }
    public void moveSelected(BaseNode to, Side side){
        if(selected.contains(to, true))return;
        for(Node node : selected)node.remove();
        int toIndex = to.getExpression().getChildren().indexOf(to, true)+side.getOffset();
        for(int i = selected.size-1; i >= 0; i--) {
            to.getExpression().getChildren().insert(toIndex, selected.get(i));
            selected.get(i).expression = to.getExpression();
        }
        insertionPoint.move(selected.get(selected.size-1),Side.RIGHT);
    }
    public void moveSelectedInto(BlankNode into){
        moveSelected(into,Side.RIGHT);
        selected.first().moveInto(into);
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
        nselected.add(new BaseNode("(",selectedExpression,firstNodeIndex, this));
        nselected.add(new BaseNode(")",selectedExpression, firstNodeIndex+nselected.size, this));
        selectBlocks(nselected);
        update();
    }
    public BlankNode addExpression(float x, float y) {
        ExpressionNode expressionNode = new ExpressionNode(x, y, this);
        BlankNode blankNode = new BlankNode(expressionNode, this);
        expressions.add(expressionNode);
        insertionPoint.move(blankNode,Side.RIGHT);
        return blankNode;
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

    public void backspace(){
        if(getInsertionPoint().getExpression().getChildren().indexOf(insertionPoint,true)>0){
            int cursorIndex=getInsertionPoint().getExpression().getChildren().indexOf(insertionPoint,true);
            if(cursorIndex==0){
                return;
            }
            getInsertionPoint().getExpression().getChildren().removeIndex(cursorIndex-1);
            System.out.println("Cursor Index: "+ (cursorIndex-1));
            if(cursorIndex>1) {
                cursorIndex-=2;
            }else if(cursorIndex==1){
                cursorIndex-=1;
            }
            getInsertionPoint().move(getInsertionPoint().getExpression().getChildren().get(cursorIndex), Side.RIGHT);
            update();
        }

    }
    protected void update(){
        validate();
        listener.update();
    }
    private void validate(){
        Iterator<ExpressionNode> iterator = expressions.iterator();
        while(iterator.hasNext()){
            if(iterator.next().getChildren().size==0){
                iterator.remove();
            }
        }
    }

}
