package uk.co.whitbread.ohipmockserver.domain.hotel.availability.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Metadata {
  private String offset;
  private String uniqueEventId;
}
