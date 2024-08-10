package com.naz.vSpace.service.serviceImplementation;

import com.naz.vSpace.dto.LandDto;
import com.naz.vSpace.entity.*;
import com.naz.vSpace.enums.Role;
import com.naz.vSpace.exception.VSpaceException;
import com.naz.vSpace.mapper.LandMapper;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.LandData;
import com.naz.vSpace.repository.*;
import com.naz.vSpace.service.CloudinaryService;
import com.naz.vSpace.service.EmailProducerService;
import com.naz.vSpace.service.LandService;
import com.naz.vSpace.util.RentNotificationTemplate;
import com.naz.vSpace.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class LandImplementation implements LandService {
    private final UserRepository userRepository;
    private final LandRepository landRepository;
    private final LandRentRepository landRentRepository;
    private final EmailProducerService emailProducerService;
    private final CloudinaryService cloudinaryService;
    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> postLand(LandDto dto) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Land land = LandMapper.mapToLand(dto, new Land());
        land.setTotalCost(dto.annualCost()+dto.upfrontCost());
        land.setPhotoUrls(cloudinaryService.uploadPhotos(dto.photos()));
        landRepository.save(land);
        return new ResponseEntity<>(new ApiResponse<>(
                "Property posted successfully", HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> rentLand(UUID landId) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Land land = landRepository.findById(landId).orElseThrow(
                () -> new VSpaceException("Land not found")
        );
        if(land.getIsAvailable()){
            User owner = land.getOwner();

            LandRent landRent = new LandRent();
            landRent.setCustomer(user);
            landRent.setOwner(owner);
            landRent.setLand(land);
            landRentRepository.save(landRent);

            emailProducerService.sendEmailMessage(owner.getEmail(), "update on your property",
                    RentNotificationTemplate.notifyRent(
                            owner.getFirstName(), user.getFirstName()));
            return new ResponseEntity<>(new ApiResponse<>(String.format(
                    "You have successfully initiated a rent for the property %s", land.getName()),
                    HttpStatus.OK), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(new ApiResponse<>("This property is not available",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#rentId")
    public ResponseEntity<ApiResponse<String>> cancelLandRent(UUID rentId) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        LandRent landRent = landRentRepository.findById(rentId).orElseThrow(
                () -> new VSpaceException("Land rent not found"));
        if(user.equals(landRent.getCustomer()) || user.equals(landRent.getOwner())){
            Land land = landRent.getLand();
            if(!land.getIsAvailable()){
                land.setIsAvailable(true);
                landRepository.save(land);
            }
            if(user.equals(land.getOwner())){
                emailProducerService.sendEmailMessage(landRent.getCustomer().getEmail(), "Rent Cancellation",
                        String.format("Dear %s, %n%n This is to notify you that the owner " +
                                        "of the land you rented has cancelled your rent.%n%n You can check V-Space " +
                                        "for more options.%n%n Best regards, V-Space.",
                                landRent.getCustomer().getFirstName()));
            }
            if(user.equals(landRent.getCustomer())){
                emailProducerService.sendEmailMessage(landRent.getCustomer().getEmail(), "Rent Cancellation",
                        String.format("Dear %s, %n%n This is to notify you that your customer %s " +
                                        "has cancelled his rent for your land %s.%n%n Best regards, V-Space.",
                                land.getOwner().getFirstName(), landRent.getCustomer().getFirstName(),
                                land.getName()));
            }
            landRentRepository.delete(landRent);
            return new ResponseEntity<>(new ApiResponse<>("You have successfully cancelled this rent",
                    HttpStatus.OK), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(new ApiResponse<>("You can not cancel this rent",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> approveLandRent(UUID rentId) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        LandRent landRent = landRentRepository.findById(rentId).orElseThrow(
                () -> new VSpaceException("Land rent not found"));
        if(user.equals(landRent.getOwner())){
            if(!landRent.getIsApproved()){
                landRent.setIsApproved(true);
                landRentRepository.save(landRent);
                Land land = landRent.getLand();
                land.setIsAvailable(false);
                landRepository.save(land);

                emailProducerService.sendEmailMessage(landRent.getCustomer().getEmail(), "Rent Cancellation",
                        String.format("Dear %s, %n%n Your rent for the land %s has been approved" +
                                        " by the owner.%n%n You can continue further processes with the owner.%n%n Please do not " +
                                        "fail to contact us if anything goes wrong.%n%n Best regards, V-Space",
                                landRent.getCustomer().getFirstName(), land.getName()));

                return new ResponseEntity<>(new ApiResponse<>("You have successfully approved this rent",
                        HttpStatus.OK), HttpStatus.OK);
            } else{
                return new ResponseEntity<>(new ApiResponse<>("This rent is already approved",
                        HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ApiResponse<>("You can not approve this rent",
                HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable
    public ResponseEntity<ApiResponse<List<LandData>>> getAllLands(Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber != null && pageNumber >= 0? pageNumber:0;
        pageSize = pageSize != null && pageSize > 0? pageSize:DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        List<LandData> landList = landRepository.findByIsAvailableTrueOrderByCreatedAtDesc(pageable)
                .stream().map( data -> LandMapper.mapToLandData(
                        data, new LandData())).collect(Collectors.toList());
        return new ResponseEntity<>(new ApiResponse<>(landList, "All lands retrieved successfully")
                , HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<LandData>>> searchLand(String keyword) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));

        List<LandData> landData = landRepository.findByDescriptionContainsIgnoreCase(keyword)
                .stream().map(data -> LandMapper.mapToLandData(data, new LandData()))
                .collect(Collectors.toList());
        if(!landData.isEmpty()){
            return new ResponseEntity<>(new ApiResponse<>(landData, "Successful"), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(new ApiResponse<>( "No results were found", HttpStatus.NOT_FOUND)
                    , HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable
    public ResponseEntity<ApiResponse<List<LandData>>> viewAllOwnerLands(Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber != null && pageNumber >= 0? pageNumber:0;
        pageSize = pageSize != null && pageSize > 0? pageSize:DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));

        List<LandData> ownerLands = landRepository.findByOwner_Email(user.getEmail(), pageable).stream()
                .map(data -> LandMapper.mapToLandData(data, new LandData())).collect(Collectors.toList());

        return new ResponseEntity<>(new ApiResponse<>(ownerLands, "Lands retrieved successfully")
                , HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    public ResponseEntity<ApiResponse<LandData>> viewLandDetail(UUID id) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Land land = landRepository.findById(id).orElseThrow(
                () -> new VSpaceException("Land not found"));
        LandData landData = LandMapper.mapToLandData(land, new LandData());
        return new ResponseEntity<>(new ApiResponse<>(landData, "Land details retrieved successfully")
                , HttpStatus.OK);
    }

    @Override
    @Transactional
    @CachePut(key = "#id")
    public ResponseEntity<ApiResponse<String>> updateLand(UUID id, LandDto dto) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Land land = landRepository.findById(id).orElseThrow(
                () -> new VSpaceException("Land not found"));

        if(!land.getOwner().equals(user)){
            return new ResponseEntity<>(new ApiResponse<>("You are not authorized to update this land",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } else{
            landRepository.save(LandMapper.mapToLand(dto, land));
            return new ResponseEntity<>(new ApiResponse<>("Land updated successfully", HttpStatus.OK),
                    HttpStatus.OK);
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public ResponseEntity<ApiResponse<String>> removeLand(UUID id) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Land land = landRepository.findById(id).orElseThrow(
                () -> new VSpaceException("Land not found"));

        if(!land.getOwner().equals(user) || !user.getUserRole().equals(Role.ADMIN)){
            return new ResponseEntity<>(new ApiResponse<>("You are not authorized to remove this land",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } else{
            landRepository.delete(land);
            return new ResponseEntity<>(new ApiResponse<>("Land removed successfully", HttpStatus.OK),
                    HttpStatus.OK);
        }
    }
}
