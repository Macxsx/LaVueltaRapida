package com.example.demo.error;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiError {

    private ApiError() {}

    public static Map<String, Object> of(String mensaje) {
        return Map.of("error", mensaje);
    }

    public static Map<String, Object> of(String mensaje, String detalle) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("error", mensaje);
        if (detalle != null) m.put("detail", detalle);
        return m;
    }
}
