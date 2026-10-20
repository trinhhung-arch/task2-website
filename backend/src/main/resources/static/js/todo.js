const flash = document.getElementById("flash");
const todoForm = document.getElementById("todoForm");
const taskList = document.getElementById("tasks");
let todos = [];

function render() {
  const done = todos.filter(function (t) { return t.done; }).length;
  document.getElementById("summary").textContent = done + " of " + todos.length + " tasks completed";
  document.getElementById("empty").hidden = todos.length > 0;

  taskList.replaceChildren();
  todos.forEach(function (todo) {
    const item = document.createElement("li");
    item.classList.toggle("done", todo.done);

    const box = document.createElement("input");
    box.type = "checkbox";
    box.checked = todo.done;
    box.id = "task-" + todo.id;
    box.addEventListener("change", function () { setDone(todo, box.checked); });

    const body = document.createElement("label");
    body.className = "task-body";
    body.htmlFor = box.id;
    const title = document.createElement("span");
    title.className = "title";
    title.textContent = todo.title;
    body.append(title);
    if (todo.dueDate) {
      const due = document.createElement("span");
      due.className = "due";
      due.textContent = "Due " + todo.dueDate;
      body.append(due);
    }

    const remove = document.createElement("button");
    remove.type = "button";
    remove.className = "small-pill";
    remove.textContent = "Delete";
    remove.setAttribute("aria-label", "Delete " + todo.title);
    remove.addEventListener("click", function () { removeTodo(todo); });

    item.append(box, body, remove);
    taskList.append(item);
  });
}

async function load() {
  const { ok, data } = await api("/api/todos");
  todos = ok && Array.isArray(data) ? data : [];
  render();
}

async function setDone(todo, done) {
  const { ok, data } = await api("/api/todos/" + todo.id, "PATCH", { done: done });
  if (!ok) {
    showFlash(flash, data.message || "Could not update the task.", true);
    await load();
    return;
  }
  todo.done = data.done;
  render();
}

async function removeTodo(todo) {
  const { ok, data } = await api("/api/todos/" + todo.id, "DELETE");
  if (!ok) {
    showFlash(flash, data.message || "Could not remove the task.", true);
    return;
  }
  todos = todos.filter(function (t) { return t.id !== todo.id; });
  render();
  showFlash(flash, "Task removed.");
}

todoForm.addEventListener("submit", async function (event) {
  event.preventDefault();
  const title = todoForm.title.value.trim();
  if (!title) {
    showFlash(flash, "Please describe the task first.", true);
    todoForm.title.focus();
    return;
  }

  const button = todoForm.querySelector("button");
  button.disabled = true;
  const { ok, data } = await api("/api/todos", "POST", {
    title: title,
    dueDate: todoForm.dueDate.value || null,
  });
  button.disabled = false;

  if (!ok) {
    showFlash(flash, data.message || "Could not add the task.", true);
    return;
  }
  todos.unshift(data);
  todoForm.reset();
  render();
  showFlash(flash, "Task added to your list.");
});

requireUser().then(load);
