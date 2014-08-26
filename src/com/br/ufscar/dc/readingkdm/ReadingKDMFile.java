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
import org.eclipse.gmt.modisco.omg.kdm.code.CodeFactory;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.CorePackage;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmFactory;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.Layer;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.omg.IOP.CodecFactory;



/** 
* @author Fernando Chagas e Rafael Durelli
* @since 21/08/14 
*/

public class ReadingKDMFile {

	private Segment segmentMain = null;
	
	
	/** 
	 * Retorna um segmento passando como parametro o caminho completo de um arquivo KDM.
	 * 
	 * @param  KDMModelFullPath  representa o caminho da arquivo KDM
	 * @return      O Segment, que e o elemento principal para manipular uma instancia do KDM.
	 * @see         org.eclipse.gmt.modisco.omg.kdm.kdm.Segment
	 */
	public Segment load(String KDMModelFullPath) {

		KdmPackage.eINSTANCE.eClass();//get the KDMPackage instance

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("fer", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();
		// Get the resource
		Resource resource = resSet.getResource(URI.createURI(KDMModelFullPath),
				true);

		return (Segment) resource.getContents().get(0); //pega o primeiro elemento, que � o Segment
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

		}

		save(segment, kdmPath);

	}

	/** 
	 * Esse metodo e responsavel por salvar uma instancia do KDM apos a realiza��o de mudancas no mesmo.
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
	 * Esse metodo e responsavel por obter todos os Calls dado um BlockUnit
	 * @param blockUnit representa uma instancia de um BlockUnit do KDM
	 * @return List<Calls>
	 */
	public List<Calls> getRelations(BlockUnit blockUnit) {

		ArrayList<Calls> relations = new ArrayList<Calls>();

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
	 * Esse metodo e responsavel por obter todos os Calls dado um BlockUnit
	 * @param actionElement representa uma instancia do ActionElement
	 * @param relations representa as relacoes
	 * @return List<Calls>
	 */
	private ArrayList<Calls> getActionsRelationships(
			ActionElement actionElement, ArrayList<Calls> relations) {

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

						if (abstractActionRelationship instanceof Calls) {
							
							ArrayList<Layer> allLayers = getAllLayers(this.segmentMain);
							
							if (verifyIfCallContainsLayer((Calls) abstractActionRelationship, allLayers)) {
								relations.add((Calls) abstractActionRelationship);
							}
							
						}

					}

				}
			}
		}

		return relations;

	}
	
	
	/** 
	 * Esse metodo e responsavel por verificar se uma instancia da metaclasse Calls contem uma relacao com um determinado Layer.
	 * @param callToVerify representa uma instancia da metaclasse Calls
	 * @param allLayers representa uma instancia de uma ArrayLista que contem todos os layer do sistema
	 * @return boolean
	 */
	private boolean verifyIfCallContainsLayer (Calls callToVerify, ArrayList<Layer> allLayers) {
		
		Package[] packageToAndFrom = getOriginAndDestinyOfaCall(callToVerify);
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
	 * @param allCalls representa todas as acoes a serem mapeadas na arquitetura. 
	 */
	public void createAggreatedRelationShips(ArrayList<Layer> layers,
			ArrayList<Calls> allCalls) {

		for (Calls calls : allCalls) {

			Package[] packageToAndFrom = getOriginAndDestinyOfaCall(calls);

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
								aggregatedRelationship.get(i).getRelation().add(calls);
								break;

							} else if ((aggregatedRelationship.size()-1) == i) {
								AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
								newRelationship.setDensity(1);
								newRelationship.setFrom(layers1);
								newRelationship.setTo(verifyLayerOwnerOfPackage(packageToAndFrom[0], layers));
								newRelationship.getRelation().add(calls);
								layers1.getAggregated().add(newRelationship);
								break;
							}
														
						}

						

					} else { //se nao existir, cria um novo relacionamento
						AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
						newRelationship.setDensity(1);
						newRelationship.setFrom(layers1);
						newRelationship.setTo(verifyLayerOwnerOfPackage(packageToAndFrom[0], layers));
						newRelationship.getRelation().add(calls);
						layers1.getAggregated().add(newRelationship);
					}
					
					break;

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
	 * @param pathToGet representa uma String no qual ser� add o caminho.
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
	public Package[] getOriginAndDestinyOfaCall(Calls callToMap) {

		Package[] packageToAndFrom = new Package[2];
		Package to = null;
		Package from = null;

		to = getToOrFrom(callToMap.getTo(), to);
		from = getToOrFrom(callToMap.getFrom(), from);
		packageToAndFrom[0] = to;
		packageToAndFrom[1] = from;

		return packageToAndFrom;

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
		} else {
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

}
