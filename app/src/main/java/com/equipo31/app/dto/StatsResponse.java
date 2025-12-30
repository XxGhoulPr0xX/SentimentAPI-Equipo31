package com.equipo31.app.dto;

public class StatsResponse {
    private long total;
    private long positivos;
    private long negativos;
    private long neutros;

    public StatsResponse(long total, long positivos, long negativos, long neutros) {
        this.total = total;
        this.positivos = positivos;
        this.negativos = negativos;
        this.neutros = neutros;
    }

    public long getTotal() { return total; }
    public long getPositivos() { return positivos; }
    public long getNegativos() { return negativos; }
    public long getNeutros() { return neutros; }
}
