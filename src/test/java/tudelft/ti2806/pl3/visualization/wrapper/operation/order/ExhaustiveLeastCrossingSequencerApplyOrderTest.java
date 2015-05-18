package tudelft.ti2806.pl3.visualization.wrapper.operation.order;

import static tudelft.ti2806.pl3.visualization.wrapper.operation.order.ExhaustiveLeastCrossingsSequencer.applyConfiguration;
import static tudelft.ti2806.pl3.visualization.wrapper.operation.order.ExhaustiveLeastCrossingsSequencer.applyOrderConfigurationOnBothDirections;
import static tudelft.ti2806.pl3.visualization.wrapper.operation.order.ExhaustiveLeastCrossingsSequencer.calculateBestConfig;
import static tudelft.ti2806.pl3.visualization.wrapper.operation.order.ExhaustiveLeastCrossingsSequencer.countIntersections;
import static tudelft.ti2806.pl3.visualization.wrapper.operation.order.ExhaustiveLeastCrossingsSequencer.getConnectionsList;
import static tudelft.ti2806.pl3.visualization.wrapper.operation.order.ExhaustiveLeastCrossingsSequencer.getOrder;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Assert;
import org.junit.Test;

import tudelft.ti2806.pl3.data.graph.GraphDataRepository;
import tudelft.ti2806.pl3.util.Pair;
import tudelft.ti2806.pl3.util.wrap.WrapUtil;
import tudelft.ti2806.pl3.visualization.wrapper.NodeWrapper;
import tudelft.ti2806.pl3.visualization.wrapper.SpaceWrapper;
import tudelft.ti2806.pl3.visualization.wrapper.WrappedGraphData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class ExhaustiveLeastCrossingSequencerApplyOrderTest {
	private static final int EXPECTED_APPLY_ORDER_TEST_GRAPH_OUTGOING_SIZE = 12;
	private static final int EXPECTED_APPLY_ORDER_TEST_GRAPH_INCOMING_SIZE = 8;
	private static final int EXPECTED_ALWAYS_CROSS_TEST_GRAPH_OUTGOING_SIZE = 24;
	private static final int EXPECTED_ALWAYS_CROSS_TEST_GRAPH_INCOMING_SIZE = 24;
	
	/**
	 * Parsing the applyOrderTest graph.
	 * 
	 * @return
	 * 
	 * @throws FileNotFoundException
	 *             if test file was not found
	 */
	public SpaceWrapper applyOrderTestData() throws FileNotFoundException {
		File nodesFile = new File("data/testdata/applyOrderTest.node.graph");
		File edgesFile = new File("data/testdata/applyOrderTest.edge.graph");
		GraphDataRepository gdr = GraphDataRepository.parseGraph(nodesFile,
				edgesFile);
		WrappedGraphData wgd = WrapUtil.collapseGraph(
				new WrappedGraphData(gdr), 1);
		
		// Test if test data is correct
		Assert.assertTrue(wgd.getPositionedNodes().size() == 1);
		Assert.assertTrue(wgd.getPositionedNodes().get(0) instanceof SpaceWrapper);
		SpaceWrapper applyOrderTestGraphWrapper = (SpaceWrapper) wgd
				.getPositionedNodes().get(0);
		Assert.assertTrue(applyOrderTestGraphWrapper.getNodeList().size() == 7);
//		Pair<Boolean, Long> direction = ExhaustiveLeastCrossingsSequencer
//				.getBestDirection(applyOrderTestGraphWrapper.getNodeList());
//		Assert.assertTrue(direction.getSecond() == EXPECTED_APPLY_ORDER_TEST_GRAPH_INCOMING_SIZE);
//		Assert.assertFalse(direction.getFirst());
		long outgoingDirection = ExhaustiveLeastCrossingsSequencer
				.getOptionCountFromLeftToRight(applyOrderTestGraphWrapper
						.getNodeList());
		Assert.assertTrue(outgoingDirection == EXPECTED_APPLY_ORDER_TEST_GRAPH_OUTGOING_SIZE);
		return applyOrderTestGraphWrapper;
	}
	
	/**
	 * Parsing the alwaysCrossTest graph.
	 * 
	 * @throws FileNotFoundException
	 *             if test file was not found
	 */
	public SpaceWrapper alwaysCrossTestData() throws FileNotFoundException {
		File nodesFile = new File("data/testdata/alwaysCrossTest.node.graph");
		File edgesFile = new File("data/testdata/alwaysCrossTest.edge.graph");
		GraphDataRepository gdr = GraphDataRepository.parseGraph(nodesFile,
				edgesFile);
		WrappedGraphData wgd = WrapUtil.collapseGraph(
				new WrappedGraphData(gdr), 1);
		
		// Test if test data is correct
		Assert.assertTrue(wgd.getPositionedNodes().size() == 1);
		Assert.assertTrue(wgd.getPositionedNodes().get(0) instanceof SpaceWrapper);
		SpaceWrapper alwaysCrossTestGraphWrapper = (SpaceWrapper) wgd
				.getPositionedNodes().get(0);
		Assert.assertTrue(alwaysCrossTestGraphWrapper.getNodeList().size() == 7);
		Assert.assertTrue(ExhaustiveLeastCrossingsSequencer
				.getOptionCountFromRightToLeft(alwaysCrossTestGraphWrapper
						.getNodeList()) == EXPECTED_ALWAYS_CROSS_TEST_GRAPH_INCOMING_SIZE);
		Assert.assertTrue(ExhaustiveLeastCrossingsSequencer
				.getOptionCountFromRightToLeft(alwaysCrossTestGraphWrapper
						.getNodeList()) == EXPECTED_ALWAYS_CROSS_TEST_GRAPH_OUTGOING_SIZE);
		return alwaysCrossTestGraphWrapper;
	}
	
	@Test
	public void applyOrderTest() throws FileNotFoundException {
		SpaceWrapper applyOrderTestGraphWrapper = alwaysCrossTestData();
		// applyOrder tests
		Assert.assertNotNull(ExhaustiveLeastCrossingsSequencer.applyOrderToY(
				applyOrderTestGraphWrapper, true));
		List<Pair<List<NodeWrapper>, NodeWrapper[]>> order = ExhaustiveLeastCrossingsSequencer
				.getOrder(ExhaustiveLeastCrossingsSequencer.getConnectionsList(
						applyOrderTestGraphWrapper.getNodeList(), true));
		for (int i = 0; i < EXPECTED_APPLY_ORDER_TEST_GRAPH_OUTGOING_SIZE; i++) {
			Assert.assertNotNull(ExhaustiveLeastCrossingsSequencer
					.applyConfiguration(i, order, applyOrderTestGraphWrapper,
							true));
		}
	}
	
	/**
	 * Test if the method returns false when a configuration is not possible.
	 * 
	 * @throws FileNotFoundException
	 *             if test file was not found
	 */
	@Test
	public void applyOrderTestWithWrongOrders() throws FileNotFoundException {
		SpaceWrapper alwaysCrossTestGraphWrapper = alwaysCrossTestData();
		// applyOrder tests
		Assert.assertNotNull(ExhaustiveLeastCrossingsSequencer.applyOrderToY(
				alwaysCrossTestGraphWrapper, true));
		List<Pair<List<NodeWrapper>, NodeWrapper[]>> order = getOrder(getConnectionsList(
				alwaysCrossTestGraphWrapper.getNodeList(), true));
		int invalidConfigurationCount = 0;
		for (int i = 0; i < EXPECTED_ALWAYS_CROSS_TEST_GRAPH_OUTGOING_SIZE; i++) {
			if (!applyConfiguration(i, order, alwaysCrossTestGraphWrapper, true)) {
				invalidConfigurationCount++;
			}
		}
		/*
		 * The edges 1-4, 2-4 or 1-3 and 2-3 causes conflicts when the
		 * configuration configures the order as: 1-4, 1-3 in node 1 and 2-3,
		 * 2-4 in the other or when this order is reversed. Both orders, this
		 * order and the reversed, should appear each in 1/4th of the orders.
		 * Because they can't appear on the same time, they add up to 1/2 times,
		 * which leads to 24/2 = 12 conflicts.
		 */
		Assert.assertTrue(invalidConfigurationCount == EXPECTED_ALWAYS_CROSS_TEST_GRAPH_OUTGOING_SIZE / 2);
	}
	
	@Test
	public void calculateBestConfigTest() throws FileNotFoundException {
		SpaceWrapper alwaysCrossTestGraphWrapper = alwaysCrossTestData();
		List<Pair<List<NodeWrapper>, NodeWrapper[]>> order = getOrder(getConnectionsList(
				alwaysCrossTestGraphWrapper.getNodeList(), true));
		int bestConfig = calculateBestConfig(alwaysCrossTestGraphWrapper,
				EXPECTED_ALWAYS_CROSS_TEST_GRAPH_OUTGOING_SIZE, true, order);
		applyOrderConfigurationOnBothDirections(bestConfig, true, order,
				alwaysCrossTestGraphWrapper);
//		displayGraph(alwaysCrossTestGraphWrapper);
		
		Assert.assertEquals(1,
				countIntersections(alwaysCrossTestGraphWrapper, true));
//		Assert.assertEquals(1,
//				countIntersections(alwaysCrossTestGraphWrapper, false));
	}
	
	private void displayGraph(SpaceWrapper alwaysCrossTestGraphWrapper) {
		Graph graph = new SingleGraph("");
		for (NodeWrapper node : alwaysCrossTestGraphWrapper.getNodeList()) {
			graph.addNode(node.getIdString()).setAttribute("xy",
					node.getPreviousNodesCount(), node.getY());
		}
		for (NodeWrapper node : alwaysCrossTestGraphWrapper.getNodeList()) {
			for (NodeWrapper to : node.getOutgoing()) {
				graph.addEdge(node.getIdString() + "-" + to.getIdString(),
						node.getIdString(), to.getIdString());
			}
		}
		graph.display(false);
		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void rightToLeftSearchTest() throws FileNotFoundException {
		SpaceWrapper applyOrderTestGraphWrapper = applyOrderTestData();
		// TODO rewrite test?
		// If this test fails, the test is useless
		Assert.assertNotEquals(0,
				countIntersections(applyOrderTestGraphWrapper, true));
		new ExhaustiveLeastCrossingsSequencer(100)
				.calculate(applyOrderTestGraphWrapper, null);
//		displayGraph(applyOrderTestGraphWrapper);
		Assert.assertEquals(0,
				countIntersections(applyOrderTestGraphWrapper, true));
//		Assert.assertEquals(0,
//				countIntersections(applyOrderTestGraphWrapper, false));
	}
}