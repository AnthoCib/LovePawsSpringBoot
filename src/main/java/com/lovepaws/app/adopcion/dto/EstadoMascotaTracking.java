package com.lovepaws.app.adopcion.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Estados de seguimiento post-adopción alineados a catálogo estado_seguimiento en BD.
public enum EstadoMascotaTracking {
    ABIERTO("PROCESO", "Abierto"),
    RESPONDIDO("PROCESO", "Respondido"),
    CERRADO("PROCESO", "Cerrado"),
    ESCALADO("PROCESO", "Escalado"),
    EXCELENTE("MASCOTA", "Excelente"),
    BUENO("MASCOTA", "Bueno"),
    EN_OBSERVACION("MASCOTA", "En observación"),
    REQUIERE_ATENCION("MASCOTA", "Requiere atención"),
    PROBLEMA_SALUD("MASCOTA", "Problema de salud"),
    INCUMPLIMIENTO("MASCOTA", "Incumplimiento"),
    RETIRADA("MASCOTA", "Mascota retirada");

    private final String tipo;
    private final String label;

    EstadoMascotaTracking(String tipo, String label) {
        this.tipo = tipo;
        this.label = label;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLabel() {
        return label;
    }

    public boolean esTipo(String tipoEstado) {
        return this.tipo.equalsIgnoreCase(tipoEstado);
    }

    public static List<EstadoMascotaTracking> valoresPorTipo(String tipoEstado) {
        return Arrays.stream(values())
                .filter(e -> e.esTipo(tipoEstado))
                .toList();
    }

    public static Set<String> idsPorTipo(String tipoEstado) {
        return valoresPorTipo(tipoEstado).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    public static String labelDesdeId(String estadoId) {
        if (estadoId == null || estadoId.isBlank()) {
            return "-";
        }
        try {
            return EstadoMascotaTracking.valueOf(estadoId.toUpperCase()).getLabel();
        } catch (IllegalArgumentException ex) {
            return estadoId;
        }
    }
}
