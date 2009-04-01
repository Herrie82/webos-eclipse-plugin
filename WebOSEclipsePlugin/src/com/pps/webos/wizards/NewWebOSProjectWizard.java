/**
 *  Copyright (c) 2009. Knests, LLC
 *  
 *  This file is part of webOS Eclipse Plug-in.
 * 
 *  webOS Eclipse Plug-in is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  webOS Eclipse Plug-in is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with webOS Eclipse Plug-in.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  Author: Justin Musgrove
 *  		justinm@knests.com
 *  
 * */
package com.pps.webos.wizards;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.osgi.framework.Bundle;

import com.pps.webos.WebOSEclipsePlugin;
import com.pps.webos.WebOSMessages;
import com.pps.webos.model.AppInfo;
import com.pps.webos.util.AppInfoHelper;

/**
 * @author justinm
 *
 */
public class NewWebOSProjectWizard extends Wizard implements
		IExecutableExtension, INewWizard {

	private IConfigurationElement fConfigElement;
	private NewWebOSProjectWizardPage1 fPage = null;
	
	/**
	 * default constructor
	 */
	public NewWebOSProjectWizard() {
		super();
		setDialogSettings(WebOSEclipsePlugin.getDefault().getDialogSettings());
		setWindowTitle(WebOSMessages.NewWebOSProjectWizard_title);
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * @param pageConfigElement
	 */
	private void initializeDefaultPageImageDescriptor(IConfigurationElement pageConfigElement) {
		String banner= pageConfigElement.getAttribute("banner"); 
		if (banner != null) {
			Bundle bundle= Platform.getBundle(pageConfigElement.getContributor().getName());
			setDefaultPageImageDescriptor(WebOSEclipsePlugin.createImageDescriptor(bundle, new Path(banner)));
		}
	}
	
	/*
	 * @see Wizard#addPages
	 */
	public void addPages() {
		super.addPages();
		if (fPage != null) {
			addPage(fPage);
		}
	}	
	
	// method should be run after the project creation is complete
	// 
	private void postProjectSetup (String projectName) {
		
		AppInfoHelper appInfoHelper = new AppInfoHelper();
		appInfoHelper.replaceAppInfoAttributes(projectName, fPage.getAppInfo());
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (fPage != null) {
			NewWebOSProjectOperation runnable= new NewWebOSProjectOperation(fPage, new ImportOverwriteQuery());

			IRunnableWithProgress op= new WorkspaceModifyDelegatingOperation(runnable);
			try {
				getContainer().run(false, true, op);
			} catch (InvocationTargetException e) {
				handleException(e.getTargetException());
				return false;
			} catch  (InterruptedException e) {
				return false;
			}
			
			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			IResource res= runnable.getElementToOpen();
			if (res != null) {
				openResource(res);
			}

			// do post processing
			postProjectSetup(fPage.getProjectName());	
		}
		return true;
	}

	/**
	 * @param target
	 */
	private void handleException(Throwable target) {
		String title= WebOSMessages.NewWebOSProjectWizard_op_error_title;
		String message= WebOSMessages.NewWebOSProjectWizard_op_error_message;
		if (target instanceof CoreException) {
			IStatus status= ((CoreException)target).getStatus();
			ErrorDialog.openError(getShell(), title, message, status);
			WebOSEclipsePlugin.log(status);
		} else {
			MessageDialog.openError(getShell(), title, target.getMessage());
			WebOSEclipsePlugin.log(target);
		}
	}	
	
	
	/**
	 * @param resource
	 */
	private void openResource(final IResource resource) {
		if (resource.getType() != IResource.FILE) {
			return;
		}
		IWorkbenchWindow window= WebOSEclipsePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		final IWorkbenchPage activePage= window.getActivePage();
		if (activePage != null) {
			final Display display= getShell().getDisplay();
			display.asyncExec(new Runnable() {
				public void run() {
					try {
						IDE.openEditor(activePage, (IFile)resource, true);
					} catch (PartInitException e) {
						WebOSEclipsePlugin.log(e);
					}
				}
			});
			BasicNewResourceWizard.selectAndReveal(resource, activePage.getWorkbenchWindow());
		}
	}

	/*
	 * Stores the configuration element for the wizard.  The config element will be used
	 * in <code>performFinish</code> to set the result perspective.
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		fConfigElement= cfig;

		/* projectsetup is read from the plugin.xml */
		IConfigurationElement[] children = cfig.getChildren("projectsetup"); 
		if (children == null || children.length != 1) {
			WebOSEclipsePlugin.log("descriptor must contain one project setup tag"); 
		} else {
			IConfigurationElement pageConfigElement= children[0];
			fPage= new NewWebOSProjectWizardPage1(0, pageConfigElement);
			initializeDefaultPageImageDescriptor(pageConfigElement);
		}
	}
	/**
	 * @author justinm
	 *
	 */
	private class ImportOverwriteQuery implements IOverwriteQuery {
		public String queryOverwrite(String file) {
			String[] returnCodes= { YES, NO, ALL, CANCEL};
			int returnVal= openDialog(file);
			return returnVal < 0 ? CANCEL : returnCodes[returnVal];
		}

		private int openDialog(final String file) {
			final int[] result= { IDialogConstants.CANCEL_ID };
			getShell().getDisplay().syncExec(new Runnable() {
				public void run() {
					String title= WebOSMessages.NewWebOSProjectWizard_overwritequery_title;
					String msg= MessageFormat.format(WebOSMessages.NewWebOSProjectWizard_overwritequery_message, new Object[] {file});
					String[] options= {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.CANCEL_LABEL};
					MessageDialog dialog= new MessageDialog(getShell(), title, null, msg, MessageDialog.QUESTION, options, 0);
					result[0]= dialog.open();
				}
			});
			return result[0];
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}
}
