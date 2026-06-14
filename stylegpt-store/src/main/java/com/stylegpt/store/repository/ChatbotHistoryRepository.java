package com.stylegpt.store.repository;

import com.stylegpt.store.entity.ChatbotHistory;
import com.stylegpt.store.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotHistoryRepository extends JpaRepository<ChatbotHistory, Long> {
    List<ChatbotHistory> findTop20ByUserOrderByCreatedAtDesc(User user);
}
