package com.naz.vSpace.service.serviceImplementation;

import com.naz.vSpace.dto.WarehouseDto;
import com.naz.vSpace.entity.*;
import com.naz.vSpace.enums.Role;
import com.naz.vSpace.exception.VSpaceException;
import com.naz.vSpace.mapper.WarehouseMapper;
import com.naz.vSpace.payload.ApiResponse;
import com.naz.vSpace.payload.WarehouseData;
import com.naz.vSpace.repository.*;
import com.naz.vSpace.service.CloudinaryService;
import com.naz.vSpace.service.EmailProducerService;
import com.naz.vSpace.service.WarehouseService;
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
public class WarehouseImplementation implements WarehouseService {
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseRentRepository warehouseRentRepository;
    private final EmailProducerService emailProducerService;
    private final CloudinaryService cloudinaryService;
    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> postWarehouse(WarehouseDto dto) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Warehouse warehouse = WarehouseMapper.mapToWarehouse(dto, new Warehouse());
        warehouse.setTotalCost(dto.annualCost()+ dto.upfrontCost());
        warehouse.setPhotoUrls(cloudinaryService.uploadPhotos(dto.photos()));
        warehouseRepository.save(warehouse);
        return new ResponseEntity<>(new ApiResponse<>(
                "Property posted successfully", HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> rentWarehouse(UUID warehouseId) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(
                () -> new VSpaceException("Warehouse not found")
        );
        if(warehouse.getIsAvailable()){
            User owner = warehouse.getOwner();

            WarehouseRent warehouseRent = new WarehouseRent();
            warehouseRent.setCustomer(user);
            warehouseRent.setOwner(owner);
            warehouseRent.setWarehouse(warehouse);
            warehouseRentRepository.save(warehouseRent);

            emailProducerService.sendEmailMessage(owner.getEmail(), "update on your property",
                    RentNotificationTemplate.notifyRent(
                            owner.getFirstName(), user.getFirstName()));

            return new ResponseEntity<>(new ApiResponse<>(String.format(
                    "You have successfully initiated a rent for the property %s", warehouse.getName()),
                    HttpStatus.OK), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(new ApiResponse<>("This property is not available",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#rentId")
    public ResponseEntity<ApiResponse<String>> cancelWarehouseRent(UUID rentId) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        WarehouseRent warehouseRent = warehouseRentRepository.findById(rentId).orElseThrow(
                () -> new VSpaceException("Warehouse rent not found"));
        if(user.equals(warehouseRent.getCustomer()) || user.equals(warehouseRent.getOwner())){
            Warehouse warehouse = warehouseRent.getWarehouse();
            if(!warehouse.getIsAvailable()){
                warehouse.setIsAvailable(true);
                warehouseRepository.save(warehouse);
            }
            if(user.equals(warehouse.getOwner())){
                emailProducerService.sendEmailMessage(warehouseRent.getCustomer().getEmail(), "Rent Cancellation",
                        String.format("Dear %s, %n%n This is to notify you that the owner " +
                                        "of the warehouse you rented has cancelled your rent.%n%n You can check V-Space " +
                                        "for more options.%n%n Best regards, V-Space.",
                                warehouseRent.getCustomer().getFirstName()));
            }
            if(user.equals(warehouseRent.getCustomer())){
                emailProducerService.sendEmailMessage(warehouse.getOwner().getEmail(), "Rent Cancellation",
                        String.format("Dear %s, %n%n This is to notify you that your customer %s " +
                                        "has cancelled his rent for your warehouse %s.%n%n Best regards, V-Space.",
                                warehouse.getOwner().getFirstName(), warehouseRent.getCustomer().getFirstName(),
                                warehouse.getName()));
            }
            warehouseRentRepository.delete(warehouseRent);
            return new ResponseEntity<>(new ApiResponse<>("You have successfully cancelled this rent",
                    HttpStatus.OK), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(new ApiResponse<>("You can not cancel this rent",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> approveWarehouseRent(UUID rentId) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        WarehouseRent warehouseRent = warehouseRentRepository.findById(rentId).orElseThrow(
                () -> new VSpaceException("Warehouse rent not found"));
        if(user.equals(warehouseRent.getOwner())){
            if(!warehouseRent.getIsApproved()){
                warehouseRent.setIsApproved(true);
                warehouseRentRepository.save(warehouseRent);
                Warehouse warehouse = warehouseRent.getWarehouse();
                warehouse.setIsAvailable(false);
                warehouseRepository.save(warehouse);

                emailProducerService.sendEmailMessage(warehouseRent.getCustomer().getEmail(), "Rent Approval",
                        String.format("Dear %s, %n%n Your rent for the warehouse %s has been approved" +
                                        " by the owner.%n%n You can continue further processes with the owner.%n%n Please do not " +
                                        "fail to contact us if anything goes wrong.%n%n Best regards, V-Space",
                                warehouseRent.getCustomer().getFirstName(), warehouse.getName()));

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
    public ResponseEntity<ApiResponse<List<WarehouseData>>> getAllWarehouses(Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber != null && pageNumber >= 0? pageNumber:0;
        pageSize = pageSize != null && pageSize > 0? pageSize:DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        List<WarehouseData> warehouseList = warehouseRepository.findByIsAvailableTrueOrderByCreatedAtDesc(pageable)
                .stream().map( data -> WarehouseMapper.mapToWarehouseData(
                        data, new WarehouseData())).collect(Collectors.toList());
        return new ResponseEntity<>(new ApiResponse<>(warehouseList, "All warehouses retrieved successfully")
                , HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<WarehouseData>>> searchWarehouse(String keyword) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));

        List<WarehouseData> warehouseData = warehouseRepository.findByDescriptionContainsIgnoreCase(keyword)
                .stream().map(data -> WarehouseMapper.mapToWarehouseData(data, new WarehouseData()))
                .collect(Collectors.toList());
        if(!warehouseData.isEmpty()){
            return new ResponseEntity<>(new ApiResponse<>(warehouseData, "Successful"), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(new ApiResponse<>( "No results were found", HttpStatus.NOT_FOUND)
                    , HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable
    public ResponseEntity<ApiResponse<List<WarehouseData>>> viewAllOwnerWarehouses(Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber != null && pageNumber >= 0? pageNumber:0;
        pageSize = pageSize != null && pageSize > 0? pageSize:DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));

        List<WarehouseData> ownerWarehouses = warehouseRepository.findByOwner_Email(user.getEmail(), pageable).stream()
                .map(data -> WarehouseMapper.mapToWarehouseData(data, new WarehouseData())).collect(Collectors.toList());

        return new ResponseEntity<>(new ApiResponse<>(ownerWarehouses, "Warehouses retrieved successfully")
                , HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    public ResponseEntity<ApiResponse<WarehouseData>> viewWarehouseDetail(UUID id) {
        userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
                () -> new VSpaceException("Warehouse not found"));
        WarehouseData warehouseData = WarehouseMapper.mapToWarehouseData(warehouse, new WarehouseData());
        return new ResponseEntity<>(new ApiResponse<>(warehouseData, "Warehouse details retrieved successfully")
                , HttpStatus.OK);
    }

    @Override
    @Transactional
    @CachePut(key = "#id")
    public ResponseEntity<ApiResponse<String>> updateWarehouse(UUID id, WarehouseDto dto) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
                () -> new VSpaceException("Warehouse not found"));

        if(!warehouse.getOwner().equals(user)){
            return new ResponseEntity<>(new ApiResponse<>("You are not authorized to update this warehouse",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } else{
            warehouseRepository.save(WarehouseMapper.mapToWarehouse(dto, warehouse));
            return new ResponseEntity<>(new ApiResponse<>("Warehouse updated successfully", HttpStatus.OK),
                    HttpStatus.OK);
        }
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public ResponseEntity<ApiResponse<String>> removeWarehouse(UUID id) {
        User user = userRepository.findByEmail(UserUtil.getLoginUser()).orElseThrow(
                () -> new VSpaceException("User not found"));
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
                () -> new VSpaceException("Warehouse not found"));

        if(!warehouse.getOwner().equals(user) || !user.getUserRole().equals(Role.ADMIN)){
            return new ResponseEntity<>(new ApiResponse<>("You are not authorized to remove this warehouse",
                    HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } else{
            warehouseRepository.delete(warehouse);
            return new ResponseEntity<>(new ApiResponse<>("Warehouse removed successfully", HttpStatus.OK),
                    HttpStatus.OK);
        }
    }
}
