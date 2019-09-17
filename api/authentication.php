<?php
/**
 * Created by PhpStorm.
 * User: kassem
 * Date: 18/4/17
 * Time: 3:58 PM
 */

use Luracast\Restler\iAuthenticate;

class authentication implements iAuthenticate
{
    const KEY = '519428fdced64894bb10cd90bd87167c';

    function __isAllowed()
    {
        return isset($_GET['key']) && $_GET['key'] == authentication::KEY ? TRUE : FALSE;
    }
    public function __getWWWAuthenticateString()
    {
        return 'Query name="key"';
    }
    function key()
    {
        return authentication::KEY;
    }

}