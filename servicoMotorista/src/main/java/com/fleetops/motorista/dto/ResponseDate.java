package com.fleetops.motorista.dto;

import java.time.LocalDate;

public class ResponseDate {
    private LocalDate data;

    public ResponseDate(LocalDate Data) {
        this.data = Data;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
