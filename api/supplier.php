<?php

/**
 * Created by PhpStorm.
 * User: kassem
 * Date: 17/4/17
 * Time: 7:31 PM
 * @access protected
 */
class supplier
{

    private function isSupplierExists($supplier_id){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select sup_id from supplier where sup_id = $supplier_id");
        $row = $result->fetch_assoc();
        mysqli_close($con);
        if ($row == null)
            return false;
        return true;
    }

    /**
     *
     * @url GET /{supplier_id}/update
     * @param $supplier_id supplier
     */
    public function editSupplier($supplier_id, $name, $address, $postcode, $phone)
    {
        if(!$this->isSupplierExists($supplier_id))
            throw new RestException(404, 'supplier not found');

        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "update supplier set sup_name = '$name',sup_address='$address',sup_postcode=$postcode,sup_phone='$phone' where sup_id = $supplier_id");
        mysqli_close($con);
        return $this->getSuplier($supplier_id);
    }

    /**
     * @access private
     * @param $product_id
     * @return stdClass
     */
    public function getProductSupplier($product_id){
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from supplier JOIN product on supplier.sup_id = product.supplier where prod_id=$product_id");
        $row = $result->fetch_assoc();

        mysqli_close($con);

        if ($row == null)
            return null;

        return supplier::encodeSupplierIntoJson($row);
    }

    /**
     * @url GET /{suppiler_id}/products
     * @url GET /products
     * @param $suppiler_id supplier id
     * @return list of suppliers products
     */
    public function getSupplierProducts($suppiler_id)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from product where supplier = $suppiler_id");
        $products = array();
        while ($row = mysqli_fetch_array($result)) {
            $prod = array(
                'id' => $row['prod_id'],
                'name' => $row['prod_name'],
                'unit_price' => $row['prod_unit_price'],
                'stock_level' => $row['prod_stock_level'],
                'replenish_level' => $row['prod_replenish_level'],
                'type' => $row['prod_type'],
                'discounts' => product::getProductDiscounts($row['prod_id']));
            $products[] = $prod;
        }
        mysqli_close($con);
        return $products;
    }

    private function encodeSupplierIntoJson($supplier)
    {
        $sup = new stdClass();
        $sup->id = $supplier['sup_id'];
        $sup->name = $supplier['sup_name'];
        $sup->address = $supplier['sup_address'];
        $sup->postcode = $supplier['sup_postcode'];
        $sup->phone = $supplier['sup_phone'];
        $sup->products = supplier::getSupplierProducts($supplier['sup_id']);
        return $sup;
    }


    /**
     * Returns supplier info with products
     *
     * @url GET /{supplier_id}
     * @param $supplier_id supplier id
     * @return supplier object
     */
    public function getSuplier($supplier_id)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from supplier where sup_id = $supplier_id");
        $row = $result->fetch_assoc();

        mysqli_close($con);

        if ($row == null)
            throw new RestException(404, 'supplier not found');

        return $this->encodeSupplierIntoJson($row);
    }

    /**
     * @url GET /
     * @return list of suppliers
     */
    public function index()
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from supplier");
        $suppliers = array();

        while ($row = mysqli_fetch_array($result)) {
            $suppliers[] = $this->encodeSupplierIntoJson($row);
        }
        mysqli_close($con);
        return $suppliers;

    }
}