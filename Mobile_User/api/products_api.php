<?php
header('Content-Type: application/json');
include '../config/db.php';
include '../config/session.php';

require_login();

$stmt = $conn->prepare("SELECT * FROM products WHERE price <= 100 LIMIT 5");
$stmt->execute();
$products = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode(['success' => true, 'data' => $products]);
?>
