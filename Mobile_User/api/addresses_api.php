<?php
include '../config/db.php';

$stmt = $conn->query("SELECT id, user_id, address_line, city, state, zip, country FROM addresses");
$addresses = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode([
    "success" => true,
    "data" => $addresses
]);
