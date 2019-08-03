package microservices.book.multiplication.domain;

public class Multiplication {
  private int factorA;
  private int factorB;

  private int result;

  public Multiplication(int factorA, int factorB) {
    this.factorA = factorA;
    this.factorB = factorB;
    this.result = this.factorA * this.factorB;
  }

  public int getFactorA() {
    return this.factorA;
  }

  public int getFactorB() {
    return this.factorB;

  }

  public int getResult() {
    return this.result;
  }

  @Override
  public String toString() {
    return "Multiplication{" + "factorA=" + this.factorA + ", factorB=" + factorB + ", result(A*B)=" + result + "}";
  }

}