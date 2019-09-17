<?php

require_once '../restler.php';

use Luracast\Restler\Restler;
$r = new Restler();
$r->setSupportedFormats('JsonFormat');
$r->addAPIClass('employee');
$r->addAPIClass('customer');
$r->addAPIClass('supplier');
$r->addAPIClass('product');
$r->addAPIClass('purchaseorder');
$r->addAPIClass('order');
$r->addAPIClass('report');
$r->addAuthenticationClass("authentication");
$r->handle();