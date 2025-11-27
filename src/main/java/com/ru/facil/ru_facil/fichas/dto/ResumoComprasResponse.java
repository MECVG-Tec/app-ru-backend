package com.ru.facil.ru_facil.fichas.dto;

import java.util.List;

public record ResumoComprasResponse(
        String emailCliente,
        List<PagamentoPorMetodoResumo> porMetodo
) { }
