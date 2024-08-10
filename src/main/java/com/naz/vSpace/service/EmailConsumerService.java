package com.naz.vSpace.service;

import com.naz.vSpace.service.serviceImplementation.EmailImplementation;
import com.naz.vSpace.util.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumerService {
    private final EmailImplementation emailImplementation;

    @Value("${rabbitmq.queue.email}")
    private String emailQueue;

    @RabbitListener(queues = "${rabbitmq.queue.email}")
    public void receiveMessage(EmailMessage emailMessage){
        emailImplementation.sendMail(emailMessage);
    }
}
