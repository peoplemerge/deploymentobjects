package org.deploymentobjects.core.domain.shared;


 interface Subscriber<S, O extends Subscriber<S, O, A>, A extends Event<S, O, A>> {
	//public  <K extends A>void handle(K a);
}