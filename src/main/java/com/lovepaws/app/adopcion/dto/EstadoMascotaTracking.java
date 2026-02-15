package com.lovepaws.app.adopcion.dto;

// Estados de seguimiento post-adopción alineados a catálogo estado_seguimiento en BD.
public enum EstadoMascotaTracking {
    ABIERTO,
    RESPONDIDO,
    CERRADO,
    ESCALADO,
    EXCELENTE,
    BUENO,
    EN_OBSERVACION,
    REQUIERE_ATENCION,
    PROBLEMA_SALUD,
    INCUMPLIMIENTO,
    RETIRADA;

    public String getLabel() {
        return switch (this) {
            case ABIERTO -> "Abierto";
            case RESPONDIDO -> "Respondido";
            case CERRADO -> "Cerrado";
            case ESCALADO -> "Escalado";
            case EXCELENTE -> "Excelente";
            case BUENO -> "Bueno";
            case EN_OBSERVACION -> "En observación";
            case REQUIERE_ATENCION -> "Requiere atención";
            case PROBLEMA_SALUD -> "Problema de salud";
            case INCUMPLIMIENTO -> "Incumplimiento";
            case RETIRADA -> "Mascota retirada";
        };
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
