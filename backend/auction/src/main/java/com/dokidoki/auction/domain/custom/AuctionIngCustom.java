package com.dokidoki.auction.domain.custom;

import com.dokidoki.auction.dto.custom.SimpleAuctionIngInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionIngCustom {
    Page<SimpleAuctionIngInfo> searchAuctionIng(String keyword, Long categoryId, LocalDateTime afterNHours,
                                                Long sellerId, Long[] biddingAuctionIdList, Pageable pageable);
}
