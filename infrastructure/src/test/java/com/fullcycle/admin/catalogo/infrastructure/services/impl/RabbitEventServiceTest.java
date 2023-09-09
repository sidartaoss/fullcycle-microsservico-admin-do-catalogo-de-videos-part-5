package com.fullcycle.admin.catalogo.infrastructure.services.impl;

import com.fullcycle.admin.catalogo.AmqpTest;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaCreated;
import com.fullcycle.admin.catalogo.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.fullcycle.admin.catalogo.infrastructure.configuration.json.Json;
import com.fullcycle.admin.catalogo.infrastructure.services.EventService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AmqpTest
class RabbitEventServiceTest {

    private static final String LISTENER = "video.created";

    @Autowired
    @VideoCreatedQueue
    private EventService publisher;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Test
    void shouldSendMessage  () throws InterruptedException {
        // Given
        final var notification = new VideoMediaCreated("resource", "filepath");
        final var expectedMessage = Json.writeValueAsString(notification);

        // When
        this.publisher.send(notification);

        // Then
        final var invocationData =
                harness.getNextInvocationDataFor(LISTENER, 1, TimeUnit.SECONDS);

        assertNotNull(invocationData);
        assertNotNull(invocationData.getArguments());

        final var actualMessage = (String) invocationData.getArguments()[0];
        assertEquals(expectedMessage, actualMessage);
    }

    @Component
    static class VideoCreatedNewsListener {

        @RabbitListener(id = LISTENER, queues = "${amqp.queues.video-created.routing-key}")
        void onVideoCreated(@Payload String message) {
            System.out.println(message);
        }
    }
}