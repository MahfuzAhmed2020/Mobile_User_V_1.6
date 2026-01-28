<?php
session_start();
include '../config/db.php';

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['success'=>false,'message'=>'Unauthorized']);
    exit;
}

$data = json_decode(file_get_contents("php://input"), true);

$first = trim($data['first_name']);
$last  = trim($data['last_name']);
$email = trim($data['email']);

$old_password = $data['old_password'] ?? '';
$new_password = $data['new_password'] ?? '';

$user_id = $_SESSION['user_id'];

/* Fetch current password */
$stmt = $conn->prepare("SELECT password FROM users WHERE id=?");
$stmt->execute([$user_id]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

/* If user wants to change password */
if ($old_password !== '' || $new_password !== '') {

    if ($old_password === '' || $new_password === '') {
        echo json_encode([
            'success'=>false,
            'message'=>'Old and new password are required'
        ]);
        exit;
    }

    if (!password_verify($old_password, $user['password'])) {
        echo json_encode([
            'success'=>false,
            'message'=>'Old password is incorrect'
        ]);
        exit;
    }

    $hashed = password_hash($new_password, PASSWORD_DEFAULT);

    $stmt = $conn->prepare(
        "UPDATE users 
         SET first_name=?, last_name=?, email=?, password=? 
         WHERE id=?"
    );
    $stmt->execute([$first,$last,$email,$hashed,$user_id]);

} else {
    /* Update profile info only */
    $stmt = $conn->prepare(
        "UPDATE users SET first_name=?, last_name=?, email=? WHERE id=?"
    );
    $stmt->execute([$first,$last,$email,$user_id]);
}

/* Update session */
$_SESSION['first_name'] = $first;
$_SESSION['last_name']  = $last;
$_SESSION['email']      = $email;

echo json_encode([
    'success'=>true,
    'message'=>'Profile updated successfully'
]);
