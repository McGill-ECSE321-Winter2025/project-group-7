<script setup>
import { ref,onMounted, onUnmounted } from 'vue';
// *** TEMPORARY OVERRIDE ***
import { useRouter } from 'vue-router';

const loginBg = new URL('@/images/GameNest-login-bg.png', import.meta.url).href;
const defaultBg = new URL('@/images/GameNest-app-bg-plain.png', import.meta.url).href;

onMounted(() => {
  document.getElementById("app").style.backgroundImage = `url('${loginBg}')`;
});

onUnmounted(() => {
  document.getElementById("app").style.backgroundImage = `url('${defaultBg}')`;
});

const isActive = ref(false);

const activateRegister = () => {
  isActive.value = true;
};

const activateLogin = () => {
  isActive.value = false;
};

// *** TEMPORARY OVERRIDE ***
// Use router for programmatic navigation
const router = useRouter();

// Method for handling override button click
const activateOverride = () => {
  router.push('/games'); // Navigate to the /games route
};
</script>

<template>
  <div :class="['container', { active: isActive }]">
    <div class="form-box login">
      <form action="#">
        <h1>Login</h1>
        <div class="input-box">
          <input type="text" placeholder="Username" required>
          <i class='bx bxs-user'></i>
        </div>
        <div class="input-box">
          <input type="password" placeholder="Password" required>
          <i class='bx bxs-lock-alt'></i>
        </div>
        <p></p>
        <button type="submit" class="btn">Login</button>
      </form>
    </div>

    <div class="form-box register">
      <form action="#">
        <h1>Sign Up</h1>
        <div class="input-box">
          <input type="text" placeholder="Username" required>
          <i class='bx bxs-user'></i>
        </div>
        <div class="input-box">
          <input type="email" placeholder="Email" required>
          <i class='bx bxs-envelope'></i>
        </div>
        <div class="input-box">
          <input type="password" placeholder="Password" required>
          <i class='bx bxs-lock-alt'></i>
        </div>
        <button type="submit" class="btn">Sign Up</button>
      </form>
    </div>

    <div class="toggle-box">
      <div class="toggle-panel toggle-left">
        <h1>Hello, Welcome!</h1>
        <p>Don't have an account?</p>
        <button class="btn register-btn" @click="activateRegister">Sign Up</button>
      </div>

      <div class="toggle-panel toggle-right">
        <h1>Welcome Back!</h1>
        <p>Already have an account?</p>
        <button class="btn login-btn" @click="activateLogin">Login</button>
      </div>
    </div>
  </div>

   <!-- *** TEMPORARY OVERRIDE *** -->
    <div class="override-button">
        <button class="btn override-btn" @click="activateOverride">Override</button>
    </div>
</template>

<style scoped>
/* Background Image */
body {
  background-image: url('/GameNest-login-bg.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  min-height: 100vh;
  min-width: 100vw; 
  width: 100%;
}

/* Login Box */
@import url('https://fonts.googleapis.com/css2?family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&display=swap');

*{
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: "Poppins", sans-serif;
    text-decoration: none;
    list-style: none;
}

body{
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(90deg, #e2e2e2, #c9d6ff);
}

/* *** TEMPORARY OVERRIDE *** */
.override-button{
    position: absolute;
    top: 1rem;
    width: 10%;
    height: 48px;
    background: #b96d39;
    border-radius: 8px;
    box-shadow: 0 0 10px rgba(0, 0, 0, .1);
    border: none;
    cursor: pointer;
    font-size: 16px;
    color: #fff;
    font-weight: 600;
}
.container{
    position: absolute;
    bottom: 0;
    left: 50%;  /* Centers it horizontally */
    transform: translateX(-50%);
    width: 850px;
    height: 550px;
    background-color: rgba(59, 24, 4, 1);
    margin: 20px;
    border-radius: 30px;
    box-shadow: 0 0 30px rgba(0, 0, 0, .2);
    overflow: hidden;
}

    .container h1{
        font-size: 36px;
        margin: -10px 0;
    }

    .container p{
        font-size: 14.5px;
        margin: 15px 0;
    }

form{ width: 100%; color: #fff;}

.form-box{
    position: absolute;
    right: 0;
    width: 50%;
    height: 100%;
    background-color: rgba(59, 24, 4, 1);
    display: flex;
    align-items: center;
    color: #333;
    text-align: center;
    padding: 40px;
    z-index: 1;
    transition: .6s ease-in-out 1.2s, visibility 0s 1s;
}

    .container.active .form-box{ right: 50%; }

    .form-box.register{ visibility: hidden; }
        .container.active .form-box.register{ visibility: visible; }

.input-box{
    position: relative;
    margin: 30px 0;
}

    .input-box input{
        width: 100%;
        padding: 13px 50px 13px 20px;
        background: #eee;
        border-radius: 8px;
        border: none;
        outline: none;
        font-size: 16px;
        color: #333;
        font-weight: 500;
    }

        .input-box input::placeholder{
            color: #888;
            font-weight: 400;
        }
    
    .input-box i{
        position: absolute;
        right: 20px;
        top: 50%;
        transform: translateY(-50%);
        font-size: 20px;
    }

.forgot-link{ margin: -15px 0 15px; }
    .forgot-link a{
        font-size: 14.5px;
        color: #333;
    }

.btn{
    width: 100%;
    height: 48px;
    background: #b96d39;
    border-radius: 8px;
    box-shadow: 0 0 10px rgba(0, 0, 0, .1);
    border: none;
    cursor: pointer;
    font-size: 16px;
    color: #fff;
    font-weight: 600;
}

.toggle-box{
    position: absolute;
    width: 100%;
    height: 100%;
}

    .toggle-box::before{
        content: '';
        position: absolute;
        left: -250%;
        width: 300%;
        height: 100%;
        background: #b96d39;
        /* border: 2px solid red; */
        border-radius: 150px;
        z-index: 2;
        transition: 1.8s ease-in-out;
    }

        .container.active .toggle-box::before{ left: 50%; }

.toggle-panel{
    position: absolute;
    width: 50%;
    height: 100%;
    /* background: seagreen; */
    color: #fff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    z-index: 2;
    transition: .6s ease-in-out;
}

    .toggle-panel.toggle-left{ 
        left: 0;
        transition-delay: 1.2s; 
    }
        .container.active .toggle-panel.toggle-left{
            left: -50%;
            transition-delay: .6s;
        }

    .toggle-panel.toggle-right{ 
        right: -50%;
        transition-delay: .6s;
    }
        .container.active .toggle-panel.toggle-right{
            right: 0;
            transition-delay: 1.2s;
        }

    .toggle-panel p{ margin-bottom: 20px; }

    .toggle-panel .btn{
        width: 160px;
        height: 46px;
        background: transparent;
        border: 2px solid #fff;
        box-shadow: none;
    }

@media screen and (max-width: 650px){
    .container{ height: calc(100vh - 40px); }

    .form-box{
        bottom: 0;
        width: 100%;
        height: 70%;
    }

        .container.active .form-box{
            right: 0;
            bottom: 30%;
        }

    .toggle-box::before{
        left: 0;
        top: -270%;
        width: 100%;
        height: 300%;
        border-radius: 20vw;
    }

        .container.active .toggle-box::before{
            left: 0;
            top: 70%;
        }

        .container.active .toggle-panel.toggle-left{
            left: 0;
            top: -30%;
        }

    .toggle-panel{ 
        width: 100%;
        height: 30%;
    }
        .toggle-panel.toggle-left{ top: 0; }
        .toggle-panel.toggle-right{
            right: 0;
            bottom: -30%;
        }

            .container.active .toggle-panel.toggle-right{ bottom: 0; }
}

@media screen and (max-width: 400px){
    .form-box { padding: 20px; }

    .toggle-panel h1{font-size: 30px; }
}
</style>