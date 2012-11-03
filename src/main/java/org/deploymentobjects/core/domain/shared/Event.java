package org.deploymentobjects.core.domain.shared;

 abstract class Event <S , O extends Subscriber<S, O, A>, A extends Event<S, O, A>> {

}
