<?php
include '../config/session.php';
require_login();
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Profile</title>

<style>
.container { max-width:800px; margin:auto; padding:20px;}
h1 {color:#667eea;}
.products {display:flex; gap:20px; flex-wrap:wrap;}
.product {border:1px solid #ccc; padding:10px; width:150px; border-radius:8px;}
.btn {padding:6px 10px; background:#667eea; color:white; border:none; border-radius:5px; cursor:pointer;}
.cart {margin-top:20px; display:flex; flex-direction:column; gap:12px;}
.cart-item {
    border:1px solid #ccc;
    border-radius:8px;
    padding:12px;
    background:#f9f9f9;
    display:flex;
    flex-direction: column;
    width:250px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}
.cart-item h4 {
    margin:0 0 6px 0;
    color:#333;
}
.cart-item p {
    margin:2px 0;
}
.cart-item button {
    align-self:flex-start;
    margin-top:8px;
    background-color:#ff5c5c;
    color:white;
    border:none;
    padding:6px 10px;
    border-radius:4px;
    cursor:pointer;
}
.cart-item button:hover { background-color:#e04444; }
.small-btn { background:#555; margin-right:5px; }
</style>
</head>

<body>
<div class="container">

<h1>Welcome <?php echo htmlspecialchars($_SESSION['first_name'].' '.$_SESSION['last_name']); ?></h1>

<h2>Products under $100</h2>
<div id="products" class="products"></div>

<h2>Cart</h2>
<div id="cart" class="cart"></div>

<h2>Checkout</h2>
<form id="checkoutForm">
    <select id="card" required></select>
    <select id="address" required></select>
    <br><br>
    <button type="submit" class="btn">Checkout</button>
</form>
<br>
<button class="btn" onclick="goToOrders()">My Orders</button>
<br>
<button class="btn" onclick="logout()">Logout</button>

<button class="btn" onclick="goToUpdateProfile()">Update Profile</button>


</div>

<script>
// ---------------- LOGOUT ----------------
async function logout() {
    const res = await fetch('../api/logout_api.php', { method: 'POST' });
    const data = await res.json();
    if (data.success) window.location.href = "login.php";
}


// ---------------- PRODUCTS ----------------
async function fetchProducts(){
    const res = await fetch('../api/products_api.php');
    const data = await res.json();
    const container = document.getElementById('products');
    container.innerHTML = '';

    data.data.forEach(p=>{
        container.innerHTML += `
            <div class="product">
                <h3>${p.name}</h3>
                <p>$${p.price}</p>
                <button class="btn" onclick="addToCart(${p.id})">Add to Cart</button>
            </div>`;
    });
}

// ---------------- CART ----------------
async function fetchCart(){
    const res = await fetch('../api/cart_api.php');
    const data = await res.json();
    const container = document.getElementById('cart');
    container.innerHTML = '';

    if (data.data.length === 0) {
        container.innerHTML = "<p>Cart is empty</p>";
        return;
    }

    data.data.forEach(c=>{
        container.innerHTML += `
            <div class="cart-item">
                <h4>${c.name}</h4>
                <p>Price: $${c.price}</p>
                <p>Quantity: ${c.quantity}</p>
                <p>Total: $${(c.price*c.quantity).toFixed(2)}</p>
                <div>
                    <button class="btn small-btn" onclick="updateQty(${c.product_id}, ${c.quantity-1})">−</button>
                    <button class="btn small-btn" onclick="updateQty(${c.product_id}, ${c.quantity+1})">+</button>
                    <button class="btn small-btn" onclick="removeItem(${c.product_id})">Remove</button>
                </div>
            </div>`;
    });
}

// ---------------- ADD TO CART ----------------
async function addToCart(id){
    await fetch('../api/cart_api.php',{
        method:'POST',
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify({product_id:id})
    });
    fetchCart();
}

// ---------------- UPDATE QTY ----------------
async function updateQty(pid, qty){
    await fetch('../api/cart_api.php',{
        method:'PATCH',
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify({product_id: pid, quantity: qty})
    });
    fetchCart();
}

// ---------------- REMOVE ITEM ----------------
async function removeItem(pid){
    await fetch('../api/cart_api.php',{
        method:'DELETE',
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify({product_id: pid})
    });
    fetchCart();
}

// ---------------- CARDS & ADDRESSES ----------------
async function populateCardsAndAddresses() {

    const cards = [
        '4111111111111111',
        '4222222222222222',
        '5555555555554444',
        '378282246310005',
        '6011111111111117'
    ];

    const cardSelect = document.getElementById('card');
    cardSelect.innerHTML = '<option value="">Select Card</option>';
    cards.forEach(c => cardSelect.innerHTML += `<option value="${c}">${c}</option>`);

    const addresses = [
        { id:1, text:'123 Main Street, New York, NY 10001, USA' },
        { id:2, text:'456 Oak Avenue, Los Angeles, CA 90001, USA' },
        { id:3, text:'789 Pine Road, Chicago, IL 60601, USA' },
        { id:4, text:'101 Maple Lane, Houston, TX 77001, USA' },
        { id:5, text:'202 Cedar Blvd, Phoenix, AZ 85001, USA' }
    ];

    const addrSelect = document.getElementById('address');
    addrSelect.innerHTML = '<option value="">Select Address</option>';
    addresses.forEach(a => addrSelect.innerHTML += `<option value="${a.id}">${a.text}</option>`);
}

// ---------------- CHECKOUT ----------------
document.getElementById('checkoutForm').addEventListener('submit', async e=>{
    e.preventDefault();

    const res = await fetch('../api/checkout_api.php',{
        method:'POST',
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify({
            card_number: document.getElementById('card').value,
            address_id: document.getElementById('address').value
        })
    });

    const data = await res.json();

    if (data.success) {
        alert(`${data.message}\nTracking Number: ${data.tracking_number}`);
        window.location.href = 'orders.php';
        //window.location.href = '/Mobile_User/public/orders.php';
    } else {
        alert(data.message);
    }
});

// ---------------- INIT ----------------
fetchProducts();
fetchCart();
populateCardsAndAddresses();
function goToOrders() {
    window.location.href = 'orders.php';
}
//----------- navigate to update profile
function goToUpdateProfile() {
    window.location.href = 'update_profile.php';
    //window.location.href = '/Mobile_User/public/update_profile.php';
}
</script>

</body>
</html>
