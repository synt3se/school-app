package com.github.synt3se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricesResponse {

    private BigDecimal lesson;
    private BigDecimal month;
    private BigDecimal year;
}
