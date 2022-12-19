package exercise1

import de.hpi.dbs2.ChosenImplementation
import de.hpi.dbs2.dbms.BlockManager
import de.hpi.dbs2.dbms.Relation
import de.hpi.dbs2.exercise1.SortOperation

@ChosenImplementation(false)
class TPMMSKotlin(
    manager: BlockManager,
    sortColumnIndex: Int
) : SortOperation(manager, sortColumnIndex) {
    override fun estimatedIOCost(inputRelation: Relation): Int = TODO()

    override fun sort(
        inputRelation: Relation,
        outputRelation: Relation
    ) {
        TODO()
    }
}
