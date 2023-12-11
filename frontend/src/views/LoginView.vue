<template>
  <div class="login">
    <h1>Welcome to BlingBank!</h1>
    <form @submit.prevent="login">
      <div class="form-group">
        <input type="text" v-model="id" id="id" placeholder="Enter your ID" />
      </div>
      <div class="form-group">
        <div class="password-input">
          <input
            :type="showPassword ? 'text' : 'password'"
            v-model="password"
            id="password"
            placeholder="Enter your password"
          />
          <input type="checkbox" id="showPassword" v-model="showPassword" />
          <label for="showPassword">Show Password</label>
        </div>
      </div>
      <button type="submit">Login</button>
    </form>
  </div>
</template>

<script lang="ts">
export default {
  data() {
    return {
      id: '',
      password: '',
      showPassword: false
    }
  },
  methods: {
    login() {
      // Simulate authentication (replace with your actual authentication logic)
      if (this.id === 'user123' && this.password === 'password123') {
        // Redirect to the homepage or perform other actions
        this.$router.push('/homepage/1') // Assuming you're using Vue Router
      } else {
        alert('Invalid credentials. Please try again.')
      }
    }
  }
}
</script>

<style scoped>
.login {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  text-align: center;
}

.login h1 {
  color: #ffffff;
}

.login-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
}

input {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  box-sizing: border-box;
  color: #ffffff;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075);
}

.password-input {
  position: relative;
}

input[type='checkbox'] {
  margin-right: 5px;
  cursor: pointer;
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
