const API = "";

function token() { return localStorage.getItem("stylegpt_token"); }
function user() { return JSON.parse(localStorage.getItem("stylegpt_user") || "null"); }
function setSession(data) {
    localStorage.setItem("stylegpt_token", data.token);
    localStorage.setItem("stylegpt_user", JSON.stringify(data.user));
}
function logout() {
    localStorage.removeItem("stylegpt_token");
    localStorage.removeItem("stylegpt_user");
    location.href = "login.html";
}
async function api(path, options = {}) {
    const headers = options.headers || {};
    if (!(options.body instanceof FormData)) headers["Content-Type"] = "application/json";
    if (token()) headers.Authorization = `Bearer ${token()}`;
    const res = await fetch(API + path, { ...options, headers });
    if (res.status === 401 || res.status === 403) {
        if (!path.startsWith("/api/products")) {
            throw new Error("Please login with the correct account");
        }
    }
    if (!res.ok) {
        let message = "Request failed";
        try {
            const data = await res.json();
            message = data.message || Object.values(data)[0] || message;
        } catch (_) {}
        throw new Error(message);
    }
    if (res.status === 204) return null;
    const text = await res.text();
    return text ? JSON.parse(text) : null;
}
function rupees(value) { return `Rs.${Number(value || 0).toLocaleString("en-IN")}`; }
function img(src, category) { 
    if (src) return src;
    const cat = (category || "").toLowerCase();
    if (cat.includes("shoe")) return "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=600&q=80";
    if (cat.includes("dress") || cat.includes("saree") || cat.includes("kurti")) return "https://images.unsplash.com/photo-1515347619152-19e48cb6582a?auto=format&fit=crop&w=600&q=80";
    if (cat.includes("shirt")) return "https://images.unsplash.com/photo-1596755094514-f87e32f85e23?auto=format&fit=crop&w=600&q=80";
    if (cat.includes("pant")) return "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?auto=format&fit=crop&w=600&q=80";
    if (cat.includes("grocery") || cat.includes("snack") || cat.includes("food")) return "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=600&q=80";
    if (cat.includes("accessori") || cat.includes("watch")) return "https://images.unsplash.com/photo-1509319117193-57bab727e09d?auto=format&fit=crop&w=600&q=80";
    return "https://images.unsplash.com/photo-1445205170230-053b83016050?auto=format&fit=crop&w=600&q=80"; 
}
function toast(message) {
    const old = document.querySelector(".toast");
    if (old) old.remove();
    const el = document.createElement("div");
    el.className = "toast";
    el.textContent = message;
    document.body.appendChild(el);
    setTimeout(() => el.remove(), 3200);
}
function requireLogin() {
    if (!token()) {
        location.href = "login.html";
        return false;
    }
    return true;
}
function setupNav() {
    const applyTheme = () => {
        const isDark = localStorage.getItem("stylegpt_dark") === "true";
        document.body.classList.toggle("dark", isDark);
        document.querySelectorAll("[data-dark]").forEach(btn => {
            btn.textContent = "";
            btn.title = isDark ? "Light mode" : "Dark mode";
            btn.setAttribute("aria-label", isDark ? "Light mode" : "Dark mode");
            btn.setAttribute("aria-pressed", String(isDark));
        });
    };
    applyTheme();
    document.querySelectorAll("[data-dark]").forEach(btn => btn.onclick = () => {
        const next = !document.body.classList.contains("dark");
        localStorage.setItem("stylegpt_dark", String(next));
        applyTheme();
    });
    document.querySelectorAll("[data-logout]").forEach(btn => btn.onclick = logout);
    document.querySelectorAll("[data-user-name]").forEach(el => {
        const u = user();
        el.textContent = u ? u.name : "Guest";
    });
    document.querySelectorAll("[data-admin-link]").forEach(el => {
        const u = user();
        el.classList.toggle("hidden", !u || u.role !== "ROLE_ADMIN");
    });
    document.querySelectorAll("[data-auth-link]").forEach(el => el.classList.toggle("hidden", !!user()));
    document.querySelectorAll("[data-logout]").forEach(el => el.classList.toggle("hidden", !user()));
}
function productCard(p) {
    const saleBadge = Math.random() > 0.5 ? '<span class="badge sale">Sale</span>' : '';
    const rating = (4.0 + Math.random()).toFixed(1);
    const priceDisplay = p.discountPrice && p.discountPrice < p.price 
        ? `<span class="muted" style="text-decoration: line-through">${rupees(p.price)}</span> <strong>${rupees(p.discountPrice)}</strong>`
        : `<strong>${rupees(p.price)}</strong>`;
    return `
    <article class="product-card">
        ${saleBadge}
        <a href="product.html?id=${p.id}"><img src="${img(p.imageUrl, p.category)}" alt="${p.name}" onerror="this.onerror=null; this.src='https://images.unsplash.com/photo-1445205170230-053b83016050?auto=format&fit=crop&w=600&q=80';"></a>
        <div class="product-body">
            <span style="font-size: 0.75rem; color: var(--primary-color); font-weight: bold; text-transform: uppercase;">${p.category}</span>
            <strong style="display:block; margin: 4px 0;">${p.name}</strong>
            <span class="muted" style="font-size: 0.85rem">${p.brand} | ${p.color}</span>
            <div style="display:flex; justify-content: space-between; align-items: center; margin: 8px 0;">
                <div>${priceDisplay}</div>
                <div style="font-size: 0.8rem; color: #f59e0b;">★ ${rating}</div>
            </div>
            <div style="display:flex; gap: 4px;">
                <button style="flex:1; padding: 6px;" onclick="addToCart(${p.id})">Add to Cart</button>
                <button style="flex:1; padding: 6px; background-color: var(--primary-color);" onclick="addToCart(${p.id}); location.href='cart.html';">Buy Now</button>
            </div>
        </div>
    </article>`;
}
async function addToCart(productId, quantity = 1) {
    if (!requireLogin()) return;
    try {
        await api("/api/cart", { method: "POST", body: JSON.stringify({ productId, quantity }) });
        toast("Added to cart");
    } catch (e) { toast(e.message); }
}
async function toggleWishlist(productId) {
    if (!requireLogin()) return;
    try {
        await api(`/api/wishlist/${productId}`, { method: "POST" });
        toast("Wishlist updated");
    } catch (e) { toast(e.message); }
}

async function initLogin() {
    setupNav();
    const form = document.querySelector("#loginForm");
    if (!form) return;
    form.addEventListener("submit", async e => {
        e.preventDefault();
        const btn = form.querySelector("button");
        btn.textContent = "Logging in...";
        btn.disabled = true;
        try {
            const data = await api("/api/auth/login", {
                method: "POST",
                body: JSON.stringify({ email: form.email.value, password: form.password.value })
            });
            setSession(data);
            location.href = "index.html";
        } catch (err) { toast(err.message); }
        finally { btn.textContent = "Login"; btn.disabled = false; }
    });
}

async function initRegister() {
    setupNav();
    const form = document.querySelector("#registerForm");
    if (!form) return;
    form.addEventListener("submit", async e => {
        e.preventDefault();
        try {
            const data = await api("/api/auth/register", {
                method: "POST",
                body: JSON.stringify({ name: form.name.value, phone: form.phone.value, email: form.email.value, password: form.password.value })
            });
            setSession(data);
            location.href = "index.html";
        } catch (err) { toast(err.message); }
    });
}
async function initHome() {
    setupNav();
    const grid = document.querySelector("#productGrid");
    if (!grid) return;
    const category = document.querySelector("#category");
    const loadCategories = async () => {
        const cats = await api("/api/products/categories");
        category.innerHTML = `<option value="">All categories</option>` + cats.map(c => `<option>${c}</option>`).join("");
    };
    const loadProducts = async () => {
        grid.innerHTML = `<div class="panel">Loading products...</div>`;
        const params = new URLSearchParams();
        for (const [key, id] of [["search", "search"], ["category", "category"], ["minPrice", "minPrice"], ["maxPrice", "maxPrice"]]) {
            const value = document.querySelector(`#${id}`).value;
            if (value) params.set(key, value);
        }
        params.set("size", 24); // Request 24 items per page
        const pageData = await api(`/api/products?${params}`);
        const products = pageData.content || pageData; // Handle both Paginated and List responses just in case
        grid.innerHTML = products.length ? products.map(productCard).join("") : `<div class="panel">No products found. Try a different search.</div>`;
        const count = document.querySelector("#productCount");
        if (count) count.textContent = `${pageData.totalElements || products.length} products found`;
    };
    window.loadHomeProducts = loadProducts;
    document.querySelector("#filterForm").addEventListener("submit", e => { e.preventDefault(); loadProducts().catch(err => toast(err.message)); });
    document.querySelector("#topSearch")?.addEventListener("submit", e => {
        e.preventDefault();
        document.querySelector("#search").value = e.currentTarget.q.value.trim();
        document.querySelector("#category").value = "";
        document.querySelectorAll("[data-category-chip]").forEach(x => x.classList.remove("active"));
        document.querySelector("[data-category-chip='']")?.classList.add("active");
        loadProducts().catch(err => toast(err.message));
    });

    const searchInputs = document.querySelectorAll("input[name='q'], #search");
    let searchTimeout;
    searchInputs.forEach(input => {
        if (!document.getElementById("searchSuggestions")) {
            const dl = document.createElement("datalist");
            dl.id = "searchSuggestions";
            document.body.appendChild(dl);
        }
        input.setAttribute("list", "searchSuggestions");
        input.addEventListener("input", e => {
            clearTimeout(searchTimeout);
            const query = e.target.value.trim();
            if (query.length < 2) return;
            searchTimeout = setTimeout(async () => {
                try {
                    const page = await api(`/api/products?search=${encodeURIComponent(query)}&size=5`);
                    const dl = document.getElementById("searchSuggestions");
                    dl.innerHTML = (page.content || page).map(p => `<option value="${p.name}">${p.category} | ${p.brand}</option>`).join("");
                } catch (err) {}
            }, 300);
        });
    });
    document.querySelector("#search").addEventListener("input", () => {
        clearTimeout(window.searchTimer);
        window.searchTimer = setTimeout(() => loadProducts().catch(err => toast(err.message)), 350);
    });
    document.querySelectorAll("[data-category-chip]").forEach(chip => chip.addEventListener("click", () => {
        document.querySelector("#category").value = chip.dataset.categoryChip;
        document.querySelectorAll("[data-category-chip]").forEach(x => x.classList.remove("active"));
        chip.classList.add("active");
        loadProducts().catch(err => toast(err.message));
    }));
    document.querySelector("#clearFilters").addEventListener("click", () => {
        document.querySelector("#filterForm").reset();
        loadProducts().catch(err => toast(err.message));
    });
    await loadCategories();
    await loadProducts();
}
async function initProduct() {
    setupNav();
    const root = document.querySelector("#productDetail");
    if (!root) return;
    const id = new URLSearchParams(location.search).get("id");
    const p = await api(`/api/products/${id}`);
    root.innerHTML = `
        <div><img class="product-detail-img" src="${img(p.imageUrl, p.category)}" alt="${p.name}" onerror="this.onerror=null; this.src='https://images.unsplash.com/photo-1445205170230-053b83016050?auto=format&fit=crop&w=600&q=80';"></div>
        <div class="panel">
            <h1>${p.name}</h1>
            <p class="muted">${p.brand} | ${p.category} | ${p.color}</p>
            <p class="price">${rupees(p.price)}</p>
            <p>${p.description}</p>
            <p><strong>Sizes:</strong> ${p.sizeOptions}</p>
            <p><strong>Stock:</strong> ${p.stock}</p>
            <div class="form-grid">
                <input id="qty" type="number" min="1" max="${p.stock}" value="1">
                <button onclick="addToCart(${p.id}, Number(document.querySelector('#qty').value || 1))">Add to cart</button>
                <button class="secondary" onclick="toggleWishlist(${p.id})">Add/remove wishlist</button>
            </div>
        </div>`;
    const related = await api(`/api/products/${id}/related`);
    document.querySelector("#relatedGrid").innerHTML = related.map(productCard).join("");
}
async function initCart() {
    setupNav();
    if (!document.querySelector("#cartItems")) return;
    if (!requireLogin()) return;
    const load = async () => {
        const cart = await api("/api/cart");
        document.querySelector("#cartItems").innerHTML = cart.items.length ? cart.items.map(item => `
            <div class="cart-line">
                <img src="${img(item.product.imageUrl, item.product.category)}" alt="${item.product.name}" onerror="this.onerror=null; this.src='https://images.unsplash.com/photo-1445205170230-053b83016050?auto=format&fit=crop&w=600&q=80';">
                <div><strong>${item.product.name}</strong><p class="muted">${rupees(item.product.price)} each</p></div>
                <div class="qty">
                    <input type="number" min="1" value="${item.quantity}" onchange="updateCart(${item.id}, this.value)">
                    <button class="danger" onclick="removeCart(${item.id})">Remove</button>
                </div>
            </div>`).join("") : `<p class="muted">Cart is empty.</p>`;
        document.querySelector("#cartTotal").textContent = rupees(cart.total);
    };
    window.updateCart = async (id, quantity) => { await api(`/api/cart/${id}`, { method: "PUT", body: JSON.stringify({ quantity: Number(quantity) }) }); load(); };
    window.removeCart = async id => { await api(`/api/cart/${id}`, { method: "DELETE" }); load(); };
    document.querySelector("#checkoutForm").addEventListener("submit", async e => {
        e.preventDefault();
        const f = e.currentTarget;
        try {
            await api("/api/orders", { method: "POST", body: JSON.stringify({
                customerName: f.customerName.value, phone: f.phone.value, address: f.address.value,
                city: f.city.value, state: f.state.value, pincode: f.pincode.value
            })});
            toast("Order placed successfully");
            location.href = "orders.html";
        } catch (err) { toast(err.message); }
    });
    await load();
}
async function initOrders() {
    setupNav();
    const root = document.querySelector("#orders");
    if (!root) return;
    if (!requireLogin()) return;
    const orders = await api("/api/orders/my");
    root.innerHTML = orders.length ? orders.map(o => `
        <div class="panel">
            <h3>Order #${o.id} <span class="status">${o.status}</span></h3>
            <p>${new Date(o.createdAt).toLocaleString()} | ${rupees(o.totalAmount)} | ${o.paymentMode}</p>
            ${o.items.map(i => `<div class="order-line"><span></span><div>${i.productName} x ${i.quantity}</div><strong>${rupees(i.price * i.quantity)}</strong></div>`).join("")}
            <p class="muted">${o.customerName}, ${o.address}, ${o.city}, ${o.state} - ${o.pincode}</p>
        </div>`).join("") : `<div class="panel">No orders yet.</div>`;
}
async function initWishlist() {
    setupNav();
    const grid = document.querySelector("#wishlistGrid");
    if (!grid) return;
    if (!requireLogin()) return;
    const load = async () => {
        const items = await api("/api/wishlist");
        grid.innerHTML = items.length ? items.map(item => {
            const p = item.product;
            return `
            <article class="product-card">
                <a href="product.html?id=${p.id}"><img src="${img(p.imageUrl, p.category)}" alt="${p.name}"></a>
                <div class="product-body">
                    <strong>${p.name}</strong>
                    <span class="muted">${p.brand} | ${p.category} | ${p.color}</span>
                    <span class="price">${rupees(p.price)}</span>
                    <div class="card-actions">
                        <button onclick="addToCart(${p.id})">Add to cart</button>
                        <button class="danger" onclick="removeWishlist(${p.id})">Remove</button>
                    </div>
                </div>
            </article>`;
        }).join("") : `<div class="panel">Wishlist is empty.</div>`;
    };
    window.removeWishlist = async productId => {
        await api(`/api/wishlist/${productId}`, { method: "DELETE" });
        toast("Removed from wishlist");
        load();
    };
    await load();
}
async function initAdmin() {
    setupNav();
    const root = document.querySelector("#adminProducts");
    if (!root) return;
    if (!requireLogin()) return;
    const u = user();
    if (!u || u.role !== "ROLE_ADMIN") {
        toast("Admin login required");
        location.href = "index.html";
        return;
    }
    const form = document.querySelector("#productForm");
    let adminProductsCache = [];
    const loadProducts = async () => {
        const products = await api("/api/products");
        adminProductsCache = products;
        root.innerHTML = products.map(p => `
            <div class="admin-row">
                <img src="${img(p.imageUrl, p.category)}" alt="${p.name}" style="width:70px;height:84px;object-fit:cover;border-radius:6px">
                <div><strong>${p.name}</strong><p class="muted">${p.category} | ${rupees(p.price)} | stock ${p.stock}</p></div>
                <div>
                    <button class="secondary" onclick="editProduct(${p.id})">Edit</button>
                    <button class="danger" onclick="deleteProduct(${p.id})">Delete</button>
                    <input type="file" accept="image/*" onchange="uploadProductImage(${p.id}, this.files[0])">
                </div>
            </div>`).join("");
    };
    window.editProduct = id => {
        const p = adminProductsCache.find(item => item.id === id);
        if (!p) return;
        form.productId.value = p.id;
        for (const key of ["name", "description", "category", "brand", "color", "sizeOptions", "price", "stock", "imageUrl"]) form[key].value = p[key] ?? "";
        form.active.checked = p.active;
        window.scrollTo({ top: 0, behavior: "smooth" });
    };
    window.deleteProduct = async id => { await api(`/api/admin/products/${id}`, { method: "DELETE" }); toast("Product deleted"); loadProducts(); };
    window.uploadProductImage = async (id, file) => {
        if (!file) return;
        const fd = new FormData();
        fd.append("image", file);
        await api(`/api/admin/products/${id}/image`, { method: "POST", body: fd, headers: {} });
        toast("Image uploaded");
        loadProducts();
    };
    form.addEventListener("submit", async e => {
        e.preventDefault();
        const body = {
            name: form.name.value, description: form.description.value, category: form.category.value,
            brand: form.brand.value, color: form.color.value, sizeOptions: form.sizeOptions.value,
            price: Number(form.price.value), stock: Number(form.stock.value), imageUrl: form.imageUrl.value,
            active: form.active.checked
        };
        const id = form.productId.value;
        await api(id ? `/api/admin/products/${id}` : "/api/admin/products", { method: id ? "PUT" : "POST", body: JSON.stringify(body) });
        form.reset();
        form.active.checked = true;
        toast("Product saved");
        loadProducts();
    });
    document.querySelector("#resetProduct").addEventListener("click", () => { form.reset(); form.productId.value = ""; form.active.checked = true; });
    document.querySelector("#loadOrders").addEventListener("click", async () => {
        const orders = await api("/api/admin/orders");
        document.querySelector("#adminOrders").innerHTML = orders.map(o => `<div class="panel"><strong>#${o.id}</strong> ${o.customerName} | ${rupees(o.totalAmount)} | ${o.status}</div>`).join("");
    });
    document.querySelector("#loadUsers").addEventListener("click", async () => {
        const users = await api("/api/admin/users");
        document.querySelector("#adminUsers").innerHTML = users.map(x => `<div class="panel">${x.name} | ${x.email} | ${x.role}</div>`).join("");
    });
    await loadProducts();
}

document.addEventListener("DOMContentLoaded", () => {
    initLogin().catch(e => toast(e.message));
    initRegister().catch(e => toast(e.message));
    initHome().catch(e => toast(e.message));
    initProduct().catch(e => toast(e.message));
    initCart().catch(e => toast(e.message));
    initOrders().catch(e => toast(e.message));
    initWishlist().catch(e => toast(e.message));
    initAdmin().catch(e => toast(e.message));
});
