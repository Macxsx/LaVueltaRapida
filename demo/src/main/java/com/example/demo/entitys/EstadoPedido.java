package com.example.demo.entitys;





public enum EstadoPedido {

    RECIBIDO,
    COCINANDO,
    ENVIADO,
    ENTREGADO;

    /** Retorna el único estado al que puede avanzar este pedido, o null si ya es el estado final. */
    public EstadoPedido siguiente() {
        return switch (this) {
            case RECIBIDO  -> COCINANDO;
            case COCINANDO -> ENVIADO;
            case ENVIADO   -> ENTREGADO;
            case ENTREGADO -> null;
        };
    }
}
