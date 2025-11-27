package com.ru.facil.ru_facil.suporte.dto;

import com.ru.facil.ru_facil.enuns.SupportStatus;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusSuporteRequest(

        @NotNull
        SupportStatus status

) { }
