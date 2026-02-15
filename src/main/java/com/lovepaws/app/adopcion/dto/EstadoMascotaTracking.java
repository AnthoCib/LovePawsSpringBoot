package com.lovepaws.app.adopcion.dto;

// Estado de mascota alineado a catálogo estado_mascota en BD.
public enum EstadoMascotaTracking {
    DISPONIBLE,
    ADOPTADA,
    NO_DISPONIBLE;

    public String getLabel() {
        return switch (this) {
            case DISPONIBLE -> "Disponible";
            case ADOPTADA -> "Adoptada";
            case NO_DISPONIBLE -> "No disponible";
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
