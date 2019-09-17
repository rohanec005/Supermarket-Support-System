package au.edu.rmit.chaos;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by kassem on 22/5/17.
 */
public class TestRemoveItem {


    @org.junit.BeforeClass
    public static void init() {
    }


    @Test
    public void removeItem() throws Exception {
        Order or = new Order();
        or.getOrderByID(190);

        ArrayList<OrderItem> items = or.getOrderItems();

        OrderItem itm = null;

        for (int x = 0; x < items.size(); x++) {
            if (items.get(x).getProduct().getID() == 2) {
                itm = items.get(x);
                break;
            }
        }
        assertNotNull(itm);
        assertTrue(or.removeProduct(itm));
    }

    @Test
    public void removeItemFromCanceledOrder() throws Exception {
        Order or = new Order();
        or.getOrderByID(181);

        ArrayList<OrderItem> items = or.getOrderItems();

        OrderItem itm = null;

        for (int x = 0; x < items.size(); x++) {
            if (items.get(x).getProduct().getID() == 2) {
                itm = items.get(x);
                break;
            }
        }
        assertFalse(or.removeProduct(itm));
    }


}