package org.example.repository;

import org.example.entity.Approval;
import org.example.enums.ApprovalLevel;
import org.example.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApprovalRepository extends JpaRepository<Approval, UUID> {
    public Approval findByExpenseIdAndLevelAndStatus(UUID expenseId,
                                                     ApprovalLevel level,
                                                     ApprovalStatus status);
    public Approval findByExpenseIdAndStatus(UUID expenseId, ApprovalStatus status);
    public List<Approval> findAllByExpenseIdAndStatus(UUID expenseId, ApprovalStatus status);
    public List<Approval> findAllByLevel(ApprovalLevel level);
    public List<Approval> findAllByLevelAndStatus(ApprovalLevel level,
                                                  ApprovalStatus status
    );

}
