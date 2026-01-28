<?php
header('Content-Type: application/json');
include '../config/db.php';

$data = json_decode(file_get_contents("php://input"), true);

$first_name = trim($data['first_name']);
$last_name  = trim($data['last_name']);
$email      = trim($data['email']);
$password   = $data['password'];
$confirm_password = $data['confirm_password'];

$errors = [];

/* ---------------- PASSWORD VALIDATION ---------------- */
if (strlen($password) < 8) $errors[] = "Password must be at least 8 characters";
if (!preg_match('/[A-Z]/', $password)) $errors[] = "Password must contain a capital letter";
if (!preg_match('/[0-9]/', $password)) $errors[] = "Password must contain a number";
if (!preg_match('/[!$%?*@#]/', $password)) $errors[] = "Password must contain a special character";
if ($password !== $confirm_password) $errors[] = "Passwords do not match";

if ($errors) {
    echo json_encode(['success'=>false,'errors'=>$errors]);
    exit;
}

/* ---------------- CHECK EMAIL ---------------- */
$stmt = $conn->prepare("SELECT id FROM users WHERE email=?");
$stmt->execute([$email]);
if ($stmt->rowCount() > 0) {
    echo json_encode(['success'=>false,'message'=>'Email already exists']);
    exit;
}

/* ---------------- INSERT USER ---------------- */
$hashed = password_hash($password, PASSWORD_DEFAULT);
$stmt = $conn->prepare(
    "INSERT INTO users (first_name,last_name,email,password) VALUES (?,?,?,?)"
);
$stmt->execute([$first_name,$last_name,$email,$hashed]);

$user_id = $conn->lastInsertId();

/* ---------------- SEND REGISTRATION EMAIL ---------------- */
$subject = "Welcome to Mobile User";
$message = "Hi $first_name,

Your account has been created successfully.

You can now log in and place orders.

Thanks,
Mobile User Team";

$headers = "From: Mobile User <no-reply@mobileuser.test>";

mail($email, $subject, $message, $headers);

/* ---------------- RESPONSE ---------------- */
echo json_encode([
    'success'=>true,
    'message'=>'Registration successful',
    'user_id'=>$user_id
]);
