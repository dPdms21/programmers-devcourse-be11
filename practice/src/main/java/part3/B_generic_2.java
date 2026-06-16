package part3;

public class B_generic_2 <T extends Number> {
    // 덧셈
    public T add(T n1,T n2){
        if (n1 instanceof Integer && n2 instanceof Integer) {
            int result = n1.intValue() + n2.intValue();
            return (T) Integer.valueOf(result);
        }
        else if (n1 instanceof Double && n2 instanceof Double) {
            double result = n1.doubleValue() + n2.doubleValue();
            return (T) Double.valueOf(result);
        }

        throw new UnsupportedOperationException("지원되지 않는 타입");
    }

    // 뺼셈
    public T sub(T n1,T n2){
        if (n1 instanceof Integer && n2 instanceof Integer) {
            int result = n1.intValue() - n2.intValue();
            return (T) Integer.valueOf(result);
        }
        else if (n1 instanceof Double && n2 instanceof Double) {
            double result = n1.doubleValue() - n2.doubleValue();
            return (T) Double.valueOf(result);
        }

        throw new UnsupportedOperationException("지원되지 않는 타입");
    }

    // 곱셈
    public T mul(T n1,T n2){
        if (n1 instanceof Integer && n2 instanceof Integer) {
            int result = n1.intValue() * n2.intValue();
            return (T) Integer.valueOf(result);
        }
        else if (n1 instanceof Double && n2 instanceof Double) {
            double result = n1.doubleValue() * n2.doubleValue();
            return (T) Double.valueOf(result);
        }

        throw new UnsupportedOperationException("지원되지 않는 타입");
    }

    // 나눗셈
    public T div(T n1,T n2){
        if (n1 instanceof Integer && n2 instanceof Integer) {
            int result = n1.intValue() / n2.intValue();
            return (T) Integer.valueOf(result);
        }
        else if (n1 instanceof Double && n2 instanceof Double) {
            double result = n1.doubleValue() / n2.doubleValue();
            return (T) Double.valueOf(result);
        }

        throw new UnsupportedOperationException("지원되지 않는 타입");
    }

    public static void main(String[] args) {
        B_generic_2<Integer> intCalculator = new B_generic_2<>();
        System.out.println("Integer Addition: " + intCalculator.add(10, 20));
        System.out.println("Integer Subtraction: " + intCalculator.sub(20, 10));
        System.out.println("Integer Multiplication: " + intCalculator.mul(10, 20));
        System.out.println("Integer Division: " + intCalculator.div(20, 10));

        B_generic_2<Double> doubleCalculator = new B_generic_2<>();
        System.out.println("Double Addition: " + doubleCalculator.add(10.5, 20.3));
        System.out.println("Double Subtraction: " + doubleCalculator.sub(20.5, 10.2));
        System.out.println("Double Multiplication: " + doubleCalculator.mul(10.0, 20.0));
        System.out.println("Double Division: " + doubleCalculator.div(20.0, 10.0));
    }
}
