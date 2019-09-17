package au.edu.rmit.chaos;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by kassem on 22/5/17.
 */
public class TestPurchaseOrder {


    static Employee emp;

    @org.junit.BeforeClass
    public static void init() {
        emp = new Employee("kim", "kim");
    }

    @org.junit.Test
    public void placePurchaseOrder() throws Exception {
        ArrayList<Supplier> suppliers = Supplier.fetchSuppliersFromServer();

        Supplier alde = suppliers.get(0);

        ArrayList<Product> products = alde.getSupplierProducts();

        Product product = products.get(1);

        PurchaseOrder pr = new PurchaseOrder(alde, emp);

        pr.addProduct(product, 500);

        assertTrue(pr.placeOrder());
    }

    @org.junit.Test
    public void placeInvalidPurchaseOrder() throws Exception {
        ArrayList<Supplier> suppliers = Supplier.fetchSuppliersFromServer();

        Supplier alde = suppliers.get(0);

        ArrayList<Product> products = alde.getSupplierProducts();

        Product product = products.get(0);

        PurchaseOrder pr = new PurchaseOrder(alde, emp);

        // invalid quantity
        pr.addProduct(product, -1);

        assertFalse(pr.placeOrder());
    }

}