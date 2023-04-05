package com.dokidoki.auction.domain.custom;

import com.dokidoki.auction.domain.entity.AuctionIngEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuctionIngCustom {
    Page<AuctionIngEntity> searchAuctionIng(Pageable pageable);
}
