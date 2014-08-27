package projetomestradofernandochagas2.popup.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.Layer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.br.ufscar.dc.readingkdm.ReadingKDMFile;

//import com.br.actions.IActionDelegate;

public class ActionRecoveryArchitecture implements IObjectActionDelegate {

	private Shell shell;

	private IFile file;
	
	/**
	 * Constructor for Action1.
	 */
	public ActionRecoveryArchitecture() {
		super();
	}

	private void executeArchitetureMapping (String kdmFilePath, Segment segment) {
		
		kdmFilePath = this.file.getLocationURI().toString();
		
		ReadingKDMFile readingKDM = new ReadingKDMFile();				
		
		segment = readingKDM.load(kdmFilePath);
		
		readingKDM.setSegmentMain(segment);
		
		readingKDM.setAllClassUnits(readingKDM.getAllClasses(segment));
		
		readingKDM.setAllPackages(readingKDM.getAllPackages(segment));
		
		readingKDM.mappingPackageToLayer(readingKDM.getAllPackages(), segment, kdmFilePath);
		
		for (ClassUnit classUnit1: readingKDM.getAllClasses(segment)) {			
			ArrayList<MethodUnit> methodUnits = readingKDM.getMethods(classUnit1);
			readingKDM.getAllMethodUnits().addAll(methodUnits);
		}
		
		for (MethodUnit methodUnit1: readingKDM.getAllMethodUnits()) {
			if (readingKDM.getBlockUnit(methodUnit1) != null)
				readingKDM.getAllBlockUnits().add(readingKDM.getBlockUnit(methodUnit1));		
		}
		
		for (BlockUnit blockUnit : readingKDM.getAllBlockUnits()) {
			readingKDM.getAllCalls().addAll(readingKDM.getRelations(blockUnit));
		}
		
		readingKDM.setAllLayers(readingKDM.getAllLayers(segment));
		
		for (ClassUnit class1 : readingKDM.getAllClassUnits()) {
			readingKDM.getAllRelationships().addAll(readingKDM.addImportsImplementsAndExtends(class1, readingKDM.getAllLayers()));
		}
		
		readingKDM.createAggreatedRelationShips(readingKDM.getAllLayers(), readingKDM.getAllCalls());
		
		
		readingKDM.createAggreatedRelationShips(readingKDM.getAllLayers(), readingKDM.getAllRelationships());
		
		readingKDM.save(segment, kdmFilePath);
		
	}
	
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		String kdmFilePath = this.file.getLocationURI().toString();
	
		ReadingKDMFile readingKDM = new ReadingKDMFile();				
		
		Segment segment = readingKDM.load(kdmFilePath);
		
		readingKDM.setSegmentMain(segment);
		
		this.executeArchitetureMapping(kdmFilePath, segment);
		
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
