<template>
  <div class="create-payment">
    <h2>Create Payment</h2>
    <form @submit.prevent="createPayment">
      <div class="form-group">
        <label for="destination">Destination:</label>
        <input type="text" v-model="destination" id="destination" required />
      </div>
      <div class="form-group">
        <label for="amount">Amount:</label>
        <input type="number" v-model="amount" id="amount" required />
      </div>
      <div class="form-group">
        <label for="currency">Currency:</label>
        <select v-model="currency" id="currency" required>
          <option value="EUR">EUR</option>
          <option value="USD">USD</option>
          <option value="GBP">GBP</option>
        </select>
      </div>
      <div class="form-group">
        <label for="date">Date to be processed:</label>
        <input type="date" v-model="date" id="date" required />
      </div>
      <div class="form-group">
        <label for="description">Description:</label>
        <textarea v-model="description" id="description" required></textarea>
      </div>
      <div class="form-group">
        <label for="sourceAccount">Source Account:</label>
        <select v-model="sourceAccount" id="sourceAccount" required>
          <option value="account1">Account 1</option>
          <option value="account2">Account 2</option>
          <!-- Add options dynamically based on user accounts from the backend -->
        </select>
      </div>
      <button type="submit">Submit Payment</button>
    </form>
    <BottomBar />
  </div>
  <LogoutButton />
</template>

<script lang="ts">
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue';
export default {
  data() {
    return {
      destination: '',
      amount: null,
      currency: '',
      date: '',
      description: '',
      sourceAccount: '' // Added for source account selection
    }
  },
  methods: {
    createPayment() {
      // Implement your logic to submit the payment details to the server
      console.log('Payment details submitted:', {
        destination: this.destination,
        amount: this.amount,
        currency: this.currency,
        date: this.date,
        description: this.description,
        sourceAccount: this.sourceAccount
      })
    }
  },
  components: {
    BottomBar,
    LogoutButton
  }
}
</script>

<style scoped>
.create-payment {
  font-size: 20px;
  max-width: 600px;
  margin: auto;
  padding: 20px;
  text-align: center;
  color: #fff; /* Light text color */
}

h2 {
  color: #fff; /* Light text color */
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  color: #ccc; /* Light text color */
}

input,
textarea,
select {
  width: 100%;
  padding: 10px;
  box-sizing: border-box;
  border: 1px solid #555; /* Darker border color */
  border-radius: 4px;
  background-color: #444; /* Darker background color */
  color: #fff; /* Light text color */
}

/* Style the placeholder text of the date input */
input[type='date']::placeholder {
  color: #fff; /* Light text color */
}

/* Style the calendar icon in the date input */
input[type='date']::-webkit-calendar-picker-indicator {
  filter: invert(1); /* Invert the color (white) */
}

button {
  background-color: #4caf50;
  color: white;
  padding: 10px 15px;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>
