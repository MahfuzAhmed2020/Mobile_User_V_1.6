<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

/* Prevent redeclaration */
if (!function_exists('is_logged_in')) {

    function is_logged_in() {
        return isset($_SESSION['user_id']);
    }

    function require_login() {
        if (!is_logged_in()) {
            header('Location: ../ui/login.php');
            exit;
        }
    }
}
