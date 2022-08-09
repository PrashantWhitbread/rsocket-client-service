package com.example.clients;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class EventDetail {
  private String newValue;
  private String oldValue;
  private String elementName;
  private String scopeFrom;
  private String scopeTo;
  private String elementSequence;
  private String elementType;
  private String elementRole;
}
