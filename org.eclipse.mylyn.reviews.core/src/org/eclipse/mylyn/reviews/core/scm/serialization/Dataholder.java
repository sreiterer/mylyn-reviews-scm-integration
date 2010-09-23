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

package org.eclipse.mylyn.reviews.core.scm.serialization;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.mylyn.reviews.core.scm.serialization.model.Mappings;

public class Dataholder {
	private static Logger logger = Logger.getLogger(Dataholder.class.getName());

	public static final String filename = "Map.xml";

	// hide the file
	public static final String prefix = ".TCMappings_";

	/**
	 * This method is used to deserialize the datastructure which contains the
	 * mappings from tasks to changesets. The datastructure is stored in an
	 * xml-file.
	 * 
	 * @param project
	 *            Load the mappings for this project.
	 * @return Deserialized datastructure. Null, if no datastructure was
	 *         persisted.
	 */
	public static Mappings loadMappings(String repository) {
		logger.info("[DATAHOLDER] load mappings from file "
				+ new File(getMappingFilePath(repository)).getAbsolutePath());

		logger.info("project path: " + getMappingFilePath(repository));
		Mappings mappings = new Mappings();

		try {
			JAXBContext context = JAXBContext.newInstance(Mappings.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			// note: setting schema to null will turn validator off
			unmarshaller.setSchema(null);
			mappings = (Mappings) (unmarshaller.unmarshal(new File(getMappingFilePath(repository))));

		} catch (Exception e) {
			return null;
		}

		return mappings;
	}

	private static String getMappingFilePath(String repositoryLocation) {
		return prefix + repositoryLocation.replace("/", "_").replace(":", "_") + "_" + filename;
	}

	/**
	 * This method is used to persist the datastructure which contains the
	 * mappings from tasks to changesets.
	 * 
	 * @param Save
	 *            the mappings for this project.
	 * @param mappings
	 *            Datastructure to persist.
	 * @throws JAXBException
	 */
	public static void saveMappings(Mappings mappings, String repository)
			throws JAXBException {
		logger.info("[DATAHOLDER] save mappings to file "
				+ new File(getMappingFilePath(repository)).getAbsolutePath());
		JAXBContext context = JAXBContext.newInstance(Mappings.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(mappings, new File(getMappingFilePath(repository)));
	}
	
	public static boolean deleteMappingFile(String filename) {
		File fileToDelete = new File(filename);
		
		return fileToDelete.delete();
	}
	
	public static void copyFile(File from, File to) throws IOException {
	    FileReader in = new FileReader(from);
	    FileWriter out = new FileWriter(to);
	    int c;

	    while ((c = in.read()) != -1)
	      out.write(c);

	    in.close();
	    out.close();
	}
	
	public static List<String> getAllMappingFiles() {
		List<String> mappingFiles = new ArrayList<String>();
		File dir = new File(".");
		
		for (String file : dir.list()) {
			if (file.startsWith(prefix)) {
				mappingFiles.add(file);
			}
		}
		
		return mappingFiles;
	}
}
