/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Reiterer (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.subclipse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.reviews.core.scm.IChangeset;
import org.eclipse.mylyn.reviews.core.scm.IRevision;
import org.eclipse.mylyn.reviews.core.scm.ISCMAdapter;
import org.eclipse.mylyn.reviews.core.scm.serialization.Dataholder;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Changeset;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.File;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Mappings;
import org.eclipse.mylyn.reviews.subclipse.util.SVNUtil;
import org.eclipse.mylyn.reviews.subclipse.util.SubclipseMetadataUpdaterJob;
import org.tigris.subversion.subclipse.core.ISVNRemoteFile;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.SVNProviderPlugin;
import org.tigris.subversion.subclipse.core.commands.GetRemoteResourceCommand;
import org.tigris.subversion.subclipse.core.repo.SVNRepositories;
import org.tigris.subversion.subclipse.core.resources.RemoteFile;
import org.tigris.subversion.subclipse.ui.actions.OpenRemoteFileAction;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

public class SVNAdapter implements ISCMAdapter {
	private static Logger logger = Logger.getLogger(SVNAdapter.class.getName());
	
	private SubclipseMetadataUpdaterJob job;
	
	public SVNAdapter() {
	}
	
	/**
	 * Determine the list of changesets, which is associated with a given task.
	 * To reduce the communication-overhead in the case of centralized organized
	 * SCM systems, it is necessary to temporarily store mappings from tasks to
	 * changesets local. The locally stored data will frequently be refreshed.
	 * 
	 * @param monitor
	 *            The progress monitor which is used.
	 * @param project
	 *            The project which is associated with the repository.
	 * @param taskId
	 *            Task identifier.
	 * @param repositoryUrl
	 *            URL that identifies a task repository.
	 * @param forceUpdate
	 *            Enforce the refreshment of locally stored mappings.
	 * @return Changesets for a given task.
	 * @throws CoreException
	 */
	public List<IChangeset> getChangesetsForTask(IProgressMonitor monitor,
			String repositoryLocation, String taskId,
			boolean forceUpdate) throws CoreException {

		logger.info("GET CHANGESETS FOR TASK!");
		List<IChangeset> chs = new ArrayList<IChangeset>();

		// parse commit messages if forceUpdate is true
		try {
			//FIXME: remove comment
			//Parser.parseCommitMessages(monitor, repositoryLocation);

			Mappings mappings = Dataholder.loadMappings(repositoryLocation);

			if (mappings != null) {
				Map<String, List<String>> tskMap = mappings.getTaskMappings();
				List<String> changesets = tskMap.get(taskId);

				if (changesets != null) {
					for (String c : changesets) {
						Changeset change = mappings.getChangesets().get(c);

						chs.add(change);
					}
				}
			} else {
				throw new CoreException(
						new Status(
								IStatus.WARNING,
								org.eclipse.mylyn.reviews.subclipse.Activator.PLUGIN_ID,
								"Mappings currently not available!"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR,
					org.eclipse.mylyn.reviews.subclipse.Activator.PLUGIN_ID,
					"Error while parsing commit messages!"));

		}

		return chs;
	}

	public IChangeset getChangeset(IProgressMonitor monitor,
			IRevision revision, String repositoryLocation) throws CoreException {
		logger.info("GET CHANGESET IN REVISION: "
				+ revision.getContentIdentifier());

		try {
			Mappings mappings = Dataholder.loadMappings(repositoryLocation);

			if (mappings != null) {
				Map<String, Changeset> chsMap = mappings.getChangesets();
				Changeset c = chsMap.get(revision.getContentIdentifier());

				if (c == null)
					throw new CoreException(
							new Status(
									IStatus.ERROR,
									org.eclipse.mylyn.reviews.subclipse.Activator.PLUGIN_ID,
									"No changeset found for this revision!"));
				else
					return c;
			} else {
				throw new CoreException(
						new Status(
								IStatus.WARNING,
								org.eclipse.mylyn.reviews.subclipse.Activator.PLUGIN_ID,
								"Mappings currently not available!"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR,
					org.eclipse.mylyn.reviews.subclipse.Activator.PLUGIN_ID,
					"Error while loading config file!"));

		}
	}

	private ISVNRemoteResource getRemoteResource(IProgressMonitor monitor,
			ISVNRepositoryLocation location, SVNRevision revision, SVNUrl url)
			throws CoreException {

		// get the remote resource
		GetRemoteResourceCommand command = new GetRemoteResourceCommand(
				location, url, revision);

		command.run(null);

		ISVNRemoteResource resource = command.getRemoteResource();
		if (resource == null) {
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Cannot get remote resource!");
			logger.info("Cannot get remote resource!");
			throw new CoreException(status);
		}

		return resource;
	}

	@Override
	public String getTeamId() {
		return SVNProviderPlugin.PROVIDER_ID;
	}

	@Override
	public IStorage getStorageForRevision(
			IProgressMonitor monitor, IRevision revNo, String repositoryLocation,String path)
			throws CoreException {

		logger.info("GET FILE FOR REVISION!");
		try {
			ISVNRepositoryLocation location = org.tigris.subversion.subclipse.core.SVNProviderPlugin
			.getPlugin().getRepository(repositoryLocation);
			SVNRevision revision = new SVNRevision.Number(Long.parseLong(revNo
					.getContentIdentifier()));

			SVNUrl url = new SVNUrl(SVNUtil.concatUrls(repositoryLocation, path));

			logger.info("FILE URL: " + url);
			ISVNRemoteResource resource = getRemoteResource(monitor, location,
					revision, url);

			// check if the resource is a file
			if (resource.isFolder()) {
				throw new CoreException(new Status(IStatus.ERROR,
						Activator.PLUGIN_ID,
						"The given path describes a folder and not a file!"));
			}

			// create remote file with the correct peg revision
			final RemoteFile file = new RemoteFile(null, location, url,
					revision, (SVNRevision.Number) revision, null, null);
			file.setPegRevision(revision);
			file.fetchContents(monitor);

			return file.getStorage(monitor);

		} catch (Exception e) {
			logger.info("Resource does not exist in this revision!");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.reviewclipse.ISVNClientAdapter#open(org.eclipse.core.runtime.
	 * IProgressMonitor, org.eclipse.core.resources.IProject,
	 * org.reviewclipse.model.subversion.LogEntry,
	 * org.reviewclipse.model.subversion.LogPath)
	 */
	public void open(IProgressMonitor monitor, String repositoryLocation, String rev,
			File path) throws CoreException {

		logger.info("GET OPEN ACTION: " + rev);
		try {
			ISVNRepositoryLocation location = org.tigris.subversion.subclipse.core.SVNProviderPlugin
			.getPlugin().getRepository(repositoryLocation);

			// revision number from log entry
			SVNRevision revision = new SVNRevision.Number(Long.parseLong(rev));

			SVNUrl url = new SVNUrl(SVNUtil.concatUrls(repositoryLocation, path.getFilename()));

			// create remote file with the correct peg revision
			final RemoteFile file = new RemoteFile(null, location, url,
					revision, (SVNRevision.Number) revision, null, null);
			// file.setPegRevision(revision);

			// check monitor
			if (monitor.isCanceled()) {
				return;
			}

			logger.info("opening file " + file.getUrl() + " for revision " //$NON-NLS-1$ //$NON-NLS-2$
					+ revision);

			OpenRemoteFileAction action = new OpenRemoteFileAction() {
				@Override
				protected ISVNRemoteFile[] getSelectedRemoteFiles() {
					return new ISVNRemoteFile[] { file };
				}
			};

			// run open action, may not throw any error
			action.run(null);
		} catch (Exception e) {
			new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Cannot open editor"));
		}
	}

	public List<String> getKnownSVNRepositores() {
		List<String> knownRepos = new ArrayList<String>();
		
		SVNRepositories repos = org.tigris.subversion.subclipse.core.SVNProviderPlugin
				.getPlugin().getRepositories();

		ISVNRepositoryLocation[] locations = repos
				.getKnownRepositories(new NullProgressMonitor());

		for (int j = 0; j < locations.length; j++) {
			knownRepos.add(locations[j].getRepositoryRoot().toString());
			
			logger.info("Known svn repository: " + locations[j].getLocation());
		}
		
		return knownRepos;
	}

	@Override
	public void startParserJob(long miliseconds, long startRevision) throws CoreException {
		job = new SubclipseMetadataUpdaterJob();
		job.setStartRevision(startRevision);
		job.setInterval(miliseconds);
		job.schedule();
	}

	@Override
	public void changeParserJobRescheduleTime(long miliseconds)
			throws CoreException {
		if (job != null) {
			job.setInterval(miliseconds);
			job.cancel();
			job.schedule();
			
		}
		else {
			new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
			"No job started!"));
		}
	}

	/**
	 * @param markedSVNRepositories the markedSVNRepositories to set
	 */
	public void setMarkedSVNRepositories(List<String> markedSVNRepositories) {
		if (job != null) {
			job.setMarkedSVNRepositories(markedSVNRepositories);
			job.cancel();
			job.schedule();
			
		}
		else {
			new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
			"No job started!"));
		}
	}

	@Override
	public void setStartRevision(long startRevision) {
		if (job != null) {
			job.setStartRevision(startRevision);
			job.cancel();
			job.schedule();
			
		}
		else {
			new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
			"No job started!"));
		}
	}

}
