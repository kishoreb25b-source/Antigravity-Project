document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("registration-form");
    const statusBanner = document.getElementById("status-banner");
    const submitBtn = document.getElementById("submit-btn");

    const API_URL = "http://localhost:8080/api/register";

    const fields = {
        name: {
            input: document.getElementById("name"),
            error: document.getElementById("name-error"),
            validate: (val) => {
                if (!val.trim()) return "Full name is required.";
                if (val.trim().length < 2) return "Name must be at least 2 characters.";
                return "";
            }
        },
        email: {
            input: document.getElementById("email"),
            error: document.getElementById("email-error"),
            validate: (val) => {
                if (!val.trim()) return "Email address is required.";
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(val.trim())) return "Please enter a valid email address.";
                return "";
            }
        },
        phone: {
            input: document.getElementById("phone"),
            error: document.getElementById("phone-error"),
            validate: (val) => {
                if (!val.trim()) return "Phone number is required.";
                const phoneRegex = /^[+]?[\d\s\-()]{7,20}$/;
                if (!phoneRegex.test(val.trim())) return "Please enter a valid phone number.";
                return "";
            }
        },
        password: {
            input: document.getElementById("password"),
            error: document.getElementById("password-error"),
            validate: (val) => {
                if (!val) return "Password is required.";
                if (val.length < 8) return "Password must be at least 8 characters long.";
                return "";
            }
        }
    };

    // Real-time error clearing when user types
    Object.values(fields).forEach(({ input, error }) => {
        input.addEventListener("input", () => {
            if (input.classList.contains("input-error")) {
                input.classList.remove("input-error");
                error.textContent = "";
            }
        });
    });

    // Form submission handler
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        hideBanner();

        let hasError = false;
        const formData = {};

        // 1. Run client-side validation
        Object.entries(fields).forEach(([fieldName, { input, error, validate }]) => {
            const errorMessage = validate(input.value);
            if (errorMessage) {
                input.classList.add("input-error");
                error.textContent = errorMessage;
                hasError = true;
            } else {
                input.classList.remove("input-error");
                error.textContent = "";
                formData[fieldName] = input.value.trim();
            }
        });

        if (hasError) {
            showBanner("Please fix the highlighted errors before submitting.", "error");
            return;
        }

        // 2. Prepare payload
        const payload = {
            name: formData.name,
            email: formData.email,
            phone: formData.phone,
            password: fields.password.input.value
        };

        // 3. Send HTTP POST request to plain Java backend
        setLoading(true);

        try {
            const response = await fetch(API_URL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (response.ok && data.success) {
                showBanner(data.message || "Registration successful!", "success");
                form.reset();
            } else {
                showBanner(data.message || "Registration failed.", "error");
            }
        } catch (err) {
            console.error("Network / Server error:", err);
            showBanner("Could not connect to the backend server. Is it running on http://localhost:8080?", "error");
        } finally {
            setLoading(false);
        }
    });

    function setLoading(isLoading) {
        submitBtn.disabled = isLoading;
        submitBtn.querySelector("span").textContent = isLoading ? "Creating Account..." : "Create Account";
    }

    function showBanner(message, type) {
        statusBanner.textContent = message;
        statusBanner.className = `status-banner ${type}`;
    }

    function hideBanner() {
        statusBanner.textContent = "";
        statusBanner.className = "status-banner hidden";
    }
});
