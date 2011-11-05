/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.maven.populator;

import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.filefilter.FileFilterUtils.trueFileFilter;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;


public class JarCollector {
	private static final WildcardFileFilter JAR_FILE_FILTER = new WildcardFileFilter("*.jar");
	
	private static final Logger logger = Logger.getLogger(JarCollector.class);
	
	public static Collection<File> listJarFiles(final String dir) {
		logger.debug("Scanning " + dir + " for jar files");
		final Collection<File> list = listFiles(new File(dir), JAR_FILE_FILTER, trueFileFilter());
		filterDuplicates(list);
		return list;
	}
	
	private static void filterDuplicates(final Collection<File> list) {
		final List<String> filenames = new LinkedList<String>();
		
		for (final Iterator<File> iterator = list.iterator(); iterator.hasNext();) {
			final File file = iterator.next();
			
			if (filenames.contains(file.getName())) {
				iterator.remove();
			} else {
				filenames.add(file.getName());
			}
		}
	}
}
