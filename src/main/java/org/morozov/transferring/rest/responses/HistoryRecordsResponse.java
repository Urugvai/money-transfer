package org.morozov.transferring.rest.responses;

import org.morozov.transferring.rest.dto.HistoryRecordDto;

import java.util.List;

public class HistoryRecordsResponse extends BaseResponse {

    private List<HistoryRecordDto> records;

    public List<HistoryRecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<HistoryRecordDto> records) {
        this.records = records;
    }
}
