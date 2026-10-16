const registerForm = document.getElementById("registerForm");
const formAlert = document.getElementById("formAlert");
const submitButton = registerForm.querySelector("button[type=submit]");

registerForm.addEventListener("submit", async function (event) {
  event.preventDefault();

  if (!document.getElementById("name").value.trim() || !registerForm.checkValidity()) {
    showAlert(formAlert, "Please enter your name, a valid email and a password of at least 6 characters.");
    return;
  }

  submitButton.disabled = true;
  const { ok, data } = await postJson("/api/users", {
    name: document.getElementById("name").value,
    email: registerForm.email.value,
    password: registerForm.password.value,
  });
  submitButton.disabled = false;

  if (!ok) {
    // e.g. "An account with this email already exists"
    showAlert(formAlert, data.message || "Sign up failed. Please try again.");
    registerForm.password.value = "";
    return;
  }

  // The login page shows the "Account created" popup and prefills this email
  sessionStorage.setItem("registeredEmail", data.email);
  window.location.href = "/login";
});
