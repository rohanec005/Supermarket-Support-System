<?php

/**
 * Created by PhpStorm.
 * User: kassem
 * Date: 17/4/17
 * Time: 7:30 PM
 * @access protected
 */
class customer
{
    private function encodeCustomerIntoJson($customer)
    {
        $cust = new stdClass();
        $cust->id = $customer['cust_id'];
        $cust->name = $customer['cust_name'];
        $cust->balance = $customer['cust_balance'];
        $cust->points = $customer['cust_points'];
        return $cust;
    }

    /**
     * @access private
     * @param $customer_id
     * @return stdClass
     */
    public function getCustomerById($customer_id)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from customer where cust_id = $customer_id");
        $row = $result->fetch_assoc();
        mysqli_close($con);
        return customer::encodeCustomerIntoJson($row);
    }

    /**
     * @access private
     * @param $con
     * @param $customer_id
     * @param $amount
     */
    public function deductBalance($con, $customer_id, $amount)
    {
        $close = false;
        if ($con == null) {
            $close = true;
            $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        }
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update customer set cust_balance=cust_balance-$amount where cust_id=$customer_id");
        if ($close)
            mysqli_close($con);
        if (!$result)
            return false;
        return true;
    }

    /**
     * @access private
     * @param $con
     * @param $customer_id
     * @param $points
     * @return bool
     */
    public function deductPoints($con, $customer_id, $points)
    {
        $close = false;
        if ($con == null) {
            $close = true;
            $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        }
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update customer set cust_points=cust_points-$points where cust_id=$customer_id");
        if ($close)
            mysqli_close($con);
        if (!$result)
            return false;
        return true;
    }

    /**
     * @access private
     * @param $customer_id
     */
    public function getBalance($customer_id)
    {
        return customer::getCustomerById($customer_id)->balance;
    }

    public function getPoints($customer_id)
    {
        return customer::getCustomerById($customer_id)->points;
    }

    /**
     * @access private
     * @param $customer_id
     * @param $order_id
     */
    public function canPlaceOrder($customer_id, $order_id)
    {
        $order_total = order::calculateOrderTotal($order_id);
        $balance = customer::getBalance($customer_id);
        $points = customer::getPoints($customer_id);
        $points_value = floor($points / 20);
        if ($order_total > ($balance + $points_value))
            return false;
        return true;
    }

    /**
     * @access private
     * @param $con
     * @param $customer_id
     * @param $amount
     */
    public function addBalanceForCustomer($con, $customer_id, $amount)
    {
        $close = false;
        if ($con == null) {
            $close = true;
            $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        }
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update customer set cust_balance =cust_balance+$amount where cust_id=$customer_id");

        if ($close)
            mysqli_close($con);

        if (!$result) {
            if ($close)
                throw new RestException(401, 'could not add balance to customer');
            return false;
        }
        if ($close)
            return $this->getCustomerById($customer_id);
        return true;
    }

    /**
     * @url POST /{customer_id}/add_balance
     * @param $customer_id
     * @param $amount
     * @return bool
     */
    public function addBalance($customer_id, $amount)
    {
        return $this->addBalanceForCustomer(null, $customer_id, $amount);
    }

    /**
     * @access private
     * @param $customer_id
     * @param $points
     * @param $con
     * @return stdClass
     */
    public function addPoints($con, $customer_id, $points)
    {
        $close = false;
        if ($con == null) {
            $close = true;
            $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        }
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update customer set cust_points =cust_points+$points where cust_id=$customer_id");

        if ($close)
            mysqli_close($con);

        if (!$result)
            return false;
        return true;
    }

    /**
     * @url GET /search
     * @param $username
     * @return stdClass
     */
    public function getCustomerByUsername($username)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from customer where cust_username ='$username'");
        $row = $result->fetch_assoc();
        mysqli_close($con);
        if ($row == null)
            throw new RestException(404, 'user not found');

        return $this->encodeCustomerIntoJson($row);
    }


    /**
     * @url GET /login
     * @param $username
     * @param $password
     * @return stdClass
     */
    public function login($username, $password)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from customer where cust_username ='$username' and cust_password='$password'");
        $row = $result->fetch_assoc();
        mysqli_close($con);
        if ($row == null)
            throw new RestException(401, 'username and password not found');

        return $this->encodeCustomerIntoJson($row);
    }
}