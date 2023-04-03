package com.dokidoki.bid.db.repository;

import com.dokidoki.bid.db.entity.AuctionIngEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionIngRepository extends JpaRepository<AuctionIngEntity, Long> {

    <T> Optional<T> findBySellerIdAndId(Long sellerId, Long auctionId, Class<T> type);

}
