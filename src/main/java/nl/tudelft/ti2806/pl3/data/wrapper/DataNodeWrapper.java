package nl.tudelft.ti2806.pl3.data.wrapper;

import nl.tudelft.ti2806.pl3.data.Genome;
import nl.tudelft.ti2806.pl3.data.graph.DataNode;
import nl.tudelft.ti2806.pl3.data.graph.Edge;
import nl.tudelft.ti2806.pl3.data.wrapper.operation.WrapperOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * NodePosition is used to store the position of nodes and the edges of nodes.
 * This responsibility is separated from {@link DataNode} so that the same node
 * can have different positions over different views without cloning the
 * expensive Node instances which contain the original data.
 *
 * @author Sam Smulders
 */
public class DataNodeWrapper extends Wrapper {
    private DataNode node;

    /**
     * Construct a list with connected and fully initialised
     * {@code NodePosition}s.
     *
     * @param nodeList
     *         the {@link List}<{@link DataNode}> of which the new
     *         {@link List}< {@link DataNodeWrapper}> is constructed from
     * @param edgeList
     *         the {@link List}<{@link Edge}> with the connections between
     *         the newly created {@link DataNodeWrapper}s
     * @return a {@link List}<{@link DataNodeWrapper}>, constructed from the
     * {@code nodeList} and {@code edgeList}
     */
    public static List<Wrapper> newNodePositionList(List<DataNode> nodeList,
            List<Edge> edgeList) {
        // Construct list
        Map<Integer, DataNodeWrapper> map = new HashMap<Integer, DataNodeWrapper>();
        for (DataNode node : nodeList) {
            map.put(node.getId(), new DataNodeWrapper(node));
        }
        // Add connections from the edge list
        for (Edge edge : edgeList) {
            DataNodeWrapper from = map.get(edge.getFromId());
            DataNodeWrapper to = map.get(edge.getToId());
            from.outgoing.add(to);
            to.incoming.add(from);
        }
        return new ArrayList<Wrapper>(map.values());
    }

    public DataNodeWrapper(DataNode node) {
        this.node = node;
    }

    public DataNode getNode() {
        return node;
    }

    @Override
    public long getBasePairCount() {
        return node.getBasePairCount();
    }

    @Override
    public String getIdString() {
        return Integer.toString(node.getId());
    }

    @Override
    public int getId() {
        return node.getId();
    }

    @Override
    public Set<Genome> getGenome() {
        return node.getCurrentGenomeSet();
    }

    @Override
    public void calculate(WrapperOperation wrapperOperation, Wrapper container) {
        wrapperOperation.calculate(this, container);
    }

    @Override
    public void collectDataNodes(Set<DataNode> set) {
        set.add(node);
    }

    @Override
    public void calculateX() {
        this.x = this.getPreviousNodesCount();
    }

    @Override
    public int getWidth() {
        return 1;
    }
}