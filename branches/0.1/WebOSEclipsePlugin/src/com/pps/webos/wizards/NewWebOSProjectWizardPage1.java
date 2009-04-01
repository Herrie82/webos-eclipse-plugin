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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import com.pps.webos.WebOSEclipsePlugin;
import com.pps.webos.WebOSMessages;
import com.pps.webos.enums.AppInfoEnum;
import com.pps.webos.model.AppInfo;

/**
 * @author justinm
 *
 */
public class NewWebOSProjectWizardPage1 extends WizardPage {

	
	/**
	 * Request a project name. Fires an event whenever the text field is
	 * changed, regardless of its content.
	 */
	private final class NameGroup extends Observable implements IDialogFieldListener {

		protected final StringDialogField fNameField;

		public NameGroup() {
			// text field for project name
			fNameField = new StringDialogField();
			fNameField.setLabelText("Project Name: "); 
			fNameField.setDialogFieldListener(this);
		}

		public Control createControl(Composite composite) {
			Composite nameComposite= new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false), false));

			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));

			return nameComposite;
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void postSetFocus() {
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}

		public void setName(String name) {
			fNameField.setText(name);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener#dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}
	}
	
	/**
	 * AppInfoGroup
	 *
	 */
	private final class AppInfoGroup extends Observable implements Observer, IDialogFieldListener  {

		private AppInfo appInfo = null;
		
		protected final StringDialogField fTitle;
		protected final StringDialogField fType;
		protected final StringDialogField fMain;
		protected final StringDialogField fIcon;
		protected final StringDialogField fId;
		protected final StringDialogField fVersion;
		protected final StringDialogField fVendorId;
		protected final StringDialogField fRemoveable;
		
		public AppInfoGroup() {
			
			appInfo = new AppInfo();
			
			// text field for project name
			fTitle = new StringDialogField();
			fTitle.setLabelText(AppInfoEnum.TITLE.getDisplayText()); 
			fTitle.setDialogFieldListener(this);

			// text field for project name
			fType = new StringDialogField();
			fType.setLabelText(AppInfoEnum.TYPE.getDisplayText()); 
			fType.setDialogFieldListener(this);
			
			// text field for project name
			fMain = new StringDialogField();
			fMain.setLabelText(AppInfoEnum.MAIN.getDisplayText()); 
			fMain.setDialogFieldListener(this);
		
			// text field for project name
			fIcon = new StringDialogField();
			fIcon.setLabelText(AppInfoEnum.ICON.getDisplayText()); 
			fIcon.setDialogFieldListener(this);
			
			// text field for project name
			fId = new StringDialogField();
			fId.setLabelText(AppInfoEnum.ID.getDisplayText()); 
			fId.setDialogFieldListener(this);
			
			// text field for project name
			fVersion = new StringDialogField();
			fVersion.setLabelText(AppInfoEnum.VERSION.getDisplayText()); 
			fVersion.setDialogFieldListener(this);
			
			// text field for project name
			fVendorId = new StringDialogField();
			fVendorId.setLabelText(AppInfoEnum.VENDORID.getDisplayText()); 
			fVendorId.setDialogFieldListener(this);
			
			// text field for project name
			fRemoveable = new StringDialogField();
			fRemoveable.setLabelText(AppInfoEnum.REMOVABLE.getDisplayText()); 
			fRemoveable.setDialogFieldListener(this);			
			
		}
		
		public Control createControl(Composite composite) {

			final int numColumns= 2;
			
			final Group group= new Group(composite, SWT.NONE);
			group.setFont(composite.getFont());
			group.setText("AppInfo Attributes"); 
			
			GridLayout layout= initGridLayout(new GridLayout(4, false), true);;
			layout.horizontalSpacing = 3;
			layout.verticalSpacing = 6;
			group.setLayout(layout);
						
			fTitle.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fTitle.getTextControl(null));
			
			fType.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fType.getTextControl(null));
			
			fMain.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fMain.getTextControl(null));
	
			fIcon.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fIcon.getTextControl(null));
			
			fId.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fId.getTextControl(null));

			fVersion.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fVersion.getTextControl(null));
			
			fVendorId.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fVendorId.getTextControl(null));
			
			fRemoveable.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fRemoveable.getTextControl(null));

			LayoutUtil.setHorizontalGrabbing(group);
			
			// Bind text fields to the model
			// probably a cleaner way to do this by looping through field vals dynamically...
			DataBindingContext bindingContext = new DataBindingContext();
			
			// fTitle;
			bindingContext.bindValue(
					SWTObservables.observeText(fTitle.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.TITLE.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
			
			// fType;
			bindingContext.bindValue(
					SWTObservables.observeText(fType.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.TYPE.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fMain;
			bindingContext.bindValue(
					SWTObservables.observeText(fMain.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.MAIN.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fIcon;
			bindingContext.bindValue(
					SWTObservables.observeText(fIcon.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.ICON.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fId;
			bindingContext.bindValue(
					SWTObservables.observeText(fId.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.ID.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fVersion;
			bindingContext.bindValue(
					SWTObservables.observeText(fVersion.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.VERSION.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fVendorId;
			bindingContext.bindValue(
					SWTObservables.observeText(fVendorId.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.VENDORID.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fRemoveable;			
			bindingContext.bindValue(
					SWTObservables.observeText(fRemoveable.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.REMOVABLE.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
			
			return group;
		}
		
		public void dialogFieldChanged(DialogField field) {
			// TODO Auto-generated method stub
			
		}

		public void update(Observable arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}
		
		/**
		 * @return the appInfo
		 */
		public AppInfo getAppInfo() {
			return appInfo;
		}
		
	}
	
	
	
	/* Static class information */
	//private Text fTextControl = null; 
	
	private IStatus fCurrStatus = null;
	private boolean fPageVisible = false;

	private IConfigurationElement fConfigurationElement = null;
	
	
	// groups
	private final AppInfoGroup appInfoGroup;
	private final NameGroup fNameGroup;
	
	/**
	 * 
	 * @param pageName
	 */
	public NewWebOSProjectWizardPage1(int pageNumber, IConfigurationElement elem) {
		super("page" + pageNumber);

		// wizard information
		fConfigurationElement = elem;
		fCurrStatus = createStatus(IStatus.OK, ""); //$NON-NLS-1$
		setTitle(getAttribute(elem, "pagetitle")); //$NON-NLS-1$
		setDescription(getAttribute(elem, "pagedescription")); //$NON-NLS-1$

		
		// make call to groups 
		fNameGroup = new NameGroup();
		appInfoGroup = new AppInfoGroup();

		// initialize all elements
		fNameGroup.notifyObservers(appInfoGroup);
		appInfoGroup.notifyObservers();
	
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {

		final Composite composite= new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// create UI elements
		Control nameControl= createNameControl(composite);
		nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// create UI elements
		Control appInfoControl = createAppInfoControl(composite);
		appInfoControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(composite);
	}	
	
	/**
	 * @param layout
	 * @param margins
	 * @return
	 */
	private GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth= 4;
			layout.marginHeight= 7;
		}
		return layout;
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
	

	
	/**
	 * Method should validate input text in the page 
	 * 
	 * @param text
	 */
	private void dialogueChange() {

		/* 'validation' for project name */
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		
		IStatus status= workspace.validateName(fNameGroup.getName(), IResource.PROJECT);
		if (status.isOK()) {
			/* check to see if project already exists in workspace */
			if (workspace.getRoot().getProject(fNameGroup.getName()).exists()) {
				status = createStatus(IStatus.ERROR, WebOSMessages.NewWebOSProjectWizardPage1_error_alreadyexists);
			}
		}
		updateStatus(status);

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
	 * Creates the controls for the name field. 
	 *  
	 * @param composite the parent composite
	 * @return the created control
	 */		
	protected Control createNameControl(Composite composite) {
		return fNameGroup.createControl(composite);
	}
	
	/**
	 * Creates the controls for the name field. 
	 *  
	 * @param composite the parent composite
	 * @return the created control
	 */		
	protected Control createAppInfoControl(Composite composite) {
		return appInfoGroup.createControl(composite);
	}	

	/**
	 * @return
	 */
	public String getProjectName() {
		return fNameGroup.getName();
	}
	
	public AppInfo getAppInfo() {
		return appInfoGroup.getAppInfo();
	}
	
	/**
	 * Returns the configuration element of this page.
	 * @return Returns a IConfigurationElement
	 */
	public IConfigurationElement getConfigurationElement() {
		return fConfigurationElement;
	}

	
	
}
