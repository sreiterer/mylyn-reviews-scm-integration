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

package org.eclipse.mylyn.reviews.core.scm;

import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.reviews.core.scm.serialization.model.File;

/** This interface represents a changeset of the underlying scm system.
 * 
 * @author Stefan Reiterer
 *
 */
public interface IChangeset {
	
	/** Get the log message which is associated with the changeset.
	 * 
	 * @return Log message
	 */
	public String getMessage();
	
	/** Set the log message which is associated with the changeset.
	 * 
	 * @param message Log message
	 */
	public void setMessage(String message);
	
	/** Get the revision of the changeset. The revision is represented
	 * as String to support the different revision representations of the
	 * different scm systems.
	 * 
	 * @return Content identifier
	 */
	public String getRevision();
	
	/** Get the revision of the changeset. The revision is represented
	 * as String to support the different revision representations of the
	 * different scm systems.
	 * 
	 *  @param revision Content identifier
	 */
	public void setRevision(String revision);
	
	/** Get the list of files which are associated with the changeset.
	 * 
	 * @return List of files.
	 */
	public List<File> getFiles();
	
	/** Set the list fo files which are associated with the changeset.
	 * 
	 * @param files List of files.
	 */
	public void setFiles(List<File> files);
	
	public String getAutor();
	public Date getDate();
	public String getProjectUrl();
}
