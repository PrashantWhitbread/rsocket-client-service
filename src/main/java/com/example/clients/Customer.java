package com.example.clients;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder
@Value
@RequiredArgsConstructor
public class Customer {

  private Integer id;
  private String name;

}
