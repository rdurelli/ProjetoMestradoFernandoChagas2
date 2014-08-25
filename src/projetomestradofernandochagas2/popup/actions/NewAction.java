package projetomestradofernandochagas2.popup.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.Layer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.br.ufscar.dc.readingkdm.ReadingKDMFile;

//import com.br.actions.IActionDelegate;

public class NewAction implements IObjectActionDelegate {

	private Shell shell;

	private IFile file;
	
	/**
	 * Constructor for Action1.
	 */
	public NewAction() {
		super();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		String kdmFilePath = this.file.getLocationURI().toString();
	
		ReadingKDMFile readingKDM = new ReadingKDMFile();				
		
		Segment segment = readingKDM.load(kdmFilePath);
		
		readingKDM.setSegmentMain(segment);
		
		ArrayList<ClassUnit> allClasses = readingKDM.getAllClasses(segment);
		
		for (ClassUnit classUnit : allClasses) {
			System.out.println("The class is " + classUnit.getName());
		}
		
		ArrayList<org.eclipse.gmt.modisco.omg.kdm.code.Package> allPackages = null;
		
		allPackages = readingKDM.getAllPackages(segment);
		
		for (org.eclipse.gmt.modisco.omg.kdm.code.Package allPackage : allPackages) {
			System.out.println("Package name: " + allPackage.getName());
		}
		
		
		readingKDM.mappingPackageToLayer(allPackages, segment, kdmFilePath);
		
		ArrayList<MethodUnit> allMethods = new ArrayList<MethodUnit>(); 
		
		for (ClassUnit classUnit1: allClasses) {			
			ArrayList<MethodUnit> methodUnits = readingKDM.getMethods(classUnit1);
			allMethods.addAll(methodUnits);
			//for (MethodUnit methodUnit : methodUnits) {
			//	System.out.println(methodUnit.getName());
			//}
		}
		
		//Get All BlockUnits
		
		ArrayList<BlockUnit> allBlockUnits = new ArrayList<BlockUnit>(); 
		
		for (MethodUnit methodUnit1: allMethods) {
			if (readingKDM.getBlockUnit(methodUnit1) != null)
				allBlockUnits.add(readingKDM.getBlockUnit(methodUnit1));		
		}
		
		System.out.println(allBlockUnits.size());
		
		for (BlockUnit blockUnit : allBlockUnits) {
			System.out.println(((MethodUnit)(blockUnit.eContainer())).getName());
		}
		
		//Get All Actions
		
		ArrayList<Calls> allRelationsShip = new ArrayList<Calls>();
		
		for (BlockUnit blockUnit : allBlockUnits) {
			allRelationsShip.addAll(readingKDM.getRelations(blockUnit));
		}

		for (Calls calls : allRelationsShip) {
			System.out.println("To " + calls.getTo().getName());
			System.out.println("From " + calls.getFrom().getName()+"\n");
		}
		
		System.out.println(allRelationsShip.size());
		
		
		for (Calls calls : allRelationsShip) {
		
			Package[] allPackagesOfCall = readingKDM.topOfTree(calls);
			
			System.out.println("To " + calls.getTo().getName());
			System.out.println("Its package is "+ allPackagesOfCall[0].getName());
			System.out.println("From " + calls.getFrom().getName()+"\n");
			System.out.println("Its package is "+ allPackagesOfCall[1].getName());
		}
		
		ArrayList<Layer> allLayers = readingKDM.getAllLayers(segment);
		
		for (Layer layer : allLayers) {			
			
			System.out.println(layer.getName());
		}
		
		readingKDM.createAggreatedRelationShips(allLayers, allRelationsShip);
		
		readingKDM.save(segment, kdmFilePath);
		
		MessageDialog.openInformation(
			shell,
			"ProjetoMestradoFernandoChagas2",
			"New Action was executed.");
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

		if (selection instanceof IStructuredSelection) {
			action.setEnabled(updateSelection((IStructuredSelection) selection));
		} else {
			action.setEnabled(false);
		}

	}

	public boolean updateSelection(IStructuredSelection selection) {
		for (Iterator<?> objects = selection.iterator(); objects.hasNext();) {
			Object object = AdapterFactoryEditingDomain.unwrap(objects.next());
			if (object instanceof IFile) {
				this.file = (IFile) object;
				return true;
			}
		}
		return false;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();

	}

}
