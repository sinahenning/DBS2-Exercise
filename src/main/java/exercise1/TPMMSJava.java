package exercise1;

import de.hpi.dbs2.ChosenImplementation;
import de.hpi.dbs2.dbms.BlockManager;
import de.hpi.dbs2.dbms.BlockOutput;
import de.hpi.dbs2.dbms.Relation;
import de.hpi.dbs2.dbms.Block;
import de.hpi.dbs2.dbms.Tuple;
import de.hpi.dbs2.dbms.utils.BlockSorter;
import de.hpi.dbs2.exercise1.SortOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ChosenImplementation(true)
public class TPMMSJava extends SortOperation {
    public TPMMSJava(@NotNull BlockManager manager, int sortColumnIndex) {
        super(manager, sortColumnIndex);
    }

    @Override
    public int estimatedIOCost(@NotNull Relation relation) {
        return relation.getEstimatedSize() * 2;
        //throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void sort(@NotNull Relation relation, @NotNull BlockOutput output) {
        var manager = this.getBlockManager();
        if (relation.getEstimatedSize() > manager.getFreeBlocks() * manager.getFreeBlocks()) {
            throw new RelationSizeExceedsCapacityException();
        }

        //Aufteilung in Blockgruppen
        List<Block> blockList = new ArrayList<>();
        List<List<Block>> partedBlockList = new ArrayList<List<Block>>();
        for(Block d: relation){
            blockList.add(d);
        }
        final int N = blockList.size();
        for (int i = 0; i < N; i += manager.getFreeBlocks()) {
            partedBlockList.add(new ArrayList<Block>(
                    blockList.subList(i, Math.min(N, i + manager.getFreeBlocks())))
            );
        }

        //Sortieren der Blockgruppen
        for(List<Block> subPartedBlockList: partedBlockList) {
            for(Block block: subPartedBlockList) {
                manager.load(block);
                for (int h = 0; h < block.getSize(); h++) {
                    System.out.println(block.get(h));
                }
            }
            System.out.println("----------------------------------------");
            BlockSorter.INSTANCE.sort(relation, subPartedBlockList, relation.getColumns().getColumnComparator(this.getSortColumnIndex()));
            for(Block block: subPartedBlockList) {
                for (int h = 0; h < block.getSize(); h++) {
                    System.out.println(block.get(h));
                }
                manager.release(block, true);
            }
            System.out.println("----------------------------------------");
        }

        //Nummerierung der Blockgruppen
        Block outputBlock = manager.allocate(true);
        List<List<Pair<Block, Integer>>> partedPairList = new ArrayList<>();
        int subPartedBlockListCounter = 0;
        for(List<Block> subPartedBlockList: partedBlockList) {
            List<Pair<Block, Integer>> subPartedPairList = new ArrayList<>();
            for (Block block : subPartedBlockList) {
                Pair<Block, Integer> pair = new Pair<>(block, subPartedBlockListCounter);
                subPartedPairList.add(pair);
            }
            partedPairList.add(subPartedPairList);
            subPartedBlockListCounter++;
        }

        //Phase 2
        List<Tuple> outputList = new ArrayList<>();
        List<List<Pair<Tuple,Integer>>> comparisonList = new ArrayList<>();
        int j = 0;

        for(List<Pair<Block, Integer>> subPartedPairList: partedPairList) {
            this.getFirst(manager, subPartedPairList, comparisonList);
        }

        boolean emptyComparisonList = true;
        do{
            var minValue = comparisonList.get(0).get(0).getFirst().get(this.getSortColumnIndex());
            Pair<Tuple, Integer> minPair = new Pair<>(comparisonList.get(0).get(0).getFirst(), comparisonList.get(0).get(0).getSecond());
            int minIndex = 0;
            for(List<Pair<Tuple, Integer>> pairList: comparisonList){
                var firstValue = pairList.get(0).getFirst().get(this.getSortColumnIndex());
                if(firstValue.equals(minValue)){
                    minValue = firstValue;
                    minIndex = comparisonList.indexOf(pairList);
                    minPair = pairList.get(0);
                }
            }
            if(outputBlock.isFull()){
                output.output(outputBlock);
                j = 0;
            }
            outputBlock.insert(j, comparisonList.get(minIndex).get(0).getFirst());
            outputList.add(comparisonList.get(minIndex).get(0).getFirst());
            comparisonList.get(minIndex).remove(0);
            if(comparisonList.get(minIndex).isEmpty()){
                comparisonList.remove(minIndex);
                for(List<Pair<Block, Integer>> subPartedPairList: partedPairList) {
                    if(subPartedPairList.get(0).getSecond().equals(minPair.getSecond())) {
                        this.getFirst(manager, subPartedPairList, comparisonList);
                    }
                    if(subPartedPairList.isEmpty()){
                        int bla = 0;
                    }
                }
            }
            j++;

            emptyComparisonList = true;
            for (List<Pair<Tuple,Integer>> list: comparisonList) {
                if (!list.isEmpty()) {
                    emptyComparisonList = false;
                }
            }

            partedPairList.removeIf(list -> list.isEmpty());
            comparisonList.removeIf(list -> list.isEmpty());

        }while (!emptyComparisonList);
        output.output(outputBlock);
        manager.release(outputBlock, false);
        //throw new UnsupportedOperationException("TODO");
    }

    public void getFirst(BlockManager manager, List<Pair<Block, Integer>> subPartedPairList, List<List<Pair<Tuple,Integer>>> comparisonList) {
        manager.load(subPartedPairList.get(0).getFirst());
        List<Pair<Tuple, Integer>> tupleList = new ArrayList<>();
        for (Tuple tuple : subPartedPairList.get(0).getFirst()) {
            tupleList.add(new Pair<>(tuple, subPartedPairList.get(0).getSecond()));
        }
        comparisonList.add(tupleList);
        manager.release(subPartedPairList.get(0).getFirst(), false);
        subPartedPairList.remove(0);
    }
}
