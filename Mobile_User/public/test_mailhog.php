<?php
$to = "user@example.com";
$subject = "MailHog Test Email";
$message = "Hello! This email is captured by MailHog.";
$headers = "From: noreply@local.dev";

if(mail($to, $subject, $message, $headers)){
    echo "Email sent successfully!";
}else{
    echo "Email failed!";
}
