package streamflow.modelo;

public enum Calidad {
    SD(1.0),
    HD(1.5),
    UHD_4K(2.0);

    private final double factor;

    Calidad(double factor) {
        this.factor = factor;
    }

    public double getFactor() {
        return factor;
    }
}
