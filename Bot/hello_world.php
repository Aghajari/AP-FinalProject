<?php
header("Content-Type: application/json");

$data = json_decode(file_get_contents('php://input'));

echo json_encode([
    "to"=>$data->chatId, 
    "text"=>"Hello World!"
]);

?>