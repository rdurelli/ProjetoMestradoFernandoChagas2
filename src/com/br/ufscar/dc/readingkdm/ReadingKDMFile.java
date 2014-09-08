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
import org.eclipse.gmt.modisco.omg.kdm.action.UsesType;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.CallableUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeFactory;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.DataElement;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.HasType;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.ParameterTo;
import org.eclipse.gmt.modisco.omg.kdm.code.ParameterUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Signature;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateType;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateUnit;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.Layer;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;


/** 
* @author Fernando Chagas e Rafael Durelli
* @since 21/08/14 
*/

public class ReadingKDMFile {

	private Segment segmentMain = null;
	
	private ArrayList<ClassUnit> allClassUnits = new ArrayList<ClassUnit>();
	
	private ArrayList<MethodUnit> allMethodUnits = new ArrayList<MethodUnit>();
	
	private ArrayList<InterfaceUnit> allInterfaceUnit = new ArrayList<InterfaceUnit>();
	
	private ArrayList<StorableUnit> allStorableUnits = new ArrayList<StorableUnit>();
	
	private ArrayList<Package> allPackages = new ArrayList<Package>();
	
	private ArrayList<BlockUnit> allBlockUnits = new ArrayList<BlockUnit>();
	
	private ArrayList<Calls> allCalls = new ArrayList<Calls>();
	
	private ArrayList<Layer> allLayers = new ArrayList<Layer>();
	
	private ArrayList<KDMRelationship> allRelationships = new ArrayList<KDMRelationship>();
	
	private ArrayList<HasType> allHasType = new ArrayList<HasType>();
	
	private ArrayList<AbstractActionRelationship> allAbstractActionRelationships = new ArrayList<AbstractActionRelationship>();
	
	/** 
	 * Retorna um segmento passando como parametro o caminho completo de um arquivo KDM.
	 * 
	 * @param  KDMModelFullPath  representa o caminho da arquivo KDM
	 * @return      O Segment, que e o elemento principal para manipular uma instancia do KDM.
	 * @see         org.eclipse.gmt.modisco.omg.kdm.kdm.Segment
	 */
	public Segment load(String KDMModelFullPath) {
		
		System.err.println(KDMModelFullPath);

		KdmPackage.eINSTANCE.eClass();//get the KDMPackage instance

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("fer", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();
		// Get the resource
		Resource resource = resSet.getResource(URI.createURI(KDMModelFullPath),
				true);

		return (Segment) resource.getContents().get(0); //pega o primeiro elemento, que e o Segment
	}
	
	
	
	public ArrayList<StorableUnit> fetchAllStorableUnitFromClassUnit (ClassUnit classUnitToGetTheStorableUnits) {
		
		ArrayList<StorableUnit> allStorableUnits = new ArrayList<StorableUnit>();
		
		EList<CodeItem> allElements = classUnitToGetTheStorableUnits.getCodeElement();
		
		for (CodeItem codeItem : allElements) {
			
			if (codeItem instanceof StorableUnit) {
				
				StorableUnit storableUnitToFetch = (StorableUnit) codeItem;
				allStorableUnits.add(storableUnitToFetch);
			}
			
		}
		
		return allStorableUnits;
		
	}
	
	public ArrayList<HasType> fetchAllHasTypeFromStorableUnits (StorableUnit storableUnitToGetTheHasType) {
		
		ArrayList<HasType> auxAllHasType = new ArrayList<HasType>();
						
		EList<AbstractCodeRelationship> allRelations = storableUnitToGetTheHasType.getCodeRelation();
		
		for (AbstractCodeRelationship abstractCodeRelationship : allRelations) {
			
			if (abstractCodeRelationship instanceof HasType) {
								
				if (verifyIfRelationContaisLayer(abstractCodeRelationship, this.allLayers)) {				
					auxAllHasType.add((HasType)abstractCodeRelationship);
				}
				
			}
			
		}
		
		return auxAllHasType;
		
	}
	
	public ArrayList<HasType> fetchAllHasTypeFromParameterUnits (ParameterUnit parameterUnitToGetTheHasType) {
		
		
		System.out.println("Entrou");
		ArrayList<HasType> auxAllHasType = new ArrayList<HasType>();
						
		EList<AbstractCodeRelationship> allRelations = parameterUnitToGetTheHasType.getCodeRelation();
		
		for (AbstractCodeRelationship abstractCodeRelationship : allRelations) {
			
			if (abstractCodeRelationship instanceof HasType) {
								
				if (verifyIfRelationContaisLayer(abstractCodeRelationship, this.allLayers)) {				
					auxAllHasType.add((HasType)abstractCodeRelationship);
				}
				
			}
			
		}
		
		return auxAllHasType;
		
	}
	
	/** 
	 * Esse metodo e responsavel por adicionar um HasType como CodeRelation para cada StorableUnit
	 * 
	 * @param  allStorableUnitOfAClass representa todas as StorableUnits de uma Classe
	 */
	
	public void addHasTypeToStorableUnit (ArrayList<StorableUnit> allStorableUnitOfAClass) {
		
		for (StorableUnit storableUnit : allStorableUnitOfAClass) {
			
			HasType hasType = CodeFactory.eINSTANCE.createHasType();
			
			CodeItem auxFrom;
			
			if (storableUnit.eContainer() instanceof ActionElement) {
				// caso de um StorableUnit dentro de ActionElement, sobe na arvore ate alcancar um methodUnit
				auxFrom = (MethodUnit)storableUnit.eContainer().eContainer().eContainer();
			} else {
				// caso de um StorableUnit dentro de um ClassUnit
				auxFrom = (ClassUnit)storableUnit.eContainer();
			}
				
			hasType.setFrom(auxFrom);
			
			Datatype dataType = storableUnit.getType();
			
			System.out.println("Todos os Types "+ dataType);
			
			//Caso de 1:*
			if (dataType instanceof TemplateType) {
				
				//desce na arvore do TemplateUnit
				TemplateType auxTemplateUnit = (TemplateType) dataType;
				EList<AbstractCodeRelationship> auxAbstractCodeRelationship = auxTemplateUnit.getCodeRelation();
				ParameterTo auxParameterTo = (ParameterTo) auxAbstractCodeRelationship.get(0);

				dataType = (Datatype) auxParameterTo.getTo();							
								
			} 
			
			hasType.setTo(dataType);
			
			storableUnit.getCodeRelation().add(hasType);					

		}			
		
	}
	
	/** 
	 * Esse metodo e responsavel por adicionar um HasType como CodeRelation para cada ParameterUnit
	 * 
	 * @param  allParameterUnits representa todas os parametros (ParameterUnit) de uma assinatura de metodo (Signature)
	 */
	
	public void addHasTypeToSignature (ArrayList<ParameterUnit> allParameterUnits) {
		
		for (ParameterUnit auxParameterUnit : allParameterUnits) {
			
			HasType hasType = CodeFactory.eINSTANCE.createHasType();
			
			hasType.setFrom((Signature)auxParameterUnit.eContainer());					
			
			Datatype dataType = auxParameterUnit.getType();
			
			hasType.setTo(dataType);
			
			auxParameterUnit.getCodeRelation().add(hasType);
			
		}
				
	}
	
	
	public void teste () {
		
		
		HasType hasType = CodeFactory.eINSTANCE.createHasType();
		
		StorableUnit attribute = CodeFactory.eINSTANCE.createStorableUnit();
		
		ClassUnit classUnit = CodeFactory.eINSTANCE.createClassUnit();
		
		hasType.setFrom(attribute);
		hasType.setTo(classUnit);
		
		AggregatedRelationship aggregated = CoreFactory.eINSTANCE.createAggregatedRelationship();
		
		
		aggregated.getRelation().add(hasType);
		
		
		
	}
	

	/** 
	 * Esse metodo e responsavel por instancia o StructureModel para atribuir os elementos arquiteturais.
	 * 
	 * @param  allPackages  representa todos os pacotes que a instancia do KDM contem.
	 * @param segment, representa uma instancia do KDM
	 * @param kdmPath representa o caminho do arquivo KDM
	 */
	public void mappingPackageToLayer(ArrayList<Package> allPackages,
			Segment segment, String kdmPath) {

		StructureModel structureModel = StructureFactory.eINSTANCE
				.createStructureModel();// create a StructureModel

		segment.getModel().add(structureModel);// add the StructureModel into the Segment

		Layer layer = null;

		for (Package package1 : allPackages) {						

			layer = StructureFactory.eINSTANCE.createLayer();

			layer.setName(package1.getName());
			layer.getImplementation().add(package1);
			structureModel.getStructureElement().add(layer);
			this.allLayers.add(layer);
		}

//		save(segment, kdmPath);

	}

	/** 
	 * Esse metodo e responsavel por salvar uma instancia do KDM apos a realizacao de mudancas no mesmo.
	 * @param segment, representa uma instancia do KDM
	 * @param kdmPath representa o caminho do arquivo KDM
	 */
	public void save(Segment segment, String KDMPath) {

		KdmPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("website", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();

		Resource resource = resSet.createResource(URI.createURI(KDMPath));

		resource.getContents().add(segment);

		try {

			resource.save(Collections.EMPTY_MAP);

		} catch (IOException e) {

		}

	}

	
	/** 
	 * Esse metodo e responsavel por obter todos os pacotes da instancia do KDM
	 * @param segment, representa uma instancia do KDM
	 * @return ArrayList<Package> todos os pacotes do sistema.
	 */
	public ArrayList<Package> getAllPackages(Segment segment) {

		ArrayList<Package> allPackages = new ArrayList<Package>();

		CodeModel codeModel = (CodeModel) segment.getModel().get(0);

		EList<AbstractCodeElement> elements = codeModel.getCodeElement();

		for (int i = 0; i < elements.size() - 1; i++) {

			System.out.println("aqui " + elements.get(i));

			if (elements.get(i) instanceof Package) {

				Package packageKDM = (Package) elements.get(i);

				allPackages = (ArrayList<Package>) this.getAllPackages(
						packageKDM, allPackages);

			}

		}

		return allPackages;
	}

	/** 
	 * Esse metodo e responsavel por obter todos os pacotes da instancia do KDM
	 * @param packageToGet, o pacote para obter
	 * @param packages a lista com todos os pacotes.
	 * @return ArrayList<Package> todos os pacotes do sistema.
	 */
	private List<Package> getAllPackages(Package packageToGet,
			List<Package> packages) {

		EList<AbstractCodeElement> elements = packageToGet.getCodeElement();

		for (AbstractCodeElement abstractCodeElement : elements) {
			if (abstractCodeElement instanceof Package)
				packages = getAllPackages((Package) abstractCodeElement,
						packages);
			else {
				packages.add(packageToGet);
				return packages;
			}

		}
		return packages;
	}

	
	/** 
	 * Esse metodo e responsavel por obter todas as classes da instancia do KDM
	 * @param segment, o segment que representa a instancia do modelo
	 * @return ArrayList<ClassUnit> todas as Classes do sistema.
	 */
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

	/** 
	 * Esse metodo e responsavel por obter todas as classes da instancia do KDM
	 * @param elements, representa todos os elementos
	 */
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
	
	/** 
	 * Esse metodo e responsavel por obter todos os imports, implements e extends contidos em uma ClassUnit
	 * @param classUnit, que representa uma instancia de uma classe do KDM
	 */
	public ArrayList<KDMRelationship> addImportsImplementsAndExtends(ClassUnit classUnit, ArrayList<Layer> allLayers) {

		EList<AbstractCodeRelationship> allRelationshipsOfTheClass = classUnit.getCodeRelation();

		ArrayList<KDMRelationship> allRelationships = new ArrayList<KDMRelationship>();

		for (AbstractCodeRelationship relationship : allRelationshipsOfTheClass) {

			if (verifyIfRelationContaisLayer( relationship, allLayers)) {
				allRelationships.add(relationship);
			}
			

		}


		return allRelationships;

	}
	
	
	
	/** 
	 * Esse metodo e responsavel por obter todos os metodos dado uma ClassUnit
	 * @param classUnit, que representa uma instancia de uma classe do KDM
	 * @return ArrayList<MethodUnit> todas as Classes do sistema.
	 */
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
	
	/** 
	 * Esse metodo e responsavel por obter todos os parameterUnit dado uma MethodUnit
	 * @param methodUnit, que representa uma instancia de um metodo do KDM
	 * @return ArrayList<ParameterUnit> todos os parametros de um Metodo.
	 */
	public ArrayList<ParameterUnit> fetchAllParameterUnits (MethodUnit methodUnit) {	
		
		ArrayList<ParameterUnit> parameterUnits = new ArrayList<ParameterUnit>();
		
		//Pega primeiro a assinatura do metodo, que esta sempre na posicao 0
		Signature auxSignature = (Signature) methodUnit.getCodeElement().get(0);
		
		
		//Pega a lista de parametros
		EList<ParameterUnit> auxListParameterUnit = auxSignature.getParameterUnit();
		
		
		for (ParameterUnit parameterUnit : auxListParameterUnit) {			
			parameterUnits.add(parameterUnit);			
		}
				
		return parameterUnits;

	}
	

	
	/** 
	 * Esse metodo e responsavel por obter um BlockUnit dado um MethodUnit
	 * @param methodUnit representa uma instancia de um Metodo do KDM
	 * @return BlockUnit retorna o blockUnit
	 */
	public BlockUnit getBlockUnit(MethodUnit methodUnit) {

		EList<AbstractCodeElement> allElementsOfTheMethod = methodUnit
				.getCodeElement();

		if ((allElementsOfTheMethod.size() == 2)
				&& (allElementsOfTheMethod.get(1) instanceof BlockUnit))
			return (BlockUnit) allElementsOfTheMethod.get(1);
		else
			return null;

	}
	
	/** 
	 * Esse metodo e responsavel por obter todos os AbstractActionRelationship (ate o momento Calls e UsesType) dado um BlockUnit
	 * @param blockUnit representa uma instancia de um BlockUnit do KDM
	 * @return List<AbstractActionRelationship>
	 */
	public List<AbstractActionRelationship> getRelations(BlockUnit blockUnit) {

		ArrayList<AbstractActionRelationship> relations = new ArrayList<AbstractActionRelationship>();

		EList<AbstractCodeElement> allElementsOfTheMethod = blockUnit
				.getCodeElement();

		for (AbstractCodeElement codeItem : allElementsOfTheMethod) {

			if (codeItem instanceof ActionElement) {

				relations = getActionsRelationships((ActionElement) codeItem,
						relations);
			}

		}

		return relations;

	}
	
	/** 
	 * Esse metodo e responsavel por obter todos os ActionElement do tipo "variable declaration" dado um BlockUnit
	 * @param blockUnit representa uma instancia de um BlockUnit do KDM
	 * @return List<StorableUnit> 
	 */
	public List<StorableUnit> fetchStorableUnitsFromBlockUnit(BlockUnit blockUnit) {

		ArrayList<StorableUnit> storableUnits = new ArrayList<StorableUnit>();

		EList<AbstractCodeElement> allElementsOfTheBlockUnit = blockUnit.getCodeElement();

		for (AbstractCodeElement auxBlockUnit : allElementsOfTheBlockUnit) {

			if (auxBlockUnit instanceof ActionElement && auxBlockUnit.getName().equals("variable declaration")) {
				ActionElement auxActionElement = (ActionElement) auxBlockUnit;
				
				StorableUnit auxStorableUnit = (StorableUnit) auxActionElement.getCodeElement().get(0);
				
				System.err.println(auxStorableUnit.getName());
				
				//O StorableUnit se encontra no primeiro codeElement do ActionElement "variable declaration"
				storableUnits.add(auxStorableUnit );
			}

		}

		return storableUnits;

	}

	
	/** 
	 * Esse metodo e responsavel por obter todos os AbstractActionRelationship (ate o momento Calls e UsesType) dado um BlockUnit
	 * @param actionElement representa uma instancia do ActionElement
	 * @param relations representa as relacoes
	 * @return List<AbstractActionRelationship>
	 */
	private ArrayList<AbstractActionRelationship> getActionsRelationships(
			ActionElement actionElement, ArrayList<AbstractActionRelationship> relations) {

		EList<AbstractCodeElement> allElements = actionElement.getCodeElement();

		for (AbstractCodeElement codeItem : allElements) {

			if (codeItem instanceof ActionElement) {

				relations = getActionsRelationships((ActionElement) codeItem,
						relations);

				if (((ActionElement) codeItem).getActionRelation() != null) {

					ActionElement element = ((ActionElement) codeItem);

					EList<AbstractActionRelationship> allRelationhips = element
							.getActionRelation();

					for (AbstractActionRelationship abstractActionRelationship : allRelationhips) {

						if (abstractActionRelationship instanceof Calls || abstractActionRelationship instanceof UsesType || (abstractActionRelationship instanceof HasValue && abstractActionRelationship.getAnnotation().size() > 0)) {
							
							ArrayList<Layer> allLayers = getAllLayers(this.segmentMain);
							
							if (verifyIfRelationContaisLayer(abstractActionRelationship, allLayers)) {
								relations.add(abstractActionRelationship);
							}
							
						}						

					}

				}
			}
		}

		return relations;

	}
	
	/** 
	 * Esse metodo e responsavel por verificar se uma instancia da metaclasse KDMRelationship contem uma relacao com um determinado Layer.
	 * @param relationToVerify representa uma instancia da metaclasse KDMRelationship
	 * @param allLayers representa uma instancia de uma ArrayLista que contem todos os layer do sistema
	 * @return boolean
	 */
	private boolean verifyIfRelationContaisLayer (KDMRelationship relationToVerify, ArrayList<Layer> allLayers) {
		
		Package[] packageToAndFrom = getOriginAndDestiny(relationToVerify.getTo(),relationToVerify.getFrom());
		
		if (packageToAndFrom[0] == null || packageToAndFrom[1] == null) {
			return false;
		}
		
		boolean to = false, from = false;
		
		
		for (Layer layer1 : allLayers) {
						
			if (mappingLayerToPackage(layer1, packageToAndFrom[0]))
				to = true;
			
			if (mappingLayerToPackage(layer1, packageToAndFrom[1]))
				from = true;									 					
			
		}
		
		if (to && from)
			return true;
		return false;
	}


	
	/** 
	 * Esse metodo e responsavel por obter todos os Layers que ja foram mapeados no sistema
	 * @param segment representa um segment
	 * @return ArrayList<Layer> 
	 */
	public ArrayList<Layer> getAllLayers(Segment segment) {

		ArrayList<Layer> allLayers = new ArrayList<Layer>();
		StructureModel structureModel = null;
		EList<KDMModel> models = segment.getModel();

		for (KDMModel kdmModel : models) {

			if (kdmModel instanceof StructureModel) {

				structureModel = (StructureModel) kdmModel;
				EList<AbstractStructureElement> allStructureElement = structureModel
						.getStructureElement();

				for (AbstractStructureElement abstractStructureElement : allStructureElement) {
					if (abstractStructureElement instanceof Layer) {

						allLayers.add((Layer) abstractStructureElement);

					}
				}

			}
		}
		return allLayers;

	}
	
	/** 
	 * Esse metodo e responsavel por criar relacionamentos entre elementos arquiteturais. 
	 * @param layers representa o conjunto de elementos arquiteturais.
	 * @param allRelations representa todas as acoes a serem mapeadas na arquitetura. 
	 */
	public void createAggreatedRelationShips(ArrayList<Layer> layers,
			ArrayList<? extends KDMRelationship> allRelations) {

		for (KDMRelationship relation : allRelations) {

			Package[] packageToAndFrom = null;
			
			EObject to = relation.getTo();
			EObject from = relation.getFrom();
			
			/*
			
			if (relation instanceof Calls) {
				to = ((Calls) relation).getTo();
				from = ((Calls) relation).getFrom();
			
			} else if (relation instanceof Extends) {
				to = ((Extends) relation).getTo();
				from = ((Extends) relation).getFrom();
				
			} else if (relation instanceof Implements) {
				
				to = ((Implements) relation).getTo();
				from = ((Implements) relation).getFrom();
			
				
			} else if (relation instanceof Imports) {
				
				to = ((Imports) relation).getTo();
				from = ((Imports) relation).getFrom();
			}
			*/
			
			packageToAndFrom = getOriginAndDestiny(to, from);
			
			//verifica se os pacotes de origem e destino sao iguais, caso sejam, nao cria novo relacionamento
			if (!packageToAndFrom[0].getName().equals(packageToAndFrom[1].getName())) {
				
				for (Layer layers1 : layers) {
					
					//Itera nas layers ate encontrar a layer que corresponde a origem(from) da chamada(call) 
					if (mappingLayerToPackage(layers1, packageToAndFrom[1])) {
	
						//recupera todos os relacionamentos da camada (layer)
						EList<AggregatedRelationship> aggregatedRelationship = layers1.getAggregated();
	
						//verifica se existe algum relacionamento na camada (layer) atual
						if (aggregatedRelationship.size() > 0) {
	
							//caso exista, o algoritmo percorre a lista com o intuito de encontrar algum relacionamento que possua o destino (to) desejado
							for (int i = 0; i < aggregatedRelationship.size(); i++) {
	
								//verifica se o campo TO da CALL existe em algum relacionamento, na pratica ele verifica se o pacote TO
								//da chamada ja esta cadastrado em algum dos relacionamentos
								if (mappingLayerToPackage((Layer) aggregatedRelationship.get(i).getTo(), packageToAndFrom[0])) {
									aggregatedRelationship.get(i).setDensity(aggregatedRelationship.get(i).getDensity() + 1);
									aggregatedRelationship.get(i).getRelation().add(relation);
									break;
	
								} else if ((aggregatedRelationship.size()-1) == i) {
									AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
									newRelationship.setDensity(1);
									newRelationship.setFrom(layers1);
									newRelationship.setTo(verifyLayerOwnerOfPackage(packageToAndFrom[0], layers));
									newRelationship.getRelation().add(relation);
									layers1.getAggregated().add(newRelationship);
									break;
								}
															
							}
	
							
	
						} else { //se nao existir, cria um novo relacionamento
							AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
							newRelationship.setDensity(1);
							newRelationship.setFrom(layers1);
							newRelationship.setTo(verifyLayerOwnerOfPackage(packageToAndFrom[0], layers));
							newRelationship.getRelation().add(relation);
							layers1.getAggregated().add(newRelationship);
						}
						
						break;
	
					}
					
					//save(segmentMain, "C:/Users/Fernando/Documents/runtime-EclipseApplication/TesteModisco/Examples/MVCBasic_kdm.xmi");
	
				}
			}

		}

	}

	
	/** 
	 * Esse metodo e responsavel por verificar em qual Layer encontra se um determinado Package 
	 * @param packageToGet representa o Package para verificar
	 * @param layers representa todos os elementos arquiteturais do KDM.
	 * @return Layer representa o Layer. 
	 */
	public Layer verifyLayerOwnerOfPackage(Package packageToGet, ArrayList<Layer> layers) {

		String pathToVerifyPackage = getPathOfPackage(packageToGet, "");

		for (Layer layer : layers) {

			EList<KDMEntity> allImplementation = layer.getImplementation();

			for (KDMEntity kdmEntity : allImplementation) {

				String pathToVerifyLayer = "";

				if (kdmEntity instanceof Package) {

					pathToVerifyLayer = getPathOfPackage((Package) kdmEntity,
							pathToVerifyLayer);

				}

				if (pathToVerifyLayer.equals(pathToVerifyPackage)) {
					return layer;
				}

			}

		}

		return null;

	}

	
	/** 
	 * Esse metodo e responsavel por verificar se uma Package e representado por uma Layer 
	 * @param packageToVerify representa o Package para verificar
	 * @param layer representa todos os elementos arquiteturais do KDM.
	 * @return Boolean 
	 */
	public Boolean mappingLayerToPackage(Layer layer, Package packageToVerify) {

		String pathToVerifyPackage = getPathOfPackage(packageToVerify, "");

		EList<KDMEntity> allImplementation = layer.getImplementation();

		for (KDMEntity kdmEntity : allImplementation) {

			String pathToVerifyLayer = "";

			if (kdmEntity instanceof Package) {

				pathToVerifyLayer = getPathOfPackage((Package) kdmEntity,
						pathToVerifyLayer);

			}

			if (pathToVerifyLayer.equals(pathToVerifyPackage)) {

				return true;

			} else
				return false;

		}

		return null;
	}

	
	/** 
	 * Esse metodo e responsavel por obter o caminho completo dado uma instancia de Package 
	 * @param packageToGetThePath representa uma instancia do Package para obter o caminho
	 * @param pathToGet representa uma String no qual sera add o caminho.
	 * @return String
	 */
	public String getPathOfPackage(EObject packageToGetThePath, String pathToGet) {

		if (packageToGetThePath instanceof Package) {

			Package packageToGet = (Package) packageToGetThePath;

			if (packageToGetThePath.eContainer() instanceof CodeModel) {
				pathToGet += packageToGet.getName();
			} else {

				pathToGet += packageToGet.getName() + ".";

			}
			pathToGet = getPathOfPackage(
					(EObject) packageToGetThePath.eContainer(), pathToGet);
		} else
			return pathToGet;

		return pathToGet;

	}
	
	/** 
	 * Esse metodo e responsavel por recuperar a origem e o destino de uma determinada acao. Alem disso, a representacao
	 * e dada em Package. Exemplo: pacote de origen da acao, pacote de destino da acao. 
	 * @param callToMap representa uma instancia do Calls
	 * @return Package[], o primeiro elemento e o TO (destino), e o segundo e o FROM (origem)
	 */
	public Package[] getOriginAndDestiny(EObject to, EObject from) {

		Package[] packageToAndFrom = new Package[2];
		Package auxTo = null;
		Package auxFrom = null;
		
		/*Identificamos um erro que quando temos Arraylist (listas), o modisco retorna um TemplateType que o topo de sua arvore
		 * eh um CodeModel ao inves de um Pacote. Porem, eh possivel buscar o codeRelation do TemplateType e assim encontrar o pacote
		 * que instacia esta classe.
		 */
		
		if (to instanceof TemplateType) {
			to = getToOfTemplateType((TemplateType)to);
		}
		if (from instanceof TemplateType) {
			from = getToOfTemplateType((TemplateType)from);
		}
			

		auxTo = getToOrFrom(to, auxTo);
		auxFrom = getToOrFrom(from, auxFrom);
		packageToAndFrom[0] = auxTo;
		packageToAndFrom[1] = auxFrom;

		return packageToAndFrom;


	}
	
	private EObject getToOfTemplateType (TemplateType element) {
		
		return element.getCodeRelation().get(0).getTo();	
	}

	/** 
	 * Esse metodo e responsavel por recuperar a origem e o destino de uma determinada acao. Alem disso, a representacao
	 * e dada em Package. Exemplo: pacote de origen da acao, pacote de destino da acao. 
	 * @param element representa um ControlElement
	 * @param toOrFrom representa o Package
	 * @return Package
	 */
	private Package getToOrFrom(EObject element, Package toOrFrom) {

		if (element instanceof Package) {

			return (Package) element;
			
		} else if (element instanceof Segment) {
			
			return null;
		}
			else {
			toOrFrom = getToOrFrom(element.eContainer(), toOrFrom);

		}

		return toOrFrom;

	}

	
	/** 
	 * Esse metodo e responsavel por recuperar a origem e o destino de uma determinada acao. Alem disso, a representacao
	 * e dada em Package. Exemplo: pacote de origen da acao, pacote de destino da acao. 
	 * @param element representa um ControlElement
	 * @param toOrFrom representa o Package
	 * @return Package
	 */
	public List<ActionRelationship> getRelationships(ActionElement actionElement) {

		EList<AbstractActionRelationship> allElementsOfTheMethod = actionElement
				.getActionRelation();

		List<ActionRelationship> actionRelationships = new ArrayList<ActionRelationship>();

		for (AbstractActionRelationship codeItem : allElementsOfTheMethod) {

			if (codeItem instanceof ActionRelationship) {

				ActionRelationship actionRelationshipToPutIntoTheList = (ActionRelationship) codeItem;

				actionRelationships.add(actionRelationshipToPutIntoTheList);

			}

		}

		return actionRelationships;

	}

	/** 
	 * Esse metodo e responsavel por recuperar todos os metodos que uma InterfaceUnit contem. 
	 * @param interfaceUnit representa uma InterfaceUnit
	 * @return List<MethodUnit>
	 */
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

	
	/** 
	 * Esse metodo e responsavel por recuperar todos os CallableUnits que uma ClassUnit contem. 
	 * @param classUnit representa uma ClassUnit
	 * @return List<CallableUnit>
	 */
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

	public Segment getSegmentMain() {
		return segmentMain;
	}

	public void setSegmentMain(Segment segmentMain) {
		this.segmentMain = segmentMain;
	}

	public ArrayList<ClassUnit> getAllClassUnits() {
		return allClassUnits;
	}

	public void setAllClassUnits(ArrayList<ClassUnit> allClassUnits) {
		this.allClassUnits = allClassUnits;
	}

	public ArrayList<MethodUnit> getAllMethodUnits() {
		return allMethodUnits;
	}

	public void setAllMethodUnits(ArrayList<MethodUnit> allMethodUnits) {
		this.allMethodUnits = allMethodUnits;
	}

	public ArrayList<InterfaceUnit> getAllInterfaceUnit() {
		return allInterfaceUnit;
	}

	public void setAllInterfaceUnit(ArrayList<InterfaceUnit> allInterfaceUnit) {
		this.allInterfaceUnit = allInterfaceUnit;
	}

	public ArrayList<StorableUnit> getAllStorableUnits() {
		return allStorableUnits;
	}

	public void setAllStorableUnits(ArrayList<StorableUnit> allStorableUnits) {
		this.allStorableUnits = allStorableUnits;
	}

	public ArrayList<Package> getAllPackages() {
		return allPackages;
	}

	public void setAllPackages(ArrayList<Package> allPackages) {
		this.allPackages = allPackages;
	}

	public ArrayList<BlockUnit> getAllBlockUnits() {
		return allBlockUnits;
	}

	public void setAllBlockUnits(ArrayList<BlockUnit> allBlockUnits) {
		this.allBlockUnits = allBlockUnits;
	}

	public ArrayList<Calls> getAllCalls() {
		return allCalls;
	}

	public void setAllCalls(ArrayList<Calls> allCalls) {
		this.allCalls = allCalls;
	}

	public ArrayList<Layer> getAllLayers() {
		return allLayers;
	}

	public void setAllLayers(ArrayList<Layer> allLayers) {
		this.allLayers = allLayers;
	}

	public ArrayList<KDMRelationship> getAllRelationships() {
		return allRelationships;
	}

	public void setAllRelationships(ArrayList<KDMRelationship> allRelationships) {
		this.allRelationships = allRelationships;
	}

	public ArrayList<HasType> getAllHasType() {
		return allHasType;
	}
	
	public void setAllHasType(ArrayList<HasType> allHasType) {
		this.allHasType = allHasType;
	}

	public ArrayList<AbstractActionRelationship> getAllAbstractActionRelationships() {
		return allAbstractActionRelationships;
	}
	
	public void setAllAbstractActionRelationships(
			ArrayList<AbstractActionRelationship> allAbstractActionRelationships) {
		this.allAbstractActionRelationships = allAbstractActionRelationships;
	}
	
}
