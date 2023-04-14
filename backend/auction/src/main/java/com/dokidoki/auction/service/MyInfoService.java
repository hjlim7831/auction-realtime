package com.dokidoki.auction.service;

import com.dokidoki.auction.common.HttpUtil;
import com.dokidoki.auction.domain.entity.AuctionEndEntity;
import com.dokidoki.auction.domain.entity.AuctionIngEntity;
import com.dokidoki.auction.domain.repository.AuctionEndRepository;
import com.dokidoki.auction.domain.repository.AuctionIngRepository;
import com.dokidoki.auction.dto.custom.SimpleAuctionIngInfo;
import com.dokidoki.auction.dto.db.ImageInterface;
import com.dokidoki.auction.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyInfoService {
    private final AuctionEndRepository auctionEndRepository;
    private final AuctionIngRepository auctionIngRepository;
    private final AuctionListService auctionListService;
    private final ImageService imageService;
    private final HttpUtil httpUtil;

    /*
    판매중인 경매 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<SimpleAuctionIngInfo> readAllMySellingAuction(Long memberId, Pageable pageable) {
        // 데이터 조회
        Page<SimpleAuctionIngInfo> auctionIngEntities = auctionIngRepository
                .searchAuctionIng(
                        null,
                        null,
                        null, memberId,
                        null,
                        pageable
                );

        // Response DTO 변환
        return auctionListService.convertToDTOWithImages(memberId, auctionIngEntities);
    }

    /*
    입찰중인 경매 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<SimpleAuctionIngInfo> readAllMyBiddingAuction(String accessToken, Long memberId, Pageable pageable) {
        // Bid Server로부터 입찰중인 경매 ID 리스트 가져오기
        BiddingResp biddingResp = httpUtil.getAuctionIdList(accessToken);

        // 데이터 조회
        Page<SimpleAuctionIngInfo> auctionIngEntities = auctionIngRepository
                .searchAuctionIng(
                        null,
                        null,
                        null,
                        null,
                        biddingResp.getAuctionIdList(),
                        pageable
                );

        // Response DTO 변환
        return auctionListService.convertToDTOWithImages(memberId, auctionIngEntities);
    }

    /*
    관심있는 경매 목록 조회
     */
    @Transactional(readOnly = true)
    public PaginationResp readAllMyInterestingAuction(Long memberId, Pageable pageable) {
        // 데이터 조회
        Page<AuctionIngEntity> auctionIngEntities = auctionIngRepository
                .findAllMyInterestingAuction(memberId, pageable);

        // Response DTO 변환
        return auctionListService.convertToDTOWithImages(memberId, auctionIngEntities);
    }

    /*
    구매내역 조회
     */
    @Transactional(readOnly = true)
    public MyHistoryResp readAllMyPurchases(Long memberId, Pageable pageable) {
        Page<AuctionEndEntity> auctionEndEntities = auctionEndRepository
                .findAllByBuyer_IdOrderByIdDesc(memberId, pageable);

        // Response DTO
        return new MyHistoryResp(
                combineHistoryWithImageURL(auctionEndEntities.getContent()),
                auctionEndEntities.isLast()
        );
    }

    /*
    판매내역 조회
     */
    @Transactional(readOnly = true)
    public MyHistoryResp readAllMySales(Long memberId, Pageable pageable) {
        Page<AuctionEndEntity> auctionEndEntities = auctionEndRepository
                .findAllBySeller_IdOrderByIdDesc(memberId, pageable);

        // Response DTO
        return new MyHistoryResp(
                combineHistoryWithImageURL(auctionEndEntities.getContent()),
                auctionEndEntities.isLast()
        );
    }

    /*
    구매내역 또는 판매내역과 해당 내역들의 대표 이미지 URL을 조합하는 메서드
     */
    @Transactional(readOnly = true)
    public List<MyHistoryInfo> combineHistoryWithImageURL(List<AuctionEndEntity> auctionEndEntities) {
        // 경매 ID 목록
        List<Long> auctionIdList = new ArrayList<>();
        for (AuctionEndEntity auctionEndEntity : auctionEndEntities)
            auctionIdList.add(auctionEndEntity.getId());

        // 각 내역의 대표 사진 조회
        List<ImageInterface> imageInterfaces = imageService.readAuctionThumbnailImage(auctionIdList);

        // ResultSet -> DTO
        List<MyHistoryInfo> myHistoryInfos = new ArrayList<>();
        int imageIdx = 0;
        for (AuctionEndEntity auctionEndEntity : auctionEndEntities) {
            // 이미지 정보 가져오기
            String imageUrl = null;  // 이미지 URL
            if (imageIdx < imageInterfaces.size()) {
                Long imageAuctionId = imageInterfaces.get(imageIdx).getAuction_id();
                // 조회한 경매와 이미지가 동일 제품이라면 imageUrl 설정
                if (imageAuctionId.equals(auctionEndEntity.getId())) {
                    imageUrl = imageInterfaces.get(imageIdx).getImage_url();
                    imageIdx++;  // 다음 이미지로 넘어가기
                }
            }
            myHistoryInfos.add(new MyHistoryInfo(auctionEndEntity, imageUrl));
        }
        return myHistoryInfos;
    }

    /*
    총 구매가 조회
     */
    public Long readMyTotalOfPurchases(Long memberId) {
        Long total = auctionEndRepository.getMyTotalOfPurchases(memberId);
        if (total == null)
            return 0L;
        return total;
    }

    /*
    총 판매가 조회
     */
    public Long readMyTotalOfSales(Long memberId) {
        Long total = auctionEndRepository.getMyTotalOfSales(memberId);
        if (total == null)
            return 0L;
        return total;
    }
}
