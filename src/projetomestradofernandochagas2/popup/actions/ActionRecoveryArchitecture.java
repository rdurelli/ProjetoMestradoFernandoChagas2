package projetomestradofernandochagas2.popup.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.ParameterUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.br.ufscar.dc.readingkdm.ReadingKDMFile;
import com.br.ufscar.dc.ui.MappingArchitectureElements;


public class ActionRecoveryArchitecture implements IObjectActionDelegate {

	private Shell shell;

	private IFile file;
	
	public static Segment plannedSegment;
	public static String kdmProjectPath = "";
	
	/**
	 * Constructor for ActionRecoveryArchitecture.
	 */
	public ActionRecoveryArchitecture() {
		super();
	}

	private void executeArchitetureMapping (String kdmFilePath, Segment segment) {								
		
		ArrayList<HasValue> auxHasValue = new ArrayList<HasValue>();
		
		ArrayList<StorableUnit> allStorableUnits = new ArrayList<StorableUnit>();
		
		ArrayList<ParameterUnit> allParameterUnits = new ArrayList<ParameterUnit>();			
		
		kdmFilePath = this.file.getLocationURI().toString();
		
		kdmProjectPath = this.file.getProject().getLocation().toString();
		
		ReadingKDMFile readingKDM = new ReadingKDMFile();			
		
		/*
		
		segment = readingKDM.load(kdmFilePath);
		
		readingKDM.setSegmentMain(segment);
		
		readingKDM.setAllClassUnits(readingKDM.getAllClasses(segment));
		
		readingKDM.setAllPackages(readingKDM.getAllPackages(segment));
		
		readingKDM.mappingPackageToLayer(readingKDM.getAllPackages(), segment, kdmFilePath);
		
		for (ClassUnit classUnit1: readingKDM.getAllClasses(segment)) {			
			ArrayList<MethodUnit> methodUnits = readingKDM.getMethods(classUnit1);
			readingKDM.getAllMethodUnits().addAll(methodUnits);
			
			//busca todos os StorableUnits de uma ClassUnit
			allStorableUnits.addAll(readingKDM.fetchAllStorableUnitFromClassUnit(classUnit1));
		}
		
		System.out.println("SU size: " + allStorableUnits.size());		
		
		for (MethodUnit auxMethodUnit: readingKDM.getAllMethodUnits()) {
			if (readingKDM.getBlockUnit(auxMethodUnit) != null)
				readingKDM.getAllBlockUnits().add(readingKDM.getBlockUnit(auxMethodUnit));		
			
			//busca todos os ParameterUnit de cada Metodo
			allParameterUnits.addAll(readingKDM.fetchAllParameterUnits(auxMethodUnit));
			
			if (readingKDM.fetchAnnotation(auxMethodUnit) != null) {
				auxHasValue.add(readingKDM.fetchAnnotation(auxMethodUnit));
				//verificar se a Annotation possui em seu campo TO algum derivado do PACOTE lang
//				readingKDM.getAllHasValues().add(readingKDM.getRelationShipBetweenAnnotation(readingKDM.getAllHasValues().get(readingKDM.getAllHasValues().size() - 1))); 
			}
		}	
		
		
			
		
		
		System.err.println("hasValue size: " + readingKDM.getAllHasValues().size());
		
		readingKDM.addHasTypeToSignature(allParameterUnits);
		
		for (ParameterUnit parameterUnit : allParameterUnits) {
			
			readingKDM.getAllHasType().addAll(readingKDM.fetchAllHasTypeFromParameterUnits(parameterUnit));			
		}
		
		System.out.println(" O Size e esse:" + readingKDM.getAllHasType().size());
		
		
		System.err.println("Size BU: " + readingKDM.getAllBlockUnits().size());
		
		for (BlockUnit blockUnit : readingKDM.getAllBlockUnits()) {
			readingKDM.getAllAbstractActionRelationships().addAll(readingKDM.getRelations(blockUnit));
			//busca os StorableUnits dentro dos blockUnits
			allStorableUnits.addAll(readingKDM.fetchStorableUnitsFromBlockUnit(blockUnit));
		}
		
		System.out.println("SU size2: " + allStorableUnits.size());
		//adiciona todos os hasType necessários para os StorablesUnits
		readingKDM.addHasTypeToStorableUnit(allStorableUnits);
		
		for (StorableUnit storableUnit : allStorableUnits) {
			
			//busca todos os HasTypes de storableUnits
			readingKDM.getAllHasType().addAll(readingKDM.fetchAllHasTypeFromStorableUnits(storableUnit));
			
			if (readingKDM.fetchAnnotation(storableUnit) != null) {
				
				auxHasValue.add(readingKDM.fetchAnnotation(storableUnit));
				
			}
			
			
			
		}
		
		for (int i = 0; i < auxHasValue.size(); i++) {		

			
			System.out.println("Antes de remover " + auxHasValue.size());
			
			HasValue aux = null;
			
			
			
			aux = readingKDM.getRelationShipBetweenAnnotation(auxHasValue.get(i));
			
			if (aux == null) {
				
				auxHasValue.remove(auxHasValue.get(i--));
				
				System.out.println("Depois de remover " + auxHasValue.size());
				
				
				System.out.println("AQUI ainda é null");
				
			} else {
			
				readingKDM.getAllHasValues().add(aux);
				
			} 

			
		}
		
		System.err.println("Size HT: " + readingKDM.getAllHasType().size());
		
		
		readingKDM.setAllLayers(readingKDM.getAllLayers(segment));
		
		for (ClassUnit class1 : readingKDM.getAllClassUnits()) {
			readingKDM.getAllRelationships().addAll(readingKDM.addImportsImplementsAndExtends(class1, readingKDM.getAllLayers()));
		}
		
		
		readingKDM.createAggreatedRelationShips(readingKDM.getAllLayers(), readingKDM.getAllHasValues());
		
		readingKDM.createAggreatedRelationShips(readingKDM.getAllLayers(), readingKDM.getAllAbstractActionRelationships());
		
		readingKDM.createAggreatedRelationShips(readingKDM.getAllLayers(), readingKDM.getAllHasType());
		
		readingKDM.createAggreatedRelationShips(readingKDM.getAllLayers(), readingKDM.getAllRelationships());		
		
		
		
		System.out.println("ProjectPath: " + kdmProjectPath);
		
		
		readingKDM.save(segment, "file:"+kdmProjectPath+"/Examples/newKDM.xmi");
		*/
		
		readingKDM.compareRelations("file:"+kdmProjectPath+"/Examples/newKDM.xmi", "file:"+kdmProjectPath+"/Examples/planned.xmi");
		
		readingKDM.save(readingKDM.getTargetArchitecture(), "file:"+kdmProjectPath+"/Examples/TARGET_KDM.xmi");
		
		MappingArchitectureElements mappingArchitectureElements = new MappingArchitectureElements();
		mappingArchitectureElements.open();
		
		ActionRecoveryArchitecture.plannedSegment = readingKDM.load("file:"+kdmProjectPath+"/Examples/planned.xmi");
		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.br.ufscar.dc.ui.DCLView");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
