package org.deploymentobjects.core.domain.model.environment.provisioning;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.Template;
import org.deploymentobjects.core.VelocityTemplate;
import org.deploymentobjects.core.domain.model.configuration.ConfigurationManagement;
import org.deploymentobjects.core.domain.model.configuration.Storage;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentEvent;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.BlockingEventStep;
import org.deploymentobjects.core.domain.shared.DomainSubscriber;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.DomainEvent.EventType;

public class KickstartTemplateService implements
		DomainSubscriber<EnvironmentEvent> {
	private String baseDir;
	private Storage storage;
	private ConfigurationManagement configMgt;
	private EventPublisher publisher;

	public enum KickstartEvent implements EventType {
		KICKSTART_REQUESTED, KICKSTART_WRITE_FAILED, KICKSTART_WRITTEN;
	}

	public static KickstartTemplateService factory(EventPublisher publisher,
			Environment environment, String baseDir, Storage storage,
			ConfigurationManagement cm) {
		KickstartTemplateService service = new KickstartTemplateService(
				publisher, baseDir, storage, cm);
		EnvironmentEvent event = new EnvironmentEvent.Builder(
				KickstartEvent.KICKSTART_REQUESTED, environment).build();
		publisher.addSubscriber(service, event);
		return service;
	}

	private KickstartTemplateService(EventPublisher publisher, String baseDir,
			Storage storage, ConfigurationManagement cm) {
		
		// TODO currently kickstart can only write files locally. You'd need to
		// run this on that machine.  Once the Storage interface is refactored,
		// this should work fine

		this.baseDir = baseDir;
		this.storage = storage;
		configMgt = cm;
		this.publisher = publisher;
	}

	@Override
	public void handle(EnvironmentEvent event) {
		if (event.type == KickstartEvent.KICKSTART_REQUESTED) {
			Map<String, Object> vars = new HashMap<String, Object>();
			vars.put("hostname", event.getHost().getHostname());
			vars.put("ksip", storage.getIp());
			vars.put("ksmount", storage.getMountPoint());
			vars.put("configmgt-repos", configMgt.getKickstartYumRepos());
			vars.put("configmgt-packages", configMgt.getKickstartPackages());
			vars.put("configmgt-post", configMgt.getKickstartPost());

			Template template = new VelocityTemplate();
			String output = template.encode("templates/clients/kickstart.tmpl",
					vars);
			File kickstartFile = new File(baseDir + File.separator + event.getHost().getHostname()
					+ ".ks");
			try {
				FileUtils.writeStringToFile(kickstartFile, output);
			} catch (IOException e) {
				// TODO add exception text
				EnvironmentEvent done = new EnvironmentEvent.Builder(
						KickstartEvent.KICKSTART_WRITE_FAILED,
						event.environment).build();
				publisher.publish(done);
				return;
			}
			// TODO consider adding to version control. But this may be wasted
			// if users Cobbler or maybe OpenStack, so that would require an
			// integration
			EnvironmentEvent done = new EnvironmentEvent.Builder(
					KickstartEvent.KICKSTART_WRITTEN, event.environment)
					.build();
			publisher.publish(done);
		}
	}
	
 	public BlockingEventStep buildStepFor(Environment environment, Host host) {

		EnvironmentEvent eventToSend = new EnvironmentEvent.Builder(KickstartEvent.KICKSTART_REQUESTED,environment).withHost(host).build();
		// TODO address kickstart failure conditions.  BlockingEventStep
		EnvironmentEvent waitingFor = new EnvironmentEvent.Builder(KickstartEvent.KICKSTART_WRITTEN,environment).withHost(host).build();

		BlockingEventStep step = BlockingEventStep.factory(publisher,
				eventToSend, waitingFor);
		return step;

 	}

}
