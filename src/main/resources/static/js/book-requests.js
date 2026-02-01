document.addEventListener("DOMContentLoaded", () => {
    const message = document.querySelector("#request-limit-message");
    if (!message) {
        return;
    }

    const buttons = document.querySelectorAll("form[action='/requests/create'] button");
    const lastRequestAt = message.getAttribute("data-last-request-at");
    const canRequestAttr = message.getAttribute("data-can-request");

    const parseDate = (value) => {
        if (!value) return null;
        const parsed = new Date(value);
        return isNaN(parsed.getTime()) ? null : parsed;
    };

    const shouldBlock = () => {
        if (canRequestAttr === "false") {
            return true;
        }
        const last = parseDate(lastRequestAt);
        if (!last) return false;
        const diffMs = Date.now() - last.getTime();
        return diffMs < 24 * 60 * 60 * 1000;
    };

    const applyState = () => {
        const blocked = shouldBlock();
        const params = new URLSearchParams(window.location.search);
        const forced = params.get("requestLimit") === "true";
        message.classList.toggle("d-none", !(blocked || forced));
        buttons.forEach((btn) => {
            if (btn.getAttribute("data-unavailable") === "true") {
                return;
            }
            if (blocked) {
                btn.setAttribute("disabled", "disabled");
            } else {
                btn.removeAttribute("disabled");
            }
        });
    };

    applyState();
    setInterval(applyState, 60 * 1000);
});
