const BASE_URL = "http://localhost:8081/students";

const statusEl = document.getElementById("status");
const tbody = document.querySelector("#studentsTable tbody");

function setStatus(message, isError = false) {
  statusEl.textContent = message;
  statusEl.style.color = isError ? "#b42318" : "#475569";
}

function readStudentForm() {
  return {
    firstName: document.getElementById("firstName").value.trim(),
    lastName: document.getElementById("lastName").value.trim(),
    email: document.getElementById("email").value.trim(),
    course: document.getElementById("course").value.trim()
  };
}

function clearStudentForm() {
  document.getElementById("firstName").value = "";
  document.getElementById("lastName").value = "";
  document.getElementById("email").value = "";
  document.getElementById("course").value = "";
}

async function parseError(res) {
  try {
    const text = await res.text();
    return text || `Request failed with status ${res.status}`;
  } catch {
    return `Request failed with status ${res.status}`;
  }
}

async function getAllStudents() {
  try {
    const res = await fetch(BASE_URL);
    if (!res.ok) {
      throw new Error(await parseError(res));
    }
    const data = await res.json();
    renderTable(data);
    setStatus(`Loaded ${data.length} student(s). Connected to API and database.`);
  } catch (error) {
    setStatus(`Could not load students: ${error.message}`, true);
  }
}

async function getStudentById() {
  const id = document.getElementById("searchId").value.trim();
  if (!id) {
    setStatus("Enter a student ID first.", true);
    return;
  }

  try {
    const res = await fetch(`${BASE_URL}/${id}`);
    if (!res.ok) {
      throw new Error(await parseError(res));
    }
    const student = await res.json();
    renderTable([student]);
    setStatus(`Loaded student #${id}.`);
  } catch (error) {
    setStatus(`Could not find student #${id}, please enter a different ID.`, true);
  }
}

async function addStudent() {
  const student = readStudentForm();
  if (!student.firstName || !student.lastName) {
    setStatus("First name and last name are required.", true);
    return;
  }

  try {
    const res = await fetch(BASE_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(student)
    });

    if (!res.ok) {
      throw new Error(await parseError(res));
    }

    clearStudentForm();
    setStatus("Student added successfully.");
    await getAllStudents();
  } catch (error) {
    setStatus(`Could not add student, please try again.`, true);
  }
}

async function deleteStudent(id) {
  try {
    const res = await fetch(`${BASE_URL}/${id}`, {
      method: "DELETE"
    });

    if (!res.ok) {
      throw new Error(await parseError(res));
    }

    setStatus(`Deleted student #${id}.`);
    await getAllStudents();
  } catch (error) {
    setStatus(`Could not delete student #${id}: ${error.message}`, true);
  }
}

async function updateStudent(id) {
  const firstName = prompt("Enter new first name:");
  if (firstName === null) return;

  const lastName = prompt("Enter new last name:");
  if (lastName === null) return;

  const email = prompt("Enter new email:");
  if (email === null) return;

  const course = prompt("Enter new course:");
  if (course === null) return;

  const updatedStudent = {
    firstName: firstName.trim(),
    lastName: lastName.trim(),
    email: email.trim(),
    course: course.trim()
  };

  try {
    const res = await fetch(`${BASE_URL}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updatedStudent)
    });

    if (!res.ok) {
      throw new Error(await parseError(res));
    }

    setStatus(`Updated student #${id}.`);
    await getAllStudents();
  } catch (error) {
    setStatus(`Could not update student #${id}: ${error.message}`, true);
  }
}

function renderTable(students) {
  tbody.innerHTML = "";

  if (!students.length) {
    const emptyRow = document.createElement("tr");
    emptyRow.innerHTML = '<td data-label="Message" colspan="6">No students found.</td>';
    tbody.appendChild(emptyRow);
    return;
  }

  students.forEach((s) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td data-label="ID">${s.id}</td>
      <td data-label="First Name">${s.firstName ?? ""}</td>
      <td data-label="Last Name">${s.lastName ?? ""}</td>
      <td data-label="Email">${s.email ?? ""}</td>
      <td data-label="Course">${s.course ?? ""}</td>
      <td data-label="Actions" class="actions">
        <button type="button" data-action="edit" data-id="${s.id}">Edit</button>
        <button type="button" class="danger" data-action="delete" data-id="${s.id}">Delete</button>
      </td>
    `;
    tbody.appendChild(row);
  });
}

// Event wiring: JS is connected to HTML controls here.
document.getElementById("addBtn").addEventListener("click", addStudent);
document.getElementById("searchBtn").addEventListener("click", getStudentById);
document.getElementById("refreshBtn").addEventListener("click", getAllStudents);

tbody.addEventListener("click", async (event) => {
  const target = event.target;
  if (!(target instanceof HTMLButtonElement)) return;

  const id = target.dataset.id;
  const action = target.dataset.action;
  if (!id || !action) return;

  if (action === "edit") {
    await updateStudent(id);
  }

  if (action === "delete") {
    await deleteStudent(id);
  }
});

getAllStudents();
