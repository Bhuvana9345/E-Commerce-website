function setupChatbot() {
    const mount = document.querySelector("#chatbotMount");
    if (!mount) return;
    mount.innerHTML = `
        <button class="chat-fab" id="chatOpen" title="StyleGPT AI">AI</button>
        <section class="chat-box hidden" id="chatBox">
            <div class="chat-head"><strong>Ask StyleGPT</strong><button class="secondary" id="chatClose">x</button></div>
            <div class="chat-messages" id="chatMessages">
                <div class="chat-empty">
                    <strong>Ask a fashion question in English</strong>
                    <span>I will answer only the question you type.</span>
                </div>
            </div>
            <form class="chat-form" id="chatForm">
                <input name="message" placeholder="Type your fashion question..." autocomplete="off">
                <button>Ask</button>
            </form>
        </section>`;

    const box = document.querySelector("#chatBox");
    const form = document.querySelector("#chatForm");
    const messages = document.querySelector("#chatMessages");

    document.querySelector("#chatOpen").addEventListener("click", () => box.classList.toggle("hidden"));
    document.querySelector("#chatClose").addEventListener("click", () => box.classList.add("hidden"));

    form.addEventListener("submit", async e => {
        e.preventDefault();
        if (!token()) {
            toast("Login to use StyleGPT chatbot");
            location.href = "login.html";
            return;
        }

        const input = form.message;
        const text = input.value.trim();
        if (!text) return;

        messages.querySelector(".chat-empty")?.remove();
        messages.insertAdjacentHTML("beforeend", `<div class="msg user">${text}</div>`);
        messages.insertAdjacentHTML("beforeend", `<div class="msg bot" id="chatThinking">Thinking...</div>`);
        input.value = "";
        messages.scrollTop = messages.scrollHeight;

        try {
            const data = await api("/api/chatbot/message", {
                method: "POST",
                body: JSON.stringify({ message: text })
            });
            const recs = data.recommendations?.length
                ? `<p>${data.recommendations.map(p => `<a href="product.html?id=${p.id}">${p.name}</a>`).join(" | ")}</p>`
                : "";
            document.querySelector("#chatThinking")?.remove();
            messages.insertAdjacentHTML("beforeend", `<div class="msg bot">${data.reply}${recs}</div>`);
        } catch (err) {
            document.querySelector("#chatThinking")?.remove();
            messages.insertAdjacentHTML("beforeend", `<div class="msg bot">${err.message}</div>`);
        }
        messages.scrollTop = messages.scrollHeight;
    });
}

document.addEventListener("DOMContentLoaded", setupChatbot);
