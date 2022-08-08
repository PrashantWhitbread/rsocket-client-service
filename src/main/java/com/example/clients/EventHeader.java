package uk.co.whitbread.ohipmockserver.domain.hotel.availability.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventHeader {
  private Metadata metadata;
  private String moduleName;
  private String eventName;
  private String primaryKey;
  private String timestamp;
  private String hotelId;
  private String publisherId;
  private String actionInstanceId;
  private EventDetail detail;
}
