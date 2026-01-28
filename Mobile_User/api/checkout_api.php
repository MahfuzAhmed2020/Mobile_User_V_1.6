<?php
header('Content-Type: application/json');

include '../config/db.php';
include '../config/session.php';
require_login();

$data = json_decode(file_get_contents("php://input"), true);

$card_number = $data['card_number'] ?? '';
$address_id  = $data['address_id'] ?? '';

if (!$card_number || !$address_id) {
    echo json_encode(["success"=>false,"message"=>"Card and address are required"]);
    exit;
}

/* =========================
   FETCH CART
========================= */
$stmt = $conn->prepare("
    SELECT c.product_id, p.name, p.price, c.quantity
    FROM cart c
    JOIN products p ON c.product_id = p.id
    WHERE c.user_id = ?
");
$stmt->execute([$_SESSION['user_id']]);
$cart_items = $stmt->fetchAll(PDO::FETCH_ASSOC);

if (!$cart_items) {
    echo json_encode(["success"=>false,"message"=>"Cart is empty"]);
    exit;
}

/* =========================
   CALCULATE TOTAL
========================= */
$total = 0;
foreach ($cart_items as $item) {
    $total += $item['price'] * $item['quantity'];
}

/* =========================
   CREATE ORDER
========================= */
$stmt = $conn->prepare("
    INSERT INTO orders (user_id, card_number, address_id, total)
    VALUES (?,?,?,?)
");
$stmt->execute([
    $_SESSION['user_id'],
    $card_number,
    $address_id,
    $total
]);
$order_id = $conn->lastInsertId();

/* =========================
   ORDER ITEMS
========================= */
$stmtItem = $conn->prepare("
    INSERT INTO order_items (order_id, product_id, quantity)
    VALUES (?,?,?)
");
foreach ($cart_items as $item) {
    $stmtItem->execute([
        $order_id,
        $item['product_id'],
        $item['quantity']
    ]);
}

/* =========================
   TRACKING
========================= */
$tracking_number = strtoupper(substr(md5(uniqid()),0,10));

$stmt = $conn->prepare("
    INSERT INTO order_tracking (order_id, tracking_number, status)
    VALUES (?,?,?)
");
$stmt->execute([$order_id, $tracking_number, 'Processing']);

/* =========================
   CLEAR CART
========================= */
$stmt = $conn->prepare("DELETE FROM cart WHERE user_id=?");
$stmt->execute([$_SESSION['user_id']]);

/* =========================
   SEND EMAIL WITH ORDER LINK
========================= */

// Fetch user email
$stmt = $conn->prepare("SELECT email, first_name FROM users WHERE id=?");
$stmt->execute([$_SESSION['user_id']]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

// Order page link
$order_link = "http://localhost/Mobile_User/public/orders.php?tracking=" . $tracking_number;

$subject = "Your Order Confirmation & Tracking";

$message = "Hi {$user['first_name']},

Your order has been placed successfully 🎉

Tracking Number: {$tracking_number}
Order Total: \${$total}
Status: Processing

👉 View your order here:
{$order_link}

Thank you for shopping with us.

Mobile User Team";

$headers = "From: Mobile User <no-reply@mobileuser.test>";

mail($user['email'], $subject, $message, $headers);


/* =========================
   SINGLE RESPONSE (IMPORTANT)
========================= */
echo json_encode([
    "success" => true,
    "message" => "Checkout successful",
    "tracking_number" => $tracking_number
]);
exit;
