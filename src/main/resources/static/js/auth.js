document.addEventListener("DOMContentLoaded", () => {
    const passwordInput = document.querySelector("#register-password");
    const rulesContainer = document.querySelector("#password-guidance");

    if (!passwordInput || !rulesContainer) {
        return;
    }

    const form = passwordInput.closest("form");
    const weakPasswords = new Set();
    let weakListLoaded = false;
    const ruleItems = {
        length: rulesContainer.querySelector('[data-rule="length"]'),
        uppercase: rulesContainer.querySelector('[data-rule="uppercase"]'),
        number: rulesContainer.querySelector('[data-rule="number"]'),
        special: rulesContainer.querySelector('[data-rule="special"]')
    };
    const successMessage = rulesContainer.querySelector("[data-password-success]");
    const weakWarning = rulesContainer.querySelector("[data-password-weak-warning]");

    const requirements = {
        length: (value) => value.length >= 8,
        uppercase: (value) => /[A-Z]/.test(value),
        number: (value) => /[0-9]/.test(value),
        special: (value) => /[^A-Za-z0-9]/.test(value)
    };

    const loadWeakPasswordList = async () => {
        try {
            const response = await fetch("/data/weak-passwords.txt", { cache: "no-store" });
            if (!response.ok) {
                return;
            }
            const text = await response.text();
            text.split(/\r?\n/).forEach((line) => {
                const cleaned = line.trim().toLowerCase();
                if (cleaned && !cleaned.startsWith("#")) {
                    weakPasswords.add(cleaned);
                }
            });
            weakListLoaded = true;
        } catch (error) {
            // Ignore fetch errors; weak list warning is optional client-side.
        }
    };

    const updateValidation = () => {
        const value = passwordInput.value || "";
        const trimmedValue = value.trim();
        let allValid = true;

        Object.keys(requirements).forEach((key) => {
            const isValid = requirements[key](value);
            allValid = allValid && isValid;
            if (ruleItems[key]) {
                ruleItems[key].classList.toggle("valid", isValid);
                ruleItems[key].classList.toggle("invalid", !isValid);
            }
        });

        successMessage.classList.toggle("show", allValid && value.length > 0);
        rulesContainer.classList.toggle("all-valid", allValid && value.length > 0);

        if (weakWarning) {
            const isWeak =
                weakListLoaded &&
                trimmedValue.length > 0 &&
                weakPasswords.has(trimmedValue.toLowerCase());
            weakWarning.classList.toggle("show", isWeak);
        }

        if (allValid || value.length === 0) {
            passwordInput.setCustomValidity("");
        } else {
            passwordInput.setCustomValidity("Password does not meet all requirements.");
        }
    };

    loadWeakPasswordList().finally(updateValidation);
    passwordInput.addEventListener("input", updateValidation);
    passwordInput.addEventListener("blur", updateValidation);

    if (form) {
        form.addEventListener("submit", (event) => {
            updateValidation();
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
        });
    }
});
