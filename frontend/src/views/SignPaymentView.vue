<template>
  <div class="payment-details-container">
    <h2 class="page-title">Payment Order Information</h2>

    <div class="table-container">
      <div class="left-column">
        <div v-for="(value, title) in paymentDetails" :key="title" class="table-row">
          <div class="table-cell component-title">{{ title }} -{{ value }}</div>
        </div>
      </div>
    </div>
  </div>

  <div class="info-container">
    <p>
      Info: To sign a payment, you must copy its hash, sign it with your private key, and then paste
      the signed hash.
    </p>
  </div>

  <div class="button-container">
    <button @click="signPayment">Sign Payment</button>
    <button @click="goBack" class="back-button">Go Back</button>
  </div>
</template>

<script lang="ts">
import { ref, onMounted } from 'vue'
import { getPaymentById } from '@/services/api'
import { PaymentDto } from '@/models/PaymentDto'
import router from '@/router'
import { signPayment } from '@/services/api'

export default {
  data() {
    return {
      signedHash: '',
      showErrorMessage: false,
      paymentDetails: new PaymentDto() // Initialize with an instance of PaymentDto
    }
  },
  async mounted() {
    // Fetch payment details when the component is mounted
    await this.fetchPaymentById()
  },
  methods: {
    async fetchPaymentById() {
      try {
        // Extract the id from the URL
        const id = this.$route.params.id

        // Make the API request to get payment details by id
        this.paymentDetails = await getPaymentById(id.toString())

        console.log('Payment details:', this.paymentDetails)
      } catch (error) {
        console.error('Error fetching payment details:', error)
      }
    },
    async signPayment() {
      const paymentId = this.$route.params.id

      await signPayment(paymentId.toString(), this.paymentDetails.hash || '')
      this.$router.go(-1)
    },
    goBack() {
      // Navigate back to the previous page
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.payment-details-container {
  max-width: auto;
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
