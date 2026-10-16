// Load the signed-in user from the server session; anyone not logged in goes back to /login
fetch("/api/me")
  .then(function (res) {
    return res.ok ? res.json() : Promise.reject(res.status);
  })
  .then(function (user) {
    document.getElementById("heroName").textContent = user.name;
    document.getElementById("profileName").textContent = user.name;
    document.getElementById("profileEmail").textContent = user.email;
    document.getElementById("avatar").textContent = user.name.charAt(0).toUpperCase();
    document.getElementById("homeContent").hidden = false;
  })
  .catch(function () {
    window.location.replace("/login");
  });

document.getElementById("logoutButton").addEventListener("click", async function () {
  await fetch("/api/logout", { method: "POST" }).catch(function () {});
  window.location.replace("/login");
});
