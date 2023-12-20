<!-- PaymentDetails.vue -->
<template>
  <div class="payment-details-container">
    <h2 class="page-title">Payment Order Information</h2>

    <div class="table-container">
      <div class="left-column">
        <div v-for="(value, title) in paymentDetails" :key="title" class="table-row">
          <div class="table-cell component-title">{{ title }}</div>
        </div>
      </div>
      <div class="right-column">
        <div v-for="(value, title) in paymentDetails" :key="title" class="table-row">
          <div class="table-cell component-value">{{ value }}</div>
        </div>
      </div>
    </div>

    <div class="button-container">
      <button v-if="paymentDetails.status === 'Pending'" @click="signPayment">Sign Payment</button>
      <button @click="goBack" class="back-button">Go Back</button>
    </div>
  </div>
</template>
  
<script lang="ts">
export default {
  data() {
    return {
      paymentDetails: {
        id: 1,
        account: 'Account#4',
        description: 'Netflix',
        value: 16,
        currency: 'USD',
        requiredApprovals: 2,
        givenApprovals: 1,
        status: 'Pending',
      },
    };
  },
  methods: {
    signPayment() {
      // Add logic to handle signing payment
      console.log('Payment signed!');
      // You may want to update the status here after signing the payment
      this.paymentDetails.status = 'Signed'; // Adjust this line based on your logic
    },
    goBack() {
      // Navigate back to the previous page
      this.$router.go(-1);
    },
  },
};
</script>

<style scoped>
.payment-details-container {
  max-width: 600px;
  margin: auto;
}

.page-title {
  text-align: center;
  font-size: 1.5em;
  margin-bottom: 20px;
  color: #ffffff;
}

.table-container {
  display: flex;
  border: 1px solid #ccc;
  border-radius: 5px;
  overflow: hidden;
}

.left-column,
.right-column {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.table-row {
  display: flex;
  align-items: center;
  padding: 30px; /* Adjust the padding to control the height */
  border-bottom: 1px solid #ccc;
}

.table-cell {
  flex: 1;
  color: white; /* Set text color to white */
}

.component-title {
  font-weight: bold;
}

.component-value {
  text-align: center;
  color: white; /* Set text color to white */
}

.button-container {
  margin-top: 20px;
}

button, .back-button {
  padding: 10px;
  margin-right: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  text-decoration: none; /* Remove default link underline */
  display: inline-block;
}

button:hover, .back-button:hover {
  background-color: #45a049;
}
</style>
