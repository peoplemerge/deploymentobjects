package org.deploymentobjects.core.domain.shared;

import org.deploymentobjects.core.infrastructure.persistence.InMemoryEventStore;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class DomainPublisherTest {

	private EventStore eventStore = new InMemoryEventStore();

	private class TestEvent extends Event<Publisher, TestSubscriber, TestEvent> {
	}

	private class TestSubscriber implements
			Subscriber<Publisher, TestSubscriber, TestEvent> {
		public boolean handled = false;

		public void handle(TestEvent a) {
			handled = true;
		}

	}

	@Test
	@Ignore
	public void addSubscriber() {
		Publisher<Publisher, TestSubscriber, TestEvent> publisher = new Publisher<Publisher, TestSubscriber, TestEvent>();

		// Add a subscriber
		TestSubscriber subscriber = new TestSubscriber();
		Assert.assertTrue(subscriber.handled == false);
		publisher.addSubscriber(subscriber, new TestEvent());
		// Create an event
		TestEvent testEvent = new TestEvent();
		// fire the event
		publisher.publish(testEvent);
		Assert.assertTrue(subscriber.handled == true);
	}

	private class AnotherTestEvent extends
			Event<Publisher, AnotherTestSubscriber, AnotherTestEvent> {
	}

	private class AnotherTestSubscriber implements
			Subscriber<Publisher, AnotherTestSubscriber, AnotherTestEvent> {
		public boolean handled = false;

		public void handle(AnotherTestEvent a) {
			handled = true;
		}

	}

	@Test
	@Ignore
	// This test is preserved for reference purposes during development. Do no
	// use!
	public void eventsNonGeneric() {
		Publisher publisher = new Publisher();
		TestSubscriber subscriber = new TestSubscriber();
		publisher.addSubscriber(subscriber, new TestEvent());
		AnotherTestSubscriber anotherSubscriber = new AnotherTestSubscriber();
		publisher.addSubscriber(anotherSubscriber, new AnotherTestEvent());

		TestEvent testEvent = new TestEvent();
		// fire the first event
		publisher.publish(testEvent);
		Assert.assertTrue(subscriber.handled == true);
		Assert.assertTrue(anotherSubscriber.handled == false);
		AnotherTestEvent anotherEvent = new AnotherTestEvent();
		publisher.publish(anotherEvent);
		Assert.assertTrue(anotherSubscriber.handled == true);

	}

	private class GenericSubscriber implements DomainSubscriber<GenericEvent> {
		public boolean handled = false;

		public void handle(GenericEvent a) {
			handled = true;

		}
	}

	private class GenericEvent extends DomainEvent<GenericEvent> {
		public boolean sameEventAs(GenericEvent other) {
			return true;
		}
	}

	private class AnotherGenericSubscriber implements
			DomainSubscriber<AnotherGenericEvent> {
		public boolean handled = false;

		public void handle(AnotherGenericEvent a) {
			handled = true;

		}
	}

	private class AnotherGenericEvent extends DomainEvent<AnotherGenericEvent> {
		public boolean sameEventAs(AnotherGenericEvent other) {
			return true;
		}
	}

	@Test
	public void generic() {
		EventPublisher publisher = new EventPublisher(eventStore);
		GenericSubscriber subscriber = new GenericSubscriber();
		publisher.addSubscriber(subscriber, new GenericEvent());
		AnotherGenericSubscriber anotherSubscriber = new AnotherGenericSubscriber();
		publisher.addSubscriber(anotherSubscriber, new AnotherGenericEvent());

		GenericEvent testEvent = new GenericEvent();
		// fire the first event
		publisher.publish(testEvent);
		Assert.assertTrue(subscriber.handled == true);
		Assert.assertTrue(anotherSubscriber.handled == false);
		AnotherGenericEvent anotherEvent = new AnotherGenericEvent();
		publisher.publish(anotherEvent);
		Assert.assertTrue(anotherSubscriber.handled == true);
	}

	private class GenericFirstTypeSubscriber implements
			DomainSubscriber<GenericEvent> {
		public boolean handled = false;

		public void handle(GenericEvent a) {
			handled = true;

		}
	}

	@Test
	public void multipleSubscribersToEventType() {
		EventPublisher publisher = new EventPublisher(eventStore);
		GenericSubscriber subscriber = new GenericSubscriber();
		publisher.addSubscriber(subscriber, new GenericEvent());
		GenericFirstTypeSubscriber anotherSubscriber = new GenericFirstTypeSubscriber();
		publisher.addSubscriber(anotherSubscriber, new GenericEvent());

		GenericEvent testEvent = new GenericEvent();
		// fire the first event
		publisher.publish(testEvent);
		Assert.assertTrue(subscriber.handled == true);
		Assert.assertTrue(anotherSubscriber.handled == true);
	}

	// TODO Fix the generic type: this appears to be written correctly
	// but the user of the API makes a natural mistake
	@Test
	public void IncorrectEventType() {
		EventPublisher publisher = new EventPublisher(eventStore);
		GenericSubscriber subscriber = new GenericSubscriber();
		publisher.addSubscriber(subscriber, new GenericEvent());
		AnotherGenericSubscriber anotherSubscriber = new AnotherGenericSubscriber();
		// the problem here is that AnotherGenericSubscriber is typed to handle
		// AnotherGenericEvent!
		publisher.addSubscriber(anotherSubscriber, new GenericEvent());

		GenericEvent testEvent = new GenericEvent();
		try {
			publisher.publish(testEvent);
		} catch (RuntimeException e) {
			System.out
					.println("This path throws an exception which is possible if the user made a "
							+ "programming error. \n Clean up generics implementation to get rid of the possibility.");
			return;
		}
		Assert
				.fail("You may have fixed generics.  Congrats!  Now fix this test.  I hope you"
						+ "didn't cover up the error to surprise users!");
	}

}
