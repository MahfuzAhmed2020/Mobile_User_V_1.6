<?php
header('Content-Type: application/json');

include '../config/db.php';
include '../config/session.php';
require_login();

$tracking = $_GET['tracking'] ?? '';

if (!$tracking) {
    echo json_encode([
        "success" => false,
        "message" => "Tracking number required"
    ]);
    exit;
}

$stmt = $conn->prepare("
    SELECT 
        o.id,
        o.total,
        t.tracking_number,
        t.status
    FROM order_tracking t
    JOIN orders o ON o.id = t.order_id
    WHERE t.tracking_number = ?
      AND o.user_id = ?
      AND o.deleted_at IS NULL
");
$stmt->execute([$tracking, $_SESSION['user_id']]);

$order = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$order) {
    echo json_encode([
        "success" => false,
        "message" => "Order not found"
    ]);
    exit;
}

$stmt = $conn->prepare("
    SELECT p.name, oi.quantity, p.price
    FROM order_items oi
    JOIN products p ON p.id = oi.product_id
    WHERE oi.order_id = ?
");
$stmt->execute([$order['id']]);
$order['items'] = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode([
    "success" => true,
    "data" => $order
]);
