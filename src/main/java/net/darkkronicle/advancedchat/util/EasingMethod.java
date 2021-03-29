package net.darkkronicle.advancedchat.util;

public interface EasingMethod {

    double apply(double x);

    enum Method implements EasingMethod {
        LINEAR((x) -> x),
        SINE((x) -> 1 - Math.cos((x * Math.PI) / 2)),
        QUAD((x) -> x * x),
        QUART((x) -> x * x * x * x),
        CIRC((x) -> 1 - Math.sqrt(1 - Math.pow(x, 2)))
        ;

        private final EasingMethod method;

        Method(EasingMethod method) {
            this.method = method;
        }

        @Override
        public double apply(double x) {
            return method.apply(x);
        }
    }

}
