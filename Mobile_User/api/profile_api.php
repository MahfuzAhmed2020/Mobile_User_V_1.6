
<?php
//header('Content-Type: application/json');
//include '../config/db.php';
//include '../config/session.php';

//require_login();

//$user_id = $_SESSION['user_id'];

//$stmt = $conn->prepare("SELECT id, first_name, last_name, email, created_at FROM users WHERE id = ?");
//$stmt->execute([$user_id]);
//$user = $stmt->fetch(PDO::FETCH_ASSOC);

//echo json_encode(['success' => true, 'data' => $user]);
//?> 




<?php
header('Content-Type: application/json');

include '../config/db.php';
include '../config/session.php';
require_login();

$user_id = $_SESSION['user_id'];
$data = json_decode(file_get_contents("php://input"), true);

/* ===========================
   GET – VIEW PROFILE
=========================== */
if ($_SERVER['REQUEST_METHOD'] === 'GET') {

    $stmt = $conn->prepare("
        SELECT id, first_name, last_name, email, created_at
        FROM users
        WHERE id=?
    ");
    $stmt->execute([$user_id]);

    echo json_encode([
        "success" => true,
        "data" => $stmt->fetch(PDO::FETCH_ASSOC)
    ]);
    exit;
}

/* ===========================
   PATCH – UPDATE PROFILE
=========================== */
if ($_SERVER['REQUEST_METHOD'] === 'PATCH') {

    $first = trim($data['first_name']);
    $last  = trim($data['last_name']);
    $email = trim($data['email']);
    $password = $data['password'] ?? null;

    // Check email uniqueness
    $stmt = $conn->prepare("
        SELECT id FROM users WHERE email=? AND id!=?
    ");
    $stmt->execute([$email, $user_id]);

    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "success" => false,
            "message" => "Email already in use"
        ]);
        exit;
    }

    // Update with password
    if ($password) {
        $hash = password_hash($password, PASSWORD_DEFAULT);
        $stmt = $conn->prepare("
            UPDATE users 
            SET first_name=?, last_name=?, email=?, password=?
            WHERE id=?
        ");
        $stmt->execute([$first, $last, $email, $hash, $user_id]);
    } 
    // Update without password
    else {
        $stmt = $conn->prepare("
            UPDATE users 
            SET first_name=?, last_name=?, email=?
            WHERE id=?
        ");
        $stmt->execute([$first, $last, $email, $user_id]);
    }

    echo json_encode([
        "success" => true,
        "message" => "Profile updated successfully"
    ]);
    exit;
}
