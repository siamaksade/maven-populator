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

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class App {
	private static final Logger logger = Logger.getLogger(App.class);

	public static void main(String[] args) throws IOException {
		final CommandLine cmd = parseArguments(args);

		if (cmd != null) {
			final String scanDir = cmd.getOptionValue("d");
			final String repoUrl = cmd.getOptionValue("m");
			final String groupId = cmd.getOptionValue("jm");
			final String version = cmd.getOptionValue("jv");
			final String username = cmd.getOptionValue("u");
			final String password = cmd.getOptionValue("p");

			final Collection<File> files = JarCollector.listJarFiles(scanDir);
			final File settings = new SettingsGenerator(username, password).generate();
			final MavenDeployer populator = new MavenDeployer(SettingsGenerator.SERVER_ID, repoUrl, settings.getAbsolutePath());

			for (File file : files) {
				populator.deploy(groupId, FilenameUtils.getBaseName(file.getName()), version, file.getAbsolutePath());
			}

			logger.info("Deploy completed.");
		}
	}

	private static CommandLine parseArguments(String[] args) {
		final Options options = createOptions();

		try {
			return new PosixParser().parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			System.out.println();
			new HelpFormatter().printHelp("maven-populator", options, true);
		}

		return null;
	}

	private static Options createOptions() {
		final Options options = new Options();
		addOption(options, "d", "dir", "path", "target directory for scaning");
		addOption(options, "m", "repo-url", "url", "maven repository url");
		addOption(options, "jm", "jar-group-id", "groupId", "groupId for all jar files");
		addOption(options, "jv", "jar-version", "version", "version for all jar files");
		addOption(options, "u", "username", "username", "maven deployer username");
		addOption(options, "p", "password", "password", "maven deployer password");
		return options;
	}

	private static void addOption(final Options options, final String opt, final String longOpt, final String argument, final String desc) {
		final Option option = new Option(opt, longOpt, true, desc);
		option.setRequired(true);
		option.setArgName(argument);
		options.addOption(option);
	}
}
