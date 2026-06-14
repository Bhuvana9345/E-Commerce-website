package com.stylegpt.store.config;

import com.stylegpt.store.entity.Product;
import com.stylegpt.store.entity.Role;
import com.stylegpt.store.entity.User;
import com.stylegpt.store.repository.ProductRepository;
import com.stylegpt.store.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(UserRepository users, ProductRepository products, PasswordEncoder encoder) {
        return args -> {
            if (!users.existsByEmail("admin@stylegpt.com")) {
                User admin = new User();
                admin.setName("StyleGPT Admin");
                admin.setEmail("admin@stylegpt.com");
                admin.setPhone("9999999999");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ROLE_ADMIN);
                users.save(admin);
            } else {
                users.findByEmail("admin@stylegpt.com").ifPresent(admin -> {
                    if (admin.getPhone() == null || admin.getPhone().isBlank()) {
                        admin.setPhone("9999999999");
                        users.save(admin);
                    }
                });
            }
            if (!users.existsByEmail("user@stylegpt.com")) {
                User user = new User();
                user.setName("Demo User");
                user.setEmail("user@stylegpt.com");
                user.setPhone("8888888888");
                user.setPassword(encoder.encode("user123"));
                user.setRole(Role.ROLE_USER);
                users.save(user);
            } else {
                users.findByEmail("user@stylegpt.com").ifPresent(user -> {
                    if (user.getPhone() == null || user.getPhone().isBlank()) {
                        user.setPhone("8888888888");
                        users.save(user);
                    }
                });
            }
            List<Product> demoProducts = List.of(
                    product("Black Oxford Shirt", "Premium black shirt for office, dates, and smart casual looks.", "Shirts", "StyleGPT", "Black", "S,M,L,XL", 1499, 30, "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=900&q=80"),
                    product("Blue Slim Jeans", "Classic blue denim that pairs well with black, white, and printed shirts.", "Pants", "DenimLab", "Blue", "30,32,34,36", 2199, 24, "https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80"),
                    product("Cream Chinos", "Clean cream chinos for black shirts and semi-formal outfits.", "Pants", "UrbanFit", "Cream", "30,32,34,36", 1899, 18, "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=900&q=80"),
                    product("White College Sneakers", "Comfortable white sneakers under budget for college and casual wear.", "Shoes", "Walkly", "White", "6,7,8,9,10", 2499, 40, "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=900&q=80"),
                    product("Tan Loafers", "Tan loafers for chinos, shirts, and wedding guest outfits.", "Shoes", "Walkly", "Brown", "6,7,8,9,10", 2999, 16, "https://images.unsplash.com/photo-1614252369475-531eba835eb1?auto=format&fit=crop&w=900&q=80"),
                    product("Maroon Party Dress", "Elegant maroon dress for parties, receptions, and evening events.", "Dresses", "Aura", "Red", "XS,S,M,L", 3499, 12, "https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=900&q=80"),
                    product("Navy Blazer", "Sharp navy blazer for wedding, reception, and formal styling.", "Jackets", "MetroMode", "Blue", "S,M,L,XL", 4599, 10, "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=900&q=80"),
                    product("Pink Casual Kurti", "Soft pink kurti for college casual and everyday ethnic styling.", "Shirts", "IndieWear", "Pink", "S,M,L,XL", 1299, 28, "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=900&q=80"),
                    product("Silk Saree", "Elegant silk saree for wedding, festival, and family functions.", "Sarees", "IndieWear", "Red", "Free Size", 2499, 18, "https://images.unsplash.com/photo-1610189026664-1ac9b7b5c0a6?auto=format&fit=crop&w=900&q=80"),
                    product("Cotton Kurti", "Daily wear cotton kurti for office, college, and casual outings.", "Kurtis", "IndieWear", "Blue", "S,M,L,XL", 899, 32, "https://images.unsplash.com/photo-1610030469668-8e9f14c2cfc0?auto=format&fit=crop&w=900&q=80"),
                    product("Floral Summer Dress", "Light floral dress for daily wear, college events, and casual outings.", "Dresses", "Meera", "Green", "XS,S,M,L,XL", 1599, 25, "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?auto=format&fit=crop&w=900&q=80"),
                    product("Running Sport Shoes", "Soft sole running shoes for daily walking, college, and gym use.", "Shoes", "Runzo", "Grey", "6,7,8,9,10,11", 1999, 35, "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?auto=format&fit=crop&w=900&q=80"),
                    product("Classic Sunglasses", "UV protected sunglasses for casual and travel outfits.", "Accessories", "ShadePro", "Black", "Free Size", 799, 45, "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=900&q=80"),
                    product("Minimal Watch", "Simple watch that matches office, college, and wedding looks.", "Accessories", "TimeCraft", "Brown", "Free Size", 1299, 22, "https://images.unsplash.com/photo-1524592094714-0f0654e20314?auto=format&fit=crop&w=900&q=80"),
                    product("Organic Rice 5kg", "Daily grocery rice pack for home essentials shopping.", "Grocery", "HomeBasket", "White", "5kg", 599, 60, "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=80"),
                    product("Cold Pressed Oil", "Healthy cooking oil for grocery essentials.", "Grocery", "HomeBasket", "Gold", "1L", 349, 50, "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=900&q=80"),
                    product("Cotton T-Shirt Combo", "Pack of two cotton t-shirts for everyday casual wear.", "Shirts", "DailyFit", "White", "S,M,L,XL", 999, 38, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80"),
                    product("Black Formal Pants", "Formal black pants for shirts, office outfits, and interviews.", "Pants", "UrbanFit", "Black", "30,32,34,36,38", 1799, 27, "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=900&q=80"),
                    product("Anarkali Kurti", "Festive anarkali kurti for functions and family events.", "Kurtis", "IndieWear", "Yellow", "S,M,L,XL", 1799, 20, "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=900&q=80"),
                    product("Printed Cotton Saree", "Printed cotton saree for daily and office wear.", "Sarees", "SareeHouse", "Blue", "Free Size", 1399, 24, "https://images.unsplash.com/photo-1610189026664-1ac9b7b5c0a6?auto=format&fit=crop&w=900&q=80"),
                    product("Designer Party Saree", "Party saree with rich border for receptions.", "Sarees", "SareeHouse", "Pink", "Free Size", 2999, 14, "https://images.unsplash.com/photo-1610189026664-1ac9b7b5c0a6?auto=format&fit=crop&w=900&q=80"),
                    product("Casual Shirt Combo", "Two casual shirts for college and office wear.", "Shirts", "DailyFit", "Blue", "S,M,L,XL", 1699, 31, "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=900&q=80"),
                    product("Denim Jacket", "Layering denim jacket for casual outfits.", "Jackets", "DenimLab", "Blue", "S,M,L,XL", 2499, 18, "https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=900&q=80"),
                    product("Women Heels", "Party heels for dresses and sarees.", "Shoes", "Walkly", "Black", "5,6,7,8,9", 1899, 26, "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=900&q=80"),
                    product("Canvas Casual Shoes", "Daily canvas shoes for college casual looks.", "Shoes", "Walkly", "Black", "6,7,8,9,10", 1299, 42, "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?auto=format&fit=crop&w=900&q=80"),
                    product("Leather Sandals", "Comfort sandals for daily and ethnic outfits.", "Shoes", "Walkly", "Brown", "6,7,8,9,10", 999, 33, "https://images.unsplash.com/photo-1603487742131-4160ec999306?auto=format&fit=crop&w=900&q=80"),
                    product("Kids Party Dress", "Cute kids party dress for birthdays.", "Dresses", "LittleStar", "Pink", "2Y,4Y,6Y,8Y", 1199, 21, "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?auto=format&fit=crop&w=900&q=80"),
                    product("Office Pencil Dress", "Formal dress for meetings and office parties.", "Dresses", "Aura", "Black", "XS,S,M,L", 2299, 16, "https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=900&q=80"),
                    product("Maxi Dress", "Comfortable maxi dress for casual outings.", "Dresses", "Meera", "Blue", "XS,S,M,L,XL", 1999, 19, "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?auto=format&fit=crop&w=900&q=80"),
                    product("Green Ethnic Kurti", "Green ethnic kurti for festive office wear.", "Kurtis", "IndieWear", "Green", "S,M,L,XL", 1199, 29, "https://images.unsplash.com/photo-1610030469668-8e9f14c2cfc0?auto=format&fit=crop&w=900&q=80"),
                    product("Leggings Pack", "Stretch leggings pack for kurtis and long tops.", "Pants", "DailyFit", "Black", "S,M,L,XL", 699, 55, "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=900&q=80"),
                    product("Track Pants", "Comfort track pants for home, travel, and gym.", "Pants", "Runzo", "Grey", "S,M,L,XL", 999, 46, "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=900&q=80"),
                    product("Handbag", "Everyday handbag for office and shopping.", "Accessories", "BagWorks", "Brown", "Free Size", 1599, 20, "https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=900&q=80"),
                    product("Backpack", "College and travel backpack with multiple pockets.", "Accessories", "BagWorks", "Black", "Free Size", 1199, 30, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80"),
                    product("Wheat Flour 5kg", "Fresh wheat flour for grocery essentials.", "Grocery", "HomeBasket", "Brown", "5kg", 329, 70, "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?auto=format&fit=crop&w=900&q=80"),
                    product("Sugar 1kg", "Daily grocery sugar pack.", "Grocery", "HomeBasket", "White", "1kg", 59, 90, "https://images.unsplash.com/photo-1581441363689-1f3c3c414635?auto=format&fit=crop&w=900&q=80"),
                    product("Masala Combo", "Kitchen masala combo pack for home cooking.", "Grocery", "HomeBasket", "Mixed", "Pack", 449, 44, "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?auto=format&fit=crop&w=900&q=80"),
                    product("Tea Powder", "Strong tea powder for daily grocery shopping.", "Grocery", "HomeBasket", "Brown", "500g", 249, 64, "https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?auto=format&fit=crop&w=900&q=80")
                    ,product("Chocolate Cookies", "Crunchy chocolate cookies for snacks shopping.", "Snacks", "SnackBox", "Brown", "Pack", 149, 100, "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&w=900&q=80"),
                    product("Masala Chips", "Spicy chips pack for evening snacks.", "Snacks", "SnackBox", "Yellow", "Pack", 99, 120, "https://images.unsplash.com/photo-1566478989037-eec170784d0b?auto=format&fit=crop&w=900&q=80"),
                    product("Dry Fruit Mix", "Healthy dry fruit mix snack pack.", "Snacks", "SnackBox", "Mixed", "250g", 499, 70, "https://images.unsplash.com/photo-1599599810769-bcde5a160d32?auto=format&fit=crop&w=900&q=80"),
                    product("Instant Noodles Combo", "Quick snack noodles combo pack.", "Snacks", "SnackBox", "Red", "Pack", 199, 85, "https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?auto=format&fit=crop&w=900&q=80"),
                    product("Cotton A-Line Dress", "Budget cotton dress under Rs.2000 for daily wear.", "Dresses", "Meera", "Yellow", "XS,S,M,L,XL", 999, 34, "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?auto=format&fit=crop&w=900&q=80"),
                    product("Denim Shirt Dress", "Casual denim dress for college and outings.", "Dresses", "Aura", "Blue", "XS,S,M,L", 1799, 22, "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?auto=format&fit=crop&w=900&q=80"),
                    product("Printed Midi Dress", "Printed midi dress under Rs.2000.", "Dresses", "Meera", "White", "XS,S,M,L,XL", 1499, 28, "https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=900&q=80"),
                    product("Budget Party Dress", "Simple party dress under Rs.2000.", "Dresses", "Aura", "Black", "XS,S,M,L", 1999, 19, "https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=900&q=80"),
                    product("Slip-On Sneakers", "Easy slip-on sneakers under Rs.2000.", "Shoes", "Walkly", "Grey", "6,7,8,9,10", 1199, 41, "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?auto=format&fit=crop&w=900&q=80"),
                    product("Budget Running Shoes", "Running shoes under Rs.2000 for daily use.", "Shoes", "Runzo", "Blue", "6,7,8,9,10", 1699, 37, "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?auto=format&fit=crop&w=900&q=80"),
                    product("Flat Sandals", "Comfort flat sandals under Rs.1000.", "Shoes", "Walkly", "Tan", "5,6,7,8,9", 799, 52, "https://images.unsplash.com/photo-1603487742131-4160ec999306?auto=format&fit=crop&w=900&q=80"),
                    product("Ballet Flats", "Simple ballet flats for dresses and kurtis.", "Shoes", "Walkly", "Pink", "5,6,7,8,9", 999, 39, "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=900&q=80"),
                    product("Fashion Earrings Set", "Combo earrings set under Rs.500.", "Accessories", "GlowBox", "Gold", "Pack", 399, 65, "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=900&q=80"),
                    product("Hair Clips Combo", "Stylish hair clips for daily wear.", "Accessories", "GlowBox", "Mixed", "Pack", 199, 80, "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?auto=format&fit=crop&w=900&q=80"),
                    product("Modern Slippers", "Comfortable and stylish modern slippers for everyday use.", "Shoes", "Walkly", "Black", "7,8,9,10", 499, 100, "https://images.unsplash.com/photo-1603487742131-4160ec999306?auto=format&fit=crop&w=900&q=80"),
                    product("Boys Casual Shoes", "Trendy boys casual shoes for school and outings.", "Shoes", "KidsFoot", "Blue", "2,3,4,5", 899, 50, "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=900&q=80"),
                    product("Premium Leather Slippers", "High quality leather slippers for men.", "Shoes", "Walkly", "Brown", "7,8,9,10", 999, 40, "https://images.unsplash.com/photo-1562183241-b937e95585b6?auto=format&fit=crop&w=900&q=80")
            );
            for (Product demo : demoProducts) {
                if (!products.existsByNameIgnoreCase(demo.getName())) {
                    products.save(demo);
                }
            }
        };
    }

    private Product product(String name, String description, String category, String brand, String color,
                            String sizes, int price, int stock, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setBrand(brand);
        p.setColor(color);
        p.setSizeOptions(sizes);
        p.setPrice(BigDecimal.valueOf(price));
        p.setStock(stock);
        p.setImageUrl(imageUrl);
        p.setActive(true);
        return p;
    }
}
