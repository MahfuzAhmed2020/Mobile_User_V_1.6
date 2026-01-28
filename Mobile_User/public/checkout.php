<!DOCTYPE html>
<html>
<head>
<title>Checkout</title>
<style>
.container {
    max-width: 500px;
    margin: auto;
    padding: 20px;
}
.btn {
    padding: 10px;
    background: #38a169;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}
.success-box {
    background: #e6fffa;
    border: 1px solid #38a169;
    padding: 15px;
    margin-top: 20px;
    display: none;
}
</style>
</head>

<body>

<div class="container">
    <h2>Checkout</h2>

    <button class="btn" onclick="checkout()">Place Order</button>

    <div id="successBox" class="success-box">
        <h3>✅ Order Successful!</h3>
        <p><b>Tracking Number:</b> <span id="trackingNumber"></span></p>
        <p>This page will refresh automatically.</p>
    </div>
</div>

<script>
async function checkout() {

    const res = await fetch('../api/checkout_api.php', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            card_number: "4111111111111111",
            address_id: 1
        })
    });

    const data = await res.json();

    if (data.success) {
        document.getElementById('successBox').style.display = 'block';
        document.getElementById('trackingNumber').innerText = data.tracking_number;

        setTimeout(() => {
            window.location.reload();
        }, 5000);
    } else {
        alert(data.message);
    }
}
</script>

</body>
</html>
