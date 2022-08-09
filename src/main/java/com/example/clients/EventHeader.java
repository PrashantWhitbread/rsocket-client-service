package com.example.clients;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class EventHeader {
  private Metadata metadata;
  private String moduleName;
  private String eventName;
  private String primaryKey;
  private String timestamp;
  private String hotelId;
  private String publisherId;
  private String actionInstanceId;
  private Set<EventDetail> detail;
}
