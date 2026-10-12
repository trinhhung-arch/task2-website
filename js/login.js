const loginForm = document.getElementById("loginForm");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const togglePass = document.getElementById("togglePass");

// Show / hide the password
togglePass.addEventListener("click", function () {
  const isHidden = passwordInput.type === "password";
  passwordInput.type = isHidden ? "text" : "password";
  togglePass.textContent = isHidden ? "Hide" : "Show";
});

// Validate the form, then go to the home page
loginForm.addEventListener("submit", function (event) {
  event.preventDefault();

  const emailOk = emailInput.checkValidity();
  const passwordOk = passwordInput.checkValidity();

  emailInput.classList.toggle("is-invalid", !emailOk);
  passwordInput.classList.toggle("is-invalid", !passwordOk);

  if (!emailOk || !passwordOk) {
    return;
  }

  alert("Login successful!");
  window.location.href = "home.html";
});

// Clear the error as soon as the user fixes the field
[emailInput, passwordInput].forEach(function (input) {
  input.addEventListener("input", function () {
    if (input.checkValidity()) {
      input.classList.remove("is-invalid");
    }
  });
});
