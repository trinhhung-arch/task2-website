// Shared by every signed-in page: draws the header nav, checks the session, talks to the API

const NAV_LINKS = [
  ["/home", "Home"],
  ["/about", "About"],
  ["/profile", "Profile"],
  ["/login-history", "Login history"],
  ["/todo", "ToDo"],
  ["/contact", "Contact"],
];

function renderHeader() {
  const header = document.getElementById("siteHeader");
  header.className = "site-header";
  header.innerHTML =
    '<div class="wrap"><a href="/home" class="logo"><span class="logo-mark">S</span>SMS</a>' +
    '<nav class="site-nav" aria-label="Main"></nav></div>';
  const nav = header.querySelector("nav");

  NAV_LINKS.forEach(function ([href, label]) {
    const link = document.createElement("a");
    link.href = href;
    link.textContent = label;
    if (location.pathname === href) {
      link.setAttribute("aria-current", "page");
    }
    nav.append(link);
  });

  const logout = document.createElement("button");
  logout.type = "button";
  logout.className = "pill dark";
  logout.textContent = "Log out";
  logout.addEventListener("click", async function () {
    await fetch("/api/logout", { method: "POST" }).catch(function () {});
    location.replace("/login");
  });
  nav.append(logout);
}

// Resolves to the signed-in user and reveals <main>; anyone not logged in goes back to /login
async function requireUser() {
  renderHeader();
  const res = await fetch("/api/me").catch(function () { return null; });
  if (!res || !res.ok) {
    location.replace("/login");
    return new Promise(function () {}); // never resolves: the page is leaving
  }
  const user = await res.json();
  document.querySelector("main").hidden = false;
  return user;
}

// Calls the API; resolves to { ok, data } and never throws. A 401 means the session ended.
async function api(url, method, body) {
  try {
    const res = await fetch(url, {
      method: method || "GET",
      headers: body === undefined ? {} : { "Content-Type": "application/json" },
      body: body === undefined ? undefined : JSON.stringify(body),
    });
    if (res.status === 401) {
      location.replace("/login");
    }
    const data = res.status === 204 ? {} : await res.json().catch(function () { return {}; });
    return { ok: res.ok, data: data };
  } catch {
    return { ok: false, data: { message: "Cannot reach the server. Please try again." } };
  }
}

// Shows a green (or red) message bar, or hides it when message is empty
function showFlash(box, message, isError) {
  box.textContent = message;
  box.classList.toggle("error", Boolean(isError));
  box.hidden = !message;
}
