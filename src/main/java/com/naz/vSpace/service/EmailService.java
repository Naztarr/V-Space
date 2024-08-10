package com.naz.vSpace.service;

import com.naz.vSpace.util.EmailMessage;

public interface EmailService {
    void sendMail(EmailMessage emailMessage);
}
