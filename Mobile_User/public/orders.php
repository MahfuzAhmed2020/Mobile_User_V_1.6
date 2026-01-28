<?php
include '../config/session.php';
require_login();
?>
<!DOCTYPE html>
<html>
<head>
<title>My Orders</title>
<style>
.container{max-width:900px;margin:auto;font-family:sans-serif}
.header{display:flex;justify-content:space-between;align-items:center}
.order{border:1px solid #ccc;padding:15px;margin-bottom:15px;border-radius:8px}
.btn{padding:6px 10px;background:#667eea;color:white;border:none;border-radius:5px;cursor:pointer;margin-right:5px}
.link{color:#667eea;cursor:pointer;text-decoration:underline}
.box{margin-top:10px}
.prf{color:white;padding:6px 10px;background:lightblue;border:none;border-radius:5px;cursor:pointer;margin-right:5px}
</style>
</head>
<body>

<div class="container">

<h1 class="prf">
User Name: <?php echo htmlspecialchars($_SESSION['first_name'].' '.$_SESSION['last_name']); ?>
</h1>

<div class="header">
    <h1>My Orders</h1>
    <button class="btn" onclick="logout()">Logout</button>
</div>

<div id="orders"></div>

<a href="/Mobile_User/public/profile.php" class="btn">Back</a>

</div>

<script>
async function fetchOrders(){
    const res = await fetch('/Mobile_User/api/orders_api.php');
    const json = await res.json();
    const div = document.getElementById('orders');
    div.innerHTML = '';

    if(!json.success || json.data.length === 0){
        div.innerHTML = '<p>No orders found.</p>';
        return;
    }

    json.data.forEach(o => {

        let items = '';
        o.items.forEach(i=>{
            items += `<li>${i.name} x ${i.quantity} ($${i.price})</li>`;
        });

        let opts = '<option value="">Select new address</option>';
        o.addresses_set_b.forEach(b=>{
            opts += `<option value="${b.id}">
                ${b.address_line}, ${b.city}
            </option>`;
        });

        div.innerHTML += `
        <div class="order">
            <p><b>Order #</b> ${o.id}</p>
            <p><b>Tracking:</b> ${o.tracking_number}</p>
            <p><b>Status:</b> ${o.status || 'Processing'}</p>
            <p><b>Total:</b> $${o.total}</p>

            <p><b>Delivery Address:</b><br>${o.delivery_address}</p>

            <span class="link" onclick="toggle(${o.id})">
                Want to change/update the delivery address?
            </span>

            <div class="box" id="box-${o.id}" style="display:none">
                <select onchange="updateAddress(${o.id},this.value)">
                    ${opts}
                </select>
            </div>

            <ul>${items}</ul>

            <button class="btn" onclick="cancelOrder(${o.id})">
                Cancel Order
            </button>
        </div>`;
    });
}

function toggle(id){
    const box = document.getElementById('box-'+id);
    box.style.display = box.style.display==='none'?'block':'none';
}

async function updateAddress(orderId,addressId){
    if(!addressId) return;
    await fetch('/Mobile_User/api/orders_api.php',{
        method:'PATCH',
        headers:{'Content-Type':'application/json'},
        body:JSON.stringify({
            order_id:orderId,
            whitelist_address_id:addressId
        })
    });
    fetchOrders();
}

async function cancelOrder(id){
    if(!confirm('Cancel this order?')) return;
    await fetch('/Mobile_User/api/orders_api.php',{
        method:'DELETE',
        headers:{'Content-Type':'application/json'},
        body:JSON.stringify({order_id:id})
    });
    fetchOrders();
}

async function logout(){
    const res = await fetch('/Mobile_User/api/logout_api.php',{method:'POST'});
    const data = await res.json();
    if(data.success){
        window.location.href = '/Mobile_User/public/login.php';
    }
}

fetchOrders();
</script>

</body>
</html>
