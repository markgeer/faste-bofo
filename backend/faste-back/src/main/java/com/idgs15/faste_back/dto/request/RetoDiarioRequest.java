package com.idgs15.faste_back.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RetoDiarioRequest {
    private Integer retoId;
    private Integer usuarioId;
    private LocalDate fechaAsignacion;
}