const flash = document.getElementById("flash");
const avatarImg = document.getElementById("avatarImg");
const avatarInitial = document.getElementById("avatarInitial");
const avatarInput = document.getElementById("avatarInput");
const passwordForm = document.getElementById("passwordForm");

// Shows the uploaded photo, or the first letter of the name when there is none (404)
function loadAvatar() {
  avatarImg.onload = function () {
    avatarImg.hidden = false;
    avatarInitial.hidden = true;
  };
  avatarImg.onerror = function () {
    avatarImg.hidden = true;
    avatarInitial.hidden = false;
  };
  avatarImg.src = "/api/me/avatar?t=" + Date.now();
}

requireUser().then(function (user) {
  document.getElementById("headName").textContent = user.name;
  document.getElementById("profileName").textContent = user.name;
  document.getElementById("profileEmail").textContent = user.email;
  avatarInitial.textContent = user.name.charAt(0).toUpperCase();
  loadAvatar();
});

avatarInput.addEventListener("change", async function () {
  const file = avatarInput.files[0];
  if (!file) {
    return;
  }
  if (file.size > 2 * 1024 * 1024) {
    showFlash(flash, "Image must be 2 MB or smaller.", true);
    avatarInput.value = "";
    return;
  }
  const form = new FormData();
  form.append("file", file);
  const res = await fetch("/api/me/avatar", { method: "POST", body: form }).catch(function () { return null; });
  avatarInput.value = "";
  if (!res || !res.ok) {
    const data = res ? await res.json().catch(function () { return {}; }) : {};
    showFlash(flash, data.message || "Could not upload the photo. Please try again.", true);
    return;
  }
  showFlash(flash, "Profile photo updated.");
  loadAvatar();
});

passwordForm.addEventListener("submit", async function (event) {
  event.preventDefault();
  const current = passwordForm.currentPassword.value;
  const next = passwordForm.newPassword.value;

  if (!current || next.length < 6) {
    showFlash(flash, "Enter your current password and a new password of at least 6 characters.", true);
    return;
  }
  if (next !== passwordForm.confirmPassword.value) {
    showFlash(flash, "The new passwords do not match.", true);
    return;
  }

  const button = passwordForm.querySelector("button");
  button.disabled = true;
  const { ok, data } = await api("/api/me/password", "POST", { currentPassword: current, newPassword: next });
  button.disabled = false;

  if (!ok) {
    showFlash(flash, data.message || "Could not update the password.", true);
    return;
  }
  passwordForm.reset();
  showFlash(flash, "Password updated.");
});
