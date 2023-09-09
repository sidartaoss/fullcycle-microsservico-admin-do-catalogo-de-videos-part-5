package com.fullcycle.admin.catalogo.domain;

import com.fullcycle.admin.catalogo.domain.event.DomainEvent;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class EntityTest extends UnitTest{

    public static class DummyEvent implements DomainEvent {

        @Override
        public Instant occurredOn() {
            return InstantUtils.now();
        }
    }

    public static class DummyID extends Identifier {

        private final String value;

        public DummyID() {
            this.value = IdUtils.uuid();
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public static class DummyEntity extends Entity<DummyID> {

        public DummyEntity() {
            this(new DummyID(), Collections.emptyList());
        }

        protected DummyEntity(final DummyID dummyID, final List<DomainEvent> domainEvents) {
            super(dummyID, domainEvents);
        }

        @Override
        public void validate(ValidationHandler handler) {

        }
    }

    @DisplayName("Create an entity with valid domain events")
    @Nested
    class CreateEntityWithValidDomainEvents {

        @Test
        void Given_empty_as_events_When_instantiate_Then_should_be_ok() {
            // given
            final var expectedId = new DummyID();
            final var anEmptyListOfDomainEvents = Collections.<DomainEvent>emptyList();

            // when
            final var anEntity = new DummyEntity(expectedId, anEmptyListOfDomainEvents);

            // then
            assertNotNull(anEntity.getDomainEvents());
            assertTrue(anEntity.getDomainEvents().isEmpty());
        }

        @Test
        void Given_empty_domain_events_When_calls_register_event_Then_Should_add_event_to_list() {
            // given
            final var expectedNumberOfEvents = 1;

            final var expectedId = new DummyID();
            final var expectedEvents = Collections.<DomainEvent>emptyList();
            final var anEntity = new DummyEntity(expectedId, expectedEvents);

            // when
            anEntity.registerEvent(new DummyEvent());

            // then
            assertNotNull(anEntity.getDomainEvents());
            assertEquals(expectedNumberOfEvents, anEntity.getDomainEvents().size());
        }

        @Test
        void Given_a_few_domain_events_When_calls_publish_events_Then_Should_call_publisher_and_clear_the_list() {
            // given
            final var expectedNumberOfEvents = 0;
            final var counter = new AtomicInteger(0);
            final var expectedSentEvents = 2;

            final var expectedId = new DummyID();
            final var expectedEvents = Collections.<DomainEvent>emptyList();
            final var anEntity = new DummyEntity(expectedId, expectedEvents);

            anEntity.registerEvent(new DummyEvent());
            anEntity.registerEvent(new DummyEvent());

            assertEquals(2, anEntity.getDomainEvents().size());


            // when
            anEntity.publishDomainEvents(event -> {
                counter.incrementAndGet();
            });

            // then
            assertNotNull(anEntity.getDomainEvents());
            assertEquals(expectedNumberOfEvents, anEntity.getDomainEvents().size());
            assertEquals(expectedSentEvents, counter.get());
        }
    }

    @DisplayName("Create an entity with invalid domain events")
    @Nested
    class CreateEntityWithInvalidDomainEvents {

        @Test
        void Given_domain_events_which_pass_in_constructor_When_instantiate_Then_should_create_a_defensive_clone() {
            // given
            final var expectedId = new DummyID();
            final var events = new ArrayList<DomainEvent>();
            events.add(new DomainEvent() {
                @Override
                public Instant occurredOn() {
                    return null;
                }
            });

            // when
            final var anEntity = new DummyEntity(expectedId, events);

            // then
            assertNotNull(anEntity.getDomainEvents());
            assertEquals(1, anEntity.getDomainEvents().size());
            assertThrows(RuntimeException.class, () -> {
                final var actualEvents = anEntity.getDomainEvents();
                actualEvents.add(new DomainEvent() {
                    @Override
                    public Instant occurredOn() {
                        return null;
                    }
                });
            });
        }
    }
}
