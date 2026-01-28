<?php
include '../config/db.php';
include '../config/session.php';
require_login();

$user_id = $_SESSION['user_id'];
$data = json_decode(file_get_contents("php://input"), true);

// ---------------- GET ORDERS ----------------
if($_SERVER['REQUEST_METHOD']==='GET'){

    $stmt = $conn->prepare("
        SELECT o.id, o.total, t.tracking_number, t.status, o.whitelist_address_id,
               a.address_line AS a_line, a.city AS a_city, a.state AS a_state, a.zip AS a_zip, a.country AS a_country,
               w.address_line AS b_line, w.city AS b_city, w.state AS b_state, w.zip AS b_zip, w.country AS b_country
        FROM orders o
        LEFT JOIN order_tracking t ON t.order_id=o.id AND t.deleted_at IS NULL
        LEFT JOIN addresses a ON a.id=o.address_id
        LEFT JOIN addresses_whitelist w ON w.id=o.whitelist_address_id
        WHERE o.user_id=? AND o.deleted_at IS NULL
        ORDER BY o.created_at DESC
    ");
    $stmt->execute([$user_id]);
    $orders = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Fetch whitelist addresses
    $stmtB = $conn->prepare("SELECT * FROM addresses_whitelist WHERE user_id=?");
    $stmtB->execute([$user_id]);
    $setB = $stmtB->fetchAll(PDO::FETCH_ASSOC);

    foreach($orders as &$o){
        // Delivery address logic
        if($o['b_line']){
            $o['delivery_address'] = "{$o['b_line']}, {$o['b_city']}, {$o['b_state']} {$o['b_zip']}, {$o['b_country']}";
        } else {
            $o['delivery_address'] = "{$o['a_line']}, {$o['a_city']}, {$o['a_state']} {$o['a_zip']}, {$o['a_country']}";
        }

        // Order items
        $stmtItems = $conn->prepare("
            SELECT p.name, oi.quantity, p.price
            FROM order_items oi
            JOIN products p ON p.id=oi.product_id
            WHERE oi.order_id=?
        ");
        $stmtItems->execute([$o['id']]);
        $o['items'] = $stmtItems->fetchAll(PDO::FETCH_ASSOC);

        $o['addresses_set_b'] = $setB;
    }

    echo json_encode(["success"=>true,"data"=>$orders]);
    exit;
}

// ---------------- PATCH: APPLY SET B ----------------
if($_SERVER['REQUEST_METHOD']==='PATCH'){
    $stmt = $conn->prepare("UPDATE orders SET whitelist_address_id=? WHERE id=? AND user_id=?");
    $stmt->execute([$data['whitelist_address_id'],$data['order_id'],$user_id]);
    echo json_encode(["success"=>true,"message"=>"Delivery address updated"]);
    exit;
}

// ---------------- DELETE: CANCEL ORDER ----------------
if($_SERVER['REQUEST_METHOD']==='DELETE'){
    $now = date('Y-m-d H:i:s');
    $stmt = $conn->prepare("UPDATE order_tracking SET deleted_at=? WHERE order_id=?");
    $stmt->execute([$now,$data['order_id']]);
    $stmt = $conn->prepare("UPDATE orders SET deleted_at=? WHERE id=? AND user_id=?");
    $stmt->execute([$now,$data['order_id'],$user_id]);
    echo json_encode(["success"=>true,"message"=>"Order cancelled"]);
    exit;
}
?>
