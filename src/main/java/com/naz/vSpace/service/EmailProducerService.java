package com.naz.vSpace.service;

import com.naz.vSpace.util.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.email}")
    private String emailQueue;

    public void sendEmailMessage(String to,String subject, String text){
        EmailMessage emailMessage = new EmailMessage(to, subject, text);
        rabbitTemplate.convertAndSend(emailQueue, emailMessage);
    }
}
