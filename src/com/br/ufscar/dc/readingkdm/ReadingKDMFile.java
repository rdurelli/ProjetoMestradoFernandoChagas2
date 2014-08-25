package com.br.ufscar.dc.readingkdm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.action.AbstractActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CallableUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.Layer;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;



public class ReadingKDMFile {	

	public Segment load(String KDMModelFullPath) {

		KdmPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("fer", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();
		// Get the resource
		Resource resource = resSet.getResource(URI.createURI(KDMModelFullPath),
				true);

		// Get the first model element and cast it to the right type, in my
		// example everything is hierarchical included in this first node
		System.out.println(resource.getContents().get(0).toString());

		System.out.println("O Contents Ž " + resource.getContents());

		System.out.println(((Segment) resource.getContents().get(0)).getModel().size());
		
		return (Segment) resource.getContents().get(0);
	}
	
	
	
	public void mappingPackageToLayer (ArrayList<Package> allPackages, Segment segment, String kdmPath) {
		
		StructureModel structureModel =  StructureFactory.eINSTANCE.createStructureModel();//create a StructureModel
		
		segment.getModel().add(structureModel);// add the StructureMOdel...
		
		Layer layer = null;
		
		for (Package package1 : allPackages) {
		
			layer = StructureFactory.eINSTANCE.createLayer();
			
			layer.setName(package1.getName());
			layer.getImplementation().add(package1);
			structureModel.getStructureElement().add(layer);
			
		}
		
		save(segment, kdmPath);
		
		
	}
	
	
//	this method is used to save the KDMModel 
	public void save(Segment model, String KDMPath)  {


		KdmPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("website", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();

		
		Resource resource = resSet.createResource(URI.createURI(KDMPath));

		resource.getContents().add(model);

		try {

			resource.save(Collections.EMPTY_MAP);

		} catch (IOException e) {

		}

	}
	
	public ArrayList<Package> getAllPackages(Segment segment) {
		
		ArrayList<Package> allPackages = new ArrayList<Package>();
		
		CodeModel codeModel = (CodeModel) segment.getModel().get(0);

		EList<AbstractCodeElement> elements = codeModel.getCodeElement();

		for (int i = 0; i < elements.size() - 1; i++) {

			System.out.println("aqui "+ elements.get(i));

			if (elements.get(i) instanceof Package) {

				Package packageKDM = (Package) elements.get(i);
											
				allPackages = (ArrayList<Package>) this.getAllPackages(packageKDM, allPackages);													

			}

		}
		
		return allPackages;
	}
	
	private List<Package> getAllPackages(Package packageToGet, List<Package> packages) {
				
				
		EList<AbstractCodeElement> elements = packageToGet.getCodeElement();

		for (AbstractCodeElement abstractCodeElement : elements) {
			if (abstractCodeElement instanceof Package) 
				packages = getAllPackages( (Package) abstractCodeElement, packages);
			else 
			{
				packages.add(packageToGet);
				return packages;
			}
				
		}									
		return packages;
	}

	public ArrayList<ClassUnit> getAllClasses(Segment segment) {

		ArrayList<ClassUnit> allClasses = new ArrayList<ClassUnit>();

		CodeModel codeModel = (CodeModel) segment.getModel().get(0);

		EList<AbstractCodeElement> elements = codeModel.getCodeElement();

		for (int i = 0; i < elements.size() - 1; i++) {

			System.out.println(elements.get(i));

			if (elements.get(i) instanceof Package) {

				Package packageKDM = (Package) elements.get(i);

				this.getClasses(packageKDM.getCodeElement(), allClasses);

			}

		}

		return allClasses;

	}

	private void getClasses(EList<AbstractCodeElement> elements,
			ArrayList<ClassUnit> allClasses) {

		for (AbstractCodeElement abstractCodeElement : elements) {

			if (abstractCodeElement instanceof ClassUnit) {

				allClasses.add((ClassUnit) abstractCodeElement);

			} else if (abstractCodeElement instanceof Package) {

				Package packageToPass = (Package) abstractCodeElement;

				getClasses(packageToPass.getCodeElement(), allClasses);

			}

		}

	}

	public ArrayList<MethodUnit> getMethods(ClassUnit classUnit) {

		EList<CodeItem> allElementsOfTheClass = classUnit.getCodeElement();

		ArrayList<MethodUnit> methodUnit = new ArrayList<MethodUnit>();

		for (CodeItem codeItem : allElementsOfTheClass) {

			if (codeItem instanceof MethodUnit) {

				MethodUnit methodUnitToPutIntoTheList = (MethodUnit) codeItem;

				methodUnit.add(methodUnitToPutIntoTheList);

			}

		}

		return methodUnit;

	}
	
	public BlockUnit getBlockUnit(MethodUnit methodUnit) {

		EList<AbstractCodeElement> allElementsOfTheMethod = methodUnit.getCodeElement();

		
		if ( (allElementsOfTheMethod.size() == 2 ) && ( allElementsOfTheMethod.get(1) instanceof BlockUnit) )
			return (BlockUnit) allElementsOfTheMethod.get(1);
		else return null;

	}		
	
	public List<Calls> getRelations(BlockUnit blockUnit) {
		
		ArrayList<Calls> relations = new ArrayList<Calls>();			

		EList<AbstractCodeElement> allElementsOfTheMethod = blockUnit.getCodeElement();			

		for (AbstractCodeElement codeItem : allElementsOfTheMethod) {

			if (codeItem instanceof ActionElement) {

				relations = getActionsRelationships((ActionElement)codeItem, relations);
			}

		}

		return relations;

	}
	
	private ArrayList<Calls> getActionsRelationships(ActionElement actionElement, ArrayList<Calls> relations) {

		EList<AbstractCodeElement> allElements = actionElement.getCodeElement();			

		for (AbstractCodeElement codeItem : allElements) {

			if (codeItem instanceof ActionElement) {

				relations = getActionsRelationships((ActionElement) codeItem, relations);
				
				if (((ActionElement)codeItem).getActionRelation() != null) {
					
					ActionElement element = ((ActionElement)codeItem);
					
					EList<AbstractActionRelationship> allRelationhips = element.getActionRelation();
					
					for (AbstractActionRelationship abstractActionRelationship : allRelationhips) {
											
						if (abstractActionRelationship instanceof Calls)
							relations.add((Calls)abstractActionRelationship);
						
					}
								
			}
			}
		}

		return relations;

	}
	
	
	
	public ArrayList<Layer> getAllLayers (Segment segment) {
		
		ArrayList<Layer> allLayers = new ArrayList<Layer>();
		StructureModel structureModel = null;
		EList<KDMModel> models = segment.getModel();
		
		for (KDMModel kdmModel : models) {
			
			if (kdmModel instanceof StructureModel) {
				
				structureModel = (StructureModel) kdmModel;
				EList<AbstractStructureElement> allStructureElement = structureModel.getStructureElement();
				
				for (AbstractStructureElement abstractStructureElement : allStructureElement) {
					if (abstractStructureElement instanceof Layer) {
						
						allLayers.add((Layer)abstractStructureElement);
						
						
					}
				}
				
			}
		}
		return allLayers;
		
	}
	
	public void createAggreatedRelationShips (ArrayList<Layer> layers, Segment segment, StructureModel structureModel, ArrayList<Calls> allCalls) {
		
		for (Calls calls : allCalls) {
			
			Package[] packageToAndFrom = topOfTree(calls);
			
			
			for (Layer layers1 : layers) {
				
//				EList<KDMEntity> layers1.getImplementation();
				
			}
			
		}
		
	}
	
	public Boolean mappingLayerToPackage (Layer layer, Package packageToVerify) {
		
		String pathToVerifyPackage = getPathOfPackage(packageToVerify, ""); 
		
		EList<KDMEntity> allImplementation = layer.getImplementation();
		
		for (KDMEntity kdmEntity : allImplementation) {
			
			String pathToVerifyLayer = "";
			
			if (kdmEntity instanceof Package) {
				
				pathToVerifyLayer = getPathOfPackage((Package)kdmEntity, pathToVerifyLayer);
				
			}
			
			if (pathToVerifyLayer.equals(pathToVerifyPackage)) {
				
				return true;
				
			}else
				return false;
			
		}
		
		return null;
	}
	
	
	public String getPathOfPackage (Package packageToGetThePath, String pathToGet) {
		
	
		if (packageToGetThePath instanceof Package) {
			if (packageToGetThePath.eContainer() instanceof CodeModel) {
				pathToGet += packageToGetThePath.getName();
			}
			else {
			
				pathToGet += packageToGetThePath.getName()+".";
				
			}
			pathToGet  = getPathOfPackage(packageToGetThePath, pathToGet);
			
		}
		return pathToGet;
		
	}
	
	public Package[] topOfTree (Calls callToMap) {
		
		Package[] packageToAndFrom = new Package[2];
		Package to = null;
		Package from = null;
		
		to = getToOrFrom(callToMap.getTo(), to);
		from = getToOrFrom(callToMap.getFrom(),from);
		packageToAndFrom[0] = to;
		packageToAndFrom[1] = from;
		
		return packageToAndFrom;
		
	}
	
	private Package getToOrFrom (EObject element, Package toOrFrom) {
		
		if (element instanceof Package) {
			
			return (Package) element;
		} else
		{
			toOrFrom = getToOrFrom(element.eContainer(),toOrFrom);
			
		}
		
		return toOrFrom;
		
	}
	
	
	
	
	public List<ActionRelationship> getRelationships(ActionElement actionElement) {

		EList<AbstractActionRelationship> allElementsOfTheMethod = actionElement.getActionRelation();			

		List<ActionRelationship> actionRelationships = new ArrayList<ActionRelationship>();

		for (AbstractActionRelationship codeItem : allElementsOfTheMethod) {

			if (codeItem instanceof ActionRelationship) {

				ActionRelationship actionRelationshipToPutIntoTheList = (ActionRelationship) codeItem;								

				actionRelationships.add(actionRelationshipToPutIntoTheList);

			}

		}

		return actionRelationships;

	}

	public List<MethodUnit> getMethods(InterfaceUnit interfaceUnit) {

		EList<CodeItem> allElementsOfTheClass = interfaceUnit.getCodeElement();

		List<MethodUnit> methodUnit = new ArrayList<MethodUnit>();

		for (CodeItem codeItem : allElementsOfTheClass) {

			if (codeItem instanceof MethodUnit) {

				MethodUnit methodUnitToPutIntoTheList = (MethodUnit) codeItem;

				methodUnit.add(methodUnitToPutIntoTheList);

			}

		}

		return methodUnit;

	}

	public List<CallableUnit> getCallableUnits(ClassUnit classUnit) {

		EList<CodeItem> allElementsOfTheClass = classUnit.getCodeElement();

		List<CallableUnit> callableUnits = new ArrayList<CallableUnit>();

		for (CodeItem codeItem : allElementsOfTheClass) {

			if (codeItem instanceof CallableUnit) {

				CallableUnit callableUnitToPutIntoTheList = (CallableUnit) codeItem;

				callableUnits.add(callableUnitToPutIntoTheList);

			}

		}

		return callableUnits;
	}

}
