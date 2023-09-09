package com.fullcycle.admin.catalogo.infrastructure.amqp;

import com.fullcycle.admin.catalogo.application.video.media.update.UpdateMediaStatusCommand;
import com.fullcycle.admin.catalogo.application.video.media.update.UpdateMediaStatusUseCase;
import com.fullcycle.admin.catalogo.domain.video.MediaStatus;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoEncoderCompleted;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoEncoderError;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoEncoderResult;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class VideoEncoderListener {

    private static final Logger log = LoggerFactory.getLogger(VideoEncoderListener.class);

    static final String LISTENER_ID = "videoEncodedListener";

    private final UpdateMediaStatusUseCase updateMediaStatusUseCase;

    public VideoEncoderListener(
            final UpdateMediaStatusUseCase updateMediaStatusUseCase) {
        this.updateMediaStatusUseCase = Objects.requireNonNull(updateMediaStatusUseCase);
    }

    @RabbitListener(id = LISTENER_ID, queues = "${amqp.queues.video-encoded.queue}")
    public void onVideoEncodedMessage(@Payload final VideoEncoderResult message,
                                      Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        if (message instanceof VideoEncoderCompleted dto) {
            log.info("[message:video.listener.income] [status:completed] [payload:{}]", message);
            final var aCommand =
                    UpdateMediaStatusCommand.with(
                            MediaStatus.COMPLETED,
                            dto.id(),
                            dto.video().resourceId(),
                            dto.video().encodedVideoFolder(),
                            dto.video().filePath());
            this.updateMediaStatusUseCase.execute(aCommand);
            channel.basicAck(tag, false);
        } else if (message instanceof VideoEncoderError) {
            log.error("[message:video.listener.income] [status:error] [payload:{}]", message);
        } else {
            log.error("[message:video.listener.income] [status:unknown] [payload:{}]", message);
        }
    }
}
