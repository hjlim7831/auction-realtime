package com.dokidoki.bid.api.controller;

import com.dokidoki.bid.api.request.AuctionBidReq;
import com.dokidoki.bid.api.request.AuctionUpdatePriceSizeReq;
import com.dokidoki.bid.api.service.BiddingService;
import com.dokidoki.bid.common.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("auctions")
public class AuctionController {

    private final BiddingService biddingService;

    @GetMapping("/{auctionId}/initial-info")
    public ResponseEntity<?> getInitialInfo(@PathVariable Long auctionId) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status_code", 200);
        resultMap.put("message", "성공");
        resultMap.put("data", biddingService.getInitialInfo(auctionId));
        return ResponseEntity.ok(resultMap);
    }

    @PostMapping("/{auctionId}/bid")
    public ResponseEntity<?> bid(@RequestBody AuctionBidReq req, @PathVariable() Long auctionId, HttpServletRequest request) throws InterruptedException {
        Map<String, Object> resultMap = new HashMap<>();
        Long memberId = JWTUtil.getUserId(request);
        biddingService.bid(auctionId, req, memberId);
        resultMap.put("status_code", 200);
        resultMap.put("message", "성공");
        return ResponseEntity.ok(resultMap);
    }

}
