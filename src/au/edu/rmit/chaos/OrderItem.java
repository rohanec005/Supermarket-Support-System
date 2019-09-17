package au.edu.rmit.chaos;

public class OrderItem extends Item {
    private double total;
    private double discount;

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public OrderItem(Product p, int qty) {
        super(qty, p);
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }


}
