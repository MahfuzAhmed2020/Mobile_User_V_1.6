<?php
header('Content-Type: application/json');
include '../config/db.php';

$data = json_decode(file_get_contents("php://input"), true);
$tracking_number = trim($data['tracking_number'] ?? '');

if (!$tracking_number) {
    echo json_encode([
        "success" => false,
        "message" => "Tracking number is required"
    ]);
    exit;
}

/* ======================
   FETCH ORDER BY TRACKING
====================== */
$stmt = $conn->prepare("
    SELECT 
        o.id AS order_id,
        o.total,
        o.created_at,
        t.tracking_number,
        t.status,
        u.first_name,
        u.last_name,
        u.email
    FROM order_tracking t
    JOIN orders o ON o.id = t.order_id
    JOIN users u ON u.id = o.user_id
    WHERE t.tracking_number = ?
      AND o.deleted_at IS NULL
");
$stmt->execute([$tracking_number]);
$order = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$order) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid tracking number"
    ]);
    exit;
}

/* ======================
   FETCH ORDER ITEMS
====================== */
$stmt = $conn->prepare("
    SELECT p.name, p.price, oi.quantity
    FROM order_items oi
    JOIN products p ON p.id = oi.product_id
    WHERE oi.order_id = ?
");
$stmt->execute([$order['order_id']]);
$items = $stmt->fetchAll(PDO::FETCH_ASSOC);

$order['items'] = $items;

/* ======================
   RESPONSE
====================== */
echo json_encode([
    "success" => true,
    "data" => $order
]);
