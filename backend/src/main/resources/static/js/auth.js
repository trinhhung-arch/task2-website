// Shared helpers for the login and register pages

// Sends JSON to the API; resolves to { ok, data } and never throws
async function postJson(url, body) {
  try {
    const res = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    const data = await res.json().catch(() => ({}));
    return { ok: res.ok, data };
  } catch {
    return { ok: false, data: { message: "Cannot reach the server. Please try again." } };
  }
}

// Shows a message in the alert box, or hides it when message is empty
function showAlert(alertBox, message) {
  alertBox.textContent = message;
  alertBox.hidden = !message;
}
