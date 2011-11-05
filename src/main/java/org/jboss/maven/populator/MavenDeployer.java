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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.log4j.Logger;

public class MavenDeployer {
	private static final String MVN_DEPLOY = "mvn -q -s ${settingsUrl} deploy:deploy-file "
			+ "-DartifactId=${artifactId} -DgroupId=${groupId} -Dversion=${version} "
			+ "-Dfile=${artifactPath} -Durl=${repositoryUrl} -Dpackaging=jar -DrepositoryId=${repositoryId}";
	
	private Logger logger = Logger.getLogger(MavenDeployer.class);
	
	private String repositoryId;
	private String repositoryUrl;
	private String settingsPath;

	public MavenDeployer(final String repositoryId, final String repositoryUrl, final String settingsPath) {
		this.repositoryId = repositoryId;
		this.repositoryUrl = repositoryUrl;
		this.settingsPath = settingsPath;
	}
	
	public void deploy(final String groupId, final String artifactId, final String version, final String artifactPath) throws IOException {
		logger.info("Deploying " + artifactPath);
		
		final Map<String,String> params = new HashMap<String, String>();
		params.put("settingsUrl", settingsPath);
		params.put("artifactId", artifactId);
		params.put("groupId", groupId);
		params.put("version", version);
		params.put("artifactPath", artifactPath);
		params.put("repositoryUrl", repositoryUrl);
		params.put("repositoryId", repositoryId);

		final CommandLine deployCmd = CommandLine.parse(MVN_DEPLOY);
		deployCmd.setSubstitutionMap(params);
		
		final DefaultExecutor executor = new DefaultExecutor();
		executor.execute(deployCmd);
	}
}
