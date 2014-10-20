package com.br.ufscar.dc.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class MappingArchitectureElements {

	protected Shell shlMappingArchitectureElements;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MappingArchitectureElements window = new MappingArchitectureElements();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlMappingArchitectureElements.open();
		shlMappingArchitectureElements.layout();
		while (!shlMappingArchitectureElements.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlMappingArchitectureElements = new Shell();
		shlMappingArchitectureElements.setSize(657, 456);
		shlMappingArchitectureElements.setText("Mapping Architecture Elements");
		
		Label lblArchitecturalElements = new Label(shlMappingArchitectureElements, SWT.NONE);
		lblArchitecturalElements.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		lblArchitecturalElements.setBounds(10, 10, 199, 21);
		lblArchitecturalElements.setText("Architectural Elements");
		
		List list = new List(shlMappingArchitectureElements, SWT.BORDER);
		list.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		list.setItems(new String[] {"layer - model", "layer - controller", "layer - view"});
		list.setBounds(10, 37, 294, 198);
		
		List list_1 = new List(shlMappingArchitectureElements, SWT.BORDER);
		list_1.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		list_1.setItems(new String[] {"layer model: package com.br.model", "layer controller: package com.br.controller", "layer view: package com.br.view"});
		list_1.setBounds(10, 268, 625, 123);
		
		Label lblArchitecturalElementsMapped = new Label(shlMappingArchitectureElements, SWT.NONE);
		lblArchitecturalElementsMapped.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		lblArchitecturalElementsMapped.setText("Architectural Elements Mapped\n");
		lblArchitecturalElementsMapped.setBounds(10, 241, 275, 21);
		
		Tree tree = new Tree(shlMappingArchitectureElements, SWT.BORDER);
		tree.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		tree.setBounds(329, 37, 306, 197);
		
		TreeItem treeItem0 = new TreeItem(tree, 0);
		treeItem0.setText("package - com.br.controller ");
		
		TreeItem treeItem00 = new TreeItem(treeItem0, 0);
		treeItem00.setText("class - ProductController.java ");
	
		TreeItem treeItem1 = new TreeItem(tree, 0);
		treeItem1.setText("package - com.br.model ");
		
		TreeItem treeItem11 = new TreeItem(treeItem1, 0);
		treeItem11.setText("class - Product ");
		
		TreeItem treeItem12 = new TreeItem(treeItem1, 0);
		treeItem12.setText("class - Menu ");
		
		TreeItem treeItem2 = new TreeItem(tree, 0);
		treeItem2.setText("package - com.br.view ");
		
		TreeItem treeItem21 = new TreeItem(treeItem2, 0);
		treeItem21.setText("class - MainTest ");
		
		Button btnMap = new Button(shlMappingArchitectureElements, SWT.NONE);
		btnMap.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		btnMap.setBounds(541, 234, 94, 28);
		btnMap.setText("Map");
		
		Label lblCodeElements = new Label(shlMappingArchitectureElements, SWT.NONE);
		lblCodeElements.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		lblCodeElements.setBounds(329, 10, 138, 21);
		lblCodeElements.setText("Code Elements");
		
		Button btnDelete = new Button(shlMappingArchitectureElements, SWT.NONE);
		btnDelete.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnDelete.setText("Delete");
		btnDelete.setBounds(541, 397, 94, 28);

	}
}
