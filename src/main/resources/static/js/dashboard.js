// TEMP role (later fetch from backend/session)

document.getElementById("userRole").innerText = role;

// Hide admin-only cards
if (role === "STUDENT") {
    document.querySelectorAll(".admin-only").forEach(el => el.style.display = "none");
}
