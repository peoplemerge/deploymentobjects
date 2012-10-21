package org.deploymentobjects.core.domain.model.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.Template;
import org.deploymentobjects.core.VelocityTemplate;
import org.deploymentobjects.core.domain.model.environment.Node;

//TODO This class kind of breaks the object model...
public class KickstartServer extends Node {
	String hostname;
	String baseDir;
	Storage storage;
	ConfigurationManagement configMgt;

	public KickstartServer(String baseDir, Storage storage,
			ConfigurationManagement cm) {
		// TODO currently kickstart can only write files locally. You'd need to
		// run this on that machine.
		// this.hostname = hostname;
		super("localhost");
		this.baseDir = baseDir;
		this.storage = storage;
		configMgt = cm;
	}

	public void writeKickstartFile(String hostname) throws Exception {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("hostname", hostname);
		vars.put("ksip", storage.getIp());
		vars.put("ksmount", storage.getMountPoint());
		vars.put("configmgt-repos", configMgt.getKickstartYumRepos());
		vars.put("configmgt-packages", configMgt.getKickstartPackages());
		vars.put("configmgt-post", configMgt.getKickstartPost());

		Template template = new VelocityTemplate();
		String output = template.encode("templates/clients/kickstart.tmpl",
				vars);
		File kickstartFile = new File(baseDir + File.separator + hostname
				+ ".ks");
		FileUtils.writeStringToFile(kickstartFile, output);

		// TODO consider adding to version control. But this may be wasted if
		// users Cobbler or maybe OpenStack, so that would require an
		// integration

	}

}
