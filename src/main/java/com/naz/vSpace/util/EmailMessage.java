package com.naz.vSpace.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage implements Serializable {
    private static final long SERIAL_VERSION_UID = 1L;

    private String to;
    private String subject;
    private String text;
}
