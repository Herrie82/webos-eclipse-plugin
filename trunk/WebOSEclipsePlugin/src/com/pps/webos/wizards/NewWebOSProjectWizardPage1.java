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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.pps.webos.WebOSEclipsePlugin;
import com.pps.webos.WebOSMessages;

/**
 * @author justinm
 *
 */
public class NewWebOSProjectWizardPage1 extends WizardPage {

	/* Static class information */
	private Text fTextControl = null; 
	
	private IStatus fCurrStatus = null;
	private boolean fPageVisible = false;
	private String fNameLabel = null;
	private String fProjectName = null;

	private IConfigurationElement fConfigurationElement;
	
	/**
	 * 
	 * @param pageName
	 */
	public NewWebOSProjectWizardPage1(int pageNumber, IConfigurationElement elem) {
		super("page" + pageNumber);

		fConfigurationElement = elem;

		fCurrStatus = createStatus(IStatus.OK, ""); //$NON-NLS-1$
		fNameLabel= getAttribute(elem, "label"); //$NON-NLS-1$
		fProjectName= getAttribute(elem, "name");		 //$NON-NLS-1$

		setTitle(getAttribute(elem, "pagetitle")); //$NON-NLS-1$
		setDescription(getAttribute(elem, "pagedescription")); //$NON-NLS-1$
	}

	/**
	 * Utility method for getting attributes
	 * 
	 * @param elem
	 * @param tag
	 * @return String
	 */
	private String getAttribute(IConfigurationElement elem, String tag) {
		String res= elem.getAttribute(tag);
		if (res == null) {
			return '!' + tag + '!';
		}
		return res;
	}	
	
	/**
	 * @param severity
	 * @param message
	 * @return IStatus
	 */
	private static IStatus createStatus(int severity, String message) {
		return new Status(severity, WebOSEclipsePlugin.getPluginId(), severity, message, null);
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {

		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout gd= new GridLayout();
		gd.numColumns= 2;
		composite.setLayout(gd);

		Label label= new Label(composite, SWT.LEFT);
		label.setText("Project Name:");
		label.setLayoutData(new GridData());

		fTextControl = new Text(composite, SWT.SINGLE | SWT.BORDER);
		fTextControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				//if (!fTextControl.isDisposed()) {
				dialogueChange();
				//}
			}
		});
		
		fTextControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTextControl.setFocus();
		setControl(composite);
	}
	
	/**
	 * Method should validate input text in the page 
	 * 
	 * @param text
	 */
	private void dialogueChange() {

		/* 'validation' for project name */
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IStatus status= workspace.validateName(fTextControl.getText(), IResource.PROJECT);
		if (status.isOK()) {
			/* check to see if project already exists in workspace */
			if (workspace.getRoot().getProject(fTextControl.getText()).exists()) {
				status = createStatus(IStatus.ERROR, WebOSMessages.NewWebOSProjectWizardPage1_error_alreadyexists);
			}
		}
		updateStatus(status);

		fProjectName = fTextControl.getText();		
	}
	
	/**
	 * Updates the status line and the ok button depending on the status
	 * 
	 * @param status
	 */
	private void updateStatus(IStatus status) {
		fCurrStatus= status;
		setPageComplete(!status.matches(IStatus.ERROR));
		if (fPageVisible) {
			applyToStatusLine(this, status);
		}
	}
	
	/**
	 * Applies the status to a dialog page
	 * 
	 * @param page
	 * @param status
	 */
	private static void applyToStatusLine(DialogPage page, IStatus status) {
		String errorMessage= null;
		String warningMessage= null;
		String statusMessage= status.getMessage();
		if (statusMessage.length() > 0) {
			if (status.matches(IStatus.ERROR)) {
				errorMessage= statusMessage;
			} else if (!status.isOK()) {
				warningMessage= statusMessage;
			}
		}
		page.setErrorMessage(errorMessage);
		page.setMessage(warningMessage);
	}

	/**
	 * @return
	 */
	public String getProjectName() {
		return fTextControl.getText();
	}
	
	/**
	 * Returns the configuration element of this page.
	 * @return Returns a IConfigurationElement
	 */
	public IConfigurationElement getConfigurationElement() {
		return fConfigurationElement;
	}
	
	
}
