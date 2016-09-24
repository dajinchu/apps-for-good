package train.chu.chu.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Da-Jin on 6/5/2016.
 * The model operates on the data and exposes it to the view
 * It is also responsible for saving and restoring state
 */
public enum Model {

    INSTANCE;

    private ModelListener listener;
    private ArrayList<ExpressionNode> expressions = new ArrayList<ExpressionNode>();

    private Array<BaseNode> selected = new Array<>();
    private ExpressionNode selectedExpression;

    private InsertionPoint insertionPoint;

    private Stack<byte[]> history = new Stack<>(); //Top of history stack is current state
    private Stack<byte[]> future = new Stack<>();
    private FileHandle handle = Gdx.files.local("savestate");

    Model(){
        if(handle.exists()){
            //Load from save file if it exists
            load();
        } else {
            ExpressionNode expressionNode = new ExpressionNode(0, 0);
            expressions.add(expressionNode);
            insertionPoint = new InsertionPoint(expressionNode);
        }
    }

    public void setListener(ModelListener listener){
        this.listener = listener;
    }

    public ArrayList<ExpressionNode> getExpressions(){
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
        BaseNode baseNode = new BaseNode(data, target);
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
            int leftest = selectedExpression.getChildren().size() - 1;
            int rightest = 0;
            for (BaseNode node : selections) {
                if (node.getExpression() != selectedExpression) {
                    //Node has different parent! Abort!
                    return;
                }
                //Check node expands the selection range by being more right or left
                int index = selectedExpression.getChildren().indexOf(node);
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
        int toIndex = to.getExpression().getChildren().indexOf(to)+side.getOffset();
        for(int i = selected.size-1; i >= 0; i--) {
            to.getExpression().getChildren().add(toIndex, selected.get(i));
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
        int firstNodeIndex = selectedExpression.getChildren().indexOf(selected.first());
        Array<BaseNode> nselected = new Array<>(selected);
        nselected.add(new BaseNode("(",selectedExpression,firstNodeIndex));
        nselected.add(new BaseNode(")",selectedExpression, firstNodeIndex+nselected.size));
        selectBlocks(nselected);
        update();
    }
    public BlankNode addExpression(float x, float y) {
        ExpressionNode expressionNode = new ExpressionNode(x, y);
        BlankNode blankNode = new BlankNode(expressionNode);
        expressions.add(expressionNode);
        insertionPoint.move(blankNode,Side.RIGHT);
        return blankNode;
    }
    public void undo(){
        //Go back in time
        if(canUndo()) {
            future.push(history.pop());
            loadFromStream(new ByteArrayInputStream(history.peek()));
            update();
        }
    }
    public void redo(){
        if(canRedo()) {
            history.push(future.pop());
            loadFromStream(new ByteArrayInputStream(history.peek()));
            update();
        }
    }
    public boolean canUndo(){
        return history.size() >= 2;
    }
    public boolean canRedo(){
        return !future.empty();
    }

    public void backspace(){
        if(getInsertionPoint().getExpression().getChildren().indexOf(insertionPoint)>0){
            int cursorIndex=getInsertionPoint().getExpression().getChildren().indexOf(insertionPoint);
            if(cursorIndex==0){
                return;
            }
            getInsertionPoint().getExpression().getChildren().remove(cursorIndex-1);
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
        if(listener != null) {
            listener.update();
        }
    }
    private void validate(){
        Iterator<ExpressionNode> iterator = expressions.iterator();
        while(iterator.hasNext()){
            ExpressionNode next = iterator.next();
            if(next.getChildren().size()==0){
                iterator.remove();
            }
            for(BaseNode node : next.getChildren()){
                if(node instanceof InsertionPoint && node != insertionPoint){
                    Gdx.app.error("ISSUES", node +" is insertion point but not the same as "+ insertionPoint);
                }
            }
        }
    }

    public void addToHistory() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveToStream(out);
        history.add(out.toByteArray());
        future.clear();
        update();
    }

    public void save(){
        saveToStream(handle.write(false));
    }

    public void load(){
        loadFromStream(handle.read());
        history.clear();
        addToHistory();
    }

    private void saveToStream(OutputStream out){
        try {
            ObjectOutputStream writer = new ObjectOutputStream(out);
            writer.writeObject(insertionPoint);
            writer.writeObject(expressions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromStream(InputStream in) {
        try {
            ObjectInputStream reader = new ObjectInputStream(in);
            insertionPoint = (InsertionPoint) reader.readObject();
            expressions = (ArrayList<ExpressionNode>) reader.readObject();
            selected.clear();
            selectedExpression = null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
