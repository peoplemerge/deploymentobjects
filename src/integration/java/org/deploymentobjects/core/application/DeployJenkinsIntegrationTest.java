package org.deploymentobjects.core.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.deploymentobjects.core.application.DeployApplicationCommand;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.model.execution.Step;
import org.deploymentobjects.core.infrastructure.execution.JschDispatch;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperEnvironmentRepository;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.Selenium;

public class DeployJenkinsIntegrationTest {

	String commands = "wget -O /etc/yum.repos.d/jenkins.repo http://pkg.jenkins-ci.org/redhat/jenkins.repo\n"
			+ "rpm --import http://pkg.jenkins-ci.org/redhat/jenkins-ci.org.key\n"
			+ "yum install -y jenkins\n"
			+ "yum install -y java-1.6.0-openjdk java-1.6.0-openjdk-devel\n"
			+ "chkconfig iptables off\n"

			+ "service iptables stop\n"

			+ "service jenkins start\n";

	@Test
	public void testDeployJenkins() throws Exception {

		EnvironmentRepository repo = new ZookeeperEnvironmentRepository(
				new ZookeeperPersistence("ino:2181"));

		DeployApplicationCommand cmd = new DeployApplicationCommand.Builder(
				"jenkins", "rfctr1", repo)
				.addCommandOnNodesByRole(commands, "web").withDispatch(
						new JschDispatch("root")).build();
		ExitCode exit = cmd.execute();
		assertEquals(ExitCode.SUCCESS, exit);
		for (Step step : cmd.getSteps()) {
			for (Host node : step.getHosts()) {
				testSiteUp(node.getHostname());
			}
		}
	}

	public void testSiteUp(String hostname) throws Exception {
		WebDriver driver = new FirefoxDriver();
		String baseUrl = "http://" + hostname + ":8080/";
		Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);

		selenium.open("/");
		// selenium.click("css=img[alt=\"title\"]");
		selenium.waitForPageToLoad("30000");
		if (selenium
				.isTextPresent("Please wait while Jenkins is getting ready to work")) {
			Thread.sleep(30000);

		}
		assertTrue(selenium
				.isTextPresent("Welcome to Jenkins! Please create new jobs to get started."));

		selenium.stop();

	}
}
