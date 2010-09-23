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
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Dataholder {
	private static Logger logger = Logger.getLogger(Dataholder.class.getName());

	public static final String filename = "Map.xml";

	// hide the file
	public static final String prefix = ".UGMappings_";

	/** This method deserializes an xml file into an object.
	 * 
	 * @param filename Path of the xml file
	 * @param clazz Class of the object
	 * @return Deserialzied xml file
	 */
	@SuppressWarnings("unchecked")
	public static Object loadObject(String filename, Class clazz) {
		logger.info("[DATAHOLDER] load objects from file "
				+ new File(filename).getAbsolutePath());

		logger.info("project path: " + filename);
		Object object = new Object();

		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			// note: setting schema to null will turn validator off
			unmarshaller.setSchema(null);
			object = (unmarshaller.unmarshal(new File(filename)));

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return object;
	}

	/** This method serializes the given object into an xml file.
	 * 
	 * @param object Object that should be serialized
	 * @param filename Path of the xml file
	 * @throws JAXBException
	 */
	public static void saveObject(Object object, String filename, Class clazz)
			throws JAXBException {
		logger.info("[DATAHOLDER] save mappings to file "
				+ new File(filename).getAbsolutePath());
		
		JAXBContext context = JAXBContext.newInstance(clazz);
		Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(object, new File(filename));
	}
	
	/** Delete file.
	 * 
	 * @param filename Path of the file that should be deleted.
	 * @return True, if delete was successfull, otherwise false.
	 */
	public static boolean deleteFile(String filename) {
		File fileToDelete = new File(filename);
		
		return fileToDelete.delete();
	}
	
	/** Copy file from one location to another location.
	 * 
	 * @param from Location from which the file should be copied
	 * @param to Target location of the file.
	 * @throws IOException
	 */
	public static void copyFile(File from, File to) throws IOException {
	    FileReader in = new FileReader(from);
	    FileWriter out = new FileWriter(to);
	    int c;

	    while ((c = in.read()) != -1)
	      out.write(c);

	    in.close();
	    out.close();
	}

}
