package com.test.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.server.ResponseStatusException;

// Each user only ever sees and changes their own tasks
@RestController
@RequestMapping("/api/todos")
public class TodoController {

	private final TodoRepository todoRepository;
	private final UserService userService;

	public TodoController(TodoRepository todoRepository, UserService userService) {
		this.todoRepository = todoRepository;
		this.userService = userService;
	}

	@GetMapping
	public List<TodoItem> list(@SessionAttribute(name = UserController.SESSION_USER_ID, required = false) Long userId) {
		Users user = userService.requireUser(userId);
		return todoRepository.findByUserIdOrderByIdDesc(user.getId()).stream().map(TodoItem::of).toList();
	}

	@PostMapping
	public ResponseEntity<TodoItem> add(@SessionAttribute(name = UserController.SESSION_USER_ID, required = false) Long userId,
			@RequestBody NewTodoRequest request) {
		Users user = userService.requireUser(userId);
		String title = request.title() == null ? "" : request.title().trim();
		if (title.isEmpty() || title.length() > 200) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please describe the task in 1 to 200 characters");
		}
		Todo todo = todoRepository.save(new Todo(user.getId(), title, request.dueDate()));
		return ResponseEntity.status(HttpStatus.CREATED).body(TodoItem.of(todo));
	}

	@PatchMapping("/{id}")
	public TodoItem setDone(@SessionAttribute(name = UserController.SESSION_USER_ID, required = false) Long userId,
			@PathVariable Long id, @RequestBody DoneRequest request) {
		Todo todo = findOwn(userId, id);
		todo.setDone(request.done());
		return TodoItem.of(todoRepository.save(todo));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@SessionAttribute(name = UserController.SESSION_USER_ID, required = false) Long userId,
			@PathVariable Long id) {
		todoRepository.delete(findOwn(userId, id));
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/export")
	public ResponseEntity<byte[]> export(@SessionAttribute(name = UserController.SESSION_USER_ID, required = false) Long userId) {
		List<List<String>> rows = list(userId).stream()
				.map(t -> List.of(t.title(), t.dueDate() == null ? "" : t.dueDate(), t.done() ? "Done" : "Pending"))
				.toList();
		return Excel.download("todo-list.xlsx", "ToDo", List.of("Task", "Due date", "Status"), rows);
	}

	private Todo findOwn(Long userId, Long id) {
		Users user = userService.requireUser(userId);
		return todoRepository.findByIdAndUserId(id, user.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
	}

	public record NewTodoRequest(String title, LocalDate dueDate) {
	}

	public record DoneRequest(boolean done) {
	}

	public record TodoItem(Long id, String title, String dueDate, boolean done) {

		static TodoItem of(Todo todo) {
			return new TodoItem(todo.getId(), todo.getTitle(),
					todo.getDueDate() == null ? null : todo.getDueDate().toString(), todo.isDone());
		}
	}
}
