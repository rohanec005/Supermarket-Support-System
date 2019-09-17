<?php

/**
 * Created by PhpStorm.
 * User: kassem
 * Date: 17/4/17
 * Time: 7:31 PM
 *
 * @access protected
 */
class order
{

    /**
     * @url POST /{order_id}/remove_item
     * @param $order_id
     * @param $product_id
     */
    public function removeItemFromOrder($order_id,$product_id){
        $ord = order::getOrder($order_id);
        if($ord->order->status == 'canceled')
            throw new RestException(400,"order status is canceled");

        if($ord->order->status == 'placed')
            throw new RestException(400,"order is already placed");

        if (!$this->isOrderItemExists($order_id,$product_id))
            throw new RestException(404,'item does not exist');

        $quantity = order::getOrderItemQuantity($order_id,$product_id);
        $item = order::getOrderItem($order_id,$product_id);
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $query = "delete from order_item where cust_order=$order_id and product = $product_id";
        $result = mysqli_query($con, $query);
        mysqli_close($con);
        if (!$result)
            throw new RestException(400, "cannot remove order item");

        product::increaseStockLevel(null, $product_id, $quantity);

        return $item;
    }

    private function getOrderItemQuantity($order_id,$product_id){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select item_quantity from order_item where cust_order =$order_id and product=$product_id");
        $row = $result->fetch_assoc();
        mysqli_close($con);
        return $row["item_quantity"];
    }

    private function increaseOrderItemQuantity($order_id,$product_id,$quantity){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");

        $pr = product::getProduct($product_id)->product;
        $quantity = $quantity+$this->getOrderItemQuantity($order_id,$product_id);

        $total = product::calculatePrice($product_id,$quantity);
        $item_discount = (($pr->unit_price * $quantity) - $total) < 0 ? 0: (($pr->unit_price * $quantity) - $total);

        $result = mysqli_query($con, "update order_item set item_quantity =$quantity, item_total =$total,item_discount=$item_discount  where product=$product_id and cust_order=$order_id");
        mysqli_close($con);
        if (!$result)
            return false;
        return true;
    }



    private function isOrderItemExists($order_id,$product_id){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from order_item where cust_order = $order_id and product=$product_id");
        $row = $result->fetch_assoc();
        mysqli_close($con);
        if ($row == null)
            return false;
        return true;
    }

    private function isOrderExists($order_id){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from customer_order WHERE order_id= $order_id");
        $row = $result->fetch_assoc();
        mysqli_close($con);
        if ($row == null)
            return false;
        return true;
    }


    private function getOrderItems($order_id){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from order_item where cust_order = $order_id");
        $items = array();
        while ($row = mysqli_fetch_array($result)) {
            $item = array(
                'order_id' => $row['cust_order'],
                'quantity' => $row['item_quantity'],
                'item_total' => $row['item_total'],
                'item_discount' => $row['item_discount'],
                'product' => product::getProduct($row['product'])->product);
            $items[] = $item;
        }
        mysqli_close($con);
        return $items;
    }


    private function getOrderItem($order_id,$product_id){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from order_item where cust_order =$order_id and product=$product_id");
        $row = $result->fetch_assoc();
        mysqli_close($con);

        $res = new stdClass();
        $item =  new stdClass();
        $item->order_id = $row["cust_order"];
        $item->quantity = $row["item_quantity"];
        $item->item_total = $row["item_total"];
        $item->item_discount = $row["item_discount"];
        $item->product = product::getProduct($product_id);
        $res->item = $item;
        return $res;
    }

    private function updateOrderStatus($con,$order_id,$status){
        $close = false;
        if ($con==null){
            $close= true;
            $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        }
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update customer_order set order_status='$status' where order_id = $order_id");

        if ($close)
            mysqli_close($con);

        if (!$result)
            return false;
        return true;

    }

    /**
     * @url POST /{order_id}/cancel
     * @param $order_id
     */
    public function cancelOrder($order_id){

        if(!order::isOrderExists($order_id))
            throw new RestException(400,"order does not exist");

        $order = order::getOrder($order_id);
        if($order ->order->status == 'canceled')
            throw new RestException(400,"order is already canceled");

        // roll back in case of any error
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        mysqli_autocommit($con, false);

        // increase stock level
        foreach ($order->order->items as $item){
            $status = product::increaseStockLevel($con,$item["product"]->id,$item["quantity"]);
            if (!$status){
                mysqli_rollback($con);
                mysqli_close($con);
                throw new RestException(400, 'could not cancel order: increase stock level');
            }
        }

        if($order ->order->status == 'placed'){
            // increase customer's purchase points

            if (!customer::addPoints($con,$order->order->customer_id,$order->order->points)){
                mysqli_rollback($con);
                mysqli_close($con);
                throw new RestException(400, "could not cancel order: increase customer's purchase points");
            }

            // increase customer's balance
            if (!customer::addBalanceForCustomer($con,$order->order->customer_id,$order->order->total)){
                mysqli_rollback($con);
                mysqli_close($con);
                throw new RestException(400, "could not cancel order: increase customer's balance");
            }

            // decrease customer's bonus points
            if (!customer::deductPoints($con,$order->order->customer_id,$order->order->bonus_points)){
                mysqli_rollback($con);
                mysqli_close($con);
                throw new RestException(400, "could not cancel order: increase customer's balance");
            }
        }

        // update order status
        if (!order::updateOrderStatus($con,$order_id,'canceled')){
            mysqli_rollback($con);
            mysqli_close($con);
            throw new RestException(400, 'could not cancel order: update status');
        }

        // commit changes to the db
        mysqli_commit($con);
        mysqli_close($con);


        $responce = new stdClass();
        $responce->code = "200";
        $responce->message = "order canceled";
        $res = new stdClass();
        $res->responce = $responce;
        return $res;

    }

    /**
     * @url POST /{order_id}/add_item
     * @param $order_id
     * @param $product_id
     * @param $quantity
     * @return bool
     */
    public function addOrderItemRecord($order_id,$product_id, $quantity)
    {
        $ord = order::getOrder($order_id);

        if($ord->order->status == 'canceled')
            throw new RestException(400,"order status is canceled");

        if($ord->order->status == 'placed')
            throw new RestException(400,"order is already placed");


        $pr = product::getProduct($product_id)->product;
        if ($pr->stock_level<$quantity)
            throw new RestException(400,'cannot add order item. only ' . $pr->stock_level . " " . $pr->type . ' available');

        if($this->isOrderItemExists($order_id,$product_id)){
            $this->increaseOrderItemQuantity($order_id,$product_id, $quantity);
        }
        else{
            $total = product::calculatePrice($product_id,$quantity);
            $item_discount = (($pr->unit_price * $quantity) - $total) < 0 ? 0: (($pr->unit_price * $quantity) - $total);

            // compute points and add discount
            $con = mysqli_connect('localhost', 'root', '', 'supermarket');
            $query = "insert into order_item (cust_order, product, item_quantity ,item_total,item_discount) values ($order_id,$product_id,$quantity,$total,$item_discount) ";
            $result = mysqli_query($con, $query);
            mysqli_close($con);
            if (!$result)
                throw new RestException(400,"cannot add order item");
        }

        product::decreaseStockLevel(null, $product_id, $quantity);


        return $this->getOrderItem($order_id,$product_id);
    }

    /**
     * @url GET /{order_id}/
     * @param $order_id
     */
    public function getOrder($order_id){

        if(!order::isOrderExists($order_id))
            throw new RestException(400,"order does not exist");

        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from customer_order where order_id = $order_id");
        $row = $result->fetch_assoc();
        mysqli_close($con);

        $res = new stdClass();
        $order =  new stdClass();
        $order->order_id = $row["order_id"];
        $order->customer_id = $row["customer"];
        $order->date = $row["order_date"];
        $order->subtotal = $row["order_subtotal"];
        $order->total = $row["order_total"];
        $order->status = $row["order_status"];
        $order->discount = $row["order_discount"];
        $order->points = $row["order_points"];
        $order->bonus_points = $row["bonus_points"];
        $order->customer = customer::getCustomerById($row["customer"]);
        $order->items = order::getOrderItems($order_id);
        $res->order = $order;
        return $res;
    }

    /**
     * @url POST /add
     * @param $customer_id
     * @return int|mixed
     */
    public function addOrderRecord($customer_id)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $result = mysqli_query($con, "insert into customer_order (customer,order_status) values($customer_id,'pending')");
        if (!$result)
            throw new RestException(400,"cannot add order");

        $responce = new stdClass();
        $responce->code = "200";
        $responce->message = "order added";
        $responce->order_id = $con->insert_id;
        $res = new stdClass();
        $res->responce = $responce;
        return $res;
    }

    /**
     * @access private
     * @param $order_id
     * @return int
     */
    public function calculateOrderTotal($order_id){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $result = mysqli_query($con, "SELECT sum(item_total) as 'order_total' from order_item where cust_order = $order_id");
        $row = $result->fetch_assoc();
        if (!$result)
            return -1;
        return $row["order_total"];
    }

    /**
     * @url POST /{order_id}/place
     * @param $order_id
     * @param $customer_id
     */
    public function placeOrder($customer_id,$order_id){
        if(order::getOrder($order_id)->order->status == 'canceled')
            throw new RestException(400,"order status is canceled");

        if(order::getOrder($order_id)->order->status == 'placed')
            throw new RestException(400,"order is already placed");

        // check if customer's balance and points are sufficient
        if(!customer::canPlaceOrder($customer_id,$order_id))
            throw new RestException(400,"insufficient balance");

        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        mysqli_autocommit($con, false);


        // deduct points
        $order_total = order::calculateOrderTotal($order_id);
        $balance = customer::getBalance($customer_id);
        $points = customer::getPoints($customer_id);
        $points_used = floor($points / 20) * 20;
        $points_used_value = floor($points / 20);
        if(!customer::deductPoints($con,$customer_id,$points_used)){
            mysqli_rollback($con);
            mysqli_close($con);
            throw new RestException(400, 'could not place order: customer deduct points');
        }

        // deduct balance
        if(!customer::deductBalance($con,$customer_id,$order_total-$points_used_value)){
            mysqli_rollback($con);
            mysqli_close($con);
            throw new RestException(400, 'could not place order: customer deduct balance');
        }

        $bonus_points = floor(($order_total/10));
        // add points to customer
        if(!customer::addPoints($con,$customer_id,$bonus_points)){
            mysqli_rollback($con);
            mysqli_close($con);
            throw new RestException(400, 'could not place order: customer add points');
        }

        $time = time();
        // update order
        $result = mysqli_query($con,"update customer_order set order_date='$time',order_subtotal=$order_total,order_discount= $points_used_value,order_total=$order_total-$points_used_value,order_status='placed',order_points=$points_used,bonus_points=$bonus_points where order_id=$order_id");

        if (!$result){
            mysqli_rollback($con);
            mysqli_close($con);
            throw new RestException(400, 'could not place order: finalise order');
        }

        mysqli_commit($con);
        mysqli_close($con);

        // check replenish level
        $order = order::getOrder($order_id);
        foreach ($order->order->items as $item)
            purchaseorder::autoPurchaseOrder($item["product"]->id);


        return order::getOrder($order_id);

//        $responce = new stdClass();
//        $responce->code = "200";
//        $responce->message = "order placed";
//        $res = new stdClass();
//        $res->responce = $responce;
//        return $res;

    }
}