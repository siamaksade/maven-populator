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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.maven.settings._1_0.ObjectFactory;
import org.apache.maven.settings._1_0.Proxy;
import org.apache.maven.settings._1_0.Server;
import org.apache.maven.settings._1_0.Settings;
import org.apache.maven.settings._1_0.Settings.Proxies;
import org.apache.maven.settings._1_0.Settings.Servers;

public class SettingsGenerator {
	private static final String SETTINGS_EXT = ".xml";

	private static final String SETTINGS_PREFIX = "settings";

	public static final String SERVER_ID = "jboss-enterprise";
	
	private String username;
	private String password;
	
	private List<ProxyConfig> proxies = new ArrayList<ProxyConfig>();

	public SettingsGenerator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public List<ProxyConfig> getProxies() {
		return proxies;
	}

	public File generate() throws IOException {
		try {
			final JAXBContext context = JAXBContext.newInstance("org.apache.maven.settings._1_0");
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			
			final File settingsFile = File.createTempFile(SETTINGS_PREFIX, SETTINGS_EXT);
			final BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(settingsFile));
			
			final ObjectFactory factory = new ObjectFactory();
			final Settings settings = createSettings();
			marshaller.marshal(factory.createSettings(settings), os);
			return settingsFile;
		} catch (Exception e) {
			throw new IllegalStateException("Failed to generate settings.xml", e);
		}
	}

	private Settings createSettings() {
		final Settings settings = new Settings();
		
		for (final ProxyConfig proxyConfig : proxies) {
			final Proxy proxy = new Proxy(); 
			proxy.setId(proxyConfig.getId());
			proxy.setActive(proxyConfig.isActive());
			proxy.setProtocol(proxyConfig.getProtocol());
			proxy.setHost(proxyConfig.getHost());
			proxy.setPort(proxyConfig.getPort());
			proxy.setUsername(proxyConfig.getUsername());
			proxy.setPassword(proxyConfig.getPassword());
			proxy.setNonProxyHosts(proxyConfig.getNonProxyHosts());
			
			settings.setProxies(new Proxies());
			settings.getProxies().getProxy().add(proxy);
		}
		
		final Server server = new Server(); 
		server.setId(SERVER_ID);
		server.setUsername(username);
		server.setPassword(password);
		settings.setServers(new Servers());
		settings.getServers().getServer().add(server);
		return settings;
	}

	public static class ProxyConfig {
		private boolean active;
		private String protocol;
		private String host;
		private int port;
		private String username;
		private String password;
		private String nonProxyHosts;
		private String id;

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getNonProxyHosts() {
			return nonProxyHosts;
		}

		public void setNonProxyHosts(String nonProxyHosts) {
			this.nonProxyHosts = nonProxyHosts;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
}
