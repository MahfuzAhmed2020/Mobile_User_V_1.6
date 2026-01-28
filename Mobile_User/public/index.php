<?php
session_start();
include '../config/db.php';

$errors = [];
$success = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $first_name = trim($_POST['first_name']);
    $last_name  = trim($_POST['last_name']);
    $email      = trim($_POST['email']);
    $password   = $_POST['password'];
    $confirm_password = $_POST['confirm_password'];
    $agree_terms = isset($_POST['agree_terms']) ? true : false;

    // Validation
    if (!$agree_terms) {
        $errors[] = "You must agree to the terms and privacy policy.";
    }
    if (strlen($password) < 6) $errors[] = "Password must be at least 6 characters.";
    if (!preg_match('/[A-Z]/', $password)) $errors[] = "Password must contain at least one capital letter.";
    if (!preg_match('/[0-9]/', $password)) $errors[] = "Password must contain at least one number.";
    if (!preg_match('/[!$%?*@#]/', $password)) $errors[] = "Password must contain at least one special character (!, $, %, ?, *, @, #).";
    if ($password !== $confirm_password) $errors[] = "Passwords do not match.";

    // Check if email already exists
    $stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->execute([$email]);
    if ($stmt->rowCount() > 0) {
        $errors[] = "Email already exists.";
    }

    // If no errors, insert user
    if (empty($errors)) {
        $hashed = password_hash($password, PASSWORD_DEFAULT);
        $stmt = $conn->prepare("INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)");
        $stmt->execute([$first_name, $last_name, $email, $hashed]);
        $success = "Registration successful! You can now <a href='login.php'>login</a>.";
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>User Registration</title>
<style>
    /* Paste your original CSS here */
    * {margin:0; padding:0; box-sizing:border-box; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;}
    body {background:linear-gradient(135deg,#667eea 0%,#764ba2 100%); min-height:100vh; display:flex; justify-content:center; align-items:center; padding:20px;}
    .container {width:100%; max-width:500px; background:white; border-radius:15px; box-shadow:0 20px 60px rgba(0,0,0,0.3); overflow:hidden;}
    .header {background:linear-gradient(135deg,#667eea 0%,#764ba2 100%); color:white; padding:30px; text-align:center;}
    .header h1 {font-size:28px; margin-bottom:10px;}
    .header p {opacity:0.9; font-size:14px;}
    .form-container {padding:40px;}
    .form-group {margin-bottom:25px;}
    .form-row {display:flex; gap:20px; margin-bottom:25px;}
    .form-row .form-group {flex:1; margin-bottom:0;}
    label {display:block; margin-bottom:8px; font-weight:600; color:#333; font-size:14px;}
    input[type="text"], input[type="email"], input[type="password"] {width:100%; padding:14px; border:2px solid #e1e5e9; border-radius:8px; font-size:15px; transition:all 0.3s ease;}
    input:focus {outline:none; border-color:#667eea; box-shadow:0 0 0 3px rgba(102,126,234,0.1);}
    .btn {width:100%; padding:16px; background:linear-gradient(135deg,#667eea 0%,#764ba2 100%); color:white; border:none; border-radius:8px; font-size:16px; font-weight:600; cursor:pointer; transition:transform 0.2s ease, box-shadow 0.2s ease;}
    .btn:hover {transform:translateY(-2px); box-shadow:0 10px 20px rgba(102,126,234,0.3);}
    .message {padding:15px; border-radius:8px; margin-bottom:20px; text-align:center; font-weight:500;}
    .error {background:#f8d7da; color:#721c24; border:1px solid #f5c6cb;}
    .success {background:#d4edda; color:#155724; border:1px solid #c3e6cb;}
    .signin-link {text-align:center; margin-top:25px; padding-top:25px; border-top:1px solid #e1e5e9; font-size:14px;}
    .signin-link a {color:#667eea; text-decoration:none; font-weight:600;}
    .signin-link a:hover {text-decoration:underline;}
</style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>Mobile User: Create Account</h1>
        <p>Join our community today</p>
    </div>
    <div class="form-container">

        <!-- Display errors -->
        <?php if(!empty($errors)): ?>
            <div class="message error">
                <?php echo implode('<br>', $errors); ?>
            </div>
        <?php endif; ?>

        <!-- Display success -->
        <?php if($success): ?>
            <div class="message success">
                <?php echo $success; ?>
            </div>
        <?php endif; ?>

        <form method="POST" action="">
            <div class="form-row">
                <div class="form-group">
                    <label for="first_name">First Name</label>
                    <input type="text" id="first_name" name="first_name" required value="<?php echo $_POST['first_name'] ?? ''; ?>">
                </div>
                <div class="form-group">
                    <label for="last_name">Last Name</label>
                    <input type="text" id="last_name" name="last_name" required value="<?php echo $_POST['last_name'] ?? ''; ?>">
                </div>
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required value="<?php echo $_POST['email'] ?? ''; ?>">
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="form-group">
                <label for="confirm_password">Confirm Password</label>
                <input type="password" id="confirm_password" name="confirm_password" required>
            </div>
            <div class="checkbox-group">
                <input type="checkbox" id="agree_terms" name="agree_terms" <?php if(isset($_POST['agree_terms'])) echo 'checked'; ?> required>
                <label for="agree_terms">
                    I agree to the <a href="#">Terms and Conditions</a> and <a href="#">Privacy Policy</a>
                </label>
            </div>
            <button type="submit" class="btn">Sign Up</button>
        </form>

        <div class="signin-link">
            Already have an account? <a href="login.php">Sign In</a>
        </div>
    </div>
</div>
</body>
</html>
