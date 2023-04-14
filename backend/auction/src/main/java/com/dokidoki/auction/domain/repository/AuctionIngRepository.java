package com.dokidoki.auction.domain.repository;

import com.dokidoki.auction.domain.custom.AuctionIngCustom;
import com.dokidoki.auction.domain.entity.AuctionIngEntity;
import com.dokidoki.auction.dto.db.AuctionIngMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionIngRepository extends JpaRepository<AuctionIngEntity, Long>, AuctionIngCustom {
    // 진행중인 경매의 상세정보 조회
    AuctionIngEntity findAuctionIngEntityByIdOrderById(Long auctionId);

    // 특정 사용자가 판매중인 경매 목록 조회
    @Query("SELECT a FROM AuctionIngEntity a WHERE a.seller.id = :member_id ORDER BY a.id DESC ")
    Page<AuctionIngEntity> findAllMySellingAuction(@Param("member_id") Long memberId, Pageable pageable);

    // 특정 사용자가 입찰중인 경매 목록 조회
    Page<AuctionIngEntity> findAllByIdInOrderByIdDesc(Long[] auctionIdList, Pageable pageable);

    // 특정 사용자가 관심 갖는 경매 목록 조회
    @Query("SELECT a FROM AuctionIngEntity a " +
            "WHERE :member_id IN (SELECT DISTINCT i.memberEntity.id FROM InterestEntity i WHERE i.auctionIngEntity.id = a.id) " +
            "ORDER BY a.id DESC ")
    Page<AuctionIngEntity> findAllMyInterestingAuction(@Param("member_id") Long memberId, Pageable pageable);

    // 특정 사용자가 판매중인 경매 ID 조회
    List<AuctionIngMapping> findAuctionIngEntityBySeller_Id(Long sellerId);
}
