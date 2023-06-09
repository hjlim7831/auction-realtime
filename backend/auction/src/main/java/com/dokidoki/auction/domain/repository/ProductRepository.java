package com.dokidoki.auction.domain.repository;

import com.dokidoki.auction.domain.entity.ProductEntity;
import com.dokidoki.auction.dto.db.MostSaleProductInterface;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("SELECT p " +
            "FROM ProductEntity p " +
            "WHERE p.name LIKE %:keyword% OR p.categoryEntity.categoryName LIKE %:keyword% ")
    List<ProductEntity> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p.name as name, p.saleCnt as sale_cnt FROM ProductEntity p WHERE p.saleCnt > 0 ORDER BY p.saleCnt DESC")
    List<MostSaleProductInterface> findMostSaleProducts(Pageable pageable);

    // 최근 N일 이내 가장 많이 팔린 제품 조회 (상위 5개)
    @Query("SELECT p.name as name, COUNT(p.name) as sale_cnt " +
            "FROM AuctionEndEntity a JOIN a.product p " +
            "WHERE :before_n_days <= a.endTime " +
            "GROUP BY p.name " +
            "ORDER BY sale_cnt DESC ")
    List<MostSaleProductInterface> findMostSaleProductsWithinNDays(
            @Param("before_n_days") LocalDateTime beforeNDays, Pageable pageable);
    default List<MostSaleProductInterface> findMostSaleProductsWithinNDays(Integer days) {
        return findMostSaleProductsWithinNDays(LocalDateTime.now().minusDays(days), PageRequest.of(0, 5));
    }

    @Modifying
    @Query("UPDATE ProductEntity p SET p.saleCnt = p.saleCnt + 1 WHERE p.id = :product_id")
    void updateSaleCount(@Param("product_id") Long productId);
}
