<template>
  <div class="payment-details-container">
    <h2 class="page-title">Payment Order Information</h2>

    <div class="table-container">
      <div class="left-column">
        <div v-for="(value, title) in paymentDetails" :key="title" class="table-row">
          <div class="table-cell component-title">{{ title }}</div>
        </div>
        <div class="table-row">
          <div class="table-cell component-title">Payment Hash</div>
        </div>
      </div>
      <div class="right-column">
        <div v-for="(value, title) in paymentDetails" :key="title" class="table-row">
          <div class="table-cell component-value">{{ value }}</div>
        </div>
        <div class="table-row">
          <div class="table-cell component-value">{{ generatePaymentHash() }}</div>
        </div>
        <div class="table-row">
          <div class="table-cell">
            <textarea v-model="signedHash" placeholder="Paste Signed Hash"></textarea>
            <div v-if="showErrorMessage" class="error-message">Error: Please enter the signed hash.</div>
          </div>
        </div>
      </div>
    </div>

    <div class="info-container">
      <p>
        Info: To sign a payment you must copy its hash, sign it with your private key, and then paste the signed hash.
      </p>
    </div>

    <div class="button-container">
      <button @click="signPayment">Sign Payment</button>
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
        status: 'Pending'
      },
      signedHash: '',
      showErrorMessage: false
    }
  },
  methods: {
    signPayment() {
      if (this.signedHash === '') {
        // Display an error message if the signed hash is empty
        this.showErrorMessage = true;
        return;
      }
      // Reset the error message when signing is successful
      this.showErrorMessage = false;
      
      // Add logic to handle signing payment
      console.log('Payment signed!')
      // You may want to update the status here after signing the payment
      this.paymentDetails.status = 'Signed' // Adjust this line based on your logic
    },
    goBack() {
      // Navigate back to the previous page
      this.$router.go(-1)
    },
    generatePaymentHash() {
      // Add logic to generate a unique payment hash
      // You can replace this with your actual hash generation logic
      return 'GeneratedHash123';
    }
  }
}
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

.error-message {
  color: red;
  margin-top: 5px;
}

textarea {
  width: 100%;
  padding: 10px;
  margin-top: 5px;
  color: #ffffff;
}

.info-container {
  margin-top: 20px;
}

button,
.back-button {
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

button:hover,
.back-button:hover {
  background-color: #45a049;
}
</style>
