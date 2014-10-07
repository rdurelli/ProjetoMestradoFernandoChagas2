package com.br.ufscar.dc.ui;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import projetomestradofernandochagas2.popup.actions.ActionRecoveryArchitecture;

public class DCLView extends ViewPart {

	public static final String ID = "com.br.ufscar.dc.ui.DCLView"; //$NON-NLS-1$

	private Graph graph;
	
	private int layout = 1;
	
	public DCLView() {
	}
	
	

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		createActions();
		initializeToolBar();
		initializeMenu();
		
		this.draw(parent);
	}
	
	
	private void draw (Composite parent) {
		
		StructureModel structureModelToDraw  = (StructureModel) ActionRecoveryArchitecture.plannedSegment.getModel().get(3);
		
		// Graph will hold all other objects
		graph = new Graph(parent, SWT.NONE);
		// Now a few nodes
		
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		
		EList<AbstractStructureElement> allElements = structureModelToDraw.getStructureElement();
		
		ArrayList<GraphNode> allNodes = new ArrayList<GraphNode>();
		
		for (AbstractStructureElement abstractStructureElement : allElements) {
		
			GraphNode node = new GraphNode(graph, SWT.NONE, abstractStructureElement.getName());
			allNodes.add(node);
			
		}
		
		ArrayList<AggregatedRelationship> allRelations = new ArrayList<AggregatedRelationship>();
		
		for (AbstractStructureElement abstractStructureElement : allElements) {
			
			if (abstractStructureElement.getAggregated().size() > 0) {
				
				allRelations.addAll(abstractStructureElement.getAggregated());
				
			}
			
		}
		
		for (AggregatedRelationship aggregatedRelationship : allRelations) {
		
			KDMEntity from = aggregatedRelationship.getFrom();
			KDMEntity to = aggregatedRelationship.getTo();
			
			GraphNode nodeFROM = null;
			
			GraphNode nodeTO = null;
			
			for (GraphNode node : allNodes) {
				
				if (node.getText().equals(from.getName())) {
					
					nodeFROM = node;
					
				} 
				
				if (node.getText().equals(to.getName())) {
					
					nodeTO = node;
					
				}
				
			}
			
			GraphConnection graphConnection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodeFROM,
					nodeTO);
			graphConnection.setText(aggregatedRelationship.getDensity().toString());
			
		}
		
		
		
		
		
		
	}

	public void setLayoutManager() {
		switch (layout) {
		case 1:
			graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
					LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			layout++;
			break;
		case 2:
			graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(
					LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			layout = 1;
			break;

		}

	}

	
	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

}
