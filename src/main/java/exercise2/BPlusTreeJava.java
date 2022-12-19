package exercise2;

import de.hpi.dbs2.ChosenImplementation;
import de.hpi.dbs2.exercise2.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * This is the B+-Tree implementation you will work on.
 * Your task is to implement the insert-operation.
 *
 */
@ChosenImplementation(true)
public class BPlusTreeJava extends AbstractBPlusTree {
    public BPlusTreeJava(int order) {
        super(order);
    }

    public BPlusTreeJava(BPlusTreeNode<?> rootNode) {
        super(rootNode);
    }

    @Nullable
    @Override
    public ValueReference insert(@NotNull Integer key, @NotNull ValueReference value) {

        BPlusTreeNode<?> currentNode = this.rootNode;
        Stack stack = new Stack();

        // Root
        if(this.rootNode.isEmpty()){
            currentNode = this.helper0((LeafNode) currentNode, key, value);
            return null;
        }

        // Find LeafNode and build Stack
        BPlusTreeNode<?> comparisonNode = currentNode;
        while (!(currentNode instanceof  LeafNode)) {
            int referencesCounter = 0;
            for (Integer currentNodeKey : currentNode.keys) {
                if (currentNodeKey == null) {
                    break;
                }
                if (key < currentNodeKey) {
                    stack.push(currentNode);
                    currentNode = (BPlusTreeNode<?>) currentNode.references[referencesCounter];
                    break;
                }
                comparisonNode = currentNode;
                referencesCounter++;
            }
            if (comparisonNode.equals(currentNode)) {
                stack.push(currentNode);
                currentNode = (BPlusTreeNode<?>) currentNode.references[referencesCounter];
                comparisonNode = currentNode;
            }
        }
        return this.insertInLeafNode(currentNode, stack, key, value);




        // Find LeafNode in which the key has to be inserted.
        //   It is a good idea to track the "path" to the LeafNode in a Stack or something alike.
        // Does the key already exist? Overwrite!
        //   leafNode.references[pos] = value;
        //   But remember return the old value!
        // New key - Is there still space?
        //   leafNode.keys[pos] = key;
        //   leafNode.references[pos] = value;
        //   Don't forget to update the parent keys and so on...
        // Otherwise
        //   Split the LeafNode in two!
        //   Is parent node root?
        //     update rootNode = ... // will have only one key
        //   Was node instanceof LeafNode?
        //     update parentNode.keys[?] = ...
        //   Don't forget to update the parent keys and so on...

        // Check out the exercise slides for a flow chart of this logic.
        // If you feel stuck, try to draw what you want to do and
        // check out Ex2Main for playing around with the tree by e.g. printing or debugging it.
        // Also check out all the methods on BPlusTreeNode and how they are implemented or
        // the tests in BPlusTreeNodeTests and BPlusTreeTests!


        //throw new UnsupportedOperationException("~~~ your implementation here ~~~");
        //return new ValueReference(value.getDummyValue());
    }

    public LeafNode helper0(LeafNode node, int key, ValueReference value){
        node.keys[0] = key;
        node.references[0] = value;
        return node;
    }

    public LeafNode helper(LeafNode node, ValueReference value, int referencesCounter){
        node.references[referencesCounter] = value;
        return node;
    }

    public LeafNode helper2(LeafNode node, ValueReference[] newReferences, Integer[] newKeys, int i){
        node.references[i] = newReferences[i];
        node.keys[i] = newKeys[i];
        return node;
    }

    public InnerNode helper2Inner(InnerNode node, BPlusTreeNode<?>[] newReferences, Integer[] newKeys, int i){
        node.references[i] = newReferences[i];
        if(i < node.n) {
            node.keys[i] = newKeys[i];
        }
        return node;
    }

    public LeafNode helper3(LeafNode node, LeafNode newNode) {
        node.nextSibling = newNode;
        return node;
    }

    public ValueReference insertInLeafNode(BPlusTreeNode<?> currentNode, Stack stack, Integer key, ValueReference value){
        // Insert Key
        int referencesCounter = 0;
        for (Integer currentNodeKey : currentNode.keys) {
            if (currentNodeKey == null) {
                break;
            }
            //Key already exists
            if(currentNodeKey == key){
                ValueReference oldValue = (ValueReference) currentNode.references[referencesCounter];
                currentNode = this.helper((LeafNode) currentNode, value, referencesCounter);
                return oldValue;
            }

            if(currentNodeKey > key){
                break;
            }
            referencesCounter++;
        }
        //Key does not exist, but still space
        if(!currentNode.isFull()) {
            ValueReference newReferences[] = new ValueReference[currentNode.n];
            Integer newKeys[] = new Integer[currentNode.n];
            for (int i = 0; i < currentNode.n; i++) {
                if (i < referencesCounter) {
                    newReferences[i] = (ValueReference) currentNode.references[i];
                    newKeys[i] = currentNode.keys[i];
                } else if (i == referencesCounter) {
                    newReferences[i] = value;
                    newKeys[i] = key;
                } else {
                    newReferences[i] = (ValueReference) currentNode.references[i - 1];
                    newKeys[i] = currentNode.keys[i - 1];
                }
            }
            for (int i = 0; i < currentNode.n; i++) {
                currentNode = this.helper2((LeafNode) currentNode, newReferences, newKeys, i);
            }
            return null;

            //Key does not exist, no space
        }else{
            int newN = (int) Math.ceil((double)currentNode.n/2);
            ValueReference newReferences1[] = new ValueReference[currentNode.n];
            ValueReference newReferences2[] = new ValueReference[currentNode.n];
            Integer newKeys1[] = new Integer[currentNode.n];
            Integer newKeys2[] = new Integer[currentNode.n];
            for (int i = 0; i < currentNode.n + 1; i++) {
                if (i < newN){
                    if (i < referencesCounter ) {
                        newReferences1[i] = (ValueReference) currentNode.references[i];
                        newKeys1[i] = currentNode.keys[i];
                    } else if (i == referencesCounter) {
                        newReferences1[i] = value;
                        newKeys1[i] = key;
                    } else {
                        newReferences1[i] = (ValueReference) currentNode.references[i - 1];
                        newKeys1[i] = currentNode.keys[i - 1];
                    }
                } else {
                    if (i < referencesCounter ) {
                        newReferences2[i - newN] = (ValueReference) currentNode.references[i];
                        newKeys2[i - newN] = currentNode.keys[i];
                    } else if (i == referencesCounter) {
                        newReferences2[i - newN] = value;
                        newKeys2[i - newN] = key;
                    } else {
                        newReferences2[i - newN] = (ValueReference) currentNode.references[i - 1];
                        newKeys2[i - newN] = currentNode.keys[i - 1];
                    }
                }

            }

            if(currentNode instanceof LeafNode){
                // Not root, Leaf
                LeafNode newNode = new LeafNode(currentNode.order);
                for (int i = 0; i < currentNode.n; i++) {
                    currentNode = this.helper2((LeafNode) currentNode, newReferences1, newKeys1, i);
                    newNode = this.helper2(newNode, newReferences2, newKeys2, i);
                }
                newNode = this.helper3(newNode, ((LeafNode) currentNode).nextSibling);
                currentNode = this.helper3((LeafNode) currentNode, newNode);

                if(!stack.empty()){
                    BPlusTreeNode<?> parentNode = (BPlusTreeNode<?>) stack.pop();
                    return this.insertInInnerNode(parentNode, stack, newNode.getSmallestKey(), newNode);
                } else {
                    InnerNode newRoot = new InnerNode(currentNode.order);
                    newRoot.keys[0] = newNode.getSmallestKey();
                    LeafNode convertedCurrentNode = new LeafNode(currentNode.order);
                    for(int i = 0; i < currentNode.n; i++){
                        convertedCurrentNode.keys[i] = currentNode.keys[i];
                        convertedCurrentNode.references[i] = (ValueReference) currentNode.references[i];
                        convertedCurrentNode.nextSibling = ((LeafNode) currentNode).nextSibling;
                    }
                    newRoot.references[0] = convertedCurrentNode;
                    newRoot.references[1] = newNode;
                    this.rootNode = newRoot;
                    return new ValueReference(78);
                }
            }
        }
        return new ValueReference(0);
    }

    public ValueReference insertInInnerNode(BPlusTreeNode<?> currentNode, Stack stack, Integer key, BPlusTreeNode<?> referenceNode){
        // Insert Key
        int referencesCounter = 0;
        for (Integer currentNodeKey : currentNode.keys) {
            if (currentNodeKey == null) {
                break;
            }
            if(currentNodeKey > key){
                break;
            }
            //Key does not exist, but still space

            referencesCounter++;
        }
        if(!currentNode.isFull()) {
            BPlusTreeNode<?> newReferences[] = new BPlusTreeNode<?>[currentNode.order];
            Integer newKeys[] = new Integer[currentNode.n];
            for (int i = 0; i < currentNode.n; i++) {
                if (i < referencesCounter) {
                    newReferences[i] = (BPlusTreeNode<?>) currentNode.references[i];
                    newKeys[i] = currentNode.keys[i];
                } else if (i == referencesCounter) {
                    newReferences[i] = (BPlusTreeNode<?>) currentNode.references[i];
                    newReferences[i+1] = referenceNode;
                    newKeys[i] = key;
                } else {
                    newReferences[i+1] = (BPlusTreeNode<?>) currentNode.references[i];
                    newKeys[i] = currentNode.keys[i - 1];
                }
            }
            for (int i = 0; i < currentNode.order; i++) {
                currentNode = this.helper2Inner((InnerNode) currentNode, newReferences, newKeys, i);
            }
            return new ValueReference(42);

            //Key does not exist, no space
        }else {
            int newN = (int) Math.ceil((double) currentNode.n / 2);
            BPlusTreeNode<?> newReferences1[] = new BPlusTreeNode<?>[currentNode.order];
            BPlusTreeNode<?> newReferences2[] = new BPlusTreeNode<?>[currentNode.order];
            Integer newKeys1[] = new Integer[currentNode.n];
            Integer newKeys2[] = new Integer[currentNode.n];
            newReferences1[0] = (BPlusTreeNode<?>) currentNode.references[0];
            for (int i = 0; i < currentNode.n + 1; i++) {
                if (i < newN) {
                    if (i < referencesCounter) {
                        newKeys1[i] = currentNode.keys[i];
                    } else if (i == referencesCounter) {
                        newKeys1[i] = key;
                    } else {
                        newKeys1[i] = currentNode.keys[i - 1];
                    }
                } else {
                    if (i < referencesCounter) {
                        newKeys2[i - newN] = currentNode.keys[i];
                    } else if (i == referencesCounter) {
                        newKeys2[i - newN] = key;
                    } else {
                        newKeys2[i - newN] = currentNode.keys[i - 1];
                    }
                }
            }
            int moveKey = newKeys1[newN - 1];
            newKeys1[newN - 1] = null;
            for (int i = 0; i < currentNode.n + 1; i++) {
                if (i < newN - 1) {
                    if (i < referencesCounter) {
                        newReferences1[i + 1] = (BPlusTreeNode<?>) currentNode.references[i + 1];
                    } else if (i == referencesCounter) {
                        newReferences1[i + 1] = referenceNode;
                    } else {
                        newReferences1[i + 1] = (BPlusTreeNode<?>) currentNode.references[i];
                    }
                } else {
                    if (i < referencesCounter) {
                        newReferences2[i - newN + 1] = (BPlusTreeNode<?>) currentNode.references[i + 1];
                    } else if (i == referencesCounter) {
                        newReferences2[i - newN + 1] = referenceNode;
                    } else {
                        newReferences2[i - newN + 1] = (BPlusTreeNode<?>) currentNode.references[i];
                    }
                }
            }
            // Not root, InnerNode
            InnerNode newNode = new InnerNode(currentNode.order);
            for (int i = 0; i < currentNode.order; i++) {
                currentNode = this.helper2Inner((InnerNode) currentNode, newReferences1, newKeys1, i);
                newNode = this.helper2Inner(newNode, newReferences2, newKeys2, i);
            }

            if(!stack.empty()){
                InnerNode parentNode = (InnerNode) stack.pop();
                return this.insertInInnerNode(parentNode, stack, moveKey, newNode);
            } else {
                InnerNode newRoot = new InnerNode(currentNode.order);
                newRoot.keys[0] = moveKey;
                newRoot.references[0] = currentNode;
                newRoot.references[1] = newNode;
                this.rootNode = newRoot;
                return new ValueReference(78);
            }
        }
    }
}
