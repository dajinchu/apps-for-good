package train.chu.chu.model;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Da-Jin on 6/8/2016.
 */
public class SelectionContainerNode implements Node {

    private Array<BaseNode> selected = new Array<>();
    private final ModelListener listener;
    private BaseNode firstNode;

    protected SelectionContainerNode(ModelListener listener){
        this.listener = listener;
    }

    public Array<BaseNode> getSelected(){
        return selected;
    }

    protected void setSelection(Array<BaseNode> selections){
        //Reset selection first
        for(BaseNode node : selected){
            node.setSelected(false);
        }
        selected.clear();
        if (selections.size > 0) {
            //Make sure they are from the same expression
            //Also get the left and right most nodes
            ExpressionNode selectedExpression = selections.first().getExpression();
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
            firstNode = selectedExpression.getChildren().get(leftest);
        }
        listener.update();
    }

    @Override
    public void move(BaseNode to, Side side) {
        if(selected.contains(to, true))return;
        for(Node node : selected)node.remove();
        int toIndex = to.getExpression().getChildren().indexOf(to, true)+side.getOffset();
        for(int i = selected.size-1; i >= 0; i--) {
            to.getExpression().getChildren().insert(toIndex, selected.get(i));
            selected.get(i).expression = to.getExpression();
        }
        listener.update();
    }

    @Override
    public void remove() {
        for(BaseNode node : selected){
            node.remove();
        }
    }

    @Override
    public ExpressionNode getExpression() {
        return selected.first().getExpression();
    }

    public BaseNode getFirstNode() {
        return firstNode;
    }

}
