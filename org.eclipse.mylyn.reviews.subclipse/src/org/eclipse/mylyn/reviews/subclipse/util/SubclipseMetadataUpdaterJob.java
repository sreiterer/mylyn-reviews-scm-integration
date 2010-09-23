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

package org.eclipse.mylyn.reviews.subclipse.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.reviews.subclipse.SVNAdapter;

/** This job continuously updates the stored task-changeset mappings
 * which are stored in an internal data structure. 
 * 
 * @author Stefan Reiterer
 *
 */
public class SubclipseMetadataUpdaterJob extends Job {
	private static Logger logger = Logger.getLogger(Parser.class.getName());
	private long interval = 600000;
	private List<String> markedSVNRepositories = new ArrayList<String>();
	
	private Long startRevision = 1L;

	/**
	 * @return the markedSVNRepositories
	 */
	public List<String> getMarkedSVNRepositories() {
		return markedSVNRepositories;
	}

	/**
	 * @param markedSVNRepositories the markedSVNRepositories to set
	 */
	public void setMarkedSVNRepositories(List<String> markedSVNRepositories) {
		this.markedSVNRepositories = markedSVNRepositories;
	}


	/**
	 * @return the startRevision
	 */
	public Long getStartRevision() {
		return startRevision;
	}

	/**
	 * @param startRevision the startRevision to set
	 */
	public void setStartRevision(Long startRevision) {
		this.startRevision = startRevision;
	}

	public SubclipseMetadataUpdaterJob() {
		super("Mylyn Reviews Subversive Metadata Updater");
	}
	
	public void setInterval(long miliseconds) {
		this.interval = miliseconds;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			SVNAdapter adapter = new SVNAdapter();
		
			for (String repo : markedSVNRepositories) {
				logger.info("# parse commit-messages for repository: " + repo);
				try {
					Parser.setStartrevision(startRevision);
					Parser.parseCommitMessages(monitor, repo);
				} catch (JAXBException e) {
					logger.info("# cannot parse Commit Messages!");
					e.printStackTrace();
				}
			}

			return Status.OK_STATUS;
		} finally {
			reschedule();
		}
		
	}
	
	private void reschedule() {
		logger.info("# reschedule parse task ... " + (interval) + " sec");
		// reschedule for every minute
		this.schedule(interval);
	}

}
