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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.scm.serialization.Dataholder;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Changeset;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Mappings;

/** This class is used for parsing the log messages of the scm system to 
 * create task-changeset associations. 
 * 
 * @author Stefan Reiterer
 *
 */
public class Parser {
	private static Logger logger = Logger.getLogger(Parser.class.getName());
	
	public static String COMMIT_PATTERN_BUG_LINK = "^.*(?:bugs?|issues?|reports?)\\s+(?:#(?:\\d+)[,\\.\\s]*)+\\d.*$.*";
	public static String COMMIT_PATTERN_BUG_FIXED1 = "^.*(?:fixe?d?s?|resolved?s?)+\\s+(?:#(?:\\d+)[,\\.\\s]*)+\\d.*$.*";
	public static String COMMIT_PATTERN_BUG_FIXED2 = "^.*#(\\d+).*$.*";
	public static String NUMBER_PATTERN = "#?(\\d+)";
	
	public static Long startrevision = 1l;
	
	/** Parse all commit-messages to establish a mapping from tasks to
	 * changesets. Internal data-structure will be serialized into a
	 * XML-document.
	 * 
	 * @param monitor Progress-monitor for this operation.
	 * @param project Get the changesets for this project.
	 * @throws JAXBException 
	 */
	public static synchronized void parseCommitMessages(IProgressMonitor monitor, String repositoryLocation) throws JAXBException {
		Mappings mappings = Dataholder.loadMappings(repositoryLocation);
		
		if (mappings != null) {
			// datastructure already exists -> update datastructure
			Long lastRevision = Long.parseLong(mappings.getLastRevision());
			
			logger.info("[PARSER] parse revisions beginning from revision " + lastRevision);
			Long newLastRevision = SVNUtil.getLastRevision(monitor, repositoryLocation);
			List<Changeset> newChangesets = SVNUtil.getChangesets(monitor, 
					repositoryLocation, 
					lastRevision.toString(),
					null, 
					""); // TODO add the project name here
			
			logger.info("[PARSER] got " + newChangesets.size() + " changesets!");
			// insert task-changeset mappings into the existing datastructure
			mappings = insertMappings(mappings, newChangesets);
			
			mappings.setLastRevision(newLastRevision.toString());
			
			Dataholder.saveMappings(mappings, repositoryLocation);
		}
		else {
			// No datastructure was persisted for this project ->
			// All commit-messages for this project need to be parsed.
			logger.info("[PARSER] There is no datastructure for this project.");
			
			Long newLastRevision = SVNUtil.getLastRevision(monitor, repositoryLocation);
			List<Changeset> newChangesets = SVNUtil.getChangesets(monitor, 
					repositoryLocation, 
					startrevision.toString(), 
					null, 
					""); // TODO add the project name here
			
			// insert task-changeset mappings into a new datastructure
			mappings = new Mappings();
			mappings = insertMappings(mappings, newChangesets);
			
			logger.info("[PARSER] Set new last revision to: " + newLastRevision.toString());
			mappings.setLastRevision(newLastRevision.toString());
			
			Dataholder.saveMappings(mappings, repositoryLocation);
		}
	}
	
	
	private static Mappings insertMappings(Mappings mappings, List<Changeset> changesets) {
		Pattern pattern_link = Pattern.compile(Parser.COMMIT_PATTERN_BUG_LINK, Pattern.DOTALL);
		Pattern pattern_fixed1 = Pattern.compile(Parser.COMMIT_PATTERN_BUG_FIXED1, Pattern.DOTALL);
		Pattern pattern_fixed2 = Pattern.compile(Parser.COMMIT_PATTERN_BUG_FIXED2, Pattern.DOTALL);
		Pattern number_pattern = Pattern.compile(Parser.NUMBER_PATTERN, Pattern.DOTALL);
		
		logger.info("INSERT NEW CHANGESETS: " + changesets.size());
		
		// parse the commit-message of each changeset
		for (Changeset change : changesets) {
			Matcher matcher_link = pattern_link.matcher(change.getMessage());
			Matcher matcher_fixed1 = pattern_fixed1.matcher(change.getMessage());
			Matcher matcher_fixed2 = pattern_fixed2.matcher(change.getMessage());
			if (matcher_link.matches() || matcher_fixed1.matches() 
					|| matcher_fixed2.matches()) {
			
				Matcher number_matcher = number_pattern.matcher(change.getMessage());
				// if this changeset is associated with a task
				if (number_matcher.find()) {
					logger.info("changeset " + change.getRevision()
							+ " is associated with task "
							+ change.getMessage().subSequence(number_matcher.start()+1, 
									number_matcher.end()));
					
					try {
						Long taskId = Long.parseLong((String) change.getMessage()
								.subSequence(number_matcher.start()+1, number_matcher.end()));
						
						HashMap<String, List<String>> mapList = (HashMap<String, List<String>>) mappings.getTaskMappings();
						List<String> cs = mappings.getTaskMappings().get(taskId.toString());
						HashMap<String, Changeset> csMap = (HashMap<String, Changeset>) mappings.getChangesets();
						if (cs == null)
							cs = new ArrayList<String>();
						
						if (!cs.contains(change.getRevision()))
							cs.add(change.getRevision());
						if (!csMap.containsKey(change.getRevision()))
							csMap.put(change.getRevision(), change);
						
						mappings.setChangesets(csMap);
						mapList.put(taskId.toString(), cs);
						mappings.setTaskMappings(mapList);
					} catch (Exception e) {
						logger.info("Not possible to parse Tasknumber!");
					}
				}
			}
		}
		
		return mappings;
	}


	/**
	 * @return the startrevision
	 */
	public static Long getStartrevision() {
		return startrevision;
	}

	/**
	 * @param startrevision the startrevision to set
	 */
	public static void setStartrevision(Long startrevision) {
		Parser.startrevision = startrevision;
	}
	
	
}
