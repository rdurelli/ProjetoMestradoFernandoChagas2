package com.br.ufscar.dc.ui;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.br.ufscar.dc.readingkdm.ReadingKDMFile;

import projetomestradofernandochagas2.popup.actions.ActionRecoveryArchitecture;

public class MappingArchitectureElements {

	protected Shell shlMappingArchitectureElements;
	
	private ReadingKDMFile readingKDMFile = new ReadingKDMFile();
	
	private Segment TOBE;
	
	private Segment ASIS;

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
		this.TOBE = this.readingKDMFile.load("file:C:/Users/Fernando/Documents/workspace/ProjetoMestradoFernandoChagas2/src/planned.xmi");
		this.ASIS = this.readingKDMFile.load("file:C:/Users/Fernando/Documents/workspace/ProjetoMestradoFernandoChagas2/src/newKDM.xmi");
		
		StructureModel structureModelTOBE = readingKDMFile.getStructureModelPassingSegment(this.TOBE);
		EList<AbstractStructureElement> structureElementsTOBE = structureModelTOBE.getStructureElement();
		
		ArrayList<Package> allPackagesTOBE = readingKDMFile.getAllPackages(this.TOBE);
		ArrayList<Package> allPackagesASIS = readingKDMFile.getAllPackages(this.ASIS);
		
		
		shlMappingArchitectureElements = new Shell();
		shlMappingArchitectureElements.setSize(657, 456);
		shlMappingArchitectureElements.setText("Mapping Architecture Elements");
		
		Label lblArchitecturalElements = new Label(shlMappingArchitectureElements, SWT.NONE);
		lblArchitecturalElements.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		lblArchitecturalElements.setBounds(10, 10, 199, 21);
		lblArchitecturalElements.setText("Architectural Elements");
		
		List list = new List(shlMappingArchitectureElements, SWT.BORDER);
		list.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		for (AbstractStructureElement element : structureElementsTOBE) {
			list.add(element.getName());
		}		
		
		//list.setItems(new String[] {"layer - model", "layer - controller", "layer - view"});
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
		
		Image packageImage = new Image(Display.getDefault(), MappingArchitectureElements.class.getResourceAsStream("package_obj.gif"));
		Image classImage = new Image(Display.getDefault(), MappingArchitectureElements.class.getResourceAsStream("class_obj.gif"));
		
		String pathToGet = null;
		TreeItem treeItem0 = null;
		TreeItem treeItem11 = null;
		for (Package package1 : allPackagesASIS) {
			pathToGet = "";
			pathToGet =  readingKDMFile.getPathOfPackage(package1, pathToGet);
			treeItem0 = new TreeItem(tree, 0);
			treeItem0.setImage(packageImage);
			treeItem0.setText(pathToGet);
			
			EList<AbstractCodeElement> allClassesAndInterfaces = readingKDMFile.getClassesAndInterfacesByPackage(package1);
			for (AbstractCodeElement classOrInterface : allClassesAndInterfaces) {
				treeItem11 = new TreeItem(treeItem0, 0);
				treeItem11.setImage(classImage);
				treeItem11.setText(classOrInterface.getName() + ".java");
			}
		}
		
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
