package com.naz.vSpace.service.serviceImplementation;

import com.naz.vSpace.dto.HouseDto;
import com.naz.vSpace.entity.House;
import com.naz.vSpace.entity.HouseRent;
import com.naz.vSpace.entity.User;
import com.naz.vSpace.enums.Role;
import com.naz.vSpace.exception.VSpaceException;
import com.naz.vSpace.mapper.HouseMapper;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.HouseData;
import com.naz.vSpace.repository.HouseRentRepository;
import com.naz.vSpace.repository.HouseRepository;
import com.naz.vSpace.repository.UserRepository;
import com.naz.vSpace.service.CloudinaryService;
import com.naz.vSpace.service.EmailProducerService;
import com.naz.vSpace.service.HouseService;
import com.naz.vSpace.util.RentNotificationTemplate;
import com.naz.vSpace.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
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
@CacheConfig(cacheNames = "House")
public class HouseImplementation implements HouseService {
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final HouseRentRepository houseRentRepository;
    private final EmailProducerService emailProducerService;
    private final CloudinaryService cloudinaryService;
    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> postHouse(HouseDto dto) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        House house = HouseMapper.mapToHouse(dto, new House());
        house.setTotalCost(dto.annualCost()+ dto.upfrontCost());
        house.setPhotoUrls(cloudinaryService.uploadPhotos(dto.photos()));
        houseRepository.save(house);
        return new ResponseEntity<>(new ApiResponse<>(
                "Property posted successfully", HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> rentHouse(UUID houseId) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        House house = houseRepository.findById(houseId).orElseThrow(
                () -> new VSpaceException("House not found")
        );
        if(house.getIsAvailable()){
            User owner = house.getOwner();

            HouseRent houseRent = new HouseRent();
            houseRent.setCustomer(user);
            houseRent.setOwner(owner);
            houseRent.setHouse(house);
            houseRentRepository.save(houseRent);

            emailProducerService.sendEmailMessage(owner.getEmail(), "Update on your property",
                    RentNotificationTemplate.notifyRent(
                            owner.getFirstName(), user.getFirstName()));

            return new ResponseEntity<>(new ApiResponse<>(String.format(
                    "You have successfully initiated a rent for the property %s", house.getName()),
                    HttpStatus.OK), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(new ApiResponse<>("This property is not available",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#rentId")
    public ResponseEntity<ApiResponse<String>> cancelHouseRent(UUID rentId) {
       User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
       HouseRent houseRent = houseRentRepository.findById(rentId).orElseThrow(
               () -> new VSpaceException("House rent not found"));
       if(user.equals(houseRent.getCustomer()) || user.equals(houseRent.getOwner())){
           House house = houseRent.getHouse();
           if(!house.getIsAvailable()){
               house.setIsAvailable(true);
               houseRepository.save(house);
           }
           if(user.equals(house.getOwner())){
               emailProducerService.sendEmailMessage(houseRent.getCustomer().getEmail(), "Rent Cancellation",
                       String.format("Dear %s, %n%n This is to notify you that the owner " +
                                       "of the house you rented has cancelled your rent.%n%n You can check V-Space " +
                                       "for more options.%n%n Best regards, V-Space.",
                               houseRent.getCustomer().getFirstName()));
           }
           if(user.equals(houseRent.getCustomer())){
               emailProducerService.sendEmailMessage(houseRent.getOwner().getEmail(), "Rent Cancellation",
                       String.format("Dear %s, %n%n This is to notify you that your customer %s " +
                                       "has cancelled his rent for your house %s.%n%n Best regards, V-Space.",
                               house.getOwner().getFirstName(), houseRent.getCustomer().getFirstName(),
                               house.getName()));
           }
           houseRentRepository.delete(houseRent);
           return new ResponseEntity<>(new ApiResponse<>("You have successfully cancelled this rent",
                   HttpStatus.OK), HttpStatus.OK);
       } else{
           return new ResponseEntity<>(new ApiResponse<>("You can not cancel this rent",
                   HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
       }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> approveHouseRent(UUID rentId) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        HouseRent houseRent = houseRentRepository.findById(rentId).orElseThrow(
                () -> new VSpaceException("House rent not found"));
        if(user.equals(houseRent.getOwner())){
            if(!houseRent.getIsApproved()){
                houseRent.setIsApproved(true);
                houseRentRepository.save(houseRent);
                House house = houseRent.getHouse();
                house.setIsAvailable(false);
                houseRepository.save(house);

                emailProducerService.sendEmailMessage(houseRent.getCustomer().getEmail(), "Rent Approval",
                        (String.format("Dear %s, %n%n Your rent for the house %s has been approved" +
                                        " by the owner.%n%n You can continue further processes with the owner.%n%n Please do not " +
                                        "fail to contact us if anything goes wrong.%n%n Best regards, V-Space",
                                houseRent.getCustomer().getFirstName(), house.getName())));


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
    public ResponseEntity<ApiResponse<List<HouseData>>> getAllHouses(Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber != null && pageNumber >= 0? pageNumber:0;
        pageSize = pageSize != null && pageSize > 0? pageSize:DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        List<HouseData> houseList = houseRepository.findByIsAvailableTrueOrderByCreatedAtDesc(pageable)
                .stream().map( data -> HouseMapper.mapToHouseData(
                        data, new HouseData())).collect(Collectors.toList());
        return new ResponseEntity<>(new ApiResponse<>(houseList, "All houses retrieved successfully")
                , HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<HouseData>>> searchHouse(String keyword) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));

        List<HouseData> houseData = houseRepository.findByDescriptionContainsIgnoreCase(keyword)
                .stream().map(data -> HouseMapper.mapToHouseData(data, new HouseData()))
                .collect(Collectors.toList());
        if(!houseData.isEmpty()){
            return new ResponseEntity<>(new ApiResponse<>(houseData, "Successful"), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(new ApiResponse<>( "No results were found", HttpStatus.NOT_FOUND)
                    , HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
    @Cacheable
    public ResponseEntity<ApiResponse<List<HouseData>>> viewAllOwnerHouses(Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber != null && pageNumber >= 0? pageNumber:0;
        pageSize = pageSize != null && pageSize > 0? pageSize:DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));

        List<HouseData> ownerHouses = houseRepository.findByOwner_Email(user.getEmail(), pageable).stream()
                .map(data -> HouseMapper.mapToHouseData(data, new HouseData())).collect(Collectors.toList());

        return new ResponseEntity<>(new ApiResponse<>(ownerHouses, "Houses retrieved successfully")
                , HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    public ResponseEntity<ApiResponse<HouseData>> viewHouseDetail(UUID id) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        House house = houseRepository.findById(id).orElseThrow(
                () -> new VSpaceException("House not found"));
        HouseData houseData = HouseMapper.mapToHouseData(house, new HouseData());
        return new ResponseEntity<>(new ApiResponse<>(houseData, "House details retrieved successfully")
                , HttpStatus.OK);
    }

    @Override
    @Transactional
    @CachePut(key = "#id")
    public ResponseEntity<ApiResponse<String>> updateHouse(UUID id, HouseDto dto) {
       User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
       House house = houseRepository.findById(id).orElseThrow(
                () -> new VSpaceException("House not found"));

       if(!house.getOwner().equals(user)){
           return new ResponseEntity<>(new ApiResponse<>("You are not authorized to update this house",
                   HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
       } else{
           houseRepository.save(HouseMapper.mapToHouse(dto, house));
           return new ResponseEntity<>(new ApiResponse<>("House updated successfully", HttpStatus.OK),
                   HttpStatus.OK);
       }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public ResponseEntity<ApiResponse<String>> removeHouse(UUID id) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        House house = houseRepository.findById(id).orElseThrow(
                () -> new VSpaceException("House not found"));

        if(!house.getOwner().equals(user) || !user.getUserRole().equals(Role.ADMIN)){
            return new ResponseEntity<>(new ApiResponse<>("You are not authorized to remove this house",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } else{
            houseRepository.delete(house);
            return new ResponseEntity<>(new ApiResponse<>("House removed successfully", HttpStatus.OK),
                    HttpStatus.OK);
        }
    }
}
