const loginForm = document.getElementById("loginForm");
const formAlert = document.getElementById("formAlert");
const submitButton = loginForm.querySelector("button[type=submit]");
const modal = document.getElementById("successModal");
const modalButton = document.getElementById("modalButton");

function openModal(title, text, buttonText, onContinue) {
  document.getElementById("modalTitle").textContent = title;
  document.getElementById("modalText").textContent = text;
  modalButton.textContent = buttonText;
  modalButton.onclick = onContinue;
  modal.hidden = false;
  modalButton.focus();
}

// Coming from the register page: prefill the email and confirm the sign-up
const registeredEmail = sessionStorage.getItem("registeredEmail");
if (registeredEmail) {
  sessionStorage.removeItem("registeredEmail");
  loginForm.email.value = registeredEmail;
  openModal("Account created", "Sign up successful! Please log in.", "Continue to log in", function () {
    modal.hidden = true;
    loginForm.password.focus();
  });
}

loginForm.addEventListener("submit", async function (event) {
  event.preventDefault();

  if (!loginForm.checkValidity()) {
    showAlert(formAlert, "Please enter a valid email and a password of at least 6 characters.");
    return;
  }

  submitButton.disabled = true;
  const { ok, data } = await postJson("/api/login", {
    email: loginForm.email.value,
    password: loginForm.password.value,
  });
  submitButton.disabled = false;

  if (!ok) {
    showAlert(formAlert, data.message || "Login failed. Please try again.");
    loginForm.password.value = "";
    return;
  }

  showAlert(formAlert, "");
  openModal("Login successful", "You're signed in. Continue to your home page.", "Continue to home", function () {
    window.location.href = "/home";
  });
});
