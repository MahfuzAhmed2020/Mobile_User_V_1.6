<?php
include '../config/session.php';
require_login();
include '../config/db.php';

$user_id = $_SESSION['user_id'];

$stmt = $conn->prepare("SELECT first_name, last_name, email FROM users WHERE id=?");
$stmt->execute([$user_id]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);
?>

<!DOCTYPE html>
<html>
<head>
<title>Update Profile</title>
<style>
.container { max-width:500px; margin:auto; padding:20px;}
input { width:100%; padding:8px; margin:6px 0;}
.btn { padding:8px 12px; background:#667eea; color:white; border:none; border-radius:5px;}
hr { margin:20px 0; }
</style>
</head>

<body>
<div class="container">
<h2>Update Profile</h2>

<form id="updateForm">
    <input type="text" name="first_name" value="<?=htmlspecialchars($user['first_name'])?>" required>
    <input type="text" name="last_name" value="<?=htmlspecialchars($user['last_name'])?>" required>
    <input type="email" name="email" value="<?=htmlspecialchars($user['email'])?>" required>

    <hr>

    <h3>Change Password (OPTIONAL)</h3>
    <input type="password" name="old_password" placeholder="Old Password">
    <input type="password" name="new_password" placeholder="New Password">

    <button class="btn">Update</button>
</form>

<br>
<button class="btn" onclick="goBack()">Back</button>
<button class="btn" onclick="logout()">Logout</button>
</div>

<script>
    
    // ---------------- LOGOUT ----------------
async function logout() {
    const res = await fetch('../api/logout_api.php', { method: 'POST' });
    const data = await res.json();
    if (data.success) window.location.href = "login.php";
}

document.getElementById('updateForm').addEventListener('submit', async e => {
    e.preventDefault();

    const form = new FormData(e.target);
    const obj = Object.fromEntries(form.entries());

    const res = await fetch('../api/update_profile_api.php', {
        method:'POST',
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify(obj)
    });

    const data = await res.json();

    if (data.success) {
        alert(data.message);
        window.location.href = 'profile.php';
    } else {
        alert(data.message);
    }
});

function goBack(){
    window.location.href = 'profile.php';
}

</script>
</body>
</html>
