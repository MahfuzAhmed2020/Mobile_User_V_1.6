<?php
include '../config/session.php';
include '../config/db.php';

require_login();

// REMOVE user filter
$stmt = $conn->prepare("SELECT card_number, user_id FROM cards");
$stmt->execute();
$cards = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode([
    "success" => true,
    "data" => $cards
]);
