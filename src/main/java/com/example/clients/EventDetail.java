package uk.co.whitbread.ohipmockserver.domain.hotel.availability.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
