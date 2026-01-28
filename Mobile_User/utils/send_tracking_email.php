<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require '../vendor/autoload.php'; // Make sure PHPMailer is installed

function sendTrackingEmail(PDO $conn, int $order_id) {
    // Fetch order and user info
    $stmt = $conn->prepare("
        SELECT o.id AS order_id, o.total, u.email, u.first_name, u.last_name,
               t.tracking_number, t.status
        FROM orders o
        JOIN users u ON u.id = o.user_id
        JOIN order_tracking t ON t.order_id = o.id
        WHERE o.id = ?
    ");
    $stmt->execute([$order_id]);
    $order = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$order) return false;

    // 5 whitelisted emails
    $whitelistedEmails = [
        'whitelist1@example.com',
        'whitelist2@example.com',
        'whitelist3@example.com',
        'whitelist4@example.com',
        'whitelist5@example.com'
    ];

    $mail = new PHPMailer(true);

    try {
        // SMTP Configuration
        $mail->isSMTP();
        $mail->Host = 'smtp.example.com';
        $mail->SMTPAuth = true;
        $mail->Username = 'your_email@example.com';
        $mail->Password = 'your_email_password';
        $mail->SMTPSecure = 'tls';
        $mail->Port = 587;

        // Recipient
        $mail->setFrom('your_email@example.com', 'Your Shop');
        $mail->addAddress($order['email'], $order['first_name'].' '.$order['last_name']);
        foreach ($whitelistedEmails as $email) {
            $mail->addCC($email);
        }

        // Email Content
        $mail->isHTML(true);
        $mail->Subject = "Your Order #{$order['order_id']} Tracking Info";
        $mail->Body = "
            <h2>Thank you for your order!</h2>
            <p><strong>Order ID:</strong> {$order['order_id']}</p>
            <p><strong>Tracking Number:</strong> <b>{$order['tracking_number']}</b></p>
            <p><strong>Total:</strong> \${$order['total']}</p>
            <p><strong>Status:</strong> {$order['status']}</p>
        ";

        $mail->send();
        return true;

    } catch (Exception $e) {
        error_log("Mail error: ".$mail->ErrorInfo);
        return false;
    }
}
