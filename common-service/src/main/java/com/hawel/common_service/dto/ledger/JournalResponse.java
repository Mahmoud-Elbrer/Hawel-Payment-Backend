package com.hawel.common_service.dto.ledger;

import com.hawel.common_service.enums.JournalStatus;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalResponse {


    private UUID journalId;


    private String journalNumber;


    private String reference;


    private JournalStatus status;

}


/*\
like this :

{
  "journalId": "uuid",
  "journalNumber": "JRN000001",
  "reference": "TRX12345",
  "status": "COMPLETED"
}

 */