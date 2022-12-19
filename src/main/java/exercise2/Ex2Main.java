package exercise2;

import com.sun.jdi.Value;
import de.hpi.dbs2.exercise2.AbstractBPlusTree;
import de.hpi.dbs2.exercise2.BPlusTreeNode;
import de.hpi.dbs2.exercise2.ValueReference;

public class Ex2Main {
    public static void main(String[] args) {
        int order = 4;

        BPlusTreeNode<?> root = BPlusTreeNode.buildTree(order,
            new AbstractBPlusTree.Entry[][]{
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(2, new ValueReference(0)),
                    new AbstractBPlusTree.Entry(3, new ValueReference(1)),
                    new AbstractBPlusTree.Entry(5, new ValueReference(2))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(7, new ValueReference(3)),
                    new AbstractBPlusTree.Entry(11, new ValueReference(4))
                }
            },
            new AbstractBPlusTree.Entry[][]{
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(13, new ValueReference(5)),
                    new AbstractBPlusTree.Entry(17, new ValueReference(6)),
                    new AbstractBPlusTree.Entry(19, new ValueReference(7))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(23, new ValueReference(8)),
                    new AbstractBPlusTree.Entry(29, new ValueReference(9))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(31, new ValueReference(10)),
                    new AbstractBPlusTree.Entry(37, new ValueReference(11)),
                    new AbstractBPlusTree.Entry(41, new ValueReference(12))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(43, new ValueReference(13)),
                    new AbstractBPlusTree.Entry(47, new ValueReference(14))
                }
            }
        );
        System.out.println(root);
        AbstractBPlusTree tree = new BPlusTreeJava(root);
        tree.insert(12, new ValueReference(15));
        tree.insert(10, new ValueReference(16));
        tree.insert(18, new ValueReference(17));
        tree.insert(40, new ValueReference(18));
        tree.insert(24, new ValueReference(19));
        tree.insert(25, new ValueReference(20));
        tree.insert(38, new ValueReference(21));
        tree.insert(39, new ValueReference(22));
        tree.insert(48, new ValueReference(23));
        tree.insert(49, new ValueReference(24));
        System.out.println(tree);

        /*
         * playground
         * ~ feel free to experiment with the tree and tree nodes here
         */
    }
}
