<?php
// api/logout_api.php

session_start();

// Store user ID before destroying session
$loggedOutUser = $_SESSION['user_id'] ?? null;

// If no user is logged in
if (!$loggedOutUser) {
    echo json_encode([
        "success" => false,
        "message" => "No active session"
    ]);
    exit;
}

// Unset all session variables
$_SESSION = [];

// Destroy session
session_destroy();

// Delete session cookie
if (ini_get("session.use_cookies")) {
    $params = session_get_cookie_params();
    setcookie(
        session_name(),
        '',
        time() - 42000,
        $params["path"],
        $params["domain"],
        $params["secure"],
        $params["httponly"]
    );
}

echo json_encode([
    "success" => true,
    "message" => "Logged out successfully",
    "user_id" => $loggedOutUser
]);
