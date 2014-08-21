package projetomestradofernandochagas2.popup.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
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
		
		ArrayList<ClassUnit> allClasses = readingKDM.getAllClasses(segment);
		
		for (ClassUnit classUnit : allClasses) {
			System.out.println("The class is " + classUnit.getName());
		}
		
		ArrayList<org.eclipse.gmt.modisco.omg.kdm.code.Package> allPackages = null;
		
		allPackages = readingKDM.getAllPackages(segment);
		
		for (org.eclipse.gmt.modisco.omg.kdm.code.Package allPackage : allPackages) {
			System.out.println("Package name: " + allPackage.getName());
		}
			
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
