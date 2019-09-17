<?php

/**
 * Created by PhpStorm.
 * User: kassem
 * Date: 17/4/17
 * Time: 7:31 PM
 *
 * @access protected
 */
class product
{

    /**
     * @access private
     * @param $product_id
     */
    public function increaseStockLevel($con, $product_id, $quantity)
    {
        $close = false;
        if ($con == null){
            $close = true;
            $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        }


        $con->set_charset("utf8");
        $result = mysqli_query($con, "update product set prod_stock_level =prod_stock_level+$quantity where prod_id=$product_id");
        if($close)
            mysqli_close($con);

        if (!$result)
            return false;
        return true;
    }

    /**
     * @access private
     * @param $product_id
     */
    public function decreaseStockLevel($con, $product_id, $quantity)
    {
        $close = false;
        if ($con == null) {
            $close = true;
            $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        }

        $con->set_charset("utf8");
        $result = mysqli_query($con, "update product set prod_stock_level =prod_stock_level-$quantity where prod_id=$product_id");
        if($close)
            mysqli_close($con);

        if (!$result)
            return false;
        return true;
    }

    /**
     * @access private
     * @param $product_id
     * @param $qty
     */
    public function calculatePrice($product_id, $qty){
        $product = product::getProduct($product_id)->product;
        $price = $product->unit_price * $qty;
        $bulk_price = 0;
        foreach ($product->discounts as $disc){
            if($qty >= $disc["quantity"]){
                $tmp = ($product->unit_price * $disc["quantity"]) * ($disc["percentage"] /100);
                $bulk_price = $bulk_price > $tmp ? $bulk_price : $tmp;
            }
        }
        return $price - $bulk_price;
    }

    private function isProductExists($product_id){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from product where prod_id = $product_id");
        $row = $result->fetch_assoc();
        mysqli_close($con);
        if ($row == null)
            return false;
        return true;
    }


    /**
     * @url GET /{product_id}/
     * @param $product_id
     */
    public function getProduct($product_id)
    {
        if(!product::isProductExists($product_id))
            throw new RestException(404,"product does not exist");

        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from product where prod_id= $product_id");
        $row = $result->fetch_assoc();

        if ($row == null) {
            $res = new stdClass();
            $res->product = null;
            return $res;
        }

        $product = new stdClass();
        $product->id = $row['prod_id'];
        $product->name= $row['prod_name'];
        $product->unit_price= $row['prod_unit_price'];
        $product->stock_level= $row['prod_stock_level'];
        $product->replenish_level= $row['prod_replenish_level'];
        $product->type= $row['prod_type'];
        $product->discounts=  product::getProductDiscounts($row['prod_id']);

        $res = new stdClass();
        $res->product = $product;
        return $res;
    }

    private function getDiscount($product_id, $percentage, $quantity)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from discount where disc_quantity=$quantity and disc_percentage=$percentage and product = $product_id");
        $row = $result->fetch_assoc();

        if ($row == null) {
            $res = new stdClass();
            $res->discount = null;
            return $res;
        }

        $discount = new stdClass();
        $discount->quantity = $row['disc_quantity'];
        $discount->percentage = $row['disc_percentage'];
        $res = new stdClass();
        $res->discount = $discount;
        return $res;
    }

    /**
     * @url POST /{product_id}/delete_discount
     * @param $product_id
     * @param $percentage
     * @param $quantity
     * @return stdClass
     */
    public function deleteDiscount($product_id, $percentage, $quantity)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "delete from discount where disc_quantity=$quantity and disc_percentage=$percentage and product=$product_id");
        if (!$result)
            throw new RestException(400, "could not delete discount");

        $disc = $this->getDiscount($product_id, $percentage, $quantity);
        if ($disc->discount!=null)
            throw new RestException(400, "could not delete discount.");

        $responce = new stdClass();
        $responce->code = "200";
        $responce->message = "discount deleted";
        $res = new stdClass();
        $res->responce = $responce;
        return $res;
    }

    /**
     * @url POST /{product_id}/add_discount
     * @param $product_id
     * @param $percentage
     * @param $quantity
     * @return stdClass
     */
    public function addDiscount($product_id, $percentage, $quantity)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "insert into discount (disc_quantity, product, disc_percentage) values($quantity,$product_id,$percentage) ");
        if (!$result)
            throw new RestException(400, "could not add discount");

        $disc = $this->getDiscount($product_id, $percentage, $quantity);
        if ($disc->discount==null)
            throw new RestException(400, "could not add discount.");

        $responce = new stdClass();
        $responce->code = "200";
        $responce->message = "discount added";
        $res = new stdClass();
        $res->responce = $responce;
        return $res;
    }


    /**
     * @url POST /{product_id}/update_price
     * @param $product_id
     * @param $price
     * @return stdClass
     */
    public function editPrice($product_id,$price)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update product set prod_unit_price = $price where prod_id=$product_id ");
        if (!$result)
            throw new RestException(400, "could not update product price");

        return $this->getProduct($product_id);
    }

    /**
     * @url POST /{product_id}/update_replenish
     * @param $product_id
     * @param $price
     * @return stdClass
     */
        public function editReplenishLevel($product_id,$level)
    {
        if($level <0)
            throw new RestException(400, "invalid replenish level");

        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update product set prod_replenish_level = $level where prod_id=$product_id ");
        if (!$result)
            throw new RestException(400, "could not update product replenish level");

        return $this->getProduct($product_id);
    }

    /**
     * @url POST /{product_id}/update_stock
     * @param $product_id
     * @param $price
     * @return stdClass
     */
    public function editStockLevel($product_id,$level)
    {
        if ($level<0)
            throw new RestException(400, "invalid stock level");

        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update product set prod_stock_level = $level where prod_id=$product_id ");
        if (!$result)
            throw new RestException(400, "could not update product stock level");

        return $this->getProduct($product_id);
    }

    /**
     * @url POST /{product_id}/update_discount
     * @param $product_id
     * @param $old_percentage
     * @param $old_quantity
     * @param $new_percentage
     * @param $new_quantity
     * @return updated discount
     */
    public function editDiscount($product_id, $old_percentage, $old_quantity, $new_percentage, $new_quantity)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update discount set disc_percentage=$new_percentage,disc_quantity=$new_quantity where disc_quantity=$old_quantity and disc_percentage=$old_percentage and product=$product_id");
        if (!$result)
            throw new RestException(400, "could not update discount");

        return $this->getDiscount($product_id, $new_percentage, $new_quantity);
    }

    /**
     * @url GET /
     * @return array
     */
    public function listAllProducts()
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from product");
        $products = array();
        while ($row = mysqli_fetch_array($result)) {
            $prod = array(
                'id' => $row['prod_id'],
                'name' => $row['prod_name'],
                'unit_price' => $row['prod_unit_price'],
                'stock_level' => $row['prod_stock_level'],
                'replenish_level' => $row['prod_replenish_level'],
                'type' => $row['prod_type'],
                'discounts' => $this->getProductDiscounts($row['prod_id']));
            $products[] = $prod;
        }
        mysqli_close($con);
        return $products;
    }



    /**
     * @url GET /discounts
     * @url GET /{product_id}/discounts
     * @param $product_id product id
     * @return list of discounts
     */
    public function getProductDiscounts($product_id)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from discount where product=$product_id");
        $discounts = array();
        while ($row = mysqli_fetch_array($result)) {
            $disc = array(
                'quantity' => $row['disc_quantity'],
                'percentage' => $row['disc_percentage']);
            $discounts[] = $disc;
        }
        mysqli_close($con);
        return $discounts;
    }
}