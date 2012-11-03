package org.deploymentobjects.core.infrastructure.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.Template;
import org.deploymentobjects.core.VelocityTemplate;
import org.deploymentobjects.core.domain.model.configuration.ConfigurationManagement;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.DispatchableStep;
import org.deploymentobjects.core.domain.model.execution.Script;
import org.deploymentobjects.core.domain.shared.EventPublisher;

public class Puppet implements ConfigurationManagement {

	private Host puppetmaster;
	private EventPublisher publisher;
	private Dispatchable dispatch;

	public Puppet(EventPublisher publisher, Host puppetmaster, Dispatchable dispatch) {
		this.puppetmaster = puppetmaster;
		this.publisher = publisher;
		this.dispatch = dispatch;
	}

	@Override
	public String getKickstartPackages() {
		return "puppet";
	}

	@Override
	public String getKickstartPost() {
		return "puppet resource ssh_authorized_key 'god@heaven' ensure=present type=ssh-rsa user=root key='AAAAB3NzaC1yc2EAAAABIwAAAQEAxJNexZQyrppEaec9s+sFA14MuD2cLmLK90kcVoMiC31cnbB/oGTdPACnBRluvwaI6D6gJ9kUlqf3qka9GJqFUY6k7TFiuCOPpMrxZV5Eyk+p+C2FtW+Q/qwMCZecnYnmyRzXaYe2IZ1uOrpPdbty0GHOleDFqHbpzWgyjMVpPTjOO21js/jm1dPxCn1q8FpYmb0DDqEUHBXQjlGsB4eHwDiRNWfARIXV0KIZqp2bvdRqy7yty+21kPLIL6wgNN/Q4nb/swFoNiTO0UivSmDPh62FAzfQWObONPqjLGEpuBkrPY1yrIlU+KEqsD11ZR0f5M6wTFGQi9goQss3z3bgfQ=='  &>>/root/puppet-out\n"
				+ "puppet resource host " + puppetmaster.getHostname() + "."
				+ puppetmaster.getDomainname() + " ensure=present ip="
				+ puppetmaster.getIp() + " host_aliases="
				+ puppetmaster.getHostname() + " &>>/root/puppet-out\n" + "\n"
				+ "cat >> /etc/puppet/puppet.conf <<EOF\n" + "    server = "
				+ puppetmaster.getHostname() + "."
				+ puppetmaster.getDomainname() + "\n" + "EOF\n" + "\n"
				+ "puppet agent --test";
	}

	@Override
	public String getKickstartYumRepos() {
		return "repo --name=\"puppetlabs\"  --baseurl=http://yum.puppetlabs.com/el/6/products/i386 --cost=100\n"
				+ "repo --name=\"puppetlabs-deps\"  --baseurl=http://yum.puppetlabs.com/el/6/dependencies/i386 --cost=100";
	}

	public DispatchableStep postCompleteStep(Host node) {
		String body = "puppet cert sign " + node.getHostname() + "."
				+ node.getDomainname();
		Script postComplete = new Script(body);
		return DispatchableStep.factory(publisher, postComplete, puppetmaster,dispatch);
	}

	// TODO write test
	public DispatchableStep newEnvironment(EnvironmentRepository repo) {
		List<Environment> envs = repo.getAll();

		File sitePpFile = new File("/mnt/media/software/puppet/site.pp");
		String sitePp = getSitePp(envs);
		try {
			FileUtils.writeStringToFile(sitePpFile, sitePp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File hostsPpFile = new File("/mnt/media/software/puppet/hosts.pp");
		String hostsPp = getHostsPp(envs);
		try {
			FileUtils.writeStringToFile(hostsPpFile, hostsPp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String body = "cp /mnt/media/software/puppet/site.pp /etc/puppet/manifests && "
				+ "cp /mnt/media/software/puppet/hosts.pp /etc/puppet/manifests";
		Script newEnv = new Script(body);
		return DispatchableStep.factory(publisher, newEnv, puppetmaster,dispatch);
	}

	String getHostsPp(List<Environment> envs) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("allenvironments", envs);

		Template template = new VelocityTemplate();
		String output = template.encode("templates/clients/puppet-hosts.tmpl",
				vars);
		return output;
	}

	String getSitePp(List<Environment> envs) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("allenvironments", envs);

		Template template = new VelocityTemplate();
		String output = template.encode("templates/clients/puppet-site.tmpl",
				vars);
		return output;
	}

	@Override
	public DispatchableStep nodeProvisioned(Host node) {
		String body = "puppet agent --test\n"
				+ "if [[ $? -eq 2 ]]; then exit 0;\n"
				+ "fi\n" 
				+ "exit 1\n";
		Script newEnv = new Script(body);
		return DispatchableStep.factory(publisher, newEnv, node, dispatch);

	}

}
