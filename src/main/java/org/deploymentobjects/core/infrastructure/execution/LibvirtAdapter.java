package org.deploymentobjects.core.infrastructure.execution;

import org.deploymentobjects.core.domain.model.execution.ControlsHosts;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;

public class LibvirtAdapter implements ControlsHosts{

	public LibvirtAdapter(String connectString){
		try {
			conn = new Connect(
					connectString,
					false);
		} catch (LibvirtException e) {
			System.out.println("exception caught:" + e);
			System.out.println(e.getError());
		}
	}
	
	Connect conn = null;
	
	public boolean startHost(String vm){
		try {
			Domain testDomain = conn.domainLookupByName(vm);
			return testDomain.create() == 0;
		} catch (LibvirtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean stopHost(String vm){
		try {
			Domain testDomain = conn.domainLookupByName(vm);
			testDomain.destroy();
			return true;
		} catch (LibvirtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean pollForDomainToStop(String vm, int pollMs, int timeoutMs) {
		int expected = 0;
		return poll(vm, pollMs, timeoutMs, expected);
	}
	public boolean pollForDomainToStart(String vm, int pollMs, int timeoutMs) {
		int expected = 1;
		return poll(vm, pollMs, timeoutMs, expected);
	}


	boolean poll(String vm, int pollMs, int timeoutMs, int expected) {
		try {
			Domain testDomain = conn.domainLookupByName(vm);
			long start = System.currentTimeMillis();
			while(start < start + timeoutMs){
				if(testDomain.isActive() == expected){
					return true;
				}
				Thread.sleep(pollMs);
			}
			return false;
		} catch (LibvirtException e) {
			System.out.println("exception caught:" + e);
			System.out.println(e.getError());
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
