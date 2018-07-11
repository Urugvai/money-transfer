package org.morozov.transferring.rest.dto;

import java.math.BigDecimal;
import java.util.Date;

public class HistoryRecordDto {

    public String fromAccount;

    public String toAccount;

    public BigDecimal amount;

    public Date timeStamp;
}
