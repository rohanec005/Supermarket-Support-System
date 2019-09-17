package au.edu.rmit.chaos;

public class Discount {

    private double percentage;
    private int quantity;

    public Discount(double percentage, int quantity) {
        this.percentage = percentage;
        this.quantity = quantity;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
