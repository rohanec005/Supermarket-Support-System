package au.edu.rmit.chaos;

public class Item {
    protected int quantity;
    protected Product product;


    public Item(int quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }
}
