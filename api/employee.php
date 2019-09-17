<?php

/**
 * Created by PhpStorm.
 * User: kassem
 * Date: 17/4/17
 * Time: 7:26 PM
 * @access protected
 */
class employee
{
    public function login($username, $password)
    {
        $con = mysqli_connect('localhost', 'root', '', 'supermarket');
        $con->set_charset("utf8");
        $result = mysqli_query($con, "select * from employee where emp_username ='$username' and emp_password='$password'");
        $row = $result->fetch_assoc();
        mysqli_close($con);
        if ($row == null)
            throw new RestException(401, 'username and password not found');

        $employee = new stdClass();
        $employee->id = $row['emp_id'];
        $employee->name = $row['emp_name'];
        $employee->role = $row['emp_role'];
        return $employee;
    }

}