package com.example.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder
@Value
@RequiredArgsConstructor
public class Greeting {
  @JsonProperty("greeting")
  private String greeting;
}
