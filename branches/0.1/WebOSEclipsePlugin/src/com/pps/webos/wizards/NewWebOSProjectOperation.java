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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;
import org.osgi.framework.Bundle;

import com.pps.webos.WebOSEclipsePlugin;
import com.pps.webos.WebOSMessages;

/**
 * @author justinm
 *
 */
public class NewWebOSProjectOperation implements IRunnableWithProgress {

	
	private IResource fElementToOpen = null;
	private NewWebOSProjectWizardPage1 fPage = null;
	private IOverwriteQuery fOverwriteQuery = null;	
	
	/**
	 * Constructor for NewWebOSProjectOperation
	 * 
	 * @param page
	 * @param overwriteQuery
	 */
	public NewWebOSProjectOperation(NewWebOSProjectWizardPage1 page, IOverwriteQuery overwriteQuery) {
		fElementToOpen = null;
		fPage = page;
		fOverwriteQuery = overwriteQuery;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
	
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		try {
			createProject(fPage, monitor);
		} finally {
			monitor.done();
		}
	}

	/**
	 * @return
	 */
	public IResource getElementToOpen() {
		return fElementToOpen;
	}
	
	/**
	 * @param page
	 * @param monitor
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	private void createProject(NewWebOSProjectWizardPage1 page, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		IWorkspaceRoot root= WebOSEclipsePlugin.getWorkspace().getRoot();
		IConfigurationElement desc= page.getConfigurationElement();

		String encoding= desc.getAttribute("encoding"); //$NON-NLS-1$

		IConfigurationElement[] imports= desc.getChildren("import"); //$NON-NLS-1$
		IConfigurationElement[] natures= desc.getChildren("nature"); //$NON-NLS-1$
		IConfigurationElement[] references= desc.getChildren("references"); //$NON-NLS-1$
		int nImports= (imports == null) ? 0 : imports.length;
		int nNatures= (natures == null) ? 0 : natures.length;
		int nReferences= (references == null) ? 0 : references.length;

		monitor.beginTask(WebOSMessages.NewWebOSProjectWizardOperation_op_desc_proj, nImports + 2);

		/* name of project */
		String name = page.getProjectName();

		String[] natureIds= new String[nNatures];
		for (int i= 0; i < nNatures; i++) {
			natureIds[i]= natures[i].getAttribute("id"); //$NON-NLS-1$
		}
		IProject[] referencedProjects= new IProject[nReferences];
		for (int i= 0; i < nReferences; i++) {
			referencedProjects[i]= root.getProject(references[i].getAttribute("id")); //$NON-NLS-1$
		}

		IProject proj= configNewProject(root, name, natureIds, referencedProjects, encoding, monitor);

		for (int i= 0; i < nImports; i++) {
			doImports(proj, imports[i], new SubProgressMonitor(monitor, 1));
		}

		String open= desc.getAttribute("open"); //$NON-NLS-1$
		if (open != null && open.length() > 0) {
			IResource fileToOpen= proj.findMember(new Path(open));
			if (fileToOpen != null) {
				fElementToOpen= fileToOpen;
			}
		}

	}

	/**
	 * @param root
	 * @param name
	 * @param natureIds
	 * @param referencedProjects
	 * @param encoding
	 * @param monitor
	 * @return
	 * @throws InvocationTargetException
	 */
	private IProject configNewProject(IWorkspaceRoot root, String name, String[] natureIds, IProject[] referencedProjects, String encoding, IProgressMonitor monitor) throws InvocationTargetException {
		try {
			IProject project= root.getProject(name);
			if (!project.exists()) {
				project.create(null);
			}
			if (!project.isOpen()) {
				project.open(null);
			}
			IProjectDescription desc= project.getDescription();
			desc.setLocation(null);
			desc.setNatureIds(natureIds);
			desc.setReferencedProjects(referencedProjects);

			project.setDescription(desc, new SubProgressMonitor(monitor, 1));

			if (encoding != null) {
				project.setDefaultCharset(encoding, new SubProgressMonitor(monitor, 1));
			}

			return project;
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * @param project
	 * @param curr
	 * @param monitor
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	private void doImports(IProject project, IConfigurationElement curr, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			IPath destPath;
			String name= curr.getAttribute("dest"); //$NON-NLS-1$
			if (name == null || name.length() == 0) {
				destPath= project.getFullPath();
			} else {
				IFolder folder= project.getFolder(name);
				if (!folder.exists()) {
					folder.create(true, true, null);
				}
				destPath= folder.getFullPath();
			}
			String importPath= curr.getAttribute("src"); //$NON-NLS-1$
			if (importPath == null) {
				importPath= ""; //$NON-NLS-1$
				WebOSEclipsePlugin.log("projectsetup descriptor: import missing"); //$NON-NLS-1$
				return;
			}

			String importType= curr.getAttribute("importtype"); //$NON-NLS-1$

			// 1 is zip
			// 2 is file directory
			if (importType != null) {
				switch (Integer.parseInt(importType)) {
					case 1:
						ZipFile zipFile1= getZipFileFromPluginDir(importPath, getContributingPlugin(curr));
						importFilesFromZip(zipFile1, destPath, new SubProgressMonitor(monitor, 1));
//					case 2:
//						ZipFile zipFile= getZipFileFromPluginDir(importPath, getContributingPlugin(curr));
//						getDirectoryFromPluginDir (importPath,  getContributingPlugin(curr), destPath, new SubProgressMonitor(monitor, 1));
				}
			}
			
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * @param configurationElement
	 * @return
	 */
	private Bundle getContributingPlugin(IConfigurationElement configurationElement) {
		String namespace= configurationElement.getContributor().getName();
		return Platform.getBundle(namespace);
	}

	/**
	 * @param pluginRelativePath
	 * @param pluginDescriptor
	 * @return
	 * @throws CoreException
	 */
	private ZipFile getZipFileFromPluginDir(String pluginRelativePath, Bundle pluginDescriptor) throws CoreException {
		try {
			URL starterURL= pluginDescriptor.getEntry(pluginRelativePath);
			return new ZipFile(FileLocator.toFileURL(starterURL).getFile());
		} catch (IOException e) {
			String message= pluginRelativePath + ": " + e.getMessage(); //$NON-NLS-1$
			Status status= new Status(IStatus.ERROR, WebOSEclipsePlugin.getPluginId(), IStatus.ERROR, message, e);
			throw new CoreException(status);
		}
	}

	/**
	 * @param srcZipFile
	 * @param destPath
	 * @param monitor
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	private void importFilesFromZip(ZipFile srcZipFile, IPath destPath, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		ZipFileStructureProvider structureProvider=	new ZipFileStructureProvider(srcZipFile);
		ImportOperation op= new ImportOperation(destPath, structureProvider.getRoot(), structureProvider, fOverwriteQuery);
		op.run(monitor);
	}
}