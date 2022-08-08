package com.example.clients;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewEventInput {
  private String chainCode;
  private String offset;
  private Boolean delta;
  private String hotelCode;
}