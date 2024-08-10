package com.naz.vSpace.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CloudinaryService {
    String uploadPhoto(MultipartFile photo);
    List<String> uploadPhotos(MultipartFile[] photos);
    String uploadFile(MultipartFile file);
}
