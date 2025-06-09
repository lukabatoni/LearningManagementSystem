package com.example.lms.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class EmailDetails {
  private String[] recipient;
  private String msgBody;
  private String subject;
}
