<?php
include '../config/db.php';
include '../config/session.php';

// No need to call session_start() here
// session_start(); // REMOVE this line

$data = json_decode(file_get_contents("php://input"), true);

$email = $data['email'] ?? '';
$password = $data['password'] ?? '';

if (!$email || !$password) {
    echo json_encode([
        "success" => false,
        "message" => "Email and password are required"
    ]);
    exit;
}

// Fetch user by email
$stmt = $conn->prepare("SELECT id, first_name, last_name, password FROM users WHERE email = ?");
$stmt->execute([$email]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

if ($user && password_verify($password, $user['password'])) {
    // Store session
    $_SESSION['user_id'] = $user['id'];
    $_SESSION['first_name'] = $user['first_name'];
    $_SESSION['last_name'] = $user['last_name'];

    echo json_encode([
        "success" => true,
        "user" => [
            "id" => $user['id'],
            "first_name" => $user['first_name'],
            "last_name" => $user['last_name']
        ]
    ]);
    exit;
} else {
    echo json_encode([
        "success" => false,
        "message" => "Invalid email or password"
    ]);
    exit;
}
