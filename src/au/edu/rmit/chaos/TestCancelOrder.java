package au.edu.rmit.chaos;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kassem on 22/5/17.
 */
public class TestCancelOrder {


    @Test
    public void orderNotExist() throws Exception {
        Order or = new Order();
        or.getOrderByID(-1);
        assertFalse(or.cancelOrder());
    }

    @Test
    public void orderAlreadyCanceled() throws Exception {
        Order or = new Order();
        or.getOrderByID(96);
        assertFalse(or.cancelOrder());
    }

    @Test
    public void cancelOrder() throws Exception {
        Order or = new Order();
        or.getOrderByID(166);
        assertTrue(or.cancelOrder());
    }

}