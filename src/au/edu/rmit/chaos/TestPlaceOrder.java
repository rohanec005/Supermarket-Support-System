package au.edu.rmit.chaos;

import java.util.ArrayList;

import static org.junit.Assert.*;


/**
 * Created by kassem on 7/5/17.
 */
public class TestPlaceOrder {

    static Customer customer;

    @org.junit.BeforeClass
    public static void init() {
        customer = new Customer("kassem", "kassem123", true);
    }

    @org.junit.Test
    public void placeOrderSufficientBalance() throws Exception {
        Order or = new Order(customer);

        or.createNewOrder();

        ArrayList<Product> products = Product.fetchProductsFromServer();

        Product pr = null;

        for (int x = 0; x < products.size(); x++) {
            if (products.get(x).getID() == 2) {
                pr = products.get(x);
                break;
            }
        }
        or.addProduct(pr, 10);
        assertEquals(or.placeOrder(), OrderStatus.placed);
    }

    @org.junit.Test
    public void placeOrderInsufficientBalance() throws Exception {

        Order or = new Order(customer);

        or.createNewOrder();

        ArrayList<Product> products = Product.fetchProductsFromServer();

        Product pr = null;

        for (int x = 0; x < products.size(); x++) {
            if (products.get(x).getID() == 2) {
                pr = products.get(x);
                break;
            }
        }
        or.addProduct(pr, 40);
        assertEquals(or.placeOrder(), OrderStatus.insufficient_balance);
    }


}